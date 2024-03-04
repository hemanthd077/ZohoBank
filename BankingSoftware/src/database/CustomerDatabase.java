package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.structureClasses.BankCustomer;
import globalUtilities.CustomException;
import globalUtilities.GlobalChecker;

public class CustomerDatabase implements ICustomerData {

	Connection connection;

	public CustomerDatabase() throws CustomException {
		try {
			connection = ConnectionCreation.getConnection();
		} catch (CustomException e) {
			throw new CustomException("Failed to Connect Customer Database");
		}
	}

	@Override
	public BankCustomer getCustomerData() throws CustomException {
		try {
			String query = "SELECT U.*, C.*" + "FROM ZohoBankUser U "
					+ "LEFT JOIN BankCustomer C ON U.USER_ID = C.CUSTOMER_ID " + "WHERE U.USER_ID = ?";

			BankCustomer bankCustomerDetails = new BankCustomer();
			try (PreparedStatement loginStatement = connection.prepareStatement(query)) {

				loginStatement.setInt(1, UserDatabase.userId);
				try (ResultSet resultSet = loginStatement.executeQuery()) {
					if (resultSet.next()) {
						bankCustomerDetails.setUserId(resultSet.getInt("USER_ID"));
						bankCustomerDetails.setEmail(resultSet.getString("Email"));
						bankCustomerDetails.setPhonenumber(resultSet.getString("PHONE_NO"));
						bankCustomerDetails.setName(resultSet.getString("NAME"));
						if (GlobalChecker.columnExists(resultSet, "DOB")) {
							bankCustomerDetails.setDateOfBirth(resultSet.getString("DOB"));
						}
						bankCustomerDetails.setGender(resultSet.getString("GENDER"));
						bankCustomerDetails.setAddress(resultSet.getString("ADDRESS"));
						bankCustomerDetails.setPanNumber(resultSet.getString("PAN"));
						bankCustomerDetails.setAadharNumber(resultSet.getString("AADHAR"));
					}
				}
			}
			return bankCustomerDetails;
		} catch (SQLException e) {
			throw new CustomException("Error occurred in the login process: ", e);
		}
	}
}
