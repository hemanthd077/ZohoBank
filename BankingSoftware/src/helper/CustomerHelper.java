package helper;

import java.util.HashMap;
import java.util.Map;

import database.BankAccountDatabase;
import database.BankCustomerDatabase;
import database.BankUserDatabase;
import database.structureClasses.BankAccount;
import database.structureClasses.BankCustomer;
import database.structureClasses.BankTransaction;
import database.structureClasses.BankUser;
import globalUtilities.GlobalChecker;
import handleError.CustomException;
import helper.enumFiles.PaymentType;
import helper.enumFiles.StatusType;

public class CustomerHelper {

	static BankCustomerDatabase customerDatabase = new BankCustomerDatabase();
	static BankAccountDatabase bankAccountDatabase = new BankAccountDatabase();
	static BankUserDatabase userDatabase = new BankUserDatabase();
	static UserHelper userHelper = new UserHelper();
	
	public BankCustomer getCustomerData() throws CustomException {
		return customerDatabase.getCustomerData();
	}
	
	public double checkBalance(BankAccount bankAccountDetails,BankUser userDetails) throws CustomException {
		GlobalChecker.checkNull(bankAccountDetails);
		GlobalChecker.checkNull(userDetails);
		
		if(!userHelper.validatePassword(userDetails)) {
			return -1;
		}
		
		bankAccountDetails.setStatus(1);
		BankAccount accStatus = bankAccountDatabase.getAccountStatus(bankAccountDetails);
		
		return accStatus.getBalance();
	}

	public int moneyTransaction(BankTransaction transactionDetails,BankAccount senderBankAccountDetails,BankUser userDetails) throws CustomException {
		GlobalChecker.checkNull(userDetails);
		GlobalChecker.checkNull(senderBankAccountDetails);
		GlobalChecker.checkNull(userDetails);
		
		if(!userHelper.validatePassword(userDetails)) {
			return -1;
		}
		
		double amount = transactionDetails.getAmount();
		if(amount<=0) {
			return 0;
		}
		
		senderBankAccountDetails.setStatus(1);
		BankAccount senderAccStatus = bankAccountDatabase.getAccountStatus(senderBankAccountDetails);
		long sAccNo = senderAccStatus.getAccountNo();
		double sBalance = senderAccStatus.getBalance();
		
		long rAccNo = 0L;
		double rBalance = 0.0;
		
		String transactionId = GlobalChecker.generateTransactionId();
		
		int type = transactionDetails.getPaymentType();
		
		if(type==2 || type ==3) {
			Map<String,Object> updateFieldMap = new HashMap<>();
			updateFieldMap.put("BALANCE", type == 2 ? sBalance - amount : sBalance + amount);
			if((sBalance>=amount && type==2)||(type==3)) {
				@SuppressWarnings("unused")
				boolean senderUpdateResult = bankAccountDatabase.updateAccount(senderAccStatus, updateFieldMap);
				BankTransaction senderTransactionDetails = transactSetter(transactionId,senderAccStatus,amount,0,type,StatusType.ACTIVE.getCode());
				if(bankAccountDatabase.transactionUpdate(senderTransactionDetails)) {
					return 3;
				}
				else {
					failedTransaction(transactionId,senderAccStatus,amount,0,type);
					return 2;
				}
			}
			else {
				failedTransaction(transactionId,senderAccStatus,amount,0,type);
				return 1;
			}
		}
		
		BankAccount receiverAccountDetails = new BankAccount();
		receiverAccountDetails.setAccountNo(transactionDetails.getTransactorAccountNumber());
		receiverAccountDetails.setStatus(1);
		
		BankAccount receiverAccStatus = bankAccountDatabase.getAccountStatus(receiverAccountDetails);
		if(receiverAccStatus.getAccountNo() == null) {
			return 0;
		}
		rAccNo = receiverAccStatus.getAccountNo();
		rBalance = receiverAccStatus.getBalance();
		if(rAccNo!=0 && sAccNo != rAccNo) {
			if(receiverAccStatus.getStatus()==1
					&& receiverAccStatus.getStatus()==1 
					&& sBalance >= amount) {

				Map<String,Object> senderFieldMap = new HashMap<>();
				senderFieldMap.put("BALANCE", sBalance-amount);
				boolean senderUpdateResult = bankAccountDatabase.updateAccount(senderAccStatus, senderFieldMap);
				BankTransaction senderTransactionDetails = transactSetter(transactionId,senderAccStatus,amount,rAccNo,PaymentType.DEBIT.getCode(),StatusType.ACTIVE.getCode());
				
				Map<String,Object> receiverFieldMap = new HashMap<>();
				receiverFieldMap.put("BALANCE", (rBalance+amount));
				boolean receiverUpdateResult = bankAccountDatabase.updateAccount(receiverAccStatus, receiverFieldMap);
				BankTransaction receiverTransactionDetails = transactSetter(transactionId,receiverAccStatus,amount,sAccNo,PaymentType.CREDIT.getCode(),StatusType.ACTIVE.getCode());
				
				if(bankAccountDatabase.transactionUpdate(senderTransactionDetails) &&
						bankAccountDatabase.transactionUpdate(receiverTransactionDetails) &&
						receiverUpdateResult && senderUpdateResult) {
					return 3;
				}
				else {
					failedTransaction(transactionId,senderAccStatus,amount,rAccNo,PaymentType.DEBIT.getCode());
					return 2;
				}
			}
			else {
				failedTransaction(transactionId,senderAccStatus,amount,rAccNo,PaymentType.DEBIT.getCode());
				return 1;
			}
		}
		else {
			return 0;
		}
	}
	
