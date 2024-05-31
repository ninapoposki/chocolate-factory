package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;
import java.nio.file.Paths;
import beans.User;
import enumerations.Gender;
import enumerations.Role;

public class UserDAO {
	 private static final String USERS_FILE = "users.txt";

	private HashMap<String, User> users = new HashMap<String, User>();
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
			
		}
		
		return u;
	}
	
	public User save(User user) {
		/*Integer maxId = -1;
		for (String id : users.keySet()) {
			int idNum = Integer.parseInt(id);
			if (idNum > maxId) {
				maxId = idNum;
			}
		}
		maxId++;
		
		user.setId(maxId.toString());
		users.put(user.getId(), user);
		saveToFile(user, contextPath);
		return user;*/
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
	    saveToFile(user, contextPath);
	    return user;
	}
	
	private void saveToFile(User user, String contextPath) {
        try {
            // Koristimo Paths.get za kreiranje putanje
            Path filePath = Paths.get("C:\\Users\\Dajana\\Documents\\VEB\\veb-programiranje\\Backend\\WebShopAppREST\\src\\main\\webapp\\users.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), true));
            System.out.println("Content of the file after writing: " + filePath.toString());
            
            // Pisanje korisničkih podataka u datoteku
            out.write(user.getId() + "," + user.getUsername() + "," + user.getPassword() + "," +
                    user.getFirstName() + "," + user.getLastName() + "," + user.getGender() + "," +
                    user.getDateOfBirth() + "," + user.getRole() + "\n");
            
            // Obavezno zatvaranje toka podataka
            out.flush();
            out.close();
            System.out.println("User saved to file successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUsers(String contextPath) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        BufferedReader in = null;
        try {
            // Koristimo Paths.get za kreiranje putanje
            Path filePath = Paths.get("C:\\Users\\Dajana\\Documents\\VEB\\veb-programiranje\\Backend\\WebShopAppREST\\src\\main\\webapp\\users.txt");
            System.out.println(filePath.toAbsolutePath());
            in = new BufferedReader(new FileReader(filePath.toString()));
            String line, id = "", username = "", password = "", firstname = "", lastname = "", gender = "", dateOfBirth = "", role = "";
            StringTokenizer st;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equals("") || line.indexOf('#') == 0)
                    continue;
                st = new StringTokenizer(line, ",");
                while (st.hasMoreTokens()) {
                    id = st.nextToken().trim();
                    System.out.println("Read ID: " + id);
                    username = st.nextToken().trim();
                    password = st.nextToken().trim();
                    firstname = st.nextToken().trim();
                    lastname = st.nextToken().trim();
                    gender = st.nextToken().trim();
                    dateOfBirth = st.nextToken().trim();
                    role = st.nextToken().trim();
                }
                Gender userGender = Gender.valueOf(gender.toUpperCase());
                Role userRole = Role.valueOf(role.toUpperCase());
                LocalDate DateOfBirth = LocalDate.parse(dateOfBirth, formatter);
                users.put(id, new User(id, username, password, firstname, lastname, userGender, DateOfBirth, userRole));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }
    }


}
