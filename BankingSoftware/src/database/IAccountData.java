package database;

import java.util.List;
import java.util.Map;

import database.structureClasses.BankAccount;
import database.structureClasses.BankCustomer;
import database.structureClasses.BankTransaction;
import globalUtilities.CustomException;
import database.structureClasses.BankBranch;

public interface IAccountData {

	public boolean createBankAccount(BankCustomer bankCustomerDetails, BankBranch branchDetails) throws CustomException;

	public boolean storeTransaction(BankTransaction bankTransactionDetails) throws CustomException;

	public Map<Integer, BankTransaction> getTransactDetails(BankAccount bankAccountDetails, int days, Long timeStamp)
			throws CustomException;

	<K, V> boolean updateAccount(BankAccount bankAccountDetails, Map<K, V> fieldWithValue) throws CustomException;

	public BankAccount getAccountData(BankAccount bankAccountDetails) throws CustomException;

	public Map<Integer, BankAccount> getAccountWithBranch(List<BankCustomer> bankCustomerDetails, int status,
			int branchId) throws CustomException;
}
