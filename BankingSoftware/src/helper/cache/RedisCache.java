package helper.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import globalutil.CustomException;
import redis.clients.jedis.Jedis;

public class RedisCache<K, V> implements ICache<K, V> {

    private final String host;
    private final int port;
    private final String cachePrefix;

    public RedisCache(String host, int port, String cachePrefix) {
        this.host = host;
        this.port = port;
        this.cachePrefix = cachePrefix;
    }

    @Override
    public synchronized void set(long key, V value) throws CustomException {
        try (Jedis jedis = new Jedis(host, port)) {
            byte[] byteKey = objectToByteArray(cachePrefix + ":" + key);
            jedis.set(byteKey, objectToByteArray(value));
            jedis.expire(byteKey, 6000);
        }
        catch (Exception e) {
			throw new CustomException("Error occured in Redis",e);
		}
    }

    @Override
    public synchronized void delete(long key) throws CustomException {
        try (Jedis jedis = new Jedis(host, port)) {
            byte[] byteKey = objectToByteArray(cachePrefix + ":" + key);
            jedis.del(byteKey);
        }
        catch (Exception e) {
        	throw new CustomException("Error occured in Redis",e);
		}
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized V get(long key) throws CustomException {
        try (Jedis jedis = new Jedis(host, port)) {
            byte[] byteKey = objectToByteArray(cachePrefix + ":" + key);
            byte[] value = jedis.get(byteKey);
            if (value != null) {
                return (V) byteArrayToObject(value);
            } else {
                return null;
            }
        }
        catch (Exception e) {
        	throw new CustomException("Error occured in Redis",e);
		}
    }

    @Override
    public synchronized long getSize() {
        try (Jedis jedis = new Jedis(host, port)) {
            return jedis.dbSize();
        }
    }

    @Override
    public synchronized void clearCache() {
        try (Jedis jedis = new Jedis(host, port)) {
            jedis.flushAll();
        }
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
}
