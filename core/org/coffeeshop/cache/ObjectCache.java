package org.coffeeshop.cache;

import java.util.PriorityQueue;
import java.util.TreeMap;


/**
 * ObjectCache provides a caching mechanism for retrieval operations that would
 * otherwise require some time to complete. The resources are identified by a
 * string key that is usually an URL of the resource.
 * 
 * @author lukacu
 */
public class ObjectCache<K, V> extends AbstractCache<K, V> {

	/**
	 * Maximum capacity of the cache
	 */
	private int limit;

	private PriorityQueue<ObjectWrapper> orderedAccess;

	private TreeMap<K, ObjectWrapper> orderedContainer;

	/**
	 * Internal wrapper of the objects that records the time
	 * 
	 * @author lukacu
	 * @see ObjectCache
	 */
	private class ObjectWrapper implements Comparable<ObjectWrapper> {

		private long timestamp;

		private V object;

		private K key;

		/**
		 * Construct a new wrapper for the object. The wrapper stores the key,
		 * the object and the time of the last access to the object.
		 * 
		 * The constructor sets the time-stamp of the last access to the time of
		 * object construction.
		 * 
		 * @param key
		 *            key of the object
		 * @param o
		 *            object to wrap
		 */
		public ObjectWrapper(K key, V o) {
			object = o;
			this.key = key;
			timestamp = System.currentTimeMillis();
		}

		/**
		 * 
		 * @param o
		 * @return
		 * 
		 * @see Comparable#compareTo(T)
		 */
		public int compareTo(ObjectWrapper o) {

			return (o.timestamp < timestamp ? 1
					: o.timestamp > timestamp ? -1 : 0);
		}

		/**
		 * Returns the object that is wrapped in this wrapper
		 * 
		 * @return
		 */
		public V getObject() {
			return this.object;
		}

		/**
		 * Returns the key of the object
		 * 
		 * @return the key
		 */
		public K getKey() {
			return key;
		}

		/**
		 * Updates the time-stamp to the current time.
		 * 
		 */
		public void touch() {
			timestamp = System.currentTimeMillis();
		}
	}

	public ObjectCache(int objectLimit) {

		limit = objectLimit;

		orderedAccess = new PriorityQueue<ObjectWrapper>();

		orderedContainer = new TreeMap<K, ObjectWrapper>();

	}

	/**
	 * Searches for the object by its key. If the object is found, the method
	 * also updates its access time.
	 * 
	 * @param key
	 *            the key to search with
	 * @return the object or <code>null</code> if no object is found
	 */
	public synchronized V query(K key) {

		if (key == null)
			return null;
		
		ObjectWrapper ow = (ObjectWrapper) orderedContainer.get(key);

		if (ow == null)
			return null;

		// update the cache access list
		orderedAccess.remove(ow);
		ow.touch();
		orderedAccess.add(ow);

		return ow.getObject();
	}
	
	/**
	 * Inserts an object to the cache. If the same (with same key) object
	 * already exists, nothing is done.
	 * 
	 * @param key
	 *            a key that is used to identify the object
	 * @param o
	 *            the object itself
	 */
	public synchronized void insert(K key, V o) {

		// prevent duplicates
		if (contains(key))
			return;

		ObjectWrapper ow = new ObjectWrapper(key, o);

		orderedAccess.add(ow);

		orderedContainer.put(key, ow);

		performPurge();
		
		fireCacheUpdate();
		
	}

	/**
	 * Performs a Least Recently Used picking and removes the suitable object
	 * from the cache.
	 */
	private synchronized boolean performPurge() {

		return performPurge(orderedContainer.size() - limit);

	}

	/**
	 * Performs a Least Recently Used picking and removes the cpecified number
	 * of object from the cache.
	 * 
	 * @param purge
	 *            number of elements to remove
	 */
	private synchronized boolean performPurge(int purge) {

		if (purge < 1)
			return false;

		boolean change = false;
		
		for (int i = 0; i < purge; i++) {
			ObjectWrapper ow = orderedAccess.poll();

			if (ow == null) 
				break;

			orderedContainer.remove(ow.getKey());
			change = true;
			
		}

		return change;
		
	}

	public synchronized void flush() {
		
		orderedAccess.clear();
		orderedContainer.clear();
		
		fireCacheUpdate();
		
	}
	
	public synchronized int size() {
		return orderedAccess.size();
	}

	public int capacity() {
		return limit;
	}
	
	public synchronized boolean contains(K key) {
		if (key == null)
			return false;
		
		ObjectWrapper ow = (ObjectWrapper) orderedContainer.get(key);

		return (ow != null);
	}

	public synchronized V remove(K key) {
	
		if (key == null)
			return null;
		
		ObjectWrapper ow = orderedContainer.remove(key);

		if (ow == null)
			return null;

		// update the cache access list
		orderedAccess.add(ow);

		fireCacheUpdate();
		
		return ow.getObject();
		
	}

}