	public int withdrawTransaction(BankTransaction transactionDetails,BankAccount senderBankAccountDetails,BankUser userDetails) throws CustomException {
		GlobalChecker.checkNull(transactionDetails);
		
		transactionDetails.setPaymentType(PaymentType.WITHDRAWAL.getCode());
		return moneyTransaction(transactionDetails, senderBankAccountDetails, userDetails);
	}
	
	public int depositTransaction(BankTransaction transactionDetails,BankAccount senderBankAccountDetails,BankUser userDetails) throws CustomException {
		GlobalChecker.checkNull(transactionDetails);
	
		transactionDetails.setPaymentType(PaymentType.DEPOSIT.getCode());
		return moneyTransaction(transactionDetails, senderBankAccountDetails, userDetails);
	}
	
	private static BankTransaction transactSetter(String transactionId,BankAccount accStatus,
															double amount,long transactAccNo,int type,int status) throws CustomException {
		
		GlobalChecker.checkNull(accStatus);
		GlobalChecker.checkNull(transactionId);
		
		BankTransaction receiverTransactionDetails = new BankTransaction();
		
		amount = (type==0 || type ==2)? (0-amount):amount;
		
		receiverTransactionDetails.setTransactionId(transactionId);
		receiverTransactionDetails.setTransactionTimestamp(GlobalChecker.getCurrentTimeMills());
		receiverTransactionDetails.setUserId(accStatus.getUserDetails().getUserId());
		receiverTransactionDetails.setAccountNumber(accStatus.getAccountNo());
		receiverTransactionDetails.setAmount(amount);
		receiverTransactionDetails.setPaymentType(type);
		receiverTransactionDetails.setCurrentBalance(accStatus.getBalance()+amount);
		receiverTransactionDetails.setTransactorAccountNumber(transactAccNo);
		receiverTransactionDetails.setStatus(status);
		
		return receiverTransactionDetails;
	}
	
	private void failedTransaction(String transactionId,BankAccount accStatus,
										double amount,long transactAccNo,int type) throws CustomException {
		GlobalChecker.checkNull(accStatus);
		GlobalChecker.checkNull(transactionId);
		
		BankTransaction failedTransaction =transactSetter(transactionId,accStatus,amount,transactAccNo,1,StatusType.INACTIVE.getCode());
		failedTransaction.setCurrentBalance(failedTransaction.getCurrentBalance()-amount);
		bankAccountDatabase.transactionUpdate(failedTransaction);
	}
	
	public Map<Integer,BankTransaction> getTransactionDetails(BankAccount bankAccountDetails) throws CustomException {
		GlobalChecker.checkNull(bankAccountDetails);
		
		return bankAccountDatabase.getTransactDetails(bankAccountDetails);
	}
}
