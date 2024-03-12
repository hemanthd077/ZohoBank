package helper.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.Deque;

import globalutil.CustomException;
import redis.clients.jedis.Jedis;

public class RedisCache<K, V> implements ICache<K, V> {

	private Jedis jedis;
	private final long maxCacheSize;
	private final Deque<byte[]> keyQueue;
	private final String cachePrefix;

	public RedisCache(String host, int port, long maxCacheSize,String cachePrefix) {
		this.jedis = new Jedis(host, port);
		this.maxCacheSize = maxCacheSize;
		this.keyQueue = new ArrayDeque<>();
		this.cachePrefix = cachePrefix;
	}

	@Override
	public void set(long key, V value) throws CustomException {
		byte[] byteKey = objectToByteArray(cachePrefix+":"+key);
		jedis.set(byteKey, objectToByteArray(value));
		jedis.expire(byteKey, 6000);
		trackKey(byteKey);
		checkAndEvictEldest();
	}

	@Override
	public boolean containKey(long key) throws CustomException {
		return jedis.exists(objectToByteArray(cachePrefix+":"+key));
	}

	@Override
	public void delete(long key) throws CustomException {
		byte[] byteKey = objectToByteArray(cachePrefix+":"+key);
		jedis.del(byteKey);
		keyQueue.remove(byteKey);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(long key) throws CustomException {
		byte[] byteKey = objectToByteArray(cachePrefix+":"+key);
		return (V) byteArrayToObject(jedis.get(byteKey));
	}

	@Override
	public long getSize() {
		return jedis.dbSize();
	}
	
	@Override
	public void clearCache() {
        jedis.flushAll();
    }

	public void close() {
		jedis.close();
	}

	private static <V> byte[] objectToByteArray(V value) throws CustomException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(value);
			return bos.toByteArray();
		} catch (IOException e) {
			throw new CustomException("Error occurred while converting to serializable object", e);
		}
	}

	private static Object byteArrayToObject(byte[] data) throws CustomException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
				ObjectInputStream ois = new ObjectInputStream(bis)) {
			return ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new CustomException("Error occurred while converting byte to Object", e);
		}
	}

	private void trackKey(byte[] key) {
		keyQueue.addLast(key);
	}

	private void checkAndEvictEldest() {
		if (getSize() > maxCacheSize) {
			byte[] eldestKey = keyQueue.pollFirst();
			if (eldestKey != null) {
				jedis.del(eldestKey);
			}
		}
	}
}
