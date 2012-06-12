package org.coffeeshop.cache;

import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.TreeMap;

import org.coffeeshop.io.TempDirectory;

/**
 * DataCache provides a caching mechanism for retrieval operations that would
 * otherwise require some time to complete. The resources are identified by a
 * string key that is usually an URL of the resource.
 * 
 * @author lukacu
 */
public abstract class DataCache<K, V> extends AbstractCache<K, V> {

	/**
	 * Maximum capacity of the cache
	 */
	private long memoryLimit, totalLimit, totalUsage = 0, memoryUsage = 0;

	private PriorityQueue<DataWrapper> orderedAccess;

	private PriorityQueue<DataWrapper> orderedAccessMemory;
	
	private TreeMap<K, DataWrapper> orderedContainer;

	private TempDirectory tempDir;
	
	/**
	 * Internal wrapper of the objects that records the time
	 * 
	 * @author lukacu
	 * @see ObjectCache
	 */
	private class DataWrapper implements Comparable<DataWrapper> {

		private long timestamp;

		private V data;

		private long length = 0;
		
		private K key;

		private File fileHandle = null;
		
		/**
		 * Construct a new wrapper for the data. The wrapper stores the key,
		 * the data and the time of the last access to the data.
		 * 
		 * The constructor sets the timestamp of the last access to the time of
		 * object construction.
		 * 
		 * @param key
		 *            key of the object
		 * @param o
		 *            data to wrap
		 */
		public DataWrapper(K key, V o) {
			data = o;
			this.key = key;
			timestamp = System.currentTimeMillis();
			length = getDataLength(data);
		}

		public long getTimestamp() {
			
			return timestamp;
			
		}
		
		/**
		 * 
		 * @param o
		 * @return
		 * 
		 * @see Comparable#compareTo(T)
		 */
		public int compareTo(DataWrapper o) {
			DataWrapper ow = (DataWrapper) o;

			return ow.getTimestamp() < getTimestamp() ? 1
					: (ow.getTimestamp() > getTimestamp() ? -1 : 0);
		}

		/**
		 * Returns the object that is wrapped in this wrapper
		 * 
		 * @return
		 */
		public V getData() throws IOException {
			if (data == null) {
				try {
					readFromDisk();
				}
				catch (IOException e) {
					data = null;
					throw e;
				}
			}
			
			return data;
		}

		public boolean pushToDisk() {
			try {
			
				writeToDisk();
			
				data = null;
				
				return true;
				
			} catch (IOException e) {
				return false;
			}
		}
		
		private void readFromDisk() throws IOException {
			if (fileHandle == null || !fileHandle.canRead())
				throw new IOException("Data does not exist or is not readable.");

			data = readData(fileHandle, length);
					
		}
		
		private void writeToDisk() throws IOException {
			if (fileHandle != null || data == null)
				return;
			
			synchronized (tempDir) {
				fileHandle = tempDir.tempFileName("cache");

				writeData(fileHandle, data);
				
			}
		}
		
		public long getLength() {
			return length;
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
		 * Updates the timestamp to the current time.
		 * 
		 */
		public void touch() {
			timestamp = System.currentTimeMillis();
		}

		public void delete() {
			
			if (fileHandle != null)
				fileHandle.delete();
			
			data = null;
			
		}
		
	}

	public DataCache(long memoryLimit, long totalLimit, TempDirectory tempDir) throws IOException {

		if (memoryLimit > totalLimit)
			throw new IllegalArgumentException("Memory limit must be lower or equal that the total limit.");
		
		if (tempDir == null) {
			totalLimit = memoryLimit;
		}
		
		this.memoryLimit = memoryLimit;
		this.totalLimit = totalLimit;
		
		orderedAccess = new PriorityQueue<DataWrapper>();

		orderedAccessMemory = new PriorityQueue<DataWrapper>();
		
		orderedContainer = new TreeMap<K, DataWrapper>();

		this.tempDir = tempDir;
		
				//new TempDirectory("webstrips", WebStrips.getStorageManager().getCacheDirectory());
		
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

		DataWrapper ow = (DataWrapper) orderedContainer.get(key);

		if (ow == null)
			return null;

		// update the cache access list
		orderedAccess.remove(ow);
		if (!orderedAccessMemory.remove(ow)) {
			memoryUsage += ow.getLength();
		}
		
		ow.touch();
		orderedAccess.add(ow);
		orderedAccessMemory.add(ow);
		
		try {
			
			return ow.getData();
		
		} catch (IOException e) {
			
			orderedAccess.remove(ow);
			if (orderedAccessMemory.remove(ow)) 
				memoryUsage -= ow.getLength();
			
			totalUsage -= ow.getLength();
			
			return null;
		
		}
	}

	/**
	 * Inserts an data to the cache. If the same (with same key) data object
	 * already exists, nothing is done.
	 * 
	 * @param key
	 *            a key that is used to identify the object
	 * @param o
	 *            the data itself
	 */
	public synchronized void insert(K key, V o) {

		// prevent duplicates
		if (orderedContainer.get(key) != null)
			return;

		DataWrapper ow = new DataWrapper(key, o);

		orderedAccess.add(ow);
		orderedContainer.put(key, ow);
		orderedAccessMemory.add(ow);
		memoryUsage += ow.getLength();
		totalUsage += ow.getLength();			
	
		performPurge();

		fireCacheUpdate();
		
	}

	/**
	 * Finds the least recently used element and removes the suitable object
	 * from the cache.
	 */
	private synchronized boolean performPurge() {

		boolean changed = false;
		
		while (totalUsage > totalLimit) {
			
			changed |= removeOldest();

		}
		
		while (memoryUsage > memoryLimit) {
			
			changed |= removeOldestFromMemory();

		}

		return changed;
	}

	private synchronized boolean removeOldest() {

		synchronized (this) {
			DataWrapper ow = orderedAccess.poll();
			
			if (ow == null)
				return false;

			orderedAccessMemory.remove(ow);
			orderedContainer.remove(ow.getKey());
			
			memoryUsage -= ow.getLength();
			totalUsage -= ow.getLength();	
			ow.delete();
			
			return true;
		}

	}

	private synchronized boolean removeOldestFromMemory() {

		synchronized (this) {
			DataWrapper ow = orderedAccessMemory.poll();
			
			if (ow == null)
				return false;

			memoryUsage -= ow.getLength();

			ow.pushToDisk();
			
			return true;
		}

	}
	
	public synchronized void flush() {
		
		synchronized (this) {
			for (DataWrapper d : orderedAccess) {
				d.delete();
			}
			
			orderedAccess.clear();
			orderedContainer.clear();
			memoryUsage = 0;
			totalUsage = 0;
		}
		
		fireCacheUpdate();
		
	}
	
	public int size() {
		return orderedAccess.size();
	}

	public synchronized boolean contains(K key) {
		DataWrapper ow = (DataWrapper) orderedContainer.get(key);

		return (ow != null);

	}

	public synchronized V remove(K key) {

		DataWrapper ow = (DataWrapper) orderedContainer.get(key);

		if (ow == null)
			return null;

		// update the cache access list
		orderedAccess.remove(ow);
		totalUsage -= ow.getLength();
		if (!orderedAccessMemory.remove(ow)) {
			memoryUsage -= ow.getLength();
		}

		try {
			
			fireCacheUpdate();
			
			return ow.getData();
		
		} catch (IOException e) {
			return null;
		}
	}

	protected abstract long getDataLength(V object);

	protected abstract V readData(File file, long length) throws IOException;
	
	protected abstract void writeData(File file, V data) throws IOException;
	
}


