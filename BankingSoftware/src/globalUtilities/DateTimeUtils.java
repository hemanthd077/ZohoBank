package globalUtilities;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

	public static String convertMillsToDateTime(Long currentTimeMillis) {
		Instant instant = Instant.ofEpochMilli(currentTimeMillis);
		LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd  hh:mm:ss");

		return dateTime.format(dateTimeFormatter);
	}

	public static String convertMillsToDate(Long currentTimeMillis) {
		Instant instant = Instant.ofEpochMilli(currentTimeMillis);
		LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");

		return dateTime.format(dateTimeFormatter);
	}

	public static long convertDateTimeToMillis(String dateString) {
		LocalDate localDate = LocalDate.parse(dateString);
		return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static long calculateNDayMills(int days) {
		return (days * 24L * 60L * 60L * 1000L);
	}

	public static Long getCurrentTimeMills() {
		return System.currentTimeMillis();
	}
}
