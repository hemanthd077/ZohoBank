package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.structure.BankAccount;
import database.structure.BankTransaction;
import globalutil.CustomException;
import helper.enumfiles.ExceptionStatus;
import helper.enumfiles.RecordStatus;

public class TransactionDatabase implements ITransactionData {

	@Override
	public boolean withdrawOrDepositTransaction(BankAccount bankAccount, BankTransaction bankTransaction)
			throws CustomException {
		Connection connection = null;

		try {
			connection = ConnectionCreation.getConnection();

			connection.setAutoCommit(false);
			updateAccountBalance(connection, bankAccount);
			updateTransaction(connection, bankTransaction);
			connection.commit();
			return true;
		} catch (SQLException | CustomException e) {
			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (SQLException rollbackException) {
				throw new CustomException(ExceptionStatus.FAILEDTRANSACTION.getStatus(), rollbackException);
			}
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException closeException) {
				throw new CustomException(ExceptionStatus.FAILEDTRANSACTION.getStatus(), closeException);
			}
		}
		return false;
	}

	private void updateTransaction(Connection connection, BankTransaction bankTransaction) throws SQLException {
		
		String updateQuery = "INSERT INTO BankTransaction(TRANS_ID,TRANS_TIMESTAMP,USER_ID,ACCOUNT_NO,AMOUNT,TYPE,"
				+ "RUNNING_BALANCE,TRANSACTOR_ACCOUNT_NO,STATUS,DESCRIPTION) VALUES(?,?,?,?,?,?,?,?,?,?)";

		try (PreparedStatement updateTransactionStatement = connection.prepareStatement(updateQuery)) {

			updateTransactionStatement.setString(1, bankTransaction.getTransactionId());
			updateTransactionStatement.setLong(2, bankTransaction.getTransactionTimestamp());
			updateTransactionStatement.setLong(3, bankTransaction.getUserId());
			updateTransactionStatement.setLong(4, bankTransaction.getAccountNumber());
			updateTransactionStatement.setDouble(5, bankTransaction.getAmount());
			updateTransactionStatement.setInt(6, bankTransaction.getPaymentType());
			updateTransactionStatement.setDouble(7, bankTransaction.getCurrentBalance());
			updateTransactionStatement.setLong(8, bankTransaction.getTransactorAccountNumber());
			updateTransactionStatement.setInt(9, RecordStatus.ACTIVE.getCode());
			updateTransactionStatement.setString(10, bankTransaction.getDecription());
			updateTransactionStatement.executeUpdate();
		}
	}

	private void updateAccountBalance(Connection connection, BankAccount bankAccount) throws SQLException {
		String updateQuery = "UPDATE ZohoBankAccount SET BALANCE = ? WHERE ACCOUNT_NO = ? AND STATUS = ?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
			preparedStatement.setDouble(1, bankAccount.getBalance());
			preparedStatement.setLong(2, bankAccount.getAccountNo());
			preparedStatement.setInt(3, 1);
			preparedStatement.executeUpdate();
		}
	}

	@Override
	public boolean netBankingTransactionSameBank(BankAccount bankAccount1, BankAccount bankAccount2,
			BankTransaction bankTransaction1, BankTransaction bankTransaction2) throws CustomException {
		Connection connection = null;

		try {
			connection = ConnectionCreation.getConnection();

			connection.setAutoCommit(false);
			updateAccountBalance(connection, bankAccount1);
			updateAccountBalance(connection, bankAccount2);
			storeTransaction(connection, bankTransaction1);
			storeTransaction(connection, bankTransaction2);
			connection.commit();
			return true;
		} catch (SQLException | CustomException e) {
			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (SQLException rollbackException) {
				throw new CustomException(ExceptionStatus.FAILEDTRANSACTION.getStatus(), rollbackException);
			}
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException closeException) {
				throw new CustomException(ExceptionStatus.FAILEDTRANSACTION.getStatus(), closeException);
			}
		}
		return false;
	}

	private void storeTransaction(Connection connection, BankTransaction bankTransactionDetails) throws SQLException {

		String transactQuery = "INSERT INTO BankTransaction (TRANS_ID,TRANS_TIMESTAMP,USER_ID,ACCOUNT_NO,AMOUNT,"
				+ "TYPE,RUNNING_BALANCE,DESCRIPTION,TRANSACTOR_ACCOUNT_NO,STATUS) VALUES(?,?,?,?,?,?,?,?,?,?)";

		try (PreparedStatement insertTransactStatement = connection.prepareStatement(transactQuery)) {
			insertTransactStatement.setString(1, bankTransactionDetails.getTransactionId());
			insertTransactStatement.setLong(2, bankTransactionDetails.getTransactionTimestamp());
			insertTransactStatement.setLong(3, bankTransactionDetails.getUserId());
			insertTransactStatement.setLong(4, bankTransactionDetails.getAccountNumber());
			insertTransactStatement.setDouble(5, bankTransactionDetails.getAmount());
			insertTransactStatement.setInt(6, bankTransactionDetails.getPaymentType());
			insertTransactStatement.setDouble(7, bankTransactionDetails.getCurrentBalance());
			insertTransactStatement.setString(8, bankTransactionDetails.getDecription());
			insertTransactStatement.setLong(9, bankTransactionDetails.getTransactorAccountNumber());
			insertTransactStatement.setInt(10, bankTransactionDetails.getStatus().getCode());

			insertTransactStatement.executeUpdate();
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
						transactionHistory.setUserId(transactionSet.getLong("USER_ID"));
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
