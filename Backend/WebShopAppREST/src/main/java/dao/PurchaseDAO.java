package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import beans.Purchase;

public class PurchaseDAO {
	
	private HashMap<String, Purchase> purchases = new HashMap<String, Purchase>();
	
	public PurchaseDAO() {
		
	}
	
	public PurchaseDAO(String contextPath) {
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
		//purchase.setId(maxId.toString());
		//purchase.put(purchase.getId(), purchase);
		return purchase;
	}

	private void loadPurchases(String contextPath) {
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
