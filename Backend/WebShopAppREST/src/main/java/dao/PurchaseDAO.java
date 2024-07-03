package dao;

import java.io.BufferedReader;
import java.time.OffsetDateTime;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import beans.Comment;
import beans.Factory;
import beans.Purchase;
import beans.ShoppingCart;
import beans.User;
import enumerations.PurchaseStatus;
import enumerations.Role;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
public class PurchaseDAO {
	
	private HashMap<String, Purchase> purchases = new HashMap<String, Purchase>();
	private String contextPath;
	private FactoryDAO factoryDAO;
	private ChocolateDAO chocolateDAO;
	private ShoppingCartDAO shoppingCartDAO;
	private CommentDAO commentDAO;

	public PurchaseDAO() {
		
	}
	
	public PurchaseDAO(String contextPath, FactoryDAO factoryDAO, ChocolateDAO chocolateDAO, ShoppingCartDAO shoppingCartDAO,CommentDAO commentDAO) {
		this.contextPath = contextPath;
		this.factoryDAO = factoryDAO;
		this.factoryDAO.loadFactories(contextPath);
		this.chocolateDAO  =chocolateDAO;
		this.chocolateDAO.loadChocolates(contextPath);
		this.shoppingCartDAO = shoppingCartDAO;
		this.shoppingCartDAO.loadShoppingCarts(contextPath);
		this.commentDAO = commentDAO;
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
			p.setChocolates(purchase.getChocolates());
			p.setCode(purchase.getCode());
			p.setCustomerFirstName(purchase.getCustomerFirstName());
			p.setCustomerId(purchase.getCustomerId());
			p.setCustomerLastName(purchase.getCustomerLastName());
			p.setDateAndTime(purchase.getDateAndTime());
			p.setFactoryId(purchase.getFactoryId());
			p.setPrice(purchase.getPrice());
			p.setStatus(purchase.getStatus());
			rewriteFile();
		}
		
