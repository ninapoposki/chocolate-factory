package beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dto.ShoppingCartDTO;

public class ShoppingCart{
	private String id;
    private Map<Integer, Integer> chocolates;
	private int customerId;
	private double overallPrice;
	private boolean state;
	
	public ShoppingCart() {
		 chocolates = new HashMap<>();
	}
	
	public ShoppingCart(Map<Integer, Integer> chocolates, int customerId, double overallPrice, boolean state) {
		super();
		this.chocolates = chocolates;
		this.customerId = customerId;
		this.overallPrice = overallPrice;
		this.state= state;
		}	
	
	
	 public void changeChocolateQuantity(ShoppingCartDTO shoppingCartDTO) {
	        int chocolateId = shoppingCartDTO.getChocolateId();
	        int newQuantity = shoppingCartDTO.getNewQuantity();

	        if (newQuantity > 0) {
	            chocolates.put(chocolateId, newQuantity);
	        } else {
	            chocolates.remove(chocolateId);
	        }

	    }
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	public boolean getState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public Map<Integer, Integer> getChocolates() {
		return chocolates;
	}

	public void setChocolates(Map<Integer, Integer> chocolates) {
		this.chocolates = chocolates;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public double getOverallPrice() {
		return overallPrice;
	}

	public void setOverallPrice(double overallPrice) {
		this.overallPrice = overallPrice;
	}
	
	
}
