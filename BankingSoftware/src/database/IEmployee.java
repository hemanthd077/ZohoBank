package database;

import java.util.List;

import database.structureClasses.EmployeeDetails;
import handleError.CustomException;

public interface IEmployee {
	
	public EmployeeDetails getEmployeeData() throws CustomException ;
}
