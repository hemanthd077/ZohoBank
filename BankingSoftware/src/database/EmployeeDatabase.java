package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import database.dbutils.CommonDatabaseUtil;
import database.structure.BankBranch;
import database.structure.BankEmployee;
import globalUtilities.CustomException;

public class EmployeeDatabase implements IEmployeeData {

	@Override
	public Map<Integer, Map<Long, BankEmployee>> getEmployeeByBranch(int branchId, int status) throws CustomException {

		Map<Integer, Map<Long, BankEmployee>> branchEmployee = new HashMap<>();

		String query = "SELECT U.*, B.*, Br.* " + "FROM ZohoBankUser U "
				+ "LEFT JOIN BranchEmployee B ON U.USER_ID = B.EMP_ID "
				+ "LEFT JOIN BranchData Br ON B.BRANCH_ID = Br.BRANCH_ID WHERE STATUS = ? AND B.BRANCH_ID = ?";

		try (Connection connection = ConnectionCreation.getConnection();
				PreparedStatement fetchByBranchStatement = connection.prepareStatement(query)) {
			fetchByBranchStatement.setInt(1, status);
			fetchByBranchStatement.setInt(2, branchId);

			long empId;
			try (ResultSet resultSet = fetchByBranchStatement.executeQuery()) {
				Map<Long, BankEmployee> tempEmployeeMap = new HashMap<>();
				while (resultSet.next()) {
					empId = resultSet.getLong("USER_ID");
					BankEmployee employeeDetails = new BankEmployee();
					employeeDetails.setUserId(empId);
					employeeDetails.setEmail(resultSet.getString("EMAIL"));
					employeeDetails.setPhonenumber(resultSet.getString("PHONE_NO"));
					employeeDetails.setName(resultSet.getString("NAME"));
					if (CommonDatabaseUtil.columnExists(resultSet, "DOB")) {
						employeeDetails.setDateOfBirth(resultSet.getLong("DOB"));
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

					tempEmployeeMap.put(empId, employeeDetails);
				}
				branchEmployee.put(branchId, tempEmployeeMap);
				return branchEmployee;
			}
		} catch (SQLException e) {
			throw new CustomException("Error Occured in employee fetch by branch", e);
		}
	}

	@Override
	public Map<Long, BankEmployee> getEmployeeData(int status, long userId, int access, int rowLimit, int pageCount)
			throws CustomException {
		try {
			Map<Long, BankEmployee> employeeMap = new HashMap<>();
			String query = "SELECT U.*, B.*, Br.* " + "FROM ZohoBankUser U "
					+ "LEFT JOIN BranchEmployee B ON U.USER_ID = B.EMP_ID "
					+ "LEFT JOIN BranchData Br ON B.BRANCH_ID = Br.BRANCH_ID WHERE U.STATUS = ? ";
			boolean accessStatus = access != -1;
			boolean userIdStatus = userId != -1;

			if (access != -1) {
				query += " AND B.ACCESS = ?";
			}
			if (userId != -1) {
				query += " AND U.USER_ID = ?";
			}
			query += " LIMIT ? OFFSET ?";
			try (Connection connection = ConnectionCreation.getConnection();
					PreparedStatement fetchStatement = connection.prepareStatement(query)) {
				fetchStatement.setInt(1, status);
				if (accessStatus && userIdStatus) {
					fetchStatement.setInt(2, access);
					fetchStatement.setLong(3, userId);
					fetchStatement.setInt(4, rowLimit);
					fetchStatement.setInt(5, pageCount);
				} else if (accessStatus) {
					fetchStatement.setInt(2, access);
					fetchStatement.setInt(3, rowLimit);
					fetchStatement.setInt(4, pageCount);
				} else if (userIdStatus) {
					fetchStatement.setLong(2, userId);
					fetchStatement.setInt(3, rowLimit);
					fetchStatement.setInt(4, pageCount);
				}
				long empUserId;
				try (ResultSet resultSet = fetchStatement.executeQuery()) {
					while (resultSet.next()) {

						empUserId = resultSet.getLong("USER_ID");
						BankEmployee employeeDetails = new BankEmployee();
						employeeDetails.setUserId(empUserId);
						employeeDetails.setEmail(resultSet.getString("EMAIL"));
						employeeDetails.setPhonenumber(resultSet.getString("PHONE_NO"));
						employeeDetails.setName(resultSet.getString("NAME"));
						if (CommonDatabaseUtil.columnExists(resultSet, "DOB")) {
							employeeDetails.setDateOfBirth(resultSet.getLong("DOB"));
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
