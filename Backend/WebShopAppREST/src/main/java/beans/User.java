package beans;

import java.time.LocalDate;
import java.util.Date;


import enumerations.Role;
import enumerations.ActivityStatus;
import enumerations.Gender;

public class User {
	private String id;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private Gender gender;
	private LocalDate dateOfBirth;
	private Role role;
	private double points;
	private ActivityStatus activity;
	
	public User() {
		super();
	}


	public User(String id,String username, String password, String firstName, String lastName, Gender gender, LocalDate dateOfBirth,
			Role role, double points,ActivityStatus activity) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.role = role;
		this.points = points;
		this.activity=activity;
		}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}

	

	public double getPoints() {
		return points;
	}


	public void setPoints(double points) {
		this.points = points;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public Gender getGender() {
		return gender;
	}


	public void setGender(Gender gender) {
		this.gender = gender;
	}


	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}


	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}


	public Role getRole() {
		return role;
	}


	public void setRole(Role role) {
		this.role = role;
	}


	public ActivityStatus getActivity() {
		return activity;
	}


	public void setActivity(ActivityStatus activity) {
		this.activity = activity;
	}
	

	

}

