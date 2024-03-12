package helper.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> implements ICache<K, V> {

    private final int capacity;
    private final Map<Long, V> cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            private static final long serialVersionUID = 1L;

			@Override
            protected boolean removeEldestEntry(Map.Entry<Long, V> eldest) {
                return size() > LRUCache.this.capacity;
            }
        };
    }

    @Override
    public V get(long key) {
        return cache.get(key);
    }

    @Override
    public void set(long key, V value) {
        cache.put(key, value);
    }
    
    @Override
    public long getSize() {
    	return cache.size();
    }
    
    
    @Override
    public boolean containKey(long key) {
    	if(cache.containsKey(key)) {
    		return true;
    	}
		return false;	
    }
    
    @Override
    public void delete(long key) {
    	cache.remove(key);
    }
    
    @Override
    public void clearCache() {
    	cache.clear();
    }
}
