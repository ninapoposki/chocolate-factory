package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import beans.Location;

public class LocationDAO {
	
	private HashMap<String, Location> locations = new HashMap<String, Location>();
	
	public LocationDAO() {
		
	}
	
	public LocationDAO(String contextPath) {
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
		}
		
		return l;
	}
	
	public Location save(Location location) {
		Integer maxId = -1;
		for (String id : locations.keySet()) {
			int idNum =Integer.parseInt(id);
			if (idNum > maxId) {
				maxId = idNum;
			}
		}
		maxId++;
		location.setId(maxId.toString());
		locations.put(location.getId(), location);
		return location;
	}

	public void loadLocations(String contextPath) {
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/locations.txt");
			System.out.println(file.getCanonicalPath());
			in = new BufferedReader(new FileReader(file));
			String line, id = "", width = "", height = "",street="",streetNumber="",city="",postalCode="";
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					id = st.nextToken().trim();
					width = st.nextToken().trim();
					height = st.nextToken().trim();
					street = st.nextToken().trim();
					streetNumber = st.nextToken().trim();
					city = st.nextToken().trim();
					postalCode = st.nextToken().trim();
					
				}
				 double parsedWidth = Double.parseDouble(width);
				 double parsedHeight = Double.parseDouble(height);
				locations.put(id, new Location(id, parsedWidth,parsedHeight,street,streetNumber,city,postalCode));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( in != null ) {
				try {
					in.close();
				}
				catch (Exception e) { }
			}
		}
		
	}
	public Location findLocation(String id) {
		return locations.containsKey(id) ? locations.get(id) : null;
	}
	
	
}
