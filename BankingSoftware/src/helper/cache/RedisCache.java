package helper.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

import globalutil.CustomException;
import redis.clients.jedis.Jedis;

public class RedisCache<K, V> implements ICache<K, V> {

	private final Jedis jedis;
	private final long maxCacheSize;
	private final Deque<byte[]> keyQueue;

	public RedisCache(String host, int port, long maxCacheSize) {
		this.jedis = new Jedis(host, port);
		this.maxCacheSize = maxCacheSize;
		this.keyQueue = new ArrayDeque<>();
	}

	@Override
	public void set(long key, V value) throws CustomException {
		byte[] byteKey = longToByteArray(key);
		jedis.set(byteKey, objectToByteArray(value));
		jedis.expire(byteKey, 6000);
		trackKey(byteKey);
		checkAndEvictEldest();
	}

	@Override
	public boolean containKey(long key) {
		return jedis.exists(longToByteArray(key));
	}

	@Override
	public void delete(long key) {
		byte[] byteKey = longToByteArray(key);
		jedis.del(byteKey);
		keyQueue.remove(byteKey);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(long key) throws CustomException {
		byte[] byteKey = longToByteArray(key);
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

	private byte[] longToByteArray(long value) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(value);
		return buffer.array();
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
