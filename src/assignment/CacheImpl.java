package assignment;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/*
 * For Simplicity Exposing only ONE method for Reading/Writing and One for Clearing Cache
 * If Other methods adding to cache (putValue) are to be exposed, synchronization needs to be across methods.
 * */
public class CacheImpl<K, V> {
	private HashMap<K, V> map = new HashMap<K, V>();
	ReentrantLock lock = new ReentrantLock();

	public V get(K key, Function<? super K, ? extends V> func) {
		if (func == null)
			throw new RuntimeException("Null Function Passed");

		V value = getValue(key);
		if (value == null) { // There is no need to Block all Reads
			lock.lock();
			// Need to check again if Value was set by other Thread/s, while Current Thread was Blocked
			if (value == null) { 									
				try {
					value = func.apply(key); 
				} catch (Exception e) { // Cascade RT Exception Back to calling function
					lock.unlock();
					System.out.println("Function throwing Exception:" + e.getStackTrace());
					throw new RuntimeException("Function throwing Exception");
				}
				putValue(key, value);
				lock.unlock();
			}
		}
		return value;
	}

	public V removeFromCache(K key) {
		lock.lock();
		V v = map.remove(key);
		lock.unlock();
		return v;
	}

	private void putValue(K key, V value) {
		map.put(key, value);
	}

	private V getValue(K key) {
		return map.get(key);
	}

}
