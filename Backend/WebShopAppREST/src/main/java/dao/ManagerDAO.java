package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import beans.Manager;

public class ManagerDAO {
	
	private HashMap<String, Manager> managers = new HashMap<String, Manager>();
	
	public ManagerDAO() {
		
	}
	
	public ManagerDAO(String contextPath) {
		loadManagers(contextPath);
	}

	public Collection<Manager> findAll() {
		return managers.values();
	}

	public Manager findProduct(String id) {
		return managers.containsKey(id) ? managers.get(id) : null;
	}
	
	public Manager updateProduct(String id, Manager manager) {
		Manager m = managers.containsKey(id) ? managers.get(id) : null;
		if (m == null) {
			return save(manager);
		} else {
			m.setChocolateFactory(manager.getChocolateFactory());
		
		}
		
		return m;
	}
	
	public Manager save(Manager manager) {
		Integer maxId = -1;
		for (String id : managers.keySet()) {
			int idNum =Integer.parseInt(id);
			if (idNum > maxId) {
				maxId = idNum;
			}
		}
		maxId++;
		manager.setId(maxId.toString());
		managers.put(manager.getId(), manager);
		return manager;
	}

	private void loadManagers(String contextPath) {
		/*BufferedReader in = null;
		try {
			File file = new File(contextPath + "/managers.txt");
			System.out.println(file.getCanonicalPath());
			in = new BufferedReader(new FileReader(file));
			String line, id = "", name = "", price = "";
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					id = st.nextToken().trim();
					name = st.nextToken().trim();
					price = st.nextToken().trim();
				}
				managers.put(id, new Manager(id, name, Double
						.parseDouble(price)));
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
		}*/
		
	}
}
