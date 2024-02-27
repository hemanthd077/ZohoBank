package database;

import java.util.List;
import java.util.Map;

import database.structureClasses.BankAccount;
import database.structureClasses.BankCustomer;
import database.structureClasses.BankTransaction;
import database.structureClasses.BankBranch;
import handleError.CustomException;

public interface IBankAccount {

	public boolean createBankAccount(BankCustomer bankCustomerDetails,BankBranch branchDetails) throws CustomException;
	
	public Map<Integer,BankAccount> getAccountAllBranch(List<BankCustomer> bankCustomerDetails,int status) throws CustomException;
	
	public Map<Integer,BankAccount> getAccountSingleBranch(List<BankCustomer> bankCustomerDetails,int status,int branchId) throws CustomException;
	
	public <K,V> boolean  updateAccount(BankAccount bankAccountDetails,Map<K,V> fieldWithValue) throws CustomException;

	public BankAccount getAccountStatus(BankAccount bankAccountDetails) throws CustomException;
	
	public boolean transactionUpdate(BankTransaction bankTransactionDetails) throws CustomException;
	
	public Map<Integer,BankTransaction> getTransactDetails(BankAccount bankAccountDetails) throws CustomException;
}

