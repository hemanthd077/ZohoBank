package database.structureClasses;

import java.util.List;

public class BankAccount {
	private Long accountNo;
	private double balance;
	private int status;
	private int userId;
	private BankBranch bankBranch;
	private List<BankTransaction> bankTransaction;

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
		this.balance = Math.round(balance * 100.0)/100.0;
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

	public List<BankTransaction> getBankTransaction() {
		return bankTransaction;
	}

	public void setBankTransaction(List<BankTransaction> bankTransaction) {
		this.bankTransaction = bankTransaction;
	}

	public BankBranch getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(BankBranch bankBranch) {
		this.bankBranch = bankBranch;
	}

}
