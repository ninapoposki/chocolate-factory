package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;

import beans.Location;

public class LocationDAO {
    
    private HashMap<String, Location> locations = new HashMap<String, Location>();
    private String contextPath;

    public LocationDAO() {
        
    }
    
    public LocationDAO(String contextPath) {
        this.contextPath = contextPath;
        loadLocations(contextPath);
    }

    public Collection<Location> findAll() {
        return locations.values();
    }

    public Location updateLocation(String id, Location location) {
        Location l = locations.containsKey(id) ? locations.get(id) : null;
        if (l == null) {
            return save(location);
        } else {
            l.setCity(location.getCity());
            l.setHeight(location.getHeight());
            l.setPostalCode(location.getPostalCode());
            l.setStreet(location.getStreet());
            l.setStreetNumber(location.getStreetNumber());
            l.setWidth(location.getWidth());
            l.setCountry(location.getCountry());
            saveLocations(); // Ova metoda treba da ažurira sve promene u CSV fajlu
        }
        
        return l;
    }

    public Location save(Location location) {
        Integer maxId = -1;
        for (String id : locations.keySet()) {
            int idNum = Integer.parseInt(id);
            if (idNum > maxId) {
                maxId = idNum;
            }
        }
        maxId++;
        location.setId(maxId.toString());
        locations.put(location.getId(), location);
        saveToFile(location);
        return location;
    }

    private void saveLocations() {
        try {
            Path filePath = Paths.get(contextPath + "/locations.csv");
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), false)); // false za prepisivanje fajla
            for (Location location : locations.values()) {
                out.write(locationToCsv(location) + "\n");
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String locationToCsv(Location location) {
        return location.getId() + "," +
               location.getWidth() + "," +
               location.getHeight() + "," +
               location.getStreet() + "," +
               location.getStreetNumber() + "," +
               location.getCity() + "," +
               location.getPostalCode() + "," +
               location.getCountry();
    }

    private void saveToFile(Location location) {
        try {
            Path filePath = Paths.get(contextPath + "/locations.csv");
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), true)); // true za dodavanje u fajl

            out.write(locationToCsv(location) + "\n");
            out.flush();
            out.close();
            System.out.println("Location saved to file successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadLocations(String contextPath) {
        BufferedReader in = null;
        try {
            File file = new File(contextPath + "/locations.csv");
            System.out.println(file.getCanonicalPath());
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equals("") || line.charAt(0) == '#')
                    continue;  // Preskoči prazne linije i komentare

                String[] data = line.split(",");
                if (data.length < 7) continue;  // Preskoči redove koji nemaju svih 7 potrebnih podataka

                String id = data[0].trim();
                double width = Double.parseDouble(data[1].trim());
                double height = Double.parseDouble(data[2].trim());
                String street = data[3].trim();
                String streetNumber = data[4].trim();
                String city = data[5].trim();
                String postalCode = data[6].trim();
                String country = data[7].trim();

                locations.put(id, new Location(id, width, height, street, streetNumber, city, postalCode, country));
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

    public Location findLocation(String id) {
        return locations.containsKey(id) ? locations.get(id) : null;
    }
}
