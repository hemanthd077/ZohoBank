package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import database.dbutils.AccountUtils;
import database.dbutils.CommonDatabaseUtil;
import database.structure.BankAccount;
import database.structure.BankBranch;
import database.structure.BankCustomer;
import globalutil.CustomException;
import globalutil.GlobalCommonChecker;

public class AccountDatabase implements IAccountData {

	@Override
	public boolean createBankAccount(BankCustomer bankCustomerDetails, BankBranch branchDetails, int accountType)
			throws CustomException {
		try {
			GlobalCommonChecker.checkNull(bankCustomerDetails);
			GlobalCommonChecker.checkNull(branchDetails);

			if (isAccountAlreadyExists(bankCustomerDetails.getUserId(), branchDetails.getBranchId(), accountType)) {
				throw new CustomException(
						" Account with the same account type already exists for the user in the branch ");
			}

			String insertQueryUser = "insert into ZohoBankAccount(ACCOUNT_NO,BRANCH_ID,USER_ID,ACCOUNT_TYPE)values(?,?,?,?)";

			try (Connection connection = ConnectionCreation.getConnection();
					PreparedStatement accountInsertStatement = connection.prepareStatement(insertQueryUser)) {
				accountInsertStatement.setLong(1, AccountUtils.generateUniqueAccountNumber(10));
				accountInsertStatement.setInt(2, branchDetails.getBranchId());
				accountInsertStatement.setLong(3, bankCustomerDetails.getUserId());
				accountInsertStatement.setInt(4, accountType);

				return accountInsertStatement.executeUpdate() != 0;
			}
		} catch (SQLException e) {
			throw new CustomException("Exception occured while creating user Account ", e);
		}
	}

	private boolean isAccountAlreadyExists(long userId, int branchId, int accountType) throws CustomException {
		String selectQuery = "SELECT COUNT(*) FROM ZohoBankAccount WHERE USER_ID = ? AND BRANCH_ID = ? AND ACCOUNT_TYPE = ?";

		try (Connection connection = ConnectionCreation.getConnection();
				PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
			selectStatement.setLong(1, userId);
			selectStatement.setInt(2, branchId);
			selectStatement.setInt(3, accountType);

			try (ResultSet resultSet = selectStatement.executeQuery()) {
				resultSet.next();
				int count = resultSet.getInt(1);
				return count > 0;
			}
		} catch (SQLException e) {
			throw new CustomException("error in Checking existing bank account");
		}
	}

	@Override
	public Map<Long, BankAccount> getAccountWithBranch(long userId, int status, int branchId) throws CustomException {
		GlobalCommonChecker.checkNull(status);

		StringBuilder query = new StringBuilder();
		query.append(
				"SELECT * FROM ZohoBankAccount Z JOIN BranchData B ON Z.BRANCH_ID = B.BRANCH_ID WHERE Z.USER_ID = ? AND Z.STATUS = ?");

		if (branchId != -1) {
			query.append(" AND Z.BRANCH_ID = ?");
		}

		Map<Long, BankAccount> resultAccountData = new HashMap<>();

		try (Connection connection = ConnectionCreation.getConnection();
				PreparedStatement getAccountStatement = connection.prepareStatement(query.toString())) {
			getAccountStatement.setLong(1, userId);
			getAccountStatement.setInt(2, status);
			if (branchId != -1) {
				getAccountStatement.setInt(3, branchId);
			}

			try (ResultSet resultSet = getAccountStatement.executeQuery()) {

				while (resultSet.next()) {
					BankAccount bankAccountDetails = new BankAccount();
					long accountNo = resultSet.getLong("ACCOUNT_NO");
					bankAccountDetails.setAccountNo(accountNo);
					bankAccountDetails.setBalance(resultSet.getDouble("BALANCE"));
					bankAccountDetails.setUserId(resultSet.getLong("USER_ID"));
					bankAccountDetails.setAccountType(resultSet.getInt("ACCOUNT_TYPE"));

					BankBranch branchDetails = new BankBranch();
					branchDetails.setIfsc(resultSet.getString("IFSC"));
					branchDetails.setCity(resultSet.getString("CITY"));
					branchDetails.setState(resultSet.getString("STATE"));
					branchDetails.setAddress(resultSet.getString("ADDRESS"));
					bankAccountDetails.setBankBranch(branchDetails);

					resultAccountData.put(accountNo, bankAccountDetails);
				}
			}
		} catch (SQLException e) {
			throw new CustomException("Error Occured in fetching the User Account : ", e);
		}

		return resultAccountData;
	}

	@Override
	public <K, V> boolean updateAccount(long accountNo, long userId, Map<K, V> fieldWithValue) throws CustomException {
		try {
			GlobalCommonChecker.checkNull(fieldWithValue);

			String fieldSet = CommonDatabaseUtil.userUpdateQueryBuilder(fieldWithValue);
			String updateQuery = "update ZohoBankAccount set " + fieldSet + " where ACCOUNT_NO = ? ";
			String updateUserStatusQuery = "UPDATE ZohoBankUser SET STATUS = ? WHERE USER_ID = ? ";

			try (Connection connection = ConnectionCreation.getConnection();
					PreparedStatement updatePreparedStatement = connection.prepareStatement(updateQuery)) {
				updatePreparedStatement.setLong(1, accountNo);
				updatePreparedStatement.addBatch();

				int updateResult = updatePreparedStatement.executeUpdate();

				if (fieldWithValue.containsKey("STATUS") && (int) fieldWithValue.get("STATUS") == 0) {
					try (PreparedStatement updateUserStatusStatement = connection
							.prepareStatement(updateUserStatusQuery)) {
						int resultValue = (updateResult == 0) ? 0 : 1;
						updateUserStatusStatement.setInt(1, resultValue);
						updateUserStatusStatement.setLong(2, userId);
						updateUserStatusStatement.executeUpdate();
					}
				}
				return updateResult != 0;
			}
		} catch (SQLException e) {
			throw new CustomException("Error Occured in Blocking the User : ", e);
		}
	}

	@Override
	public BankAccount getAccountData(long accountNo, int status) throws CustomException {
		try {
			String transactQuery = "SELECT * FROM ZohoBankAccount WHERE ACCOUNT_NO = ? AND STATUS = ?";

			BankAccount resultAccountDetails = new BankAccount();

			try (Connection connection = ConnectionCreation.getConnection();
					PreparedStatement preparedStatement = connection.prepareStatement(transactQuery)) {
				preparedStatement.setLong(1, accountNo);
				preparedStatement.setInt(2, status);

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next()) {

						resultAccountDetails.setUserId(resultSet.getLong("USER_ID"));
						resultAccountDetails.setAccountNo(resultSet.getLong("ACCOUNT_NO"));
						resultAccountDetails.setBalance(resultSet.getDouble("BALANCE"));
						resultAccountDetails.setStatus(resultSet.getInt("STATUS"));
						resultAccountDetails.setAccountType(resultSet.getInt("ACCOUNT_TYPE"));
					}
				}
				return resultAccountDetails;
			}
		} catch (SQLException e) {
			throw new CustomException("Error occured in geting Account Status : ", e);
		}
	}

}
