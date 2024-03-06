package database.dbutils;

import java.util.Random;

public class AccountUtils {
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
}
