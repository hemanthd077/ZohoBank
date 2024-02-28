package database;

import java.util.Map;

import database.structureClasses.BankEmployee;
import handleError.CustomException;

public interface IEmployeeData {

	public BankEmployee getEmployeeData() throws CustomException;

	public Map<Integer, BankEmployee> getEmployeeDetails(int status) throws CustomException;
}
