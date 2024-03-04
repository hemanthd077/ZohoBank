package database;

import java.util.List;
import java.util.Map;

import database.structureClasses.BankCustomer;
import database.structureClasses.BankUser;
import globalUtilities.CustomException;

public interface IUserData {

	public Map<String, Integer> userLogin(BankUser userDetails) throws CustomException;

	public boolean validatePassword(BankUser userDetails) throws CustomException;

	boolean createUser(List<? extends BankUser> userDetails, boolean isEmployee) throws CustomException;

	public <K, V, T> boolean updateUser(T customerDetails, Map<K, V> fieldAndValue) throws CustomException;

	public Map<Integer, BankCustomer> getUserDetails(int status) throws CustomException;

}
