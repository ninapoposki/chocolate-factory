package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import beans.Product;

public class ProductDAO {
	
	private HashMap<String, Product> products = new HashMap<String, Product>();
	
	public ProductDAO() {
		
	}
	
	public ProductDAO(String contextPath) {
		loadProducts(contextPath);
	}

	public Collection<Product> findAll() {
		return products.values();
	}

	public Product findProduct(String id) {
		return products.containsKey(id) ? products.get(id) : null;
	}
	
	public Product updateProduct(String id, Product product) {
		Product p = products.containsKey(id) ? products.get(id) : null;
		if (p == null) {
			return save(product);
		} else {
			p.setName(product.getName());
			p.setPrice(product.getPrice());
		}
		
		return p;
	}
	
	public Product save(Product product) {
		Integer maxId = -1;
		for (String id : products.keySet()) {
			int idNum =Integer.parseInt(id);
			if (idNum > maxId) {
				maxId = idNum;
			}
		}
		maxId++;
		product.setId(maxId.toString());
		products.put(product.getId(), product);
		return product;
	}

	private void loadProducts(String contextPath) {
		BufferedReader in = null;
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
		}
		
	}
}
