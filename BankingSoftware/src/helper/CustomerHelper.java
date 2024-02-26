package helper;

import java.util.HashMap;
import java.util.Map;

import database.BankAccountDatabase;
import database.CustomerDatabase;
import database.UserDatabase;
import database.structureClasses.BankAccountDetails;
import database.structureClasses.BankCustomerDetails;
import database.structureClasses.BankTransactionDetails;
import database.structureClasses.UserDetails;
import globalUtilities.GlobalChecker;
import handleError.CustomException;

public class CustomerHelper {

	static CustomerDatabase customerDatabase = new CustomerDatabase();
	static BankAccountDatabase bankAccountDatabase = new BankAccountDatabase();
	static UserDatabase userDatabase = new UserDatabase();
	
	public BankCustomerDetails getCustomerData() throws CustomException {
		return customerDatabase.getCustomerData();
	}
	
	public double checkBalance(BankAccountDetails bankAccountDetails,UserDetails userDetails) throws CustomException {
		GlobalChecker.checkNull(bankAccountDetails);
		GlobalChecker.checkNull(userDetails);
		
		if(!userDatabase.validatePassword(userDetails)) {
			throw new CustomException("Invalid Password to Check Balance");
		}
		
		bankAccountDetails.setStatus(1);
		BankAccountDetails accStatus = bankAccountDatabase.getAccountStatus(bankAccountDetails);
		
		return accStatus.getBalance();
	}

	public int moneyTransaction(BankTransactionDetails transactionDetails,BankAccountDetails senderBankAccountDetails,UserDetails userDetails) throws CustomException {
		GlobalChecker.checkNull(userDetails);
		GlobalChecker.checkNull(senderBankAccountDetails);
		
		if(!userDatabase.validatePassword(userDetails)) {
			return -1;
		}
		
		double amount = transactionDetails.getAmount();
		amount = Math.abs(amount);
		
		senderBankAccountDetails.setStatus(1);
		BankAccountDetails senderAccStatus = bankAccountDatabase.getAccountStatus(senderBankAccountDetails);
		long sAccNo = senderAccStatus.getAccountNo();
		double sBalance = senderAccStatus.getBalance();
		
		long rAccNo = 0L;
		double rBalance = 0.0;
		
		String transactionId = BankCommonHelper.generateTransactionId();
		
		int type = transactionDetails.getPaymentType();
		
		if(type==2 || type ==3) {
			Map<String,Object> updateFieldMap = new HashMap<>();
			updateFieldMap.put("BALANCE", type == 2 ? sBalance - amount : sBalance + amount);
			if((sBalance>=amount && type==2)||(type==3)) {
				@SuppressWarnings("unused")
				boolean senderUpdateResult = bankAccountDatabase.updateAccount(senderAccStatus, updateFieldMap);
				BankTransactionDetails senderTransactionDetails = transactSetter(transactionId,senderAccStatus,amount,0,type,1);
				if(bankAccountDatabase.transactionUpdate(senderTransactionDetails)) {
					return 3;
				}
				else {
					failedTransaction(transactionId,senderAccStatus,amount,0,type,0);
					return 2;
				}
			}
			else {
				failedTransaction(transactionId,senderAccStatus,amount,0,type,0);
				return 1;
			}
		}
		
		BankAccountDetails receiverAccountDetails = new BankAccountDetails();
		receiverAccountDetails.setAccountNo(transactionDetails.getTransactorAccountNumber());
		receiverAccountDetails.setStatus(1);
		
		BankAccountDetails receiverAccStatus = bankAccountDatabase.getAccountStatus(receiverAccountDetails);
		
		rAccNo = receiverAccStatus.getAccountNo();
		rBalance = receiverAccStatus.getBalance();
		if(rAccNo!=0 && sAccNo != rAccNo) {
			if(receiverAccStatus.getStatus()==1
					&& receiverAccStatus.getStatus()==1 
					&& sBalance >= amount) {

				Map<String,Object> senderFieldMap = new HashMap<>();
				senderFieldMap.put("BALANCE", sBalance-amount);
				boolean senderUpdateResult = bankAccountDatabase.updateAccount(senderAccStatus, senderFieldMap);
				BankTransactionDetails senderTransactionDetails = transactSetter(transactionId,senderAccStatus,amount,rAccNo,PaymentType.DEBIT.getCode(),1);
				
				Map<String,Object> receiverFieldMap = new HashMap<>();
				receiverFieldMap.put("BALANCE", (rBalance+amount));
				boolean receiverUpdateResult = bankAccountDatabase.updateAccount(receiverAccStatus, receiverFieldMap);
				BankTransactionDetails receiverTransactionDetails = transactSetter(transactionId,receiverAccStatus,amount,sAccNo,PaymentType.CREDIT.getCode(),1);
				
				if(bankAccountDatabase.transactionUpdate(senderTransactionDetails) &&
						bankAccountDatabase.transactionUpdate(receiverTransactionDetails) &&
						receiverUpdateResult && senderUpdateResult) {
					return 3;
				}
				else {
					failedTransaction(transactionId,senderAccStatus,amount,rAccNo,PaymentType.DEBIT.getCode(),0);
					return 2;
				}
			}
			else {
				failedTransaction(transactionId,senderAccStatus,amount,rAccNo,PaymentType.DEBIT.getCode(),0);
				return 1;
			}
		}
		else {
			return 0;
		}
	}
	
