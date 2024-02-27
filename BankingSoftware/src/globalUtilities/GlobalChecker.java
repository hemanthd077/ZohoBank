package globalUtilities;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import handleError.CustomException;

public class GlobalChecker {
	//null check
	public static void checkNull(Object input) throws CustomException{
		if(input == null){
			throw new CustomException("The NULL Pointer Exception Occured, Input is NULL");
		}
	}
	
	public static void loggerHandler() throws CustomException {
		try {
			Logger logger = Logger.getGlobal();
			checkNull(logger);
			ColoredConsoleHandler coloredConsoleHandler = new ColoredConsoleHandler();
	        logger.addHandler(coloredConsoleHandler);
			
			coloredConsoleHandler.setFormatter(new Formatter() {
				
				@Override
				public String format(LogRecord record) {
					
					return record.getMessage();
				}
			});
	        logger.setUseParentHandlers(false);
		}
	    catch (SecurityException e) {
			throw new CustomException("Error occured in loggerHandler",e);
		}
	}
	

    public static String hashPassword(String password) {
        try {
        	MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    public <K,V> boolean checkContainsKey(Map<K,V> inputMap,K inputCheck) {
		return inputMap.containsKey(inputCheck);
	}
	
	public static boolean checkElementsNonZero(int[] array) throws CustomException {
		GlobalChecker.checkNull(array);
		
	    for (int value : array) {
	        if (value == 0) {
	            return false;
	        }
	    }
	    return true;
	}
	
	public static String generateTransactionId() {
        long timestamp = System.currentTimeMillis();
        UUID uuid = UUID.randomUUID();
        String transactionId = timestamp + "_" + uuid.toString();
        return transactionId;
    }

	public static Long getCurrentTimeMills() {
		return System.currentTimeMillis();
	}
	
	public static String stringToPattern(List<String> inputStringList,String pattern)  throws CustomException{
		GlobalChecker.checkNull(pattern);
		GlobalChecker.checkNull(inputStringList);
		
		return inputStringList.stream().map(String::toUpperCase)
                .collect(Collectors.joining(pattern)); 
	}
	
	public static <K,V> String userUpdateQueryBuilder(Map<K,V> keyWithValue)  throws CustomException{
		GlobalChecker.checkNull(keyWithValue);
		
		return keyWithValue.entrySet().stream()
		            .map(entry -> entry.getKey() + " = " + entry.getValue())
		            .collect(Collectors.joining(", "));
	}
	
	public static Long generateUniqueAccountNumber(int length) {
		String accountNumber;
		do {
			long timestamp = System.currentTimeMillis();
			int randomPart = new Random().nextInt(9000) + 1000;
	        accountNumber = String.valueOf(timestamp) + String.valueOf(randomPart);
		}
		while(accountNumber.length() == 12);

		return Long.parseLong(accountNumber);
	}
	
	
	public static boolean columnExists(ResultSet resultSet, String columnName) throws CustomException {
	    try {
	    	GlobalChecker.checkNull(columnName);
	    	GlobalChecker.checkNull(resultSet);
	    	
	        resultSet.findColumn(columnName);
	        return true;
	    } catch (SQLException e) {
	        return false;
	    }
	}
}
