package database;

import database.structureClasses.BankCustomer;
import globalUtilities.CustomException;

public interface ICustomerData {

	public BankCustomer getCustomerData() throws CustomException;
}
