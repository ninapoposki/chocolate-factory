package dto;

public class ShoppingCartDTO {
    private String cartId;
    private int chocolateId;
    private int newQuantity;
    private double newOverallPrice;

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public int getChocolateId() {
        return chocolateId;
    }

    public void setChocolateId(int chocolateId) {
        this.chocolateId = chocolateId;
    }

    public int getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(int newQuantity) {
        this.newQuantity = newQuantity;
    }

	public double getOverallPrice() {
		return newOverallPrice;
	}

	public void setOverallPrice(double overallPrice) {
		this.newOverallPrice = overallPrice;
	}
    
}
