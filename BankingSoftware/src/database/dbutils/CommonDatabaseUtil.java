package database.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

import globalutil.CustomException;
import globalutil.GlobalCommonChecker;

public class CommonDatabaseUtil {

	public static boolean checkElementsNonZero(int[] array) throws CustomException {
		GlobalCommonChecker.checkNull(array);

		for (int value : array) {
			if (value == 0) {
				return false;
			}
		}
		return true;
	}

	public static boolean columnExists(ResultSet resultSet, String columnName) throws CustomException {
		try {
			GlobalCommonChecker.checkNull(columnName);
			GlobalCommonChecker.checkNull(resultSet);

			resultSet.findColumn(columnName);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public static <K, V> String userUpdateQueryBuilder(Map<K, V> keyWithValue) throws CustomException {
		GlobalCommonChecker.checkNull(keyWithValue);

		return keyWithValue.entrySet().stream().map(entry -> entry.getKey() + " = " + formatValue(entry.getValue()))
				.collect(Collectors.joining(", "));
	}

	private static <V> String formatValue(V value) {
		if (value instanceof String) {
			return "'" + value + "'";
		}
		return value.toString();
	}
}
