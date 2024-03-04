package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.structureClasses.BankBranch;
import globalUtilities.CustomException;
import globalUtilities.GlobalChecker;

public class BranchDatabase implements IBranchData {

	Connection connection;

	public BranchDatabase() throws CustomException {
		try {
			connection = ConnectionCreation.getConnection();
		} catch (CustomException e) {
			throw new CustomException("Connection Failed Branch Database", e);
		}
	}

	@Override
	public Map<Integer, BankBranch> getBranchDetails(List<String> fieldList) throws CustomException {
		String fields = GlobalChecker.stringToPattern(fieldList, ",");
		Map<Integer, BankBranch> branchData = new HashMap<>();

		String Query = "select " + fields + " from BranchData";
		try {
			try (PreparedStatement getBranchStatement = connection.prepareStatement(Query)) {
				try (ResultSet resultSet = getBranchStatement.executeQuery()) {
					while (resultSet.next()) {
						int branchId = resultSet.getInt("BRANCH_ID");
						BankBranch branchDetails = new BankBranch();
						if (GlobalChecker.columnExists(resultSet, "BRANCH_ID")) {
							branchDetails.setBranch_id(resultSet.getInt("BRANCH_ID"));
						}
						if (GlobalChecker.columnExists(resultSet, "IFSC")) {
							branchDetails.setIfsc(resultSet.getString("IFSC"));
						}
						if (GlobalChecker.columnExists(resultSet, "CITY")) {
							branchDetails.setCity(resultSet.getString("CITY"));
						}
						if (GlobalChecker.columnExists(resultSet, "STATE")) {
							branchDetails.setState(resultSet.getString("STATE"));
						}
						if (GlobalChecker.columnExists(resultSet, "ADDRESS")) {
							branchDetails.setAddress(resultSet.getString("ADDRESS"));
						}
						branchData.put(branchId, branchDetails);
					}
				}
			}
			return branchData;
		} catch (SQLException e) {
			throw new CustomException("Error occured while getting Branch Data", e);
		}
	}
}
