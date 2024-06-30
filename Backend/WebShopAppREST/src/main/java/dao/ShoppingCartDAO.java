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
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import beans.Chocolate;
import beans.Purchase;
import beans.ShoppingCart;
import dto.ShoppingCartDTO;

public class ShoppingCartDAO {
	
	 private HashMap<String, ShoppingCart> shoppingCarts = new HashMap<String, ShoppingCart>();
	   private String contextPath;
	private ChocolateDAO chocolateDAO;
	
	public ShoppingCartDAO(String contextPath, ChocolateDAO chocolateDAO) {
		this.contextPath = contextPath;
		this.chocolateDAO = chocolateDAO;
		loadShoppingCarts(contextPath);
	}


	public Collection<ShoppingCart> findAll() {
		return shoppingCarts.values();
	}

	public ShoppingCart findShoppingCart(String id) {
		return shoppingCarts.containsKey(id) ? shoppingCarts.get(id) : null;
	}
	  public int findChocolateQuantity(String cartId, int chocolateId) {

	        ShoppingCart shoppingCart = findShoppingCart(cartId);
	        if (shoppingCart != null) {
	            Map<Integer, Integer> chocolates = shoppingCart.getChocolates();
	            if (chocolates.containsKey(chocolateId)) {
	                return chocolates.get(chocolateId);
	            }
	        }
	        return -1; // ili neka druga indikativna vrednost ako čokolada nije pronađena
	    }
	
	/*public ShoppingCart updateProduct(String id, ShoppingCart shoppingCart) {
		ShoppingCart p = shoppingCarts.containsKey(id) ? shoppingCarts.get(id) : null;
		if (p == null) {
			return save(shoppingCart);
		} else {
			
		}
		
		return p;
	}*/
	
	public ShoppingCart save(ShoppingCart shoppingCart) {
		Integer maxId = -1;
		for (String id : shoppingCarts.keySet()) {
			int idNum =Integer.parseInt(id);
			if (idNum > maxId) {
				maxId = idNum;
			}
		}
		maxId++;
		shoppingCart.setId(maxId.toString());
		shoppingCarts.put(shoppingCart.getId(), shoppingCart);
		 saveToFile(shoppingCart);
		return shoppingCart;
	}

