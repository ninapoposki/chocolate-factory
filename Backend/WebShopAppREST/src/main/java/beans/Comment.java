package beans;

import enumerations.Role;

public class Comment{
	 private String id;
	 private String text;
	 private double grade;
	 private int factoryId;
	 private int userId; //kupac koji je ostavio komentar-ja stavila user jer kao menadzer moze staviti komentar kada odbije 
	 private boolean rejection; //ovo posle iskoristi-da se vidi sta je doobreno a sta odbijeno
	 private Role role;
	 public Comment() {
		 
	 }

	public Comment(String id, String text, double grade, int factoryId, int userId,Role role) {
		super();
		this.id = id;
		this.text = text;
		this.grade = grade;
		this.factoryId = factoryId;
		this.userId = userId;
		this.role=role;
	}

	
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getGrade() {
		return grade;
	}

	public void setGrade(double grade) {
		this.grade = grade;
	}

	public int getFactoryId() {
		return factoryId;
	}

	public void setFactoryId(int factoryId) {
		this.factoryId = factoryId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	 
	 
	
	
	
	
}