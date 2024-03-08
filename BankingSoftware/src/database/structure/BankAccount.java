package database.structure;

import helper.enumfiles.AccountType;
import helper.enumfiles.RecordStatus;

public class BankAccount {
	private long accountNo;
	private double balance;
	private RecordStatus status;
	private long userId;
	private AccountType accountType;
	private BankBranch bankBranch;

	public long getAccountNo() {
		return accountNo;
	}

	public double getBalance() {
		return balance;
	}

	public RecordStatus getStatus() {
		return status;
	}

	public void setAccountNo(long accountNo) {
		this.accountNo = accountNo;
	}

	public void setBalance(double balance) {
		this.balance = Math.round(balance * 100.0) / 100.0;
	}

	public void setStatus(int status) {
		this.status = RecordStatus.getByCode(status);
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

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(int accountType) {
		this.accountType = AccountType.getByCode(accountType);
	}

	@Override
	public String toString() {
		return "BankAccount [accountNo=" + accountNo + ", balance=" + balance + ", status=" + status + ", userId="
				+ userId + ", accountType=" + accountType + ", bankBranch=" + bankBranch + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

	
}
