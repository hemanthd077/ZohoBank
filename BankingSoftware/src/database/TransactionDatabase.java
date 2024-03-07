package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import database.structure.BankAccount;
import database.structure.BankTransaction;
import globalUtilities.CustomException;
import helper.enumFiles.ExceptionStatus;
import helper.enumFiles.RecordStatus;

public class TransactionDatabase implements ITransactionData {

	@Override
	public int withdrawOrDepositTransaction(BankAccount bankAccount, BankTransaction bankTransaction)
			throws CustomException {
		Connection connection = null;

		try {
			connection = ConnectionCreation.getConnection();

			connection.setAutoCommit(false);
			updateAccountBalance(connection, bankAccount);
			updateTransaction(connection, bankTransaction);
			connection.commit();
			return 1;
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
		return 0;
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
	public int netBankingTransactionSameBank(BankAccount bankAccount1, BankAccount bankAccount2,
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
			return 1;
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
		return 0;
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

}
