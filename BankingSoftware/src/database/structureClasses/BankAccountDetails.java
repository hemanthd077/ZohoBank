package database.structureClasses;

public class BankAccountDetails {
	private Long accountNo;
	private double balance;
	private int status;
	private BranchDetails branchDetails;
	private UserDetails userDetails;
	
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

	public UserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(UserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public BranchDetails getBranchDetails() {
		return branchDetails;
	}

	public void setBranchDetails(BranchDetails branchDetails) {
		this.branchDetails = branchDetails;
	}
	
}
