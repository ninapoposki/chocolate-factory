package beans;

import java.util.Date;
import java.util.List;

import enumerations.Gender;
import enumerations.Role;
import enumerations.ActivityStatus;
import enumerations.CustomerType;
import beans.ShoppingCart;

public class Customer extends User {

    private List<Purchase> purchases;
    private ShoppingCart cart;
    private int loyaltyPoints;
    private CustomerType customerType;
    private int discount; 
    private int requiredPoints;
    private ActivityStatus activity;

    public Customer() {
        super();
    }

    public Customer(User user, List<Purchase> purchases, ShoppingCart cart, int loyaltyPoints, CustomerType customerType, int discount, int requiredPoints,ActivityStatus activity) {
        super(user.getId(), user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getGender(), user.getDateOfBirth(), user.getRole(), user.getPoints(),user.getActivity());
        this.purchases = purchases;
        this.cart = cart;
        this.loyaltyPoints = loyaltyPoints;
        this.customerType = customerType;
        this.discount = discount;
        this.requiredPoints = requiredPoints;
        this.activity=activity;
        
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public void setCart(ShoppingCart cart) {
        this.cart = cart;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

	public int getRequiredPoints() {
		return requiredPoints;
	}

	public void setRequiredPoints(int requiredPoints) {
		this.requiredPoints = requiredPoints;
	}

	public ActivityStatus getActivity() {
		return activity;
	}

	public void setActivity(ActivityStatus activity) {
		this.activity = activity;
	}

    
}