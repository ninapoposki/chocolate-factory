package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import beans.ShoppingCart;

public class ShoppingCartDAO {
	
	private HashMap<String, ShoppingCart> shoppingCarts = new HashMap<String, ShoppingCart>();
	
	public ShoppingCartDAO() {
		
	}
	
	public ShoppingCartDAO(String contextPath) {
		loadShoppingCart(contextPath);
	}

	public Collection<ShoppingCart> findAll() {
		return shoppingCarts.values();
	}

	public ShoppingCart findShoppingCart(String id) {
		return shoppingCarts.containsKey(id) ? shoppingCarts.get(id) : null;
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
		//shoppingCart.setId(maxId.toString());
		//shoppingCarts.put(product.getId(), shoppingCart);
		return shoppingCart;
	}

	private void loadShoppingCart(String contextPath) {
		/*BufferedReader in = null;
		try {
			File file = new File(contextPath + "/products.txt");
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
				products.put(id, new Product(id, name, Double
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
