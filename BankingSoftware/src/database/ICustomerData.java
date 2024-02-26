package database;

import java.util.List;
import java.util.Map;

import database.structureClasses.BankCustomerDetails;
import handleError.CustomException;

public interface ICustomerData {
	
	public Map<Integer,BankCustomerDetails> getUserDetails(int status) throws CustomException;
	
	public <K,V> boolean updateUser(BankCustomerDetails bankCustomerDetails,Map<K,V> fieldAndValue) throws CustomException;
	
	public BankCustomerDetails getCustomerData() throws CustomException;
}
