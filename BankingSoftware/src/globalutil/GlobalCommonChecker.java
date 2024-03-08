package globalutil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class GlobalCommonChecker {
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

	public static String generateTransactionId() {
		long timestamp = System.currentTimeMillis();
		UUID uuid = UUID.randomUUID();
		String transactionId = timestamp + "_" + uuid.toString();
		return transactionId;
	}
}
