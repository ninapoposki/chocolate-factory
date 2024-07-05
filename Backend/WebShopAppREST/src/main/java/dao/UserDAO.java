package dao;

import beans.User;
import enumerations.ActivityStatus;
import enumerations.Gender;
import enumerations.Role;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class UserDAO {

    private HashMap<String, User> users = new HashMap<>();
    private String contextPath;

    public UserDAO() {
    }

    public UserDAO(String contextPath) {
        this.contextPath = contextPath;
        loadUsers(contextPath);
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public User findUser(String id) {
        return users.containsKey(id) ? users.get(id) : null;
    }
    public User findByUsername(String username) {
        for (User user : users.values()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public User updateUser(String id, User user) {
        User u = users.containsKey(id) ? users.get(id) : null;
        if (u == null) {
            return save(user);
        } else {
            u.setUsername(user.getUsername());
            u.setPassword(user.getPassword());
            u.setFirstName(user.getFirstName());
            u.setLastName(user.getLastName());
            u.setGender(user.getGender());
            u.setDateOfBirth(user.getDateOfBirth());
            u.setRole(user.getRole());
            u.setPoints(user.getPoints());
            u.setActivity(user.getActivity());
            saveToFile();
        }
        return u;
    }

    public User save(User user) {
    	 loadUsers(contextPath);
        var users1 = findAll();
        int nextId;
        if (users1.isEmpty()) {
            nextId = 1;
        } else {
            int maxId = 0;
            for (String id : users.keySet()) {
                int idNum = Integer.parseInt(id);
                if (idNum > maxId) {
                    maxId = idNum;
                }
            }
            nextId = maxId + 1;
        }
        user.setId(Integer.toString(nextId));
        users.put(user.getId(), user);
        saveToFile();
        return user;
    }

    private void saveToFile() {
        try {
            Path filePath = Paths.get(contextPath + "/users.csv");
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), false));
            for (User user : users.values()) {
                out.write(userToCsv(user) + "\n");
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String userToCsv(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return user.getId() + "," +
               user.getUsername() + "," +
               user.getPassword() + "," +
               user.getFirstName() + "," +
               user.getLastName() + "," +
               user.getGender().toString() + "," +
               user.getDateOfBirth().format(formatter) + "," +
               user.getRole().toString() + "," + 
               user.getPoints()+ ","+
               user.getActivity().toString();
    }

    public void loadUsers(String contextPath) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        BufferedReader in = null;
        try {
            Path filePath = Paths.get(contextPath + "/users.csv");
            in = new BufferedReader(new FileReader(filePath.toString()));
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equals("") || line.startsWith("#"))
                    continue;

                String[] data = parseCsvLine(line);
                String id = data[0].trim();
                String username = data[1].trim();
                String password = data[2].trim();
                String firstName = data[3].trim();
                String lastName = data[4].trim();
                Gender gender = Gender.valueOf(data[5].trim().toUpperCase());
                LocalDate dateOfBirth = LocalDate.parse(data[6].trim(), formatter);
                Role role = Role.valueOf(data[7].trim().toUpperCase());
                double points = Double.parseDouble(data[8].trim());
                ActivityStatus activity=ActivityStatus.valueOf(data[9].trim().toUpperCase());
                
                User user = new User(id, username, password, firstName, lastName, gender, dateOfBirth, role, points,activity);
                users.put(id, user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(currentToken.toString());
                currentToken.setLength(0);
            } else {
                currentToken.append(c);
            }
        }
        tokens.add(currentToken.toString());
        return tokens.toArray(new String[0]);
    }
    
    public boolean existsByUsername(String username) {
        for (User user : users.values()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean validateUser(String username, String password) {
        for (User user : users.values()) {
            if (user.getUsername().equalsIgnoreCase(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
    
    //za pronalazak slobodnih 
    //ovo ti ne treba mozes direktno pozvati
    public List<User> findUnassignedManagers(Collection<String> assignedManagerIds) {
        return users.values().stream()
                .filter(user -> user.getRole() == Role.MANAGER && !assignedManagerIds.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public Collection<User> getAllWithoutAdministrators(){
    	return users.values().stream().filter(user->user.getRole()!=Role.ADMINISTRATOR).collect(Collectors.toList());
    }

//    public Collection<User> searchUsers(String searchTerm) {
//        if (searchTerm == null || searchTerm.trim().isEmpty()) {
//            return getAllWithoutAdministrators();
//        }
//        String lowerCaseSearchTerm = searchTerm.toLowerCase();
//        return users.values().stream()
//            .filter(user -> user.getRole() != Role.ADMINISTRATOR &&
//                            (user.getFirstName().toLowerCase().contains(lowerCaseSearchTerm) ||
//                             user.getLastName().toLowerCase().contains(lowerCaseSearchTerm) ||
//                             user.getUsername().toLowerCase().contains(lowerCaseSearchTerm)))
//            .collect(Collectors.toList());
//    }
//    public Collection<User> sortUsers(String sortBy, boolean ascending) {
//        Comparator<User> comparator;
//
//        switch (sortBy.toLowerCase()) {
//            case "firstname":
//                comparator = Comparator.comparing(User::getFirstName);
//                break;
//            case "lastname":
//                comparator = Comparator.comparing(User::getLastName);
//                break;
//            case "username":
//                comparator = Comparator.comparing(User::getUsername);
//                break;
//            case "points":
//                comparator = Comparator.comparingDouble(User::getPoints);
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid sort parameter");
//        }
//
//        if (!ascending) {
//            comparator = comparator.reversed();
//        }
//
//        return users.values().stream()
//                .filter(user -> user.getRole() != Role.ADMINISTRATOR)
//                .sorted(comparator)
//                .collect(Collectors.toList());
//    }
//    
//    public Collection<User> filterUsers(String role, String type) {
//        return users.values().stream()
//                .filter(user -> {
//                    if (role != null && !role.isEmpty()) {
//                        return user.getRole().toString().equalsIgnoreCase(role);
//                    }
//                    if (type != null && !type.isEmpty()) {
//                        switch (type.toLowerCase()) {
//                            case "gold":
//                                return user.getPoints() > 5000;
//                            case "silver":
//                                return user.getPoints() < 5000 && user.getPoints() >= 3000;
//                            case "bronze":
//                                return user.getPoints() < 3000;
//                        }
//                    }
//                    return true; // Return all if no filter is provided
//                })
//                .filter(user -> user.getRole() != Role.ADMINISTRATOR)
//                .collect(Collectors.toList());
//    }
    public Collection<User> searchSortFilterUsers(String searchTerm, String sortBy, boolean ascending, String role, String type) {
        Comparator<User> comparator = Comparator.comparing(User::getId); // Default comparator

        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy.toLowerCase()) {
                case "firstname":
                    comparator = Comparator.comparing(User::getFirstName);
                    break;
                case "lastname":
                    comparator = Comparator.comparing(User::getLastName);
                    break;
                case "username":
                    comparator = Comparator.comparing(User::getUsername);
                    break;
                case "points":
                    comparator = Comparator.comparingDouble(User::getPoints);
                    break;
            }
            if (!ascending) {
                comparator = comparator.reversed();
            }
        }

        String lowerCaseSearchTerm = (searchTerm != null) ? searchTerm.toLowerCase() : "";

        return users.values().stream()
                .filter(user -> user.getRole() != Role.ADMINISTRATOR)
                .filter(user -> {
                    boolean matchesSearch = lowerCaseSearchTerm.isEmpty() || 
                                            user.getFirstName().toLowerCase().contains(lowerCaseSearchTerm) ||
                                            user.getLastName().toLowerCase().contains(lowerCaseSearchTerm) ||
                                            user.getUsername().toLowerCase().contains(lowerCaseSearchTerm);
                    boolean matchesRole = (role == null || role.isEmpty()) || user.getRole().toString().equalsIgnoreCase(role);
                    boolean matchesType = true;
                    if (type != null && !type.isEmpty()) {
                        switch (type.toLowerCase()) {
                            case "gold":
                                matchesType = user.getPoints() > 5000;
                                break;
                            case "silver":
                                matchesType = user.getPoints() < 5000 && user.getPoints() >= 3000;
                                break;
                            case "bronze":
                                matchesType = user.getPoints() < 3000;
                                break;
                        }
                    }
                    return matchesSearch && matchesRole && matchesType;
                })
                .sorted(comparator)
                .collect(Collectors.toList());
    }




}


