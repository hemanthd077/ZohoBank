package database;

import java.util.Map;

import database.structure.BankEmployee;
import globalUtilities.CustomException;

public interface IEmployeeData {

	public Map<Integer, BankEmployee> getEmployeeData(int status, int empId, int access, int rowLimit, int pageCount)
			throws CustomException;

}
