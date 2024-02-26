package database;

import java.util.List;
import java.util.Map;

import database.structureClasses.BankAccountDetails;
import database.structureClasses.BankCustomerDetails;
import database.structureClasses.BankTransactionDetails;
import database.structureClasses.BranchDetails;
import handleError.CustomException;

public interface IBankAccount {

	public boolean createBankAccount(BankCustomerDetails bankCustomerDetails,BranchDetails branchDetails) throws CustomException;
	
	public Map<Integer,BankAccountDetails> getAccountAllBranch(List<BankCustomerDetails> bankCustomerDetails,int status) throws CustomException;
	
	public Map<Integer,BankAccountDetails> getAccountSingleBranch(List<BankCustomerDetails> bankCustomerDetails,int status,int branchId) throws CustomException;
	
	public <K,V> boolean  updateAccount(BankAccountDetails bankAccountDetails,Map<K,V> fieldWithValue) throws CustomException;

	public BankAccountDetails getAccountStatus(BankAccountDetails bankAccountDetails) throws CustomException;
	
	public boolean transactionUpdate(BankTransactionDetails bankTransactionDetails) throws CustomException;
	
	public Map<Integer,BankTransactionDetails> getTransactDetails(BankAccountDetails bankAccountDetails) throws CustomException;
}

