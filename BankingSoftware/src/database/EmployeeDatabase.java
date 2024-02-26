package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import database.structureClasses.BankCustomerDetails;
import database.structureClasses.BranchDetails;
import database.structureClasses.EmployeeDetails;
import globalUtilities.GlobalChecker;
import handleError.CustomException;
import helper.BankCommonHelper;

public class EmployeeDatabase implements IEmployee {
	
	Connection connection;

	public EmployeeDatabase() {
		try {
			connection = ConnectionCreation.getConnection();
		} catch (CustomException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public EmployeeDetails getEmployeeData() throws CustomException {
	    try {
	    	String query = "SELECT U.*, B.*, Br.* "
	    	        + "FROM ZohoBankUser U "
	    	        + "LEFT JOIN BranchEmployee B ON U.USER_ID = B.EMP_ID "
	    	        + "LEFT JOIN BranchData Br ON B.BRANCH_ID = Br.BRANCH_ID "
	    	        + "WHERE U.USER_ID = ?";

	        EmployeeDetails employeeDetails = new EmployeeDetails();
	        try (PreparedStatement loginStatement = connection.prepareStatement(query)) {

	            loginStatement.setInt(1, UserDatabase.userId);
	            try (ResultSet resultSet = loginStatement.executeQuery()) {
	                if (resultSet.next()) {
	                	employeeDetails.setEmail(resultSet.getString("Email"));
	                	employeeDetails.setPhonenumber(resultSet.getString("PHONE_NO"));
	                	employeeDetails.setName(resultSet.getString("NAME"));
	                	if(BankCommonHelper.columnExists(resultSet, "DOB")) {
		                	employeeDetails.setDateOfBirth(resultSet.getString("DOB")); 
	                	}
	                	employeeDetails.setGender(resultSet.getString("GENDER"));
	                	employeeDetails.setAddress(resultSet.getString("ADDRESS"));
	                	BranchDetails branchDetails = new BranchDetails();
	                	branchDetails.setBranch_id(resultSet.getInt("BRANCH_ID"));
	                	branchDetails.setCity(resultSet.getString("CITY"));
	                	branchDetails.setIfsc(resultSet.getString("IFSC"));
	                	branchDetails.setState(resultSet.getString("STATE"));
	                	branchDetails.setAddress(resultSet.getString("ADDRESS"));
	                	employeeDetails.setBranchDetails(branchDetails);
	                } 
	            }
	        }
	        return employeeDetails;
	    } catch (SQLException e) {
	        throw new CustomException("Error occurred in the login process: ", e);
	    }
	}
}
