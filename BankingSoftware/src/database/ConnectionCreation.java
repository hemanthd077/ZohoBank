package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import globalutil.CustomException;

public class ConnectionCreation {

	private static final String URL = "jdbc:mysql://localhost:3306/ZohoBank";
	private static final String USER = "root";
	private static final String PASSWORD = "Hemanth@123";

	public static Connection getConnection() throws CustomException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (SQLException | ClassNotFoundException e) {
			throw new CustomException("Error occurred in connection generation", e);
		}
	}

	public static void closeConnection(Connection connection) throws CustomException {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new CustomException("error while Closing Connection");
			}
		}
	}
}
