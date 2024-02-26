package database.structureClasses;

public class BranchDetails {
	private int branch_id;
	private String ifsc;
	private String address;
	private String city;
	private String state;
	
	public int getBranch_id() {
		return branch_id;
	}
	
	public String getIfsc() {
		return ifsc;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getCity() {
		return city;
	}
	
	public String getState() {
		return state;
	}
	
	public void setBranch_id(int branch_id) {
		this.branch_id = branch_id;
	}
	
	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public void setState(String state) {
		this.state = state;
	}
}
