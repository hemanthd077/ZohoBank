package database.structureClasses;

public class BankCustomerDetails extends UserDetails {
	private String panNumber;
	private String aadharNumber;
	
	public String getPanNumber() {
		return panNumber;
	}
	
	public String getAadharNumber() {
		return aadharNumber;
	}
	
	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}
	
	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}
}
