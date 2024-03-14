package helper.cache;

import java.util.Hashtable;

public class LockCache {
	private static Hashtable<Long, Long> hashtable = new Hashtable<Long, Long>();
	
	@SuppressWarnings("deprecation")
	public static Object getLock(long num) {
		Long returnLong = hashtable.get(num);
		if(returnLong == null) {
			hashtable.put(num, new Long(num));
		}
		return hashtable.get(num);
	}
	
}
