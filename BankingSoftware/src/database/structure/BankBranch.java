package database.structure;

public class BankBranch {
	private int branchId;
	private String ifsc;
	private String address;
	private String city;
	private String state;

	public int getBranchId() {
		return branchId;
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

	public void setBranch_id(int branchId) {
		this.branchId = branchId;
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
