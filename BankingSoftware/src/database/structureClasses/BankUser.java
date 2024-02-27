package database.structureClasses;

public class BankUser {
	private int userId;
	private String Email;
	private String password;
	private String phonenumber;
	private String name;
	private String dateOfBirth;
	private String gender;
	private String address;
	
	public int getUserId() {
		return userId;
	}
	
	public String getEmail() {
		return Email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getPhonenumber() {
		return phonenumber;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	
	public String getGender() {
		return gender;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public void setEmail(String email) {
		Email = email;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
}
