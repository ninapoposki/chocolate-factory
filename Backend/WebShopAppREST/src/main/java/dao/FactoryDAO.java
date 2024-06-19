package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

import beans.Factory;
import beans.Location;

public class FactoryDAO {

    private HashMap<String, Factory> factories = new HashMap<String, Factory>();
    private LocationDAO locationDAO;
    private String contextPath;

    public FactoryDAO() {
        
    }

    public FactoryDAO(String contextPath, LocationDAO locationDAO) {
        this.contextPath = contextPath;
        this.locationDAO = locationDAO;
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
            saveFactories(); // Ova metoda treba da ažurira sve promene u CSV fajlu
        }
        
        return f;
    }

    public Factory save(Factory factory) {
        // Prvo sačuvaj lokaciju ako ne postoji
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
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), false)); // false za prepisivanje fajla
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
        return factory.getId() + "," +
               factory.getFactoryName() + "," +
               factory.getWorkingTime() + "," +
               factory.getIsStatus() + "," +
               factory.getLogoUri() + "," +
               factory.getGrade() + "," +
               factory.getLocation().getId();
    }

    private void saveToFile(Factory factory) {
        try {
            Path filePath = Paths.get(contextPath + "/factories.csv");
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), true)); // true za dodavanje u fajl

            out.write(factoryToCsv(factory) + "\n");
            out.flush();
            out.close();
            System.out.println("Factory saved to file successfully.");
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
                if (line.isEmpty() || line.startsWith("#")) continue;  // Preskoči prazne linije i komentare

                String[] data = line.split(",");
                if (data.length < 7) continue;  // Preskoči redove koji nemaju svih 7 podataka

                String id = data[0].trim();
                String factoryName = data[1].trim();
                int workingTime = Integer.parseInt(data[2].trim());
                boolean isStatus = Boolean.parseBoolean(data[3].trim());
                String logoUri = data[4].trim();
                double grade = Double.parseDouble(data[5].trim());
                String locationId = data[6].trim();

                Location location = locationDAO.findLocation(locationId);
                Factory factory = new Factory(id, factoryName, workingTime, isStatus, logoUri, grade, location);
                factories.put(id, factory);  // Koristi ID kao ključ za mapu
                System.out.println("Loaded factory: " + factoryName);
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
            // Ignoriši grešku jer to znači da nije prosečna ocena
        }
        
        final double finalAverageGrade = averageGrade;
        final boolean finalIsAverageGrade = isAverageGrade;
        return factories.values().stream()
                .filter(factory -> {
                    boolean matchesFactoryName = search != null && !search.isEmpty() && factory.getFactoryName().toLowerCase().contains(search.toLowerCase());
                    boolean matchesLocation = search != null && !search.isEmpty() && (factory.getLocation().getCity().toLowerCase().contains(search.toLowerCase()) || factory.getLocation().getCountry().toLowerCase().contains(search.toLowerCase()));
                    boolean matchesAverageGrade = finalIsAverageGrade && factory.getGrade() >= finalAverageGrade;
                    boolean matches = matchesFactoryName || matchesLocation || matchesAverageGrade;
                    System.out.println("Factory: " + factory.getFactoryName() + ", Matches Factory Name: " + matchesFactoryName + ", Matches Location: " + matchesLocation 
                            + ", Matches Average Grade: " + matchesAverageGrade + ", Overall Matches: " + matches);
                    
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
                    return matchesOpenOnly;
                })
                .collect(Collectors.toList());
    }

    public LocationDAO getLocationDAO() {
        return locationDAO;
    }
}
