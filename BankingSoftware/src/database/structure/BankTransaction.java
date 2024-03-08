package database.structure;

import helper.enumfiles.TransactionStatus;

public class BankTransaction {
	private String transactionId;
	private long transactionTimestamp;
	private long userId;
	private long accountNumber;
	private double amount;
	private int paymentType;
	private double currentBalance;
	private long transactorAccountNumber;
	private String description;
	private TransactionStatus status;

	public String getTransactionId() {
		return transactionId;
	}

	public long getTransactionTimestamp() {
		return transactionTimestamp;
	}

	public long getUserId() {
		return userId;
	}

	public long getAccountNumber() {
		return accountNumber;
	}

	public double getAmount() {
		return amount;
	}

	public double getCurrentBalance() {
		return currentBalance;
	}

	public long getTransactorAccountNumber() {
		return transactorAccountNumber;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public void setTransactionTimestamp(long transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setAmount(double amount) {
		this.amount = Math.round(amount * 100.0) / 100.0;
	}

	public void setCurrentBalance(double currentBalance) {
		this.currentBalance = Math.round(currentBalance * 100.0) / 100.0;
	}

	public void setTransactorAccountNumber(long transactorAccountNumber) {
		this.transactorAccountNumber = transactorAccountNumber;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = TransactionStatus.getByCode(status);
	}

	public int getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}

	public String getDecription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "BankTransaction [transactionId=" + transactionId + ", transactionTimestamp=" + transactionTimestamp
				+ ", userId=" + userId + ", accountNumber=" + accountNumber + ", amount=" + amount + ", paymentType="
				+ paymentType + ", currentBalance=" + currentBalance + ", transactorAccountNumber="
				+ transactorAccountNumber + ", description=" + description + ", status=" + status + "]";
	}
}
