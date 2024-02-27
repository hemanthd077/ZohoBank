package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import database.structureClasses.BankCustomer;
import database.structureClasses.BankEmployee;
import database.structureClasses.BankUser;
import globalUtilities.GlobalChecker;
import handleError.CustomException;

public class BankUserDatabase implements IUserData {

	Connection connection;
	static int userId;

	public BankUserDatabase() {
		try {
			connection = ConnectionCreation.getConnection();
		} catch (CustomException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int userLogin(BankUser userDetails) throws CustomException {
	    try {
	    	GlobalChecker.checkNull(userDetails);
	        String query = "SELECT U.USER_ID,U.USER_TYPE,U.STATUS, B.ACCESS " +
	                       "FROM ZohoBankUser U " +
	                       "LEFT JOIN BranchEmployee B ON U.USER_ID = B.EMP_ID " +
	                       "WHERE U.PHONE_NO = ? AND U.PASSWORD = ?";

	        try (PreparedStatement loginStatement = connection.prepareStatement(query)) {

	            loginStatement.setString(1, userDetails.getPhonenumber());
	            loginStatement.setString(2, userDetails.getPassword());
	            try (ResultSet resultSet = loginStatement.executeQuery()) {
	                if (resultSet.next()) {
	                	if(resultSet.getInt("STATUS")==0) {
	                		return 0;
	                	}
	                	setUserId(resultSet.getInt("USER_ID"));
	                	int access = resultSet.getInt("ACCESS");
	                    int userType = resultSet.getInt("USER_TYPE");
	                    return (userType == 1) ? 1 : ((userType==2 && access==1)? 3 : 2);
	                } else {
	                    return 0;
	                }
	            }
	        }
	    } catch (SQLException e) {
	        throw new CustomException("Error occurred in the login process: ", e);
	    }
	}

	@Override
	public boolean validatePassword(BankUser userDetails) throws CustomException {
		try {
			String validateQuery = "SELECT USER_ID FROM ZohoBankUser WHERE USER_ID = ? AND PASSWORD = ?;";
			
			try(PreparedStatement validateStatement = connection.prepareStatement(validateQuery)){
				validateStatement.setInt(1, BankUserDatabase.getUserId());
	            validateStatement.setString(2, userDetails.getPassword());
	            
	            try (ResultSet resultSet = validateStatement.executeQuery()) {
	                if (resultSet.next()) {
	                    return true;
	                } else {
	                    return false;
	                }
	            }
			}
		}
		catch(SQLException e) {
			throw new CustomException("Error Occured while Validating Password",e);
		}
	}
	
	public static int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		BankUserDatabase.userId = userId;
	}
	
	@Override
	public boolean createBankUserOrEmployee(List<? extends BankUser> userDetails, boolean isEmployee) throws CustomException {
	    try {
	        GlobalChecker.checkNull(userDetails);

	        String insertQueryUser = "INSERT INTO ZohoBankUser(EMAIL, PASSWORD, PHONE_NO, NAME, GENDER, ADDRESS, USER_TYPE) VALUES(?,?,?,?,?,?,?)";
	        String insertQuerySpecific;

	        if (isEmployee) {
	            insertQuerySpecific = "INSERT INTO BranchEmployee(EMP_ID, ACCESS, BRANCH_ID) VALUES(?,?,?)";
	        } else {
	            insertQuerySpecific = "INSERT INTO BankCustomer(CUSTOMER_ID, PAN, AADHAR) VALUES(?,?,?)";
	        }

	        try (PreparedStatement insertStatementUser = connection.prepareStatement(insertQueryUser, Statement.RETURN_GENERATED_KEYS);
	             PreparedStatement insertStatementSpecific = connection.prepareStatement(insertQuerySpecific)) {

	            int accountCreationSize = userDetails.size();

	            for (int i = 0; i < accountCreationSize; i++) {
	                insertStatementUser.setString(1, userDetails.get(i).getEmail());
	                insertStatementUser.setString(2, userDetails.get(i).getPassword());
	                insertStatementUser.setString(3, userDetails.get(i).getPhonenumber());
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
	                        insertStatementSpecific.setInt(3, ((BankEmployee) userDetailsSpecific).getBranchDetails().getBranch_id());
	                    } else {
	                        insertStatementSpecific.setInt(1, generatedKeys.getInt(1));
	                        insertStatementSpecific.setString(2, ((BankCustomer) userDetailsSpecific).getPanNumber());
	                        insertStatementSpecific.setString(3, ((BankCustomer) userDetailsSpecific).getAadharNumber());
	                    }
	                    insertStatementSpecific.addBatch();
	                    i++;
	                }
	            }

	            int[] specificAccountResult = insertStatementSpecific.executeBatch();

	            return GlobalChecker.checkElementsNonZero(userAccountResult) &&
	            		GlobalChecker.checkElementsNonZero(specificAccountResult);
	        }
	    } catch (SQLException e) {
	        throw new CustomException("Exception occurred while creating user : ", e);
	    }
	}

}
