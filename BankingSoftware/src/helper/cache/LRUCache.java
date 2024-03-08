package helper.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 1L;
	private final int maxCapacity;

	public LRUCache(int maxCapacity) {
		super(maxCapacity + 1, 0.75f, true);
		this.maxCapacity = maxCapacity;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > maxCapacity;
	}

	public void clearCache() {
		super.clear();
	}
}
