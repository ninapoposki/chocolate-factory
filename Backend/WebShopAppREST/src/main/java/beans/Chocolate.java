package beans;

public class Chocolate{
	private String id;
	private String chocolateName;
	private double price;
	private String variety; //za kuvanje,obicna...
	private Factory factory; //factoryId?
	private int factoryId;
	private String type; //crna,mlecna...
	private double weight;
	private String description;
	private String imageUri; //ili image da imamo  i da u sebi ima tipove?
	//private ChocolateStatus chocolateStatus; -ima na stanju nema na stanju\
	private int numberOfChocolates;
	private boolean isOnStock;
	
	public Chocolate() {
		
	}

	/*public Chocolate(String id,String chocolateName, double price, String variety, Factory factory, String type, double weight,
			String description, String imageUri, int numberOfChocolates) {
		super();
		this.id=id;
		this.chocolateName = chocolateName;
		this.price = price;
		this.variety = variety;
		this.factory = factory;
		this.type = type;
		this.weight = weight;
		this.description = description;
		this.imageUri = imageUri;
		this.numberOfChocolates = numberOfChocolates;
	}
	*/

	
	public Chocolate(String id, String chocolateName, double price, String variety, int i, String type,
			double weight, String description, String imageUri, int numberOfChocolates, boolean isOnStock) {
		super();
		this.id = id;
		this.chocolateName = chocolateName;
		this.price = price;
		this.variety = variety;
		this.factoryId = i;
		this.type = type;
		this.weight = weight;
		this.description = description;
		this.imageUri = imageUri;
		this.numberOfChocolates = numberOfChocolates;
		this.isOnStock = isOnStock;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getChocolateName() {
		return chocolateName;
	}

	public void setChocolateName(String chocolateName) {
		this.chocolateName = chocolateName;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getVariety() {
		return variety;
	}

	public void setVariety(String variety) {
		this.variety = variety;
	}

	public Factory getFactory() {
		return factory;
	}
	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	//public void setFactory(Object object) {
		//this.factory = object;
	//}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUri() {
		return imageUri;
	}

	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}

	public int getNumberOfChocolates() {
		return numberOfChocolates;
	}

	public void setNumberOfChocolates(int numberOfChocolates) {
		this.numberOfChocolates = numberOfChocolates;
	}

	public int getFactoryId() {
		return factoryId;
	}

	public void setFactoryId(int factoryId) {
		this.factoryId = factoryId;
	}

	public boolean getIsOnStock() {
		return isOnStock;
	}

	public void setIsOnStock(boolean isOnStock) {
		this.isOnStock = isOnStock;
	}
	
	
}