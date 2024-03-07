package helper.enumFiles;

import java.util.HashMap;
import java.util.Map;

public enum EmployeeAccess {
	EMPLOYEE(0), ADMIN(1);

	private final int code;
	private static final Map<Integer, EmployeeAccess> codeMap = new HashMap<>();

	static {
		for (EmployeeAccess status : EmployeeAccess.values()) {
			codeMap.put(status.getCode(), status);
		}
	}

	EmployeeAccess(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static EmployeeAccess getByCode(int code) {
		return codeMap.getOrDefault(code, throwInvalidCodeException(code));
	}

	private static EmployeeAccess throwInvalidCodeException(int code) {
		throw new IllegalArgumentException("No enum constant with code " + code + " found");
	}
}
