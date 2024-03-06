package database;

import database.structure.BankCustomer;
import globalUtilities.CustomException;

public interface ICustomerData {

	// get the Logged in CustomerData
	BankCustomer getCustomerData() throws CustomException;

}
