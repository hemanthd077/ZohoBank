package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.structureClasses.BranchDetails;
import handleError.CustomException;
import helper.BankCommonHelper;

public class BranchDatabase {
	
	Connection connection;
	
	public BranchDatabase() {
		try {
			connection = ConnectionCreation.getConnection();
		} catch (CustomException e) {
			e.printStackTrace();
		}
	}
	
	public <T> Map<Integer,BranchDetails> getBranchDetails(List<String> fieldList)  throws CustomException{
		String fields = BankCommonHelper.stringToPattern(fieldList, ",");
		Map<Integer,BranchDetails> branchData = new HashMap<>();
		
		String Query = "select "+fields+" from BranchData";
		try {
			try(PreparedStatement getBranchStatement = connection.prepareStatement(Query)){
				try(ResultSet resultSet = getBranchStatement.executeQuery()){
					while(resultSet.next()) {
						int branchId = resultSet.getInt("BRANCH_ID");
						BranchDetails branchDetails = new BranchDetails();
						if(BankCommonHelper.columnExists(resultSet,"BRANCH_ID")) {
							branchDetails.setBranch_id(resultSet.getInt("BRANCH_ID"));
						}
						if(BankCommonHelper.columnExists(resultSet,"IFSC")) {
							branchDetails.setIfsc(resultSet.getString("IFSC"));
						}
						if(BankCommonHelper.columnExists(resultSet,"CITY")) {
							branchDetails.setCity(resultSet.getString("CITY"));
						}
						if(BankCommonHelper.columnExists(resultSet,"STATE")) {
							branchDetails.setState(resultSet.getString("STATE"));
						}
						if(BankCommonHelper.columnExists(resultSet,"ADDRESS")) {
							branchDetails.setAddress(resultSet.getString("ADDRESS"));
						}
						branchData.put(branchId,branchDetails);
					}
				}
			}
			return branchData;
		}
		catch(SQLException e) {
			throw new CustomException("Error occured while getting Branch Data",e);
		}
	}
}
