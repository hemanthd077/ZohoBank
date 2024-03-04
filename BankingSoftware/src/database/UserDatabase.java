package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.structureClasses.BankCustomer;
import database.structureClasses.BankEmployee;
import database.structureClasses.BankUser;
import globalUtilities.CustomException;
import globalUtilities.GlobalChecker;

public class UserDatabase implements IUserData {

	Connection connection;

	public UserDatabase() throws CustomException {
		try {
			connection = ConnectionCreation.getConnection();
		} catch (CustomException e) {
			throw new CustomException("Failed to Connect User Database");
		}
	}

	static int userId;

	public static int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		UserDatabase.userId = userId;
	}

	@Override
	public boolean createUser(List<? extends BankUser> userDetails, boolean isEmployee) throws CustomException {
		try {
			GlobalChecker.checkNull(userDetails);

			String insertQueryUser = "INSERT INTO ZohoBankUser(EMAIL, PASSWORD, PHONE_NO, NAME, GENDER, ADDRESS, USER_TYPE) VALUES(?,?,?,?,?,?,?)";
			String insertQuerySpecific;

			if (isEmployee) {
				insertQuerySpecific = "INSERT INTO BranchEmployee(EMP_ID, ACCESS, BRANCH_ID) VALUES(?,?,?)";
			} else {
				insertQuerySpecific = "INSERT INTO BankCustomer(CUSTOMER_ID, PAN, AADHAR) VALUES(?,?,?)";
			}

			try (PreparedStatement insertStatementUser = connection.prepareStatement(insertQueryUser,
					Statement.RETURN_GENERATED_KEYS);
					PreparedStatement insertStatementSpecific = connection.prepareStatement(insertQuerySpecific)) {

				@SuppressWarnings("unchecked")
				int accountCreationSize = ((Map<String, Object>) userDetails).size();

				for (int i = 0; i < accountCreationSize; i++) {
					insertStatementUser.setString(1, userDetails.get(i).getEmail());
					insertStatementUser.setString(2, userDetails.get(i).getPassword());
					insertStatementUser.setString(3, userDetails.get(i).getPhoneNumber());
					insertStatementUser.setString(4, userDetails.get(i).getName());
					insertStatementUser.setString(5, userDetails.get(i).getGender());
					insertStatementUser.setString(6, userDetails.get(i).getAddress());
					insertStatementUser.setInt(7, isEmployee ? 2 : 1);
					insertStatementUser.addBatch();
				}

				int[] userAccountResult = insertStatementUser.executeBatch();

				try (ResultSet generatedKeys = insertStatementUser.getGeneratedKeys()) {
					int i = 0;
					while (generatedKeys.next()) {
						BankUser userDetailsSpecific = userDetails.get(i);
						if (isEmployee) {
							insertStatementSpecific.setInt(1, generatedKeys.getInt(1));
							insertStatementSpecific.setInt(2, ((BankEmployee) userDetailsSpecific).getEmployeeAccess());
							insertStatementSpecific.setInt(3,
									((BankEmployee) userDetailsSpecific).getBankBranch().getBranchId());
						} else {
							insertStatementSpecific.setInt(1, generatedKeys.getInt(1));
							insertStatementSpecific.setString(2, ((BankCustomer) userDetailsSpecific).getPanNumber());
							insertStatementSpecific.setString(3,
									((BankCustomer) userDetailsSpecific).getAadharNumber());
						}
						insertStatementSpecific.addBatch();
						i++;
					}
				}

				int[] specificAccountResult = insertStatementSpecific.executeBatch();

				return GlobalChecker.checkElementsNonZero(userAccountResult)
						&& GlobalChecker.checkElementsNonZero(specificAccountResult);
			}
		} catch (SQLException e) {
			throw new CustomException("Exception occurred while creating user : ", e);
		}
	}

	@Override
	public Map<String, Integer> userLogin(BankUser userDetails) throws CustomException {
		try {
			Map<String, Integer> map = new HashMap<String, Integer>();
			GlobalChecker.checkNull(userDetails);
			String query = "SELECT U.USER_ID,U.USER_TYPE,U.STATUS, B.ACCESS " + "FROM ZohoBankUser U "
					+ "LEFT JOIN BranchEmployee B ON U.USER_ID = B.EMP_ID " + "WHERE U.PHONE_NO = ? AND U.PASSWORD = ?";

			try (PreparedStatement loginStatement = connection.prepareStatement(query)) {

				loginStatement.setString(1, userDetails.getPhoneNumber());
				loginStatement.setString(2, userDetails.getPassword());
				try (ResultSet resultSet = loginStatement.executeQuery()) {
					if (resultSet.next()) {
						map.put("STATUS", (resultSet.getInt("STATUS") == 0 ? 0 : 1));
						setUserId(resultSet.getInt("USER_ID"));

						map.put("ACCESS", resultSet.getInt("ACCESS"));
						map.put("USER_TYPE", resultSet.getInt("USER_TYPE"));
					}
					return map;
				}
			}
		} catch (SQLException e) {
			throw new CustomException("Error occurred in the login process: ", e);
		}
	}

	@Override
	public <K, V, T> boolean updateUser(T customerDetails, Map<K, V> fieldAndValue) throws CustomException {
		try {
			GlobalChecker.checkNull(fieldAndValue);

			String setFields = GlobalChecker.userUpdateQueryBuilder(fieldAndValue);
			String updateQuery = "update ZohoBankUser set " + setFields + " where USER_ID = ?";

			try (PreparedStatement updatePreparedStatement = connection.prepareStatement(updateQuery)) {
				updatePreparedStatement.setInt(1, ((BankUser) customerDetails).getUserId());

				return updatePreparedStatement.executeUpdate() != 0;
			}
		} catch (SQLException e) {
			throw new CustomException("Error Occured in Blocking the User : ", e);
		}
	}

	@Override
	public boolean validatePassword(BankUser userDetails) throws CustomException {
		try {
			String validateQuery = "SELECT USER_ID FROM ZohoBankUser WHERE USER_ID = ? AND PASSWORD = ?;";

			try (PreparedStatement validateStatement = connection.prepareStatement(validateQuery)) {
				validateStatement.setInt(1, UserDatabase.getUserId());
				validateStatement.setString(2, userDetails.getPassword());

				try (ResultSet resultSet = validateStatement.executeQuery()) {
					if (resultSet.next()) {
						return true;
					} else {
						return false;
					}
				}
			}
		} catch (SQLException e) {
			throw new CustomException("Error Occured while Validating Password", e);
		}
	}

	@Override
	public Map<Integer, BankCustomer> getUserDetails(int status) throws CustomException {
		Map<Integer, BankCustomer> resultUserData = new HashMap<>();

		String query = "SELECT * FROM ZohoBankUser U "
				+ "JOIN BankCustomer C ON U.USER_ID = C.CUSTOMER_ID where STATUS = ?;";
		try {
			try (PreparedStatement getBranchStatement = connection.prepareStatement(query)) {
				getBranchStatement.setInt(1, status);
				try (ResultSet resultSet = getBranchStatement.executeQuery()) {

					int id = 1;
					while (resultSet.next()) {
						BankCustomer bankCustomerDetails = new BankCustomer();

						bankCustomerDetails.setUserId(resultSet.getInt("USER_ID"));
						bankCustomerDetails.setEmail(resultSet.getString("EMAIL"));
						bankCustomerDetails.setPhonenumber(resultSet.getString("PHONE_NO"));
						bankCustomerDetails.setName(resultSet.getString("NAME"));
						bankCustomerDetails.setGender(resultSet.getString("GENDER"));
						bankCustomerDetails.setAddress(resultSet.getString("ADDRESS"));
						bankCustomerDetails.setPanNumber(resultSet.getString("PAN"));
						bankCustomerDetails.setAadharNumber(resultSet.getString("AADHAR"));

						resultUserData.put(id++, bankCustomerDetails);
					}
				}
			}
			return resultUserData;
		} catch (SQLException e) {
			throw new CustomException("Error occured while getting Branch Data", e);
		}
	}

}
