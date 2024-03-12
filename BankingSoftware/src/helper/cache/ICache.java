package helper.cache;

import globalutil.CustomException;

public interface ICache<K, V> {

	public void set(long key, V value) throws CustomException;

	public boolean containKey(long key);

	public void delete(long key);

	public V get(long key) throws CustomException;

	long getSize();

	void clearCache();
}
