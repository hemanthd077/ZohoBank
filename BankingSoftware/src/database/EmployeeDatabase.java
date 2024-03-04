package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import database.structureClasses.BankBranch;
import database.structureClasses.BankEmployee;
import globalUtilities.CustomException;
import globalUtilities.GlobalChecker;

public class EmployeeDatabase implements IEmployeeData {

	Connection connection;

	public EmployeeDatabase() throws CustomException {
		try {
			connection = ConnectionCreation.getConnection();
		} catch (CustomException e) {
			throw new CustomException("Failed to Connect Employee Database");
		}
	}

	@Override
	public Map<Integer, BankEmployee> getEmployeeData(int status, int userId, int access) throws CustomException {
		try {
			Map<Integer, BankEmployee> employeeMap = new HashMap<>();
			String query = "SELECT U.*, B.*, Br.* " + "FROM ZohoBankUser U "
					+ "LEFT JOIN BranchEmployee B ON U.USER_ID = B.EMP_ID "
					+ "LEFT JOIN BranchData Br ON B.BRANCH_ID = Br.BRANCH_ID WHERE STATUS = ? ";
			boolean accessStatus = access != -1;
			boolean userIdStatus = userId != -1;

			if (access != -1) {
				query += " AND B.ACCESS = ?";
			}
			if (userId != -1) {
				query += " AND U.USER_ID = ?";
			}
			try (PreparedStatement fetchStatement = connection.prepareStatement(query)) {
				fetchStatement.setInt(1, status);
				if (accessStatus && userIdStatus) {
					fetchStatement.setInt(2, access);
					fetchStatement.setInt(3, userId);
				} else if (accessStatus) {
					fetchStatement.setInt(2, access);
				} else if (userIdStatus) {
					fetchStatement.setInt(2, userId);
				}
				int empUserId;
				try (ResultSet resultSet = fetchStatement.executeQuery()) {
					while (resultSet.next()) {

						empUserId = resultSet.getInt("USER_ID");
						BankEmployee employeeDetails = new BankEmployee();
						employeeDetails.setUserId(empUserId);
						employeeDetails.setEmail(resultSet.getString("EMAIL"));
						employeeDetails.setPhonenumber(resultSet.getString("PHONE_NO"));
						employeeDetails.setName(resultSet.getString("NAME"));
						if (GlobalChecker.columnExists(resultSet, "DOB")) {
							employeeDetails.setDateOfBirth(resultSet.getString("DOB"));
						}
						employeeDetails.setGender(resultSet.getString("GENDER"));
						employeeDetails.setAddress(resultSet.getString("ADDRESS"));
						employeeDetails.setEmployeeAccess(resultSet.getInt("ACCESS"));

						BankBranch branchDetails = new BankBranch();
						branchDetails.setBranch_id(resultSet.getInt("BRANCH_ID"));
						branchDetails.setCity(resultSet.getString("CITY"));
						branchDetails.setIfsc(resultSet.getString("IFSC"));
						branchDetails.setState(resultSet.getString("STATE"));
						branchDetails.setAddress(resultSet.getString("ADDRESS"));
						employeeDetails.setBankBranch(branchDetails);

						employeeMap.put(empUserId, employeeDetails);
					}
				}
			}
			return employeeMap;
		} catch (SQLException e) {
			throw new CustomException("Error occurred in the login process: ", e);
		}
	}
}
