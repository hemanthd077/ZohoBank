package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.dbutils.CommonDatabaseUtil;
import database.structure.BankCustomer;
import database.structure.CurrentUser;
import globalutil.CustomException;

public class CustomerDatabase implements ICustomerData {

	@Override
	public BankCustomer getCustomerData() throws CustomException {
		try {
			String query = "SELECT U.*, C.*" + "FROM ZohoBankUser U "
					+ "LEFT JOIN BankCustomer C ON U.USER_ID = C.CUSTOMER_ID " + "WHERE U.USER_ID = ?";

			BankCustomer bankCustomerDetails = new BankCustomer();
			try (Connection connection = ConnectionCreation.getConnection();
					PreparedStatement loginStatement = connection.prepareStatement(query)) {

				loginStatement.setLong(1, CurrentUser.getUserId());
				try (ResultSet resultSet = loginStatement.executeQuery()) {
					if (resultSet.next()) {

						bankCustomerDetails.setUserId(resultSet.getLong("USER_ID"));
						bankCustomerDetails.setEmail(resultSet.getString("Email"));
						bankCustomerDetails.setPhonenumber(resultSet.getString("PHONE_NO"));
						bankCustomerDetails.setName(resultSet.getString("NAME"));
						if (CommonDatabaseUtil.columnExists(resultSet, "DOB")) {
							bankCustomerDetails.setDateOfBirth(resultSet.getLong("DOB"));
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
