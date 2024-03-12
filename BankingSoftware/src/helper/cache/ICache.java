package helper.cache;

import globalutil.CustomException;

public interface ICache<K, V> {

	public void set(long key, V value) throws CustomException;

	public boolean containKey(long key) throws CustomException;

	public void delete(long key) throws CustomException;

	long getSize();

	void clearCache();

	V get(long key) throws CustomException;
}
