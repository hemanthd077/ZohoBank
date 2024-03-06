package database;

import database.structure.BankCustomer;
import globalUtilities.CustomException;

public interface ICustomerData {

	// get the Logged in CustomerData
	public BankCustomer getCustomerData() throws CustomException;

}
