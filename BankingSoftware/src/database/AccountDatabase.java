package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.structure.BankAccount;
import database.structure.BankBranch;
import database.structure.BankCustomer;
import database.structure.BankTransaction;
import globalUtilities.CustomException;
import globalUtilities.GlobalChecker;

public class AccountDatabase implements IAccountData {

	@Override
	public boolean createBankAccount(BankCustomer bankCustomerDetails, BankBranch branchDetails, int accountType)
			throws CustomException {
		try {
			GlobalChecker.checkNull(bankCustomerDetails);
			GlobalChecker.checkNull(branchDetails);

			if (isAccountAlreadyExists(bankCustomerDetails.getUserId(), branchDetails.getBranchId(), accountType)) {
				throw new CustomException(
						" Account with the same account type already exists for the user in the branch ");
			}

			String insertQueryUser = "insert into ZohoBankAccount(ACCOUNT_NO,BRANCH_ID,USER_ID,ACCOUNT_TYPE)values(?,?,?,?)";

			try (Connection connection = ConnectionCreation.getConnection();
					PreparedStatement accountInsertStatement = connection.prepareStatement(insertQueryUser)) {
				accountInsertStatement.setLong(1, GlobalChecker.generateUniqueAccountNumber(10));
				accountInsertStatement.setInt(2, branchDetails.getBranchId());
				accountInsertStatement.setInt(3, bankCustomerDetails.getUserId());
				accountInsertStatement.setInt(4, accountType);

				return accountInsertStatement.executeUpdate() != 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CustomException("Exception occured while creating user Account ", e);
		}
	}

	private boolean isAccountAlreadyExists(int userId, int branchId, int accountType) throws CustomException {
		String selectQuery = "SELECT COUNT(*) FROM ZohoBankAccount WHERE USER_ID = ? AND BRANCH_ID = ? AND ACCOUNT_TYPE = ?";

		try (Connection connection = ConnectionCreation.getConnection();
				PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
			selectStatement.setInt(1, userId);
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
	public Map<Long, BankAccount> getAccountWithBranch(int userId, int status, int branchId) throws CustomException {
		GlobalChecker.checkNull(status);

		String query = "SELECT * FROM ZohoBankAccount Z JOIN BranchData B ON Z.BRANCH_ID = B.BRANCH_ID "
				+ "WHERE Z.USER_ID = ? AND Z.STATUS = ?";

		if (branchId != -1) {
			query += " AND Z.BRANCH_ID = ?";
		}

		Map<Long, BankAccount> resultAccountData = new HashMap<>();

		try (Connection connection = ConnectionCreation.getConnection();
				PreparedStatement getAccountStatement = connection.prepareStatement(query)) {
			getAccountStatement.setInt(1, userId);
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
					bankAccountDetails.setUserId(resultSet.getInt("USER_ID"));
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
	public <K, V> boolean updateAccount(long accountNo, int userId, Map<K, V> fieldWithValue) throws CustomException {
		try {
			GlobalChecker.checkNull(fieldWithValue);

			String fieldSet = GlobalChecker.userUpdateQueryBuilder(fieldWithValue);
			String updateQuery = "update ZohoBankAccount set " + fieldSet + " where ACCOUNT_NO = ?";
			String updateUserStatusQuery = "UPDATE ZohoBankUser SET STATUS = ? WHERE USER_ID = ?";

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
						updateUserStatusStatement.setInt(2, userId);
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

						resultAccountDetails.setUserId(resultSet.getInt("USER_ID"));
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

	@SuppressWarnings("unused")
	@Override
	public boolean storeTransaction(BankTransaction bankTransactionDetails) throws CustomException {
		try {
			GlobalChecker.checkNull(bankTransactionDetails);

			Long transactorAccNo = bankTransactionDetails.getTransactorAccountNumber();
			String transactQuery;

			if (transactorAccNo != null) {
				transactQuery = "INSERT INTO BankTransaction (TRANS_ID,TRANS_TIMESTAMP,USER_ID,ACCOUNT_NO,AMOUNT,"
						+ "TYPE,RUNNING_BALANCE,DESCRIPTION,TRANSACTOR_ACCOUNT_NO,STATUS) VALUES(?,?,?,?,?,?,?,?,?,?)";
			} else {
				transactQuery = "INSERT INTO BankTransaction (TRANS_ID,TRANS_TIMESTAMP,USER_ID,ACCOUNT_NO,AMOUNT,"
						+ "TYPE,RUNNING_BALANCE,DESCRIPTION,STATUS) VALUES(?,?,?,?,?,?,?,?,?)";
			}

			try (Connection connection = ConnectionCreation.getConnection();
					PreparedStatement insertTransactStatement = connection.prepareStatement(transactQuery)) {
				insertTransactStatement.setString(1, bankTransactionDetails.getTransactionId());
				insertTransactStatement.setLong(2, bankTransactionDetails.getTransactionTimestamp());
				insertTransactStatement.setInt(3, bankTransactionDetails.getUserId());
				insertTransactStatement.setLong(4, bankTransactionDetails.getAccountNumber());
				insertTransactStatement.setDouble(5, bankTransactionDetails.getAmount());
				insertTransactStatement.setInt(6, bankTransactionDetails.getPaymentType());
				insertTransactStatement.setDouble(7, bankTransactionDetails.getCurrentBalance());
				insertTransactStatement.setString(8, bankTransactionDetails.getDecription());

				if (transactorAccNo != null) {
					insertTransactStatement.setLong(9, bankTransactionDetails.getTransactorAccountNumber());
					insertTransactStatement.setInt(10, bankTransactionDetails.getStatus());
				} else {
					insertTransactStatement.setInt(9, bankTransactionDetails.getStatus());
				}
				return insertTransactStatement.executeUpdate() != 0;
			}
		} catch (SQLException e) {
			throw new CustomException("Error Occured in Updating Transaction Details", e);
		}
	}

	@Override
	public List<BankTransaction> getTransactDetailsWithinPeriod(long accountNo, long startDate, long endDate)
			throws CustomException {
		try {
			String getQuery = "SELECT * FROM BankTransaction WHERE ACCOUNT_NO = ? AND TRANS_TIMESTAMP >= ? AND TRANS_TIMESTAMP<= ? ;";
			List<BankTransaction> transactionList = new ArrayList<>();

			try (Connection connection = ConnectionCreation.getConnection();
					PreparedStatement fetchHistoryStatement = connection.prepareStatement(getQuery)) {
				fetchHistoryStatement.setLong(1, accountNo);
				fetchHistoryStatement.setLong(2, startDate);
				fetchHistoryStatement.setLong(3, endDate);
				try (ResultSet transactionSet = fetchHistoryStatement.executeQuery()) {

					while (transactionSet.next()) {
						BankTransaction transactionHistory = new BankTransaction();
						transactionHistory.setTransactionId(transactionSet.getString("TRANS_ID"));
						transactionHistory.setTransactionTimestamp(transactionSet.getLong("TRANS_TIMESTAMP"));
						transactionHistory.setAccountNumber(transactionSet.getLong("ACCOUNT_NO"));
						transactionHistory.setAmount(transactionSet.getDouble("AMOUNT"));
						transactionHistory.setPaymentType(transactionSet.getInt("TYPE"));
						transactionHistory.setCurrentBalance(transactionSet.getDouble("RUNNING_BALANCE"));
						transactionHistory.setStatus(transactionSet.getInt("STATUS"));
						transactionHistory.setDescription(transactionSet.getString("DESCRIPTION"));
						transactionHistory.setTransactorAccountNumber(transactionSet.getLong("TRANSACTOR_ACCOUNT_NO"));

						transactionList.add(transactionHistory);
					}
				}
			}
			return transactionList;
		} catch (SQLException e) {
			throw new CustomException("Error Occured in fetch transaction Details", e);
		}
	}

}
