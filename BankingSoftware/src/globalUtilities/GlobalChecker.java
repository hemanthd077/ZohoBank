package globalUtilities;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GlobalChecker {
	// null check
	public static void checkNull(Object input) throws CustomException {
		if (input == null) {
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
			logger.setLevel(Level.FINEST);
			coloredConsoleHandler.setLevel(Level.FINEST);
			logger.setUseParentHandlers(false);
		} catch (SecurityException e) {
			throw new CustomException("Error occured in loggerHandler", e);
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

	public static <K, V> String userUpdateQueryBuilder(Map<K, V> keyWithValue) throws CustomException {
		GlobalChecker.checkNull(keyWithValue);

		return keyWithValue.entrySet().stream().map(entry -> entry.getKey() + " = " + entry.getValue())
				.collect(Collectors.joining(", "));
	}

	public static Long generateUniqueAccountNumber(int length) {
		long timestamp = System.currentTimeMillis();
		int randomNumber = new Random().nextInt(1000000000);

		String accountNumber = "" + timestamp + randomNumber;
		if (accountNumber.length() > 12) {
			accountNumber = accountNumber.substring(0, 12);
		} else if (accountNumber.length() < 12) {
			int paddingLength = 12 - accountNumber.length();
			String padding = "0".repeat(paddingLength);
			accountNumber = accountNumber + padding;
		}

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

	public static long convertDateTimeToMillis(String dateString) {
		LocalDate localDate = LocalDate.parse(dateString);
		return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static long calculateNDayMills(int days) {
		return (days * 24L * 60L * 60L * 1000L);
	}

}
