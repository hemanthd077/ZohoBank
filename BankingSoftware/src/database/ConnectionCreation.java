package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import globalUtilities.CustomException;

public class ConnectionCreation {

	private static class SingletonHelper {
		private static ConnectionCreation INSTANCE;

		static {
			try {
				INSTANCE = new ConnectionCreation();
			} catch (CustomException e) {
				throw new RuntimeException("Error occurred in connection generation", e);
			}
		}
	}

	private Connection connection;
	private String URL;
	private String USER;
	private String PASSWORD;

	private ConnectionCreation() throws CustomException {
		URL = "jdbc:mysql://localhost:3306/ZohoBank";
		USER = "root";
		PASSWORD = "Hemanth@123";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (SQLException | ClassNotFoundException e) {
			throw new CustomException("Error occurred in connection generation", e);
		}
	}

	public static Connection getConnection() throws CustomException {
		return SingletonHelper.INSTANCE.connection;
	}

	public static void closeConnection() {
		SingletonHelper.INSTANCE = null;
	}
}
