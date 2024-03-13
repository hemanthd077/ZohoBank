package database.structure;

import java.io.Serializable;

public class BankBranch implements Serializable {
	private static final long serialVersionUID = 1L;
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

	@Override
	public String toString() {
		return "BankBranch [branchId=" + branchId + ", ifsc=" + ifsc + ", address=" + address + ", city=" + city
				+ ", state=" + state + "]";
	}
	
	
}
