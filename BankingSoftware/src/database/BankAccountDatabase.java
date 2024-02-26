package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.structureClasses.BankAccountDetails;
import database.structureClasses.BankCustomerDetails;
import database.structureClasses.BankTransactionDetails;
import database.structureClasses.BranchDetails;
import database.structureClasses.UserDetails;
import globalUtilities.GlobalChecker;
import handleError.CustomException;
import helper.BankCommonHelper;

public class BankAccountDatabase implements IBankAccount {

	Connection connection;
	
	public BankAccountDatabase() {
		try {
			connection = ConnectionCreation.getConnection();
		} catch (CustomException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean createBankAccount(BankCustomerDetails bankCustomerDetails,BranchDetails branchDetails) throws CustomException{
		try {
			GlobalChecker.checkNull(bankCustomerDetails);
			GlobalChecker.checkNull(branchDetails);
			
			String insertQueryUser = "insert into ZohoBankAccount(ACCOUNT_NO,BRANCH_ID,USER_ID)values(?,?,?)";
			
			try(PreparedStatement accountInsertStatement = connection.prepareStatement(insertQueryUser)){
					accountInsertStatement.setLong(1, BankCommonHelper.generateUniqueAccountNumber(10));
					accountInsertStatement.setInt(2, branchDetails.getBranch_id());
					accountInsertStatement.setInt(3, bankCustomerDetails.getUserId());
					
				return accountInsertStatement.executeUpdate()!=0;
			}
		} catch (SQLException e) {
			throw new CustomException("Exception occured while creating user : ",e);
		}
	}
	
	@Override
	public Map<Integer,BankAccountDetails> getAccountAllBranch(List<BankCustomerDetails> bankCustomerDetails,int status) throws CustomException{
		GlobalChecker.checkNull(bankCustomerDetails);
		GlobalChecker.checkNull(status);

		String query = "SELECT Z.ACCOUNT_NO, Z.BALANCE, Z.STATUS, Z.BRANCH_ID, Z.USER_ID, B.* "
		         + "FROM ZohoBankAccount Z "
		         + "JOIN BranchData B ON Z.BRANCH_ID = B.BRANCH_ID "
		         + "WHERE Z.USER_ID = ? AND Z.STATUS = ?";

		return fetchingAccountData(bankCustomerDetails, status, query,0);
	}
	
	@Override
	public Map<Integer,BankAccountDetails> getAccountSingleBranch(List<BankCustomerDetails> bankCustomerDetails,int status,int branchId) throws CustomException{
		GlobalChecker.checkNull(bankCustomerDetails);
        GlobalChecker.checkNull(status);
        
        String query = "SELECT Z.ACCOUNT_NO, Z.BALANCE, Z.STATUS, Z.BRANCH_ID, Z.USER_ID, B.* "
                     + "FROM ZohoBankAccount Z "
                     + "JOIN BranchData B ON Z.BRANCH_ID = B.BRANCH_ID "
                     + "WHERE Z.USER_ID = ? AND Z.STATUS = ?";

        if (branchId !=0) {
            query += " AND Z.BRANCH_ID = ?";
        }

		return fetchingAccountData(bankCustomerDetails, status, query,branchId);
	}
	
	
	private Map<Integer,BankAccountDetails> fetchingAccountData(List<BankCustomerDetails> bankCustomerDetails,int status,String query,int branchId) throws CustomException {
		
		Map<Integer,BankAccountDetails> resultAccountData = new HashMap<>();
		int listLength = bankCustomerDetails.size();
		
		try(PreparedStatement getAccountStatement = connection.prepareStatement(query)){
			
			for(int i=0;i<listLength;i++) {
				getAccountStatement.setInt(1, bankCustomerDetails.get(i).getUserId());
				getAccountStatement.setInt(2, status);
				if(branchId!=0) {
					getAccountStatement.setInt(3, branchId);
				}
				
				try(ResultSet resultSet = getAccountStatement.executeQuery()){
					
					int id=1;
					while(resultSet.next()) {
						BankAccountDetails bankAccountDetails = new BankAccountDetails();
						bankAccountDetails.setAccountNo(resultSet.getLong("ACCOUNT_NO"));
						bankAccountDetails.setBalance(resultSet.getDouble("BALANCE"));
						
						UserDetails userDetails = new UserDetails();
						userDetails.setUserId(resultSet.getInt("USER_ID"));
						bankAccountDetails.setUserDetails(userDetails);
						
						BranchDetails branchDetails = new BranchDetails();
						branchDetails.setIfsc(resultSet.getString("IFSC"));
						branchDetails.setCity(resultSet.getString("CITY"));
						branchDetails.setState(resultSet.getString("STATE"));
						branchDetails.setAddress(resultSet.getString("ADDRESS"));
						
						bankAccountDetails.setBranchDetails(branchDetails);
						resultAccountData.put(id++,bankAccountDetails);
					}
				}
			}
		}
		catch(SQLException e) {
			throw new CustomException("Error Occured in fetching the User Account : ",e);
		}
		return resultAccountData;
	}
	
	@Override
	public <K,V> boolean updateAccount(BankAccountDetails bankAccountDetails,Map<K,V> fieldWithValue) throws CustomException{
		try {
			GlobalChecker.checkNull(bankAccountDetails);
			GlobalChecker.checkNull(fieldWithValue);
			
			String fieldSet = BankCommonHelper.userUpdateQueryBuilder(fieldWithValue);
			String updateQuery = "update ZohoBankAccount set "+fieldSet+" where ACCOUNT_NO = ?";
			String updateUserStatusQuery = "UPDATE ZohoBankUser SET STATUS = ? WHERE USER_ID = ?";
			
			try(PreparedStatement updatePreparedStatement = connection.prepareStatement(updateQuery)){
					updatePreparedStatement.setLong(1,bankAccountDetails.getAccountNo());
					updatePreparedStatement.addBatch();
					
				int updateResult =  updatePreparedStatement.executeUpdate();
				
				if (fieldWithValue.containsKey("STATUS") && (int) fieldWithValue.get("STATUS") == 0) {
                    try (PreparedStatement updateUserStatusStatement = connection.prepareStatement(updateUserStatusQuery)) {
                    	int resultValue = (updateResult ==0) ? 0: 1;
                    	updateUserStatusStatement.setInt(1, resultValue);
                        updateUserStatusStatement.setInt(2, bankAccountDetails.getUserDetails().getUserId());
                        updateUserStatusStatement.executeUpdate();
                    }
	            }
				return updateResult!=0;
			}
		}
		catch(SQLException e) {
			throw new CustomException("Error Occured in Blocking the User : ",e);
		}
	}
	
	@Override
	public BankAccountDetails getAccountStatus(BankAccountDetails bankAccountDetails) throws CustomException{
		try {
			GlobalChecker.checkNull(bankAccountDetails);
			String transactQuery = "SELECT * FROM ZohoBankAccount WHERE ACCOUNT_NO = ? AND STATUS = ?";
	
			BankAccountDetails resultAccountDetails = new BankAccountDetails(); 
			
			try (PreparedStatement preparedStatement = connection.prepareStatement(transactQuery)) {
				preparedStatement.setLong(1,bankAccountDetails.getAccountNo());
				preparedStatement.setInt(2, bankAccountDetails.getStatus());
				
				try(ResultSet resultSet = preparedStatement.executeQuery()){
					if(resultSet.next()) {
						UserDetails details = new UserDetails();
						details.setUserId(resultSet.getInt("USER_ID"));
						resultAccountDetails.setUserDetails(details);
						resultAccountDetails.setAccountNo(resultSet.getLong("ACCOUNT_NO"));
						resultAccountDetails.setBalance(resultSet.getDouble("BALANCE"));
						resultAccountDetails.setStatus(resultSet.getInt("STATUS"));
					}
				}
				return resultAccountDetails;
			}
		}
		catch(SQLException e) {
			throw new CustomException("Error occured in geting Account Status : ",e);
		}
	}
	
	@Override
	public boolean transactionUpdate(BankTransactionDetails bankTransactionDetails) throws CustomException{
		try {
			GlobalChecker.checkNull(bankTransactionDetails);
			
			Long transactorAccNo = bankTransactionDetails.getTransactorAccountNumber();
			String transactQuery;
			
			if(transactorAccNo!=null) {
				transactQuery = "INSERT INTO BankTransaction (TRANS_ID,TRANS_TIMESTAMP,USER_ID,ACCOUNT_NO,AMOUNT,"
								+ "TYPE,RUNNING_BALANCE,TRANSACTOR_ACCOUNT_NO,STATUS) VALUES(?,?,?,?,?,?,?,?,?)";
			}
			else {
				transactQuery = "INSERT INTO BankTransaction (TRANS_ID,TRANS_TIMESTAMP,USER_ID,ACCOUNT_NO,AMOUNT,"
						+ "TYPE,RUNNING_BALANCE,STATUS) VALUES(?,?,?,?,?,?,?,?)";
			}
			
			try(PreparedStatement insertTransactStatement = connection.prepareStatement(transactQuery)){
				insertTransactStatement.setString(1, bankTransactionDetails.getTransactionId());
				insertTransactStatement.setLong(2, bankTransactionDetails.getTransactionTimestamp());
				insertTransactStatement.setInt(3, bankTransactionDetails.getUserId());
				insertTransactStatement.setLong(4, bankTransactionDetails.getAccountNumber());
				insertTransactStatement.setDouble(5, bankTransactionDetails.getAmount());
				insertTransactStatement.setInt(6,bankTransactionDetails.getPaymentType());
				insertTransactStatement.setDouble(7, bankTransactionDetails.getCurrentBalance());
				
				if(transactorAccNo!=null) {
					insertTransactStatement.setLong(8, bankTransactionDetails.getTransactorAccountNumber());
					insertTransactStatement.setInt(9, bankTransactionDetails.getStatus());
				}
				else {
					insertTransactStatement.setInt(8, bankTransactionDetails.getStatus());
				}
				return insertTransactStatement.executeUpdate()!=0;
			}
		}
		catch(SQLException e) {
			throw new CustomException("Error Occured in Updating Transaction Details",e);
		}
	}

	@Override
	public Map<Integer,BankTransactionDetails> getTransactDetails(BankAccountDetails bankAccountDetails) throws CustomException {
		GlobalChecker.checkNull(bankAccountDetails);
		try {
			String getQuery = "SELECT * FROM BankTransaction WHERE ACCOUNT_NO = ? ;";
			Map<Integer,BankTransactionDetails> transactionMap = new HashMap<>();
			
			try(PreparedStatement fetchHistoryStatement = connection.prepareStatement(getQuery)){
				fetchHistoryStatement.setLong(1,bankAccountDetails.getAccountNo());
				
				try(ResultSet transactionSet = fetchHistoryStatement.executeQuery()){
					int index=0;
					while(transactionSet.next()) {
						BankTransactionDetails transactionHistory = new BankTransactionDetails();
						transactionHistory.setTransactionId(transactionSet.getString("TRANS_ID"));
						transactionHistory.setTransactionTimestamp(transactionSet.getLong("TRANS_TIMESTAMP"));
						transactionHistory.setAccountNumber(transactionSet.getLong("ACCOUNT_NO"));
						transactionHistory.setAmount(transactionSet.getDouble("AMOUNT"));
						transactionHistory.setPaymentType(transactionSet.getInt("TYPE"));
						transactionHistory.setCurrentBalance(transactionSet.getDouble("RUNNING_BALANCE"));
						transactionHistory.setStatus(transactionSet.getInt("STATUS"));
						transactionHistory.setTransactorAccountNumber(transactionSet.getLong("TRANSACTOR_ACCOUNT_NO"));
						
						transactionMap.put(index++, transactionHistory);
					}
				}
			}
			return transactionMap;
		}
		catch(SQLException e) {
			throw new CustomException("Error Occured in fetch transaction Details",e);
		}
	}
	
}