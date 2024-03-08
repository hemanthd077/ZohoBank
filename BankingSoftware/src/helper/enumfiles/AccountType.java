package helper.enumfiles;

import java.util.HashMap;
import java.util.Map;

public enum AccountType {
	SAVING(1), SALARY(2), CURRENT(3);

	private final int code;
	private static final Map<Integer, AccountType> codeMap = new HashMap<>();

	static {
		for (AccountType status : AccountType.values()) {
			codeMap.put(status.getCode(), status);
		}
	}

	AccountType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static AccountType getByCode(int code) {
		return codeMap.getOrDefault(code,null);
	}
}
