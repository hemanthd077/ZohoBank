package database.structureClasses;

import java.util.List;

public class BankCustomer extends BankUser {
	private String panNumber;
	private String aadharNumber;
	private List<BankAccount> bankAccount;

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

	public List<BankAccount> getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(List<BankAccount> bankAccount) {
		this.bankAccount = bankAccount;
	}
}