	public int withdrawTransaction(BankTransactionDetails transactionDetails,BankAccountDetails senderBankAccountDetails,UserDetails userDetails) throws CustomException {
		GlobalChecker.checkNull(transactionDetails);
		transactionDetails.setPaymentType(PaymentType.WITHDRAWAL.getCode());
		return moneyTransaction(transactionDetails, senderBankAccountDetails, userDetails);
	}
	
	public int depositTransaction(BankTransactionDetails transactionDetails,BankAccountDetails senderBankAccountDetails,UserDetails userDetails) throws CustomException {
		GlobalChecker.checkNull(transactionDetails);
		transactionDetails.setPaymentType(PaymentType.DEPOSIT.getCode());
		return moneyTransaction(transactionDetails, senderBankAccountDetails, userDetails);
	}
	
	private static BankTransactionDetails transactSetter(String transactionId,BankAccountDetails accStatus,
															double amount,long transactAccNo,int type,int status) {
		
		BankTransactionDetails receiverTransactionDetails = new BankTransactionDetails();
		
		amount = (type==0 || type ==2)? (0-amount):amount;
		
		receiverTransactionDetails.setTransactionId(transactionId);
		receiverTransactionDetails.setTransactionTimestamp(BankCommonHelper.getCurrentTimeMills());
		receiverTransactionDetails.setUserId(accStatus.getUserDetails().getUserId());
		receiverTransactionDetails.setAccountNumber(accStatus.getAccountNo());
		receiverTransactionDetails.setAmount(amount);
		receiverTransactionDetails.setPaymentType(type);
		receiverTransactionDetails.setCurrentBalance(accStatus.getBalance()+amount);
		receiverTransactionDetails.setTransactorAccountNumber(transactAccNo);
		receiverTransactionDetails.setStatus(status);
		
		return receiverTransactionDetails;
	}
	
	private void failedTransaction(String transactionId,BankAccountDetails accStatus,
										double amount,long transactAccNo,int type,int status) throws CustomException {
		
		BankTransactionDetails failedTransaction =transactSetter(transactionId,accStatus,amount,transactAccNo,1,0);
		failedTransaction.setCurrentBalance(failedTransaction.getCurrentBalance()-amount);
		bankAccountDatabase.transactionUpdate(failedTransaction);
	}
	
	public Map<Integer,BankTransactionDetails> getTransactionDetails(BankAccountDetails bankAccountDetails) throws CustomException {
		return bankAccountDatabase.getTransactDetails(bankAccountDetails);
	}
}
