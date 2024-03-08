package helper.enumFiles;

import java.util.HashMap;
import java.util.Map;

public enum TransactionStatus {
	SUCCESS(1),
	FAILED(1);

	private final int code;
	private static final Map<Integer, TransactionStatus> codeMap = new HashMap<>();

	static {
		for (TransactionStatus status : TransactionStatus.values()) {
			codeMap.put(status.getCode(), status);
		}
	}

	TransactionStatus(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static TransactionStatus getByCode(int code) {
		return codeMap.getOrDefault(code,null);
	}
}
