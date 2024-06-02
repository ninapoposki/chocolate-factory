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
import java.util.stream.Collectors;

import beans.Chocolate;

public class ChocolateDAO {
	
	private HashMap<String, Chocolate> chocolates = new HashMap<String, Chocolate>();
	private String contextPath;
	
	public ChocolateDAO() {
		
	}
	
	public ChocolateDAO(String contextPath) {
		this.contextPath = contextPath;
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
	
		Integer maxId = 0;
		for(String id: chocolates.keySet()) {
			int idNum =Integer.parseInt(id);
			if(idNum > maxId) {
				maxId = idNum;
			}
		}
		
		maxId++;
		
	    chocolate.setId(maxId.toString());
	    chocolates.put(chocolate.getId(), chocolate);
	    saveToFile(chocolate);
	    return chocolate;
	}
	
	public Collection<Chocolate> findChocolatesByFactoryId(String factoryId) {
        return chocolates.values().stream()
                .filter(chocolate -> (chocolate.getFactoryId()==Integer.parseInt(factoryId)))
                .collect(Collectors.toList());
    }
	
	private void saveToFile(Chocolate chocolate) {
        try {
            Path filePath = Paths.get(contextPath + "/chocolates.csv");
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), true));

            out.write(chocolate.getId() + "," + chocolate.getChocolateName() + "," +
                    chocolate.getPrice() + "," + chocolate.getVariety() + "," +
                    chocolate.getFactoryId() + "," + chocolate.getType() + "," +
                    chocolate.getWeight() + "," + chocolate.getDescription() + "," +
                    chocolate.getImageUri() + "," + chocolate.getNumberOfChocolates() + ","+ chocolate.getIsOnStock()+ "\n");

            out.flush();
            out.close();
            System.out.println("Chocolate saved to file successfully.");

            System.out.println("Kontekstna putanja: " + contextPath);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void loadChocolates(String contextPath) {
		BufferedReader in = null;
		try {
			File file = new File(contextPath +"/chocolates.csv");
			in = new BufferedReader(new FileReader(file));
			String line;

			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.startsWith("#"))
					continue;

				String[] data = line.split(",");

				Chocolate chocolate = new Chocolate();
				chocolate.setId(data[0]);
				chocolate.setChocolateName(data[1]);
				chocolate.setPrice(Double.parseDouble(data[2]));
				chocolate.setVariety(data[3]);
				chocolate.setFactoryId(Integer.parseInt(data[4]));
				chocolate.setType(data[5]);
				chocolate.setWeight(Double.parseDouble(data[6]));
				chocolate.setDescription(data[7]);
				chocolate.setImageUri(data[8]);
				chocolate.setNumberOfChocolates(Integer.parseInt(data[9]));
				chocolate.setIsOnStock(Boolean.parseBoolean(data[10]));

				chocolates.put(chocolate.getId(), chocolate);
		        System.out.println("Kontekstna putanja: " + contextPath);

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
}
