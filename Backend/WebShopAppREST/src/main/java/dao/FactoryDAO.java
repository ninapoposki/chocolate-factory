package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import beans.Factory;
import beans.Location;

public class FactoryDAO {
	
	private HashMap<String, Factory> factories = new HashMap<String, Factory>();
	private LocationDAO locationDAO;  // Dodajte referencu na LocationDAO

	public FactoryDAO() {
		
	}
	
	public FactoryDAO(String contextPath, LocationDAO locationDAO) {
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
		}
		
		return f;
	}
	
	public Factory save(Factory factory) {
		Integer maxId = -1;
		for (String id : factories.keySet()) {
			int idNum =Integer.parseInt(id);
			if (idNum > maxId) {
				maxId = idNum;
			}
		}
		maxId++;
		factory.setId(maxId.toString());
		factories.put(factory.getId(), factory);
		return factory;
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
	            .sorted(Comparator.comparing(Factory::getIsStatus).reversed())  // Sortiranje po statusu, true prvo
	            .sorted(Comparator.comparing(Factory::getId))  // Zatim sortiranje po ID-u
	            .collect(Collectors.toList());
	}


	
}
		

	
	
