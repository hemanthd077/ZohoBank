package database.structure;

public class CurrentUser {
	private static final ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();

	public static void setUserId(long userId) {
		userIdThreadLocal.set(userId);
	}

	public static long getUserId() {
		return userIdThreadLocal.get();
	}

	public static void clearUserData() {
		userIdThreadLocal.remove();
	}
}
