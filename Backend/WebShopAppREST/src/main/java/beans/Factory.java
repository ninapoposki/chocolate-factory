package beans;

import java.util.List;

import javax.json.bind.annotation.JsonbTransient;

public class Factory{
	private String id;
	private String factoryName;
    @JsonbTransient
	private List<Chocolate> chocolates;
	private int workingTime;
	private boolean status; //true-radi,false-ne radi
	private Location location;
	//idLocation?
	private String logoUri;
	private double grade;
	
	public Factory() {
		
	}

	 public Factory(String id, String factoryName, int workingTime, boolean status, String logoUri, double grade, Location location) {
	        this.id = id;
	        this.factoryName = factoryName;
	        this.workingTime = workingTime;
	        this.status = status;
	        this.logoUri = logoUri;
	        this.grade = grade;
	        this.location = location; // Setovanje lokacije direktno u konstruktoru
	    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFactoryName() {
		return factoryName;
	}

	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}
    @JsonbTransient
	public List<Chocolate> getChocolates() {
		return chocolates;
	}

	public void setChocolates(List<Chocolate> chocolates) {
		this.chocolates = chocolates;
	}

	public int getWorkingTime() {
		return workingTime;
	}

	public void setWorkingTime(int workingTime) {
		this.workingTime = workingTime;
	}

	public boolean getIsStatus() {
		return status;
	}

	public void setIsStatus(boolean status) {
		this.status = status;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getLogoUri() {
		return logoUri;
	}

	public void setLogoUri(String logoUri) {
		this.logoUri = logoUri;
	}

	public double getGrade() {
		return grade;
	}

	public void setGrade(double grade) {
		this.grade = grade;
	}
	
	
	
	
	
	
	
	
	
	
}