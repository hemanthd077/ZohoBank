package database.structure;

public class BankAccount {
	private long accountNo;
	private double balance;
	private int status;
	private long userId;
	private int accountType;
	private BankBranch bankBranch;

	public long getAccountNo() {
		return accountNo;
	}

	public double getBalance() {
		return balance;
	}

	public int getStatus() {
		return status;
	}

	public void setAccountNo(long accountNo) {
		this.accountNo = accountNo;
	}

	public void setBalance(double balance) {
		this.balance = Math.round(balance * 100.0) / 100.0;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
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