		return p;
	}
	private void rewriteFile() {
        try {
            Path filePath = Paths.get(contextPath + "/purchases.csv");
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), false)); // false za prepisivanje fajla

            for (Purchase purchase : purchases.values()) {
                StringBuilder chocolateIds = new StringBuilder();
                for (Integer chocolate : purchase.getChocolates()) {
                    chocolateIds.append(chocolate).append("|");
                }

                String formattedDateAndTime = purchase.getDateAndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                out.write(purchase.getId() + "," + purchase.getCode() + "," + chocolateIds.toString() + "," + formattedDateAndTime + "," +
                        purchase.getPrice() + "," + purchase.getCustomerId() + "," + purchase.getCustomerFirstName() + "," +
                        purchase.getCustomerLastName() + "," + purchase.getStatus().toString() + "," + purchase.getFactoryId() + "\n");
            }

            out.flush();
            out.close();
            System.out.println("Purchases file rewritten successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
	public Purchase updateStatusAndAddComment(String purchaseId, PurchaseStatus status, String commentText, Integer userId) {
	    try {
	        // Log input parameters
	        System.out.println("Updating Purchase with ID: " + purchaseId);
	        System.out.println("Status: " + status);
	        System.out.println("Comment Text: " + commentText);
	        System.out.println("User ID: " + userId);

	        Purchase purchase = purchases.get(purchaseId);
	        if (purchase == null) {
	            throw new IllegalArgumentException("Purchase not found");
	        }

	        // Update status
	        purchase.setStatus(status);

	        // Add comment if status is DECLINED
	        if (status == PurchaseStatus.DECLINED && userId != null) {
	            Comment comment = new Comment();
	            comment.setId(generateUniqueCommentId());
	            comment.setText(commentText);
	            comment.setGrade(0.0);
	            comment.setFactoryId(purchase.getFactoryId());
	            comment.setUserId(userId);
	            comment.setRole(Role.MANAGER);

	            commentDAO.save(comment);
	        }

	        // Update purchase in the data store
	        updatePurchases(purchaseId, purchase);
	        return purchase;
	    } catch (Exception e) {
	        // Log the exception
	        e.printStackTrace();
	        throw e;
	    }
	}

	private String generateUniqueCommentId() {
	    return String.valueOf(System.currentTimeMillis());
	}
	private void saveToFile(Purchase purchase) {
	    try {
	        Path filePath = Paths.get(contextPath + "/purchases.csv");
	        BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), true));

	        StringBuilder chocolateIds = new StringBuilder();
	      /*  for (Integer chocolate : purchase.getChocolates()) {
	            chocolateIds.append(chocolate.getId()).append("|");
	        }*/
	        for (Integer chocolate : purchase.getChocolates()) {
	            chocolateIds.append(chocolate).append("|");
	        }
	        
	       // DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
	      //  LocalDateTime dateAndTime = LocalDateTime.parse(purchase.getDateAndTime(), formatter); // Correctly parse the string
	        String formattedDateAndTime = purchase.getDateAndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

	        
	        out.write(purchase.getId() + "," + purchase.getCode() + "," + chocolateIds.toString() + "," +                     formattedDateAndTime + "," +
	                purchase.getPrice() + "," + purchase.getCustomerId() + "," + purchase.getCustomerFirstName() + "," +
	                purchase.getCustomerLastName() + "," + purchase.getStatus().toString() + "," + purchase.getFactoryId()+ "\n");

	        out.flush();
	        out.close();
	        System.out.println("Purchase saved to file successfully.");
	        System.out.println("Context path: " + contextPath);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	//ja izmenila zbog logova da vidim sta pise
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

	            String factoryId = data[9].trim();
	            //  Factory factory = factoryDAO.findFactory(factoryId);

	            List<Integer> chocolates = new ArrayList<>();
	            String[] chocolateIds = data[2].split("\\|");  //TREBA DA BUDU SHOPPINGCARTS
	            for (String chocolateId : chocolateIds) {
	                ShoppingCart cart = shoppingCartDAO.findShoppingCart(chocolateId.trim());
	                if (cart != null) {
	                    chocolates.add(Integer.parseInt(cart.getId()));
	                } else {
	                    System.err.println("ShoppingCart with ID " + chocolateId.trim() + " not found.");
	                }
	            }

	            Purchase purchase = new Purchase();
	            purchase.setId(data[0]);
	            purchase.setCode(data[1]);
	            purchase.setChocolates(chocolates);
	            purchase.setFactoryId(Integer.parseInt(factoryId));

	            

	           // purchase.setDateAndTime(new Date(Long.parseLong(data[3])));
	           // DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME; // Use appropriate formatter
	          //  LocalDateTime dateAndTime = LocalDateTime.parse(data[3].trim(), formatter); // Correctly parse the string


	            OffsetDateTime dateAndTime = OffsetDateTime.parse(data[3].trim(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	            purchase.setDateAndTime(dateAndTime);

	            purchase.setPrice(Double.parseDouble(data[4]));
	            purchase.setCustomerId(Integer.parseInt(data[5]));
	            purchase.setCustomerFirstName(data[6]);
	            purchase.setCustomerLastName(data[7]);
	            purchase.setStatus(PurchaseStatus.valueOf(data[8]));

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

	  public Purchase updatePurchaseStatus(String id, PurchaseStatus status) {
	        Purchase p = purchases.containsKey(id) ? purchases.get(id) : null;
	        if (p != null) {
	            p.setStatus(status);
	            purchases.put(id, p); 
	            rewriteFile();
	        }
	        return p;
	    }

	 
	 public Collection<Purchase> findByFactoryId(int factoryId) {
		    Collection<Purchase> result = purchases.values().stream()
		            .filter(purchase -> purchase.getFactoryId() == factoryId)
		            .collect(Collectors.toList());
		    System.out.println("Found " + result.size() + " purchases for factoryId " + factoryId);
		    return result;
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
	

	
	public Collection<Purchase> searchAndSortPurchasesByFactory(int factoryId,  Double minPrice, Double maxPrice, String startDate, String endDate, String sortBy, boolean ascending) {
	    final OffsetDateTime start;
	    final OffsetDateTime end;

	    try {
	        start = (startDate != null && !startDate.isEmpty()) ? OffsetDateTime.parse(startDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null;
	        end = (endDate != null && !endDate.isEmpty()) ? OffsetDateTime.parse(endDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null;
	    } catch (DateTimeParseException e) {
	        e.printStackTrace();
	        throw new IllegalArgumentException("Invalid date format. Please use ISO 8601 format.");
	    }

	    Stream<Purchase> purchaseStream = purchases.values().stream()
	        .filter(purchase -> 
	            purchase.getFactoryId() == factoryId &&
	            (minPrice == null || purchase.getPrice() >= minPrice) &&
	            (maxPrice == null || purchase.getPrice() <= maxPrice) &&
	            (start == null || !purchase.getDateAndTime().isBefore(start)) &&
	            (end == null || !purchase.getDateAndTime().isAfter(end))
	        );

	    Comparator<Purchase> comparator = null;

	    if (sortBy != null) {
	        switch (sortBy.toLowerCase()) {
	            case "price":
	                comparator = Comparator.comparing(Purchase::getPrice);
	                break;
	            case "date":
	                comparator = Comparator.comparing(Purchase::getDateAndTime);
	                break;
	        }
	    }

	    if (comparator != null) {
	        purchaseStream = ascending ? purchaseStream.sorted(comparator) : purchaseStream.sorted(comparator.reversed());
	    }

	    return purchaseStream.collect(Collectors.toList());
	}
	public Collection<Purchase> searchAndSortPurchasesByUser(int userId, String factoryName, Double minPrice, Double maxPrice, String startDate, String endDate, String sortBy, boolean ascending) {
	    final OffsetDateTime start;
	    final OffsetDateTime end;

	    try {
	        start = (startDate != null && !startDate.isEmpty()) ? OffsetDateTime.parse(startDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null;
	        end = (endDate != null && !endDate.isEmpty()) ? OffsetDateTime.parse(endDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null;
	    } catch (DateTimeParseException e) {
	        e.printStackTrace();
	        throw new IllegalArgumentException("Invalid date format. Please use ISO 8601 format.");
	    }

	    Stream<Purchase> purchaseStream = purchases.values().stream()
	        .filter(purchase -> 
	            purchase.getCustomerId() == userId &&
	            (factoryName == null || factoryName.isEmpty() || factoryDAO.findFactory(String.valueOf(purchase.getFactoryId())).getFactoryName().equalsIgnoreCase(factoryName)) &&
	            (minPrice == null || purchase.getPrice() >= minPrice) &&
	            (maxPrice == null || purchase.getPrice() <= maxPrice) &&
	            (start == null || !purchase.getDateAndTime().isBefore(start)) &&
	            (end == null || !purchase.getDateAndTime().isAfter(end))
	        );

	    Comparator<Purchase> comparator = null;

	    if (sortBy != null) {
	        switch (sortBy.toLowerCase()) {
	            case "factoryname":
	                comparator = Comparator.comparing(purchase -> factoryDAO.findFactory(String.valueOf(purchase.getFactoryId())).getFactoryName(), String.CASE_INSENSITIVE_ORDER);
	                break;
	            case "price":
	                comparator = Comparator.comparing(Purchase::getPrice);
	                break;
	            case "date":
	                comparator = Comparator.comparing(Purchase::getDateAndTime);
	                break;
	        }
	    }

	    if (comparator != null) {
	        purchaseStream = ascending ? purchaseStream.sorted(comparator) : purchaseStream.sorted(comparator.reversed());
	    }

	    return purchaseStream.collect(Collectors.toList());
	}
	

public Collection<User> getSuspiciousUsers(UserDAO userDAO) {
    OffsetDateTime oneMonthAgo = OffsetDateTime.now().minus(1, ChronoUnit.MONTHS);

    Map<Integer, Long> cancellations = purchases.values().stream()
            .filter(purchase -> purchase.getStatus() == PurchaseStatus.CANCELLED && purchase.getDateAndTime().isAfter(oneMonthAgo))
            .collect(Collectors.groupingBy(Purchase::getCustomerId, Collectors.counting()));

    return cancellations.entrySet().stream()
            .filter(entry -> entry.getValue() > 5)
            .map(entry -> userDAO.findUser(entry.getKey().toString()))
            .collect(Collectors.toList());
}





	
}
