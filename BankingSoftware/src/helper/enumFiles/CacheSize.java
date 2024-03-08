package helper.enumFiles;

public enum CacheSize {

	CUSTOMER_CACHE(50),
	EMPLOYEE_CACHE(50),
	ACCOUNT_CACHE(20);

	private final int size;

	CacheSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}
}
