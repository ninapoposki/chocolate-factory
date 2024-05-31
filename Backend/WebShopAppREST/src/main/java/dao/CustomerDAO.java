package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import beans.Customer;

public class CustomerDAO {
	
	private HashMap<String, Customer> customers = new HashMap<String, Customer>();
	
	public CustomerDAO() {
		
	}
	
	public CustomerDAO(String contextPath) {
		loadCustomers(contextPath);
	}

	public Collection<Customer> findAll() {
		return customers.values();
	}

	public Customer findCustomer(String id) {
		return customers.containsKey(id) ? customers.get(id) : null;
	}
	
	public Customer updateCustomer(String id, Customer customer) {
		Customer c = customers.containsKey(id) ? customers.get(id) : null;
		if (c == null) {
			return save(customer);
		} else {
			c.setPurchases(customer.getPurchases()); //??
			c.setCart(customer.getCart());//nastavi ako bude trebalo
			
			
		}
		
	return c;
	}
	
	public Customer save(Customer customer) {
		Integer maxId = -1;
		for (String id : customers.keySet()) {
			int idNum =Integer.parseInt(id);
			if (idNum > maxId) {
				maxId = idNum;
			}
		}
		maxId++;
		customer.setId(maxId.toString());
		customers.put(customer.getId(), customer);
		return customer;
	}

	private void loadCustomers(String contextPath) {
	/*	BufferedReader in = null;
		try {
			File file = new File(contextPath + "/customers.txt");
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
				customers.put(id, new Customer(id, name, Double
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
		*/
	}
}
