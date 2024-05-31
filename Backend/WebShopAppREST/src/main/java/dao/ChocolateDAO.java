package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import beans.Chocolate;

public class ChocolateDAO {
	
	private HashMap<String, Chocolate> chocolates = new HashMap<String, Chocolate>();
	
	public ChocolateDAO() {
		
	}
	
	public ChocolateDAO(String contextPath) {
		loadChocolates(contextPath);
	}

	public Collection<Chocolate> findAll() {
		return chocolates.values();
	}

	public Chocolate findChocolates(String id) {
		return chocolates.containsKey(id) ? chocolates.get(id) : null;
	}
	
	public Chocolate updateChocolate(String id, Chocolate chocolate) {
		Chocolate c = chocolates.containsKey(id) ? chocolates.get(id) : null;
		if (c == null) {
			return save(chocolate);
		} else {
			c.setChocolateName(chocolate.getChocolateName());
			c.setDescription(chocolate.getDescription());
			c.setFactory(chocolate.getFactory());
			c.setImageUri(c.getImageUri());
			c.setNumberOfChocolates(c.getNumberOfChocolates());
			c.setPrice(chocolate.getPrice());
			c.setType(chocolate.getType());
			c.setVariety(chocolate.getVariety());
			c.setWeight(chocolate.getWeight());
		}
		
		return c;
	}
	
	public Chocolate save(Chocolate chocolate) {
		Integer maxId = -1;
		for (String id : chocolates.keySet()) {
			int idNum =Integer.parseInt(id);
			if (idNum > maxId) {
				maxId = idNum;
			}
		}
		maxId++;
		chocolate.setId(maxId.toString());
		chocolates.put(chocolate.getId(), chocolate);
		return chocolate;
	}

	private void loadChocolates(String contextPath) {
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/chocolates.txt");
			System.out.println(file.getCanonicalPath());
			in = new BufferedReader(new FileReader(file));
			String line, id = "", chocolateName = "", price = "",variety="",factory="",type="",weight="",description="",imageUri="",numberOfChocolates="";
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					id = st.nextToken().trim();
					chocolateName = st.nextToken().trim();
					price = st.nextToken().trim();
					variety = st.nextToken().trim();
					factory = st.nextToken().trim();
					type = st.nextToken().trim();
					weight = st.nextToken().trim();
					description = st.nextToken().trim();
					imageUri = st.nextToken().trim();
					price = st.nextToken().trim();
					numberOfChocolates = st.nextToken().trim();


				}
				//chocolates.put(id, new Chocolate(id, chocolateName, Double
						//.parseDouble(price),variety,factory,type,Double.parseDouble(weight),description,imageUri,Integer.parseInt(numberOfChocolates)));
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
}
