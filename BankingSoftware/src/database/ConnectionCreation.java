package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import handleError.CustomException;

public class ConnectionCreation {
	
	@SuppressWarnings("unused")
	private static volatile ConnectionCreation connectionCreation;
	private static  volatile Connection connection;
	private String URL;
	private String USER ;
	private String PASSWORD;
	
	private ConnectionCreation() throws CustomException {
		URL = "jdbc:mysql://localhost:3306/ZohoBank";
		USER = "root";
		PASSWORD = "Hemanth@123";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(URL,USER,PASSWORD);
		} catch (SQLException | ClassNotFoundException e) {
			throw new CustomException("Error occured in connection generation",e);
		}
	}
	
	public static Connection getConnection() throws CustomException {
		
		if(connection == null) {
			synchronized (ConnectionCreation.class) {
				if(connection == null) {
					connectionCreation = new ConnectionCreation();
				}
			}
		}
		return connection;
	}
	
	public static void closeConnection() {
		connectionCreation = null;
		connection = null;
	}
 	
}
