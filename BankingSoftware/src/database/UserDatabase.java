package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import database.dbutils.CommonDatabaseUtil;
import database.structure.BankCustomer;
import database.structure.BankEmployee;
import database.structure.BankUser;
import database.structure.CurrentUser;
import globalutil.CustomException;
import globalutil.GlobalCommonChecker;

public class UserDatabase implements IUserData {

	@Override
	public <T> boolean createUser(T userDetails, boolean employeeStatus) throws CustomException {
		try {
			GlobalCommonChecker.checkNull(userDetails);

			String insertQueryUser = "INSERT INTO ZohoBankUser(EMAIL, PASSWORD, PHONE_NO, NAME, GENDER, ADDRESS, USER_TYPE) VALUES(?,?,?,?,?,?,?)";
			String insertQuerySpecific;

			if (employeeStatus) {
				insertQuerySpecific = "INSERT INTO BranchEmployee(EMP_ID, ACCESS, BRANCH_ID) VALUES(?,?,?)";
			} else {
				insertQuerySpecific = "INSERT INTO BankCustomer(CUSTOMER_ID, PAN, AADHAR) VALUES(?,?,?)";
			}

			try (Connection connection = ConnectionCreation.getConnection();
					PreparedStatement insertStatementUser = connection.prepareStatement(insertQueryUser,
							Statement.RETURN_GENERATED_KEYS);
					PreparedStatement insertStatementSpecific = connection.prepareStatement(insertQuerySpecific)) {
				
				insertStatementUser.setString(1, ((BankUser) userDetails).getEmail());
				insertStatementUser.setString(2, ((BankUser) userDetails).getPassword());
				insertStatementUser.setString(3, ((BankUser) userDetails).getPhoneNumber());
				insertStatementUser.setString(4, ((BankUser) userDetails).getName());
				insertStatementUser.setString(5, ((BankUser) userDetails).getGender());
				insertStatementUser.setString(6, ((BankUser) userDetails).getAddress());
				insertStatementUser.setInt(7, employeeStatus ? 2 : 1);
				insertStatementUser.addBatch();
				int[] userAccountResult = insertStatementUser.executeBatch();

				try (ResultSet generatedKeys = insertStatementUser.getGeneratedKeys()) {
					while (generatedKeys.next()) {
						BankUser userDetailsSpecific = (BankUser) userDetails;
						if (employeeStatus) {
							insertStatementSpecific.setInt(1, generatedKeys.getInt(1));
							insertStatementSpecific.setInt(2, ((BankEmployee) userDetailsSpecific).getEmployeeAccess().getCode());
							insertStatementSpecific.setInt(3,
									((BankEmployee) userDetailsSpecific).getBankBranch().getBranchId());
						} else {
							insertStatementSpecific.setInt(1, generatedKeys.getInt(1));
							insertStatementSpecific.setString(2, ((BankCustomer) userDetailsSpecific).getPanNumber());
							insertStatementSpecific.setString(3,
									((BankCustomer) userDetailsSpecific).getAadharNumber());
						}
						insertStatementSpecific.addBatch();
					}
				}

				int[] specificAccountResult = insertStatementSpecific.executeBatch();

				return CommonDatabaseUtil.checkElementsNonZero(userAccountResult)
						&& CommonDatabaseUtil.checkElementsNonZero(specificAccountResult);
			}
		} catch (SQLException e) {
			throw new CustomException("Exception occurred while creating user : ", e);
		}
	}

	@Override
	public int userValidation(long checkUserId, String password) throws CustomException {
		try {
			GlobalCommonChecker.checkNull(password);

			String query = "SELECT * FROM ZohoBankUser WHERE USER_ID = ? AND PASSWORD = ? AND STATUS = ? ";

			try (Connection connection = ConnectionCreation.getConnection();
					PreparedStatement loginStatement = connection.prepareStatement(query)) {

				loginStatement.setLong(1, checkUserId);
				loginStatement.setString(2, password);
				loginStatement.setInt(3, 1);
				try (ResultSet resultSet = loginStatement.executeQuery()) {
					if (resultSet.next()) {
						CurrentUser.setUserId(checkUserId);
						return resultSet.getInt("USER_TYPE");
					}
					return 0;
				}
			}
		} catch (SQLException e) {
			throw new CustomException("Error occurred in the login process: ", e);
		}
	}

	@Override
	public boolean isAdmin(long checkUserId) throws CustomException {
		try {
			String query = "SELECT * FROM BranchEmployee WHERE EMP_ID = ? ";

			try (Connection connection = ConnectionCreation.getConnection();
					PreparedStatement loginStatement = connection.prepareStatement(query)) {

				loginStatement.setLong(1, checkUserId);
				try (ResultSet resultSet = loginStatement.executeQuery()) {
					if (resultSet.next()) {
						int access = resultSet.getInt("ACCESS");
						return access == 1;
					}
					return false;
				}
			}
		} catch (SQLException e) {
			throw new CustomException("Error occurred in the fetching Employee credentials: ", e);
		}
	}

	@Override
	public <K, V> boolean updateUser(long userId, Map<K, V> fieldAndValue) throws CustomException {
		try {
			GlobalCommonChecker.checkNull(fieldAndValue);

			String setFields = CommonDatabaseUtil.userUpdateQueryBuilder(fieldAndValue);
			String updateQuery = "update ZohoBankUser set " + setFields + " where USER_ID = ?";

			try (Connection connection = ConnectionCreation.getConnection();
					PreparedStatement updatePreparedStatement = connection.prepareStatement(updateQuery)) {
				updatePreparedStatement.setLong(1, userId);
				return updatePreparedStatement.executeUpdate() != 0;
			}
		} catch (SQLException e) {
			throw new CustomException("Error Occured in Blocking the User : ", e);
		}
	}

	@Override
	public Map<Long, BankCustomer> getUserDetails(int status, int limit, int offset) throws CustomException {
		Map<Long, BankCustomer> resultUserData = new HashMap<>();

		String query = "SELECT * FROM ZohoBankUser U " + "JOIN BankCustomer C ON U.USER_ID = C.CUSTOMER_ID "
				+ "WHERE STATUS = ? LIMIT ? OFFSET ?";

		try {
			try (Connection connection = ConnectionCreation.getConnection();
					PreparedStatement getBranchStatement = connection.prepareStatement(query)) {
				getBranchStatement.setInt(1, status);
				getBranchStatement.setInt(2, limit);
				getBranchStatement.setInt(3, offset);
				try (ResultSet resultSet = getBranchStatement.executeQuery()) {

					while (resultSet.next()) {
						BankCustomer bankCustomerDetails = new BankCustomer();
						long userId = resultSet.getLong("USER_ID");
						bankCustomerDetails.setUserId(userId);
						bankCustomerDetails.setEmail(resultSet.getString("EMAIL"));
						bankCustomerDetails.setPhonenumber(resultSet.getString("PHONE_NO"));
						bankCustomerDetails.setName(resultSet.getString("NAME"));
						bankCustomerDetails.setGender(resultSet.getString("GENDER"));
						bankCustomerDetails.setAddress(resultSet.getString("ADDRESS"));
						bankCustomerDetails.setPanNumber(resultSet.getString("PAN"));
						bankCustomerDetails.setAadharNumber(resultSet.getString("AADHAR"));

						resultUserData.put(userId, bankCustomerDetails);
					}
				}
			}
			return resultUserData;
		} catch (SQLException e) {
			throw new CustomException("Error occured while getting Branch Data", e);
		}
	}

}
