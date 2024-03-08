package helper.enumFiles;

import java.util.HashMap;
import java.util.Map;

public enum RecordStatus {
	INACTIVE(0),
	ACTIVE(1);

	private final int code;
	private static final Map<Integer, RecordStatus> codeMap = new HashMap<>();

	static {
		for (RecordStatus status : RecordStatus.values()) {
			codeMap.put(status.getCode(), status);
		}
	}

	RecordStatus(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static RecordStatus getByCode(int code) {
		return codeMap.getOrDefault(code, null);
	}
}
