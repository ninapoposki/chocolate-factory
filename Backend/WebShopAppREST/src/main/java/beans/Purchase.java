package beans;

import java.sql.Date;
import java.util.List;
import beans.Chocolate;
import beans.Factory;
import enumerations.PurchaseStatus;

public class Purchase{
	
	private String id;
	private String code; //10 karaktera
	private List<Chocolate> chocolates;
	private int factoryId;
	private Date dateAndTime;
	private double price;
	private int customerId;
	private String customerFirstName;
	private String customerLastName;
	private PurchaseStatus status;
	
	public Purchase() {}
	
	public Purchase(String id, String code, List<Chocolate> chocolates, int factoryId, Date dateAndTime, double price,
			int customerId, String customerFirstName, String customerLastName, PurchaseStatus status) {
		super();
		this.id = id;
		this.code = code;
		this.chocolates = chocolates;
		this.factoryId = factoryId;
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
	public List<Chocolate> getChocolates() {
		return chocolates;
	}
	public void setChocolates(List<Chocolate> chocolates) {
		this.chocolates = chocolates;
	}
	public int getFactoryId() {
		return factoryId;
	}
	public void setFactoryId(int factoryId) {
		this.factoryId = factoryId;
	}
	public Date getDateAndTime() {
		return dateAndTime;
	}
	public void setDateAndTime(Date dateAndTime) {
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