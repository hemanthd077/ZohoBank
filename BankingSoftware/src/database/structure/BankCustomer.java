package database.structure;

import java.io.Serializable;

public class BankCustomer extends BankUser implements Serializable {
	private static final long serialVersionUID = 1L;
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

	@Override
	public String toString() {
		return "BankCustomer [panNumber=" + panNumber + ", aadharNumber=" + aadharNumber + ", getUserId()="
				+ getUserId() + ", getEmail()=" + getEmail() + ", getPassword()=" + getPassword()
				+ ", getPhoneNumber()=" + getPhoneNumber() + ", getName()=" + getName() + ", getDateOfBirth()="
				+ getDateOfBirth() + ", getGender()=" + getGender() + ", getAddress()=" + getAddress() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

}
