package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import database.structureClasses.BankBranch;
import database.structureClasses.BankEmployee;
import globalUtilities.GlobalChecker;
import handleError.CustomException;

public class BankEmployeeDatabase implements IEmployee {
	
	Connection connection;

	public BankEmployeeDatabase() {
		try {
			connection = ConnectionCreation.getConnection();
		} catch (CustomException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public BankEmployee getEmployeeData() throws CustomException {
	    try {
	    	String query = "SELECT U.*, B.*, Br.* "
	    	        + "FROM ZohoBankUser U "
	    	        + "LEFT JOIN BranchEmployee B ON U.USER_ID = B.EMP_ID "
	    	        + "LEFT JOIN BranchData Br ON B.BRANCH_ID = Br.BRANCH_ID "
	    	        + "WHERE U.USER_ID = ?";

	        BankEmployee employeeDetails = new BankEmployee();
	        try (PreparedStatement loginStatement = connection.prepareStatement(query)) {

	            loginStatement.setInt(1, BankUserDatabase.userId);
	            try (ResultSet resultSet = loginStatement.executeQuery()) {
	                if (resultSet.next()) {
	                	employeeDetails.setEmail(resultSet.getString("Email"));
	                	employeeDetails.setPhonenumber(resultSet.getString("PHONE_NO"));
	                	employeeDetails.setName(resultSet.getString("NAME"));
	                	if(GlobalChecker.columnExists(resultSet, "DOB")) {
		                	employeeDetails.setDateOfBirth(resultSet.getString("DOB")); 
	                	}
	                	employeeDetails.setGender(resultSet.getString("GENDER"));
	                	employeeDetails.setAddress(resultSet.getString("ADDRESS"));
	                	BankBranch branchDetails = new BankBranch();
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
	
	@Override
	public Map<Integer,BankEmployee> getEmployeeDetails(int status) throws CustomException{
		Map<Integer,BankEmployee> resultEmployeeData = new HashMap<>();
		
		String Query ="SELECT * FROM ZohoBankUser U "
				+ "JOIN BranchEmployee E ON U.USER_ID = E.EMP_ID where STATUS = ?;";
		try {
			try(PreparedStatement getBranchStatement = connection.prepareStatement(Query)){
				getBranchStatement.setInt(1, status);
				try(ResultSet resultSet = getBranchStatement.executeQuery()){

					int id = 1;
					while(resultSet.next()) {
						BankEmployee employeeDetails = new BankEmployee();
						
						employeeDetails.setUserId(resultSet.getInt("USER_ID"));
						employeeDetails.setEmail(resultSet.getString("EMAIL"));
						employeeDetails.setPhonenumber(resultSet.getString("PHONE_NO"));
						employeeDetails.setName(resultSet.getString("NAME"));
						employeeDetails.setGender(resultSet.getString("GENDER"));
						employeeDetails.setAddress(resultSet.getString("ADDRESS"));
						employeeDetails.setEmployeeAccess(resultSet.getInt("ACCESS"));
						BankBranch branchDetails = new BankBranch();
						branchDetails.setBranch_id(resultSet.getInt("BRANCH_ID"));
						employeeDetails.setBranchDetails(branchDetails);
						resultEmployeeData.put(id++,employeeDetails);
					}
				}
			}
			return resultEmployeeData;
		}
		catch(SQLException e) {
			throw new CustomException("Error occured while getting Branch Data",e);
		}
	}
}
