package database;

import java.util.Map;

import database.structure.BankAccount;
import database.structure.BankBranch;
import database.structure.BankCustomer;
import globalutil.CustomException;

public interface IAccountData {

	boolean createBankAccount(BankCustomer bankCustomerDetails, BankBranch branchDetails, int accountType)
			throws CustomException;

	<K, V> boolean updateAccount(long accountNo, long userId, Map<K, V> fieldWithValue) throws CustomException;

	BankAccount getAccountData(long accountNo, int status) throws CustomException;

	Map<Long, BankAccount> getAccountWithBranch(long userId, int status, int branchId) throws CustomException;

}