	private void saveToFile(ShoppingCart shoppingCart) {
	    try {
	        Path filePath = Paths.get(contextPath + "/shoppingCarts.csv");
	        BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), true));

	        StringBuilder chocolateData = new StringBuilder();
	        for (Map.Entry<Integer, Integer> entry : shoppingCart.getChocolates().entrySet()) {
                int chocolateId = entry.getKey();
                int quantity = entry.getValue();
                chocolateData.append(chocolateId).append(":").append(quantity).append("|");
            }
	        out.write(shoppingCart.getId() + "," + 
	        		 chocolateData.toString() + "," +
	        		shoppingCart.getCustomerId() + "," + 
	                shoppingCart.getOverallPrice() + "," +
	        		shoppingCart.getState() +"\n");
	        out.flush();
	        out.close();
	        System.out.println("shoppingCart saved to file successfully.");
	        System.out.println("Context path: " + contextPath);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public void loadShoppingCarts(String contextPath) {
	    BufferedReader in = null;
	    try {
	        File file = new File(contextPath + "/shoppingCarts.csv");
	        in = new BufferedReader(new FileReader(file));
	        String line;

	        while ((line = in.readLine()) != null) {
	            line = line.trim();
	            if (line.equals("") || line.startsWith("#"))
	                continue;

	            System.out.println("Reading line: " + line); // Debug statement

	            String[] data = parseCsvLine(line);
	            
	            String cartId = data[0].trim();
	            String[] chocolateData = data[1].split("\\|");
	            Map<Integer, Integer> chocolateQuantities = new HashMap<>();
	            for (String itemData : chocolateData) {
	                String[] itemParts = itemData.split(":");
	                int chocolateId = Integer.parseInt(itemParts[0].trim());
	                int quantity = Integer.parseInt(itemParts[1].trim());
	                // Validate chocolateId and quantity here if needed
	                chocolateQuantities.put(chocolateId, quantity);
	            }
	            int customerId = Integer.parseInt(data[2].trim());
	            double overallPrice = Double.parseDouble(data[3].trim());

	            ShoppingCart shoppingCart = new ShoppingCart();
	            shoppingCart.setId(cartId);
	            shoppingCart.setChocolates(chocolateQuantities);
	            shoppingCart.setCustomerId(customerId);
	            shoppingCart.setOverallPrice(overallPrice);
	            shoppingCart.setState(Boolean.parseBoolean(data[4]));

	            shoppingCarts.put(shoppingCart.getId(), shoppingCart);
	            System.out.println("Loaded ShoppingCart: " + shoppingCart); // Debug statement
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

	
	  public List<String> findShoppingCartIdsByCustomerId(int customerId) {
	        return shoppingCarts.values().stream()
	                .filter(shoppingCart -> shoppingCart.getCustomerId() == customerId && shoppingCart.getState())
	                .map(ShoppingCart::getId)
	                .collect(Collectors.toList());
	    }
	  
	  public void deleteShoppingCart(String id) {
		    if (shoppingCarts.containsKey(id)) {
		        ShoppingCart shoppingCart = shoppingCarts.get(id);
		        shoppingCart.setState(false);
		        saveShoppingCarts();

		    
		        if (shoppingCart.getChocolates() != null) {
		            Collection<ShoppingCart> customerCarts = findByCustomerId(shoppingCart.getCustomerId());
		            customerCarts.removeIf(c -> !c.getState()); 

		   
		            List<ShoppingCart> updatedCarts = customerCarts.stream()
		                .map(c -> {
		                    ShoppingCart cartCopy = new ShoppingCart();
		                    cartCopy.setId(c.getId());
		                    cartCopy.setChocolates(new HashMap<>(c.getChocolates()));
		                    cartCopy.setCustomerId(c.getCustomerId());
		                    cartCopy.setOverallPrice(c.getOverallPrice());
		                    cartCopy.setState(c.getState());
		                    // Postavi ostale atribute ako je potrebno
		                    return cartCopy;
		                })
		                .collect(Collectors.toList());

		            // Pretpostavka je da imate neki način da setujete ažurirane korpe za kupca, možete implementirati ovu logiku u skladu sa vašim zahtevima
		            // setUpdatedCartsForCustomer(shoppingCart.getCustomerId(), updatedCarts);
		        }
		    }
		}
	  
	  public void changeChocolateQuantity(ShoppingCartDTO shoppingCartDTO) {
		    ShoppingCart shoppingCart = findShoppingCart(shoppingCartDTO.getCartId());
		    if (shoppingCart != null) {
		        shoppingCart.changeChocolateQuantity(shoppingCartDTO);
		        shoppingCart.setOverallPrice(shoppingCartDTO.getOverallPrice());
		        updateShoppingCart(shoppingCart.getId(), shoppingCart); 
		        
		    }
		}

	  private void saveShoppingCarts() {
		    try {
		        Path filePath = Paths.get(contextPath + "/shoppingCarts.csv");
		        BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), false)); // false za prepisivanje fajla
		        for (ShoppingCart shoppingCart : shoppingCarts.values()) {
		        	saveToFile(shoppingCart);
		        }
		        out.flush();
		        out.close();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
	  
	  public ShoppingCart updateShoppingCart(String id, ShoppingCart shoppingCart) {
		  ShoppingCart c = shoppingCarts.containsKey(id) ? shoppingCarts.get(id) : null;
		    if (c == null) {
		        return save(shoppingCart);
		    } else {
		        c.setChocolates(shoppingCart.getChocolates());
		        c.setCustomerId(shoppingCart.getCustomerId());
		        c.setOverallPrice(shoppingCart.getOverallPrice());
		        c.setState(shoppingCart.getState());
		     
		        saveShoppingCarts(); // Ova metoda treba da ažurira sve promene u CSV fajlu
		    }
		    
		    return c;
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
	
	 public Collection<ShoppingCart> findByCustomerId(int customerId) {
	        return shoppingCarts.values().stream()
	                .filter(shoppingCart -> shoppingCart.getCustomerId() == customerId)
	                .collect(Collectors.toList());
	    }
	
}
