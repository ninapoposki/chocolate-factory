package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import beans.Chocolate;
import beans.Factory;
import beans.Purchase;
import enumerations.PurchaseStatus;

public class PurchaseDAO {
	
	private HashMap<String, Purchase> purchases = new HashMap<String, Purchase>();
	private String contextPath;
	private FactoryDAO factoryDAO;
	private ChocolateDAO chocolateDAO;
	
	public PurchaseDAO() {
		
	}
	
	public PurchaseDAO(String contextPath, FactoryDAO factoryDAO, ChocolateDAO chocolateDAO) {
		this.contextPath = contextPath;
		this.factoryDAO = factoryDAO;
		this.factoryDAO.loadFactories(contextPath);
		this.chocolateDAO  =chocolateDAO;
		this.chocolateDAO.loadChocolates(contextPath);
		loadPurchases(contextPath);
	}

	public Collection<Purchase> findAll() {
		return purchases.values();
	}

	public Purchase findPurchases(String id) {
		return purchases.containsKey(id) ? purchases.get(id) : null;
	}
	
	public Purchase updatePurchases(String id, Purchase purchase) {
		Purchase p = purchases.containsKey(id) ? purchases.get(id) : null;
		if (p == null) {
			return save(purchase);
		} else {
			
		}
		
		return p;
	}
	
	public Purchase save(Purchase purchase) {
		Integer maxId = -1;
		for (String id : purchases.keySet()) {
			int idNum =Integer.parseInt(id);
			if (idNum > maxId) {
				maxId = idNum;
			}
		}
		maxId++;
		purchase.setId(maxId.toString());
		purchases.put(purchase.getId(), purchase);
		saveToFile(purchase);
		return purchase;
	}
	private void saveToFile(Purchase purchase) {
	    try {
	        Path filePath = Paths.get(contextPath + "/purchases.csv");
	        BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), true));

	        StringBuilder chocolateIds = new StringBuilder();
	        for (Chocolate chocolate : purchase.getChocolates()) {
	            chocolateIds.append(chocolate.getId()).append("|");
	        }

	        out.write(purchase.getId() + "," + purchase.getCode() + "," + chocolateIds.toString() + "," +
	                purchase.getFactoryId() + "," + purchase.getDateAndTime().getTime() + "," +
	                purchase.getPrice() + "," + purchase.getCustomerId() + "," + purchase.getCustomerFirstName() + "," +
	                purchase.getCustomerLastName() + "," + purchase.getStatus().toString() + "\n");

	        out.flush();
	        out.close();
	        System.out.println("Purchase saved to file successfully.");
	        System.out.println("Context path: " + contextPath);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	private void loadPurchases(String contextPath) {
	    BufferedReader in = null;
	    try {
	        File file = new File(contextPath + "/purchases.csv");

	        // Provera postojanja fajla i ispis putanje
	        if (file.exists()) {
	            System.out.println("Purchases file exists at: " + file.getAbsolutePath());
	        } else {
	            System.out.println("Purchases file does not exist at: " + file.getAbsolutePath());
	            return;
	        }

	        in = new BufferedReader(new FileReader(file));
	        String line;

	        while ((line = in.readLine()) != null) {
	            line = line.trim();
	            if (line.equals("") || line.startsWith("#"))
	                continue;

	            String[] data = parseCsvLine(line);
	            String factoryId = data[3].trim();
	            //  Factory factory = factoryDAO.findFactory(factoryId);

	            List<Chocolate> chocolates = new ArrayList<>();
	            String[] chocolateIds = data[2].split("\\|");
	            for (String chocolateId : chocolateIds) {
	                chocolates.add(chocolateDAO.findChocolates(chocolateId.trim()));
	            }

	            Purchase purchase = new Purchase();
	            purchase.setId(data[0]);
	            purchase.setCode(data[1]);
	            purchase.setChocolates(chocolates);
	            purchase.setFactoryId(Integer.parseInt(factoryId));
	            purchase.setDateAndTime(new Date(Long.parseLong(data[4])));
	            purchase.setPrice(Double.parseDouble(data[5]));
	            purchase.setCustomerId(Integer.parseInt(data[6]));
	            purchase.setCustomerFirstName(data[7]);
	            purchase.setCustomerLastName(data[8]);
	            purchase.setStatus(PurchaseStatus.valueOf(data[9]));

	            purchases.put(purchase.getId(), purchase);
	            System.out.println("Context path: " + contextPath);
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

	 public Collection<Purchase> findByCustomerId(int customerId) {
	        return purchases.values().stream()
	                .filter(purchase -> purchase.getCustomerId() == customerId)
	                .collect(Collectors.toList());
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
}
