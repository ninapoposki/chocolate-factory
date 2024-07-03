package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import beans.Factory;
import beans.Location;
import beans.User;
import enumerations.Role;

public class FactoryDAO {
    private HashMap<String, Factory> factories = new HashMap<String, Factory>();
    private LocationDAO locationDAO;  
    private ChocolateDAO chocolateDAO; 
    private UserDAO userDAO; 

    private String contextPath;

    public FactoryDAO() {
    }

    public FactoryDAO(String contextPath, LocationDAO locationDAO, ChocolateDAO chocolateDAO, UserDAO userDAO) {
        this.contextPath = contextPath;
        this.locationDAO = locationDAO;
        this.chocolateDAO = chocolateDAO;
        this.userDAO = userDAO;
        this.userDAO.loadUsers(contextPath);
        this.locationDAO.loadLocations(contextPath);
        loadFactories(contextPath);
    }

    public Collection<Factory> findAll() {
        return factories.values();
    }

    public Factory findFactory(String factoryId) {
        return factories.containsKey(factoryId) ? factories.get(factoryId) : null;
    }

    public Factory updateFactory(String id, Factory factory) {
        Factory f = factories.containsKey(id) ? factories.get(id) : null;
        if (f == null) {
            return save(factory);
        } else {
            f.setFactoryName(factory.getFactoryName());
            f.setGrade(factory.getGrade());
            f.setIsStatus(factory.getIsStatus());
            f.setLocation(factory.getLocation());
            f.setLogoUri(factory.getLogoUri());
            f.setWorkingTime(factory.getWorkingTime());
            f.setChocolates(factory.getChocolates());
            saveFactories(); 
        }
        return f;
    }

    public Factory save(Factory factory) {
        if (factory.getLocation() != null && factory.getLocation().getId() == null) {
            Location newLocation = locationDAO.save(factory.getLocation());
            factory.setLocation(newLocation);
        }

        Integer maxId = -1;
        for (String id : factories.keySet()) {
            int idNum = Integer.parseInt(id);
            if (idNum > maxId) {
                maxId = idNum;
            }
        }
        maxId++;
        factory.setId(maxId.toString());
        factories.put(factory.getId(), factory);
        saveToFile(factory);
        return factory;
    }

