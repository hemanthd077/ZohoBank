package database;

import java.util.Map;

import database.structureClasses.BankEmployee;
import globalUtilities.CustomException;

public interface IEmployeeData {

	public Map<Integer, BankEmployee> getEmployeeData(int status, int UserId, int access) throws CustomException;

}
