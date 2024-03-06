package database;

import java.util.Map;

import database.structure.BankCustomer;
import globalUtilities.CustomException;

public interface IUserData {

	int userValidation(long checkUserId, String password) throws CustomException;

	<T> boolean createUser(T userDetails, boolean employeeStatus) throws CustomException;

	<K, V> boolean updateUser(long userId, Map<K, V> fieldAndValue) throws CustomException;

	Map<Long, BankCustomer> getUserDetails(int status, int limit, int offset) throws CustomException;

	boolean isAdmin(long checkUserId) throws CustomException;
}
