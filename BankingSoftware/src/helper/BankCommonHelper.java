package helper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import globalUtilities.GlobalChecker;
import handleError.CustomException;

public class BankCommonHelper {

	public <K,V> boolean checkContainsKey(Map<K,V> inputMap,K inputCheck) {
		return inputMap.containsKey(inputCheck);
	}
	
	public static boolean checkElementsNonZero(int[] array) {
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
	
	
	public static boolean columnExists(ResultSet resultSet, String columnName) {
	    try {
	        resultSet.findColumn(columnName);
	        return true;
	    } catch (SQLException e) {
	        return false;
	    }
	}
}
