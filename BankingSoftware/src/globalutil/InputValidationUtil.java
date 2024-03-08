package globalutil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidationUtil {

	public static boolean isValidPassword(String password) {
		if (password == null) {
			return false;
		}
		String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{" + 8 + ",}$";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(password).matches();
	}

	public static boolean isValidEmail(String email) {
		if (email == null) {
			return false;
		}

		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}

	public static boolean isValidIndianPhoneNumber(String phoneNumber) {
		if (phoneNumber == null) {
			return false;
		}
		String indianPhoneNumberRegex = "^(\\+91[\\-\\s]?)?[0]?[6789]\\d{9}$";

		Pattern pattern = Pattern.compile(indianPhoneNumberRegex);
		Matcher matcher = pattern.matcher(phoneNumber);

		return matcher.matches();
	}
}
