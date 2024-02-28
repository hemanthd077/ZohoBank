package database.structureClasses;

public class BankAccount {
	private Long accountNo;
	private double balance;
	private int status;
	private BankBranch branchDetails;
	private BankUser userDetails;

	public Long getAccountNo() {
		return accountNo;
	}

	public double getBalance() {
		return balance;
	}

	public int getStatus() {
		return status;
	}

	public void setAccountNo(Long accountNo) {
		this.accountNo = accountNo;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public BankUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(BankUser userDetails) {
		this.userDetails = userDetails;
	}

	public BankBranch getBranchDetails() {
		return branchDetails;
	}

	public void setBranchDetails(BankBranch branchDetails) {
		this.branchDetails = branchDetails;
	}

}
