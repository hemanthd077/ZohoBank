package globalutil;

public final class CustomException extends Exception {
	private static final long serialVersionUID = 1L;

	public CustomException(String message) {
		super(message);
	}

	public CustomException(String message, Throwable throwableCause) {
		super(message, throwableCause);
	}
}