    private void saveFactories() {
        try {
            Path filePath = Paths.get(contextPath + "/factories.csv");
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), false)); 
            for (Factory factory : factories.values()) {
                out.write(factoryToCsv(factory) + "\n");
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String factoryToCsv(Factory factory) {
        StringBuilder employeesBuilder = new StringBuilder();
        if (factory.getEmployees() != null && !factory.getEmployees().isEmpty()) {
            for (User employee : factory.getEmployees()) {
                employeesBuilder.append(employee.getId()).append(";");
            }
        }
        return factory.getId() + "," +
               factory.getFactoryName() + "," +
               factory.getWorkingTime() + "," +
               factory.getIsStatus() + "," +
               factory.getLogoUri() + "," +
               factory.getGrade() + "," +
               factory.getLocation().getId() + "," +
               factory.getUser().getId() + "," +
               employeesBuilder.toString();
    }

    private void saveToFile(Factory factory) {
        try {
            Path filePath = Paths.get(contextPath + "/factories.csv");
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), true)); 

            out.write(factoryToCsv(factory) + "\n");
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadFactories(String contextPath) {
        BufferedReader in = null;
        try {
            File file = new File(contextPath + "/factories.csv");
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue; 

                String[] data = line.split(",");
                if (data.length < 8) continue;  

                String id = data[0].trim();
                String factoryName = data[1].trim();
                int workingTime = Integer.parseInt(data[2].trim());
                boolean isStatus = Boolean.parseBoolean(data[3].trim());
                String logoUri = data[4].trim();
                double grade = Double.parseDouble(data[5].trim());
                String locationId = data[6].trim();
                String userId = data[7].trim();
                String[] employeeIds = data.length > 8 ? data[8].trim().split(";") : new String[0];

                Location location = locationDAO.findLocation(locationId);
                User user = userDAO.findUser(userId);
                Factory factory = new Factory(id, factoryName, workingTime, isStatus, logoUri, grade, location, user);

                List<User> employees = new ArrayList<>();
                for (String employeeId : employeeIds) {
                    if (!employeeId.isEmpty()) {
                        User employee = userDAO.findUser(employeeId);
                        if (employee != null) {
                            employees.add(employee);
                        }
                    }
                }
                factory.setEmployees(employees);
                factory.setChocolates(chocolateDAO.findChocolatesByFactoryId(id).stream().collect(Collectors.toList()));
                factories.put(id, factory);  
                System.out.println("Factory loaded: " + factory.getFactoryName());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Factory addEmployeeToFactory(String factoryId, User employee) {
        Factory factory = factories.get(factoryId);
        if (factory != null) {
            List<User> employees = factory.getEmployees();
            if (employees == null) {
                employees = new ArrayList<>();
            }
            employee.setRole(Role.EMPLOYEE);
            employees.add(employee);
            factory.setEmployees(employees);
            factories.put(factoryId, factory); 
            saveFactories(); 
            return factory;
        }
        return null;
    }

    public Collection<Factory> findAllAndSort() {
        return factories.values().stream()
                .sorted(Comparator.comparing(Factory::getIsStatus).reversed())  
                .sorted(Comparator.comparing(Factory::getId)) 
                .collect(Collectors.toList());
    }

    public Collection<Factory> searchFactories(String search) {
        double averageGrade = 0;
        boolean isAverageGrade = false;
        try {
            averageGrade = Double.parseDouble(search);
            isAverageGrade = true;
        } catch (NumberFormatException e) {
        }

        final double finalAverageGrade = averageGrade;
        final boolean finalIsAverageGrade = isAverageGrade;
        return factories.values().stream()
                .filter(factory -> {
                    boolean matchesFactoryName = search != null && !search.isEmpty() && factory.getFactoryName().toLowerCase().contains(search.toLowerCase());
                    boolean matchesLocation = search != null && !search.isEmpty() && (factory.getLocation().getCity().toLowerCase().contains(search.toLowerCase()) || factory.getLocation().getCountry().toLowerCase().contains(search.toLowerCase()));
                    boolean matchesAverageGrade = finalIsAverageGrade && factory.getGrade() >= finalAverageGrade;

                    boolean matchesChocolateName = search != null && !search.isEmpty() && factory.getChocolates().stream()
                            .anyMatch(chocolate -> chocolate.getChocolateName().toLowerCase().contains(search.toLowerCase()));

                    boolean matches = matchesFactoryName || matchesLocation || matchesAverageGrade || matchesChocolateName;

                    return matches;
                })
                .collect(Collectors.toList());
    }

    public Collection<Factory> sortFactories(String sortBy, boolean ascending) {
        Comparator<Factory> comparator;

        switch (sortBy.toLowerCase()) {
            case "factoryname":
                comparator = Comparator.comparing(Factory::getFactoryName);
                break;
            case "location":
                comparator = Comparator.comparing(factory -> factory.getLocation().getCity());
                break;
            case "averagegrade":
                comparator = Comparator.comparing(Factory::getGrade);
                break;
            default:
                throw new IllegalArgumentException("Invalid sort parameter: " + sortBy);
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return factories.values().stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public Collection<Factory> filterFactories(String chocolateType, String chocolateKind, Boolean openOnly) {
        return factories.values().stream()
                .filter(factory -> {
                    boolean matchesOpenOnly = (openOnly == null || !openOnly) || factory.getIsStatus();
                    boolean matchesChocolateType = (chocolateType == null || chocolateType.isEmpty()) || factory.getChocolates().stream()
                            .anyMatch(chocolate -> chocolate.getType().equalsIgnoreCase(chocolateType));
                    boolean matchesChocolateKind = (chocolateKind == null || chocolateKind.isEmpty()) || factory.getChocolates().stream()
                            .anyMatch(chocolate -> chocolate.getVariety().equalsIgnoreCase(chocolateKind));

                    return matchesOpenOnly && matchesChocolateType && matchesChocolateKind;
                })
                .collect(Collectors.toList());
    }

    public LocationDAO getLocationDAO() {
        return locationDAO;
    }
    
    public List<String> getManagersWithFactories() {
        return factories.values().stream()
                .filter(factory -> factory.getUser() != null)
                .map(factory -> factory.getUser().getId())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<User> findUnassignedManagers() {
        List<String> assignedManagerIds = getManagersWithFactories();
        return userDAO.findAll().stream()
            .filter(user -> user.getRole() == Role.MANAGER && !assignedManagerIds.contains(user.getId()))
            .collect(Collectors.toList());
    }

    public Collection<Factory> findFactoriesByUserId(String userId) {
        return factories.values().stream()
                .filter(factory -> factory.getUser() != null && factory.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public Factory findFactoryByUserId(String userId) {
        for (Factory factory : factories.values()) {
            if (factory.getEmployees() != null) {
                for (User user : factory.getEmployees()) {
                    if (user.getId().equals(userId)) {
                        return factory;
                    }
                }
            }
        }
        return null;
    }
}
