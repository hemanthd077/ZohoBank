package helper.enumFiles;

public enum StatusType {
	INACTIVE(0), ACTIVE(1), ADMINACCESS(1), EMPLOYEEACCESS(0), CUSTOMER(1), EMPLOYEE(2), ADMIN(3), BRANCH(4),
	ACCOUNT(5), TRANSACTION(6);

	private final int code;

	StatusType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
