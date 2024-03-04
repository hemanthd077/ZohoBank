package database.structureClasses;

public class BankTransaction {
	private String transactionId;
	private Long transactionTimestamp;
	private int userId;
	private Long accountNumber;
	private double amount;
	private int paymentType;
	private double currentBalance;
	private Long transactorAccountNumber;
	private String decription;
	private int status;

	public String getTransactionId() {
		return transactionId;
	}

	public Long getTransactionTimestamp() {
		return transactionTimestamp;
	}

	public int getUserId() {
		return userId;
	}

	public Long getAccountNumber() {
		return accountNumber;
	}

	public double getAmount() {
		return amount;
	}

	public double getCurrentBalance() {
		return currentBalance;
	}

	public Long getTransactorAccountNumber() {
		return transactorAccountNumber;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public void setTransactionTimestamp(Long transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setAmount(double amount) {
		this.amount = Math.round(amount *100.0)/100.0;
	}

	public void setCurrentBalance(double currentBalance) {
		this.currentBalance = Math.round(currentBalance *100.0)/100.0;
	}

	public void setTransactorAccountNumber(Long transactorAccountNumber) {
		this.transactorAccountNumber = transactorAccountNumber;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}

	public String getDecription() {
		return decription;
	}

	public void setDecription(String decription) {
		this.decription = decription;
	}
}
