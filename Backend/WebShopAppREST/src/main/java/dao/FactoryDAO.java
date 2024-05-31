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

	public Factory findFactory(String id) {
		return factories.containsKey(id) ? factories.get(id) : null;
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

	private void loadFactories(String contextPath) {
        BufferedReader in = null;
        try {
            File file = new File(contextPath + "/factories.txt");
            in = new BufferedReader(new FileReader(file));
            String line, id, factoryName, workingTime, status, logoUri, grade, locationId;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.charAt(0) == '#') continue;
                StringTokenizer st = new StringTokenizer(line, ";");
                id = st.nextToken().trim();
                factoryName = st.nextToken().trim();
                workingTime = st.nextToken().trim();
                status = st.nextToken().trim();
                logoUri = st.nextToken().trim();
                grade = st.nextToken().trim();
                locationId = st.nextToken().trim();  

                boolean statusBool = Boolean.parseBoolean(status);
                Location location = locationDAO.findLocation(locationId);  
                factories.put(id, new Factory(id, factoryName, Integer.parseInt(workingTime), statusBool, logoUri, Double.parseDouble(grade), location));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) { }
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
		

	
	
