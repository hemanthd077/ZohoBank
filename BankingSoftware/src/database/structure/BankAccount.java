package database.structure;

public class BankAccount {
	private Long accountNo;
	private double balance;
	private int status;
	private int userId;
	private int accountType;
	private BankBranch bankBranch;

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
		this.balance = Math.round(balance * 100.0) / 100.0;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public BankBranch getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(BankBranch bankBranch) {
		this.bankBranch = bankBranch;
	}

	public int getAccountType() {
		return accountType;
	}

	public void setAccountType(int accountType) {
		this.accountType = accountType;
	}

}
