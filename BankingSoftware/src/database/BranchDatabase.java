package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import database.structure.BankBranch;
import globalUtilities.CustomException;

public class BranchDatabase implements IBranchData {

	@Override
	public Map<Integer, BankBranch> getBranchDetails() throws CustomException {
		Map<Integer, BankBranch> branchData = new HashMap<>();

		String Query = "select * from BranchData";
		try {
			try (Connection connection = ConnectionCreation.getConnection();
					PreparedStatement getBranchStatement = connection.prepareStatement(Query)) {

				try (ResultSet resultSet = getBranchStatement.executeQuery()) {
					while (resultSet.next()) {

						int branchId = resultSet.getInt("BRANCH_ID");
						BankBranch branchDetails = new BankBranch();
						branchDetails.setBranch_id(resultSet.getInt("BRANCH_ID"));
						branchDetails.setIfsc(resultSet.getString("IFSC"));
						branchDetails.setCity(resultSet.getString("CITY"));
						branchDetails.setState(resultSet.getString("STATE"));
						branchDetails.setAddress(resultSet.getString("ADDRESS"));
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
