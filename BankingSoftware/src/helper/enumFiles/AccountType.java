package helper.enumFiles;

public enum AccountType {
	SAVING(1), SALARY(2), CURRENT(3);

	private final int code;

	AccountType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
