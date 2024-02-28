package database;

import java.util.Map;

import database.structureClasses.BankCustomer;
import handleError.CustomException;

public interface ICustomerData {

	public Map<Integer, BankCustomer> getUserDetails(int status) throws CustomException;

	public <K, V, T> boolean updateUser(T bankCustomerDetails, Map<K, V> fieldAndValue) throws CustomException;

	public BankCustomer getCustomerData() throws CustomException;
}
