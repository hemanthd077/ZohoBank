package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.structureClasses.BankCustomerDetails;
import globalUtilities.GlobalChecker;
import handleError.CustomException;
import helper.BankCommonHelper;

public class CustomerDatabase implements ICustomerData {
	
	Connection connection;
	
	public CustomerDatabase() {
		try {
			connection = ConnectionCreation.getConnection();
		} catch (CustomException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Map<Integer,BankCustomerDetails> getUserDetails(int status) throws CustomException{
		Map<Integer,BankCustomerDetails> resultUserData = new HashMap<>();
		String Query;
		if(status == 0) {
			Query = "SELECT * FROM ZohoBankUser U "
					+ "JOIN BankCustomer C ON U.USER_ID = C.CUSTOMER_ID ;";
		}else {
			Query = "SELECT * FROM ZohoBankUser U "
				+ "JOIN BankCustomer C ON U.USER_ID = C.CUSTOMER_ID  where STATUS = "+status+";";
		}
		try {
			try(PreparedStatement getBranchStatement = connection.prepareStatement(Query)){
				try(ResultSet resultSet = getBranchStatement.executeQuery()){

					int id = 1;
					while(resultSet.next()) {
						BankCustomerDetails bankCustomerDetails = new BankCustomerDetails();
						
						bankCustomerDetails.setUserId(resultSet.getInt("USER_ID"));
						bankCustomerDetails.setEmail(resultSet.getString("EMAIL"));
						bankCustomerDetails.setPhonenumber(resultSet.getString("PHONE_NO"));
						bankCustomerDetails.setName(resultSet.getString("NAME"));
						bankCustomerDetails.setGender(resultSet.getString("GENDER"));
						bankCustomerDetails.setAddress(resultSet.getString("ADDRESS"));
						bankCustomerDetails.setPanNumber(resultSet.getString("PAN"));
						bankCustomerDetails.setAadharNumber(resultSet.getString("AADHAR"));
						
						resultUserData.put(id++,bankCustomerDetails);
					}
				}
			}
			return resultUserData;
		}
		catch(SQLException e) {
			throw new CustomException("Error occured while getting Branch Data",e);
		}
	}
	
	@Override
	public <K,V> boolean updateUser(BankCustomerDetails customerDetails,Map<K,V> fieldAndValue) throws CustomException {
		try {
			String setFields =  BankCommonHelper.userUpdateQueryBuilder(fieldAndValue);
			String updateQuery = "update ZohoBankUser set "+setFields+" where USER_ID = ?";
			
			try(PreparedStatement updatePreparedStatement = connection.prepareStatement(updateQuery)){
				updatePreparedStatement.setInt(1,customerDetails.getUserId());
				
				return updatePreparedStatement.executeUpdate()!=0;
			}
		}
		catch(SQLException e) {
			throw new CustomException("Error Occured in Blocking the User : ",e);
		}
	}
	
	@Override
	public BankCustomerDetails getCustomerData() throws CustomException {
	    try {
	    	String query = "SELECT U.*, C.*"
	    	        + "FROM ZohoBankUser U "
	    	        + "LEFT JOIN BankCustomer C ON U.USER_ID = C.CUSTOMER_ID "
	    	        + "WHERE U.USER_ID = ?";

	    	BankCustomerDetails bankCustomerDetails = new BankCustomerDetails();
	        try (PreparedStatement loginStatement = connection.prepareStatement(query)) {

	            loginStatement.setInt(1, UserDatabase.userId);
	            try (ResultSet resultSet = loginStatement.executeQuery()) {
	                if (resultSet.next()) {
	                	bankCustomerDetails.setUserId(resultSet.getInt("USER_ID"));
	                	bankCustomerDetails.setEmail(resultSet.getString("Email"));
	                	bankCustomerDetails.setPhonenumber(resultSet.getString("PHONE_NO"));
	                	bankCustomerDetails.setName(resultSet.getString("NAME"));
	                	if(BankCommonHelper.columnExists(resultSet, "DOB")) {
	                		bankCustomerDetails.setDateOfBirth(resultSet.getString("DOB")); 
	                	}
	                	bankCustomerDetails.setGender(resultSet.getString("GENDER"));
	                	bankCustomerDetails.setAddress(resultSet.getString("ADDRESS"));
	                	bankCustomerDetails.setPanNumber(resultSet.getString("PAN"));
	                	bankCustomerDetails.setAadharNumber(resultSet.getString("AADHAR"));
	                } 
	            }
	        }
	        return bankCustomerDetails;
	    } catch (SQLException e) {
	        throw new CustomException("Error occurred in the login process: ", e);
	    }
	}
}
