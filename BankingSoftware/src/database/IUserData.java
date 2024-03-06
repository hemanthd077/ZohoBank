package database;

import java.util.Map;

import database.structure.BankCustomer;
import globalUtilities.CustomException;

public interface IUserData {

	// return required key with value to find (userType)
	public Map<String, Integer> userLogin(String phoneNo, String password) throws CustomException;

	public boolean validatePassword(String password) throws CustomException;

	<T> boolean createUser(T userDetails, boolean isEmployee) throws CustomException;

	public <K, V, T> boolean updateUser(T customerDetails, Map<K, V> fieldAndValue) throws CustomException;

	public Map<Integer, BankCustomer> getUserDetails(int status, int limit, int offset) throws CustomException;

}
