package database;

import database.structure.BankCustomer;
import globalutil.CustomException;

public interface ICustomerData {

	// get the Logged in CustomerData
	BankCustomer getCustomerData() throws CustomException;

}
