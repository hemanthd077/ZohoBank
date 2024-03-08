package database;

import java.util.Map;

import database.structure.BankEmployee;
import globalutil.CustomException;

public interface IEmployeeData {

	Map<Long, BankEmployee> getEmployeeData(int status, long empId, int access, int rowLimit, int pageCount)
			throws CustomException;

	Map<Integer, Map<Long, BankEmployee>> getEmployeeByBranch(int branchId, int status) throws CustomException;

}
