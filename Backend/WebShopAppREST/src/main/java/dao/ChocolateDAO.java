package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

import beans.Chocolate;
import beans.Factory;

public class ChocolateDAO {
	
	private HashMap<String, Chocolate> chocolates = new HashMap<String, Chocolate>();
	private String contextPath;
	private FactoryDAO factoryDAO;
	
	public ChocolateDAO() {
		
	}
	
	public ChocolateDAO(String contextPath,FactoryDAO factoryDAO) {
		this.contextPath = contextPath;
		this.factoryDAO = factoryDAO; 
	    this.factoryDAO.loadFactories(contextPath);
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
	        c.setImageUri(chocolate.getImageUri());
	        c.setNumberOfChocolates(chocolate.getNumberOfChocolates());
	        c.setPrice(chocolate.getPrice());
	        c.setType(chocolate.getType());
	        c.setVariety(chocolate.getVariety());
	        c.setWeight(chocolate.getWeight());
	        c.setFactoryId(chocolate.getFactoryId());
	        var factory=factoryDAO.findFactory(String.valueOf(chocolate.getFactoryId()));
	        c.setFactory(factory);
	        saveChocolates(); // Ova metoda treba da ažurira sve promene u CSV fajlu
	    }
	    
	    return c;
	}
	
	private void saveChocolates() {
	    try {
	        Path filePath = Paths.get(contextPath + "/chocolates.csv");
	        BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), false)); // false za prepisivanje fajla
	        for (Chocolate chocolate : chocolates.values()) {
	            out.write(chocolateToCsv(chocolate) + "\n");
	        }
	        out.flush();
	        out.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	private String chocolateToCsv(Chocolate chocolate) {
	    return chocolate.getId() + "," +
	           chocolate.getChocolateName() + "," +
	           chocolate.getPrice() + "," +
	           chocolate.getVariety() + "," +
	           chocolate.getFactoryId() + "," +
	           chocolate.getType() + "," +
	           chocolate.getWeight() + "," +
	           chocolate.getDescription() + "," +
	           chocolate.getImageUri() + "," +
	           chocolate.getNumberOfChocolates() + "," +
	           chocolate.getIsOnStock()+','+
	           chocolate.getIsActive();
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
	            .filter(chocolate -> chocolate.getFactoryId() == Integer.parseInt(factoryId) && chocolate.getIsActive())
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
                    chocolate.getImageUri() + "," + chocolate.getNumberOfChocolates() + ","+ chocolate.getIsOnStock()+ ","+ chocolate.getIsActive()+"\n");

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
				String factoryId = data[4].trim();
                Factory factory = factoryDAO.findFactory(factoryId);
				Chocolate chocolate = new Chocolate();
				chocolate.setId(data[0]);
				chocolate.setChocolateName(data[1]);
				chocolate.setPrice(Double.parseDouble(data[2]));
				chocolate.setVariety(data[3]);
                chocolate.setFactoryId(Integer.parseInt(factoryId));
				chocolate.setType(data[5]);
				chocolate.setWeight(Double.parseDouble(data[6]));
				chocolate.setDescription(data[7]);
				chocolate.setImageUri(data[8]);
				chocolate.setNumberOfChocolates(Integer.parseInt(data[9]));
				chocolate.setIsOnStock(Boolean.parseBoolean(data[10]));
				chocolate.setIsActive(Boolean.parseBoolean(data[11]));
				
				
				chocolate.setFactory(factory);
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
	public void deleteChocolate(String id) {
	    if (chocolates.containsKey(id)) {
	        Chocolate chocolate = chocolates.get(id);
	        chocolate.setIsActive(false);  
	        saveChocolates();  

	        // Ažuriraj listu čokolada u fabrici ako postoji
	        if (chocolate.getFactory() != null) {
	            Collection<Chocolate> factoryChocolates = findChocolatesByFactoryId(String.valueOf(chocolate.getFactoryId()));
	            factoryChocolates.removeIf(c -> !c.getIsActive()); // Ukloni sve neaktivne čokolade iz liste
	            // Ažuriraj čokolade u Factory objektu
	            chocolate.getFactory().setChocolates(new ArrayList<>(factoryChocolates));
	        }
	    }
	}


}
