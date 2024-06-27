package beans;


import java.util.List;
import beans.Chocolate;
import beans.Factory;
import enumerations.PurchaseStatus;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;


public class Purchase{
	
	private String id;
	private String code; 
	private List<Integer> chocolates; // zapravo shopping carts u kojoj su cokolade
	
	private OffsetDateTime dateAndTime;
	private double price;
	private int customerId;
	private String customerFirstName;
	private String customerLastName;
	private PurchaseStatus status;
	
	public Purchase() {}
	
	public Purchase(String id, String code, List<Integer> chocolates, OffsetDateTime dateAndTime, double price,
			int customerId, String customerFirstName, String customerLastName, PurchaseStatus status) {
		super();
		this.id = id;
		this.code = code;
		this.chocolates = chocolates;
		this.dateAndTime = dateAndTime;
		this.price = price;
		this.customerId = customerId;
		this.customerFirstName = customerFirstName;
		this.customerLastName = customerLastName;
		this.status = status;
	}
	
	

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<Integer> getChocolates() {
		return chocolates;
	}
	public void setChocolates(List<Integer> chocolates) {
		this.chocolates = chocolates;
	}

	public OffsetDateTime getDateAndTime() {
		return dateAndTime;
	}
	public void setDateAndTime(OffsetDateTime dateAndTime) {
		this.dateAndTime = dateAndTime;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getCustomerFirstName() {
		return customerFirstName;
	}
	
	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public void setCustomerFirstName(String customerFirstName) {
		this.customerFirstName = customerFirstName;
	}
	public String getCustomerLastName() {
		return customerLastName;
	}
	public void setCustomerLastName(String customerLastName) {
		this.customerLastName = customerLastName;
	}
	public PurchaseStatus getStatus() {
		return status;
	}
	public void setStatus(PurchaseStatus status) {
		this.status = status;
	}
	
	
	
}