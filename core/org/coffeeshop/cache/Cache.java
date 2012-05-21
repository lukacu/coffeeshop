package org.coffeeshop.cache;

public interface Cache<K, V> {

	/**
	 * Instert an object into cache using specified key
	 * 
	 * @param key key used to retreive cached object
	 * @param object the object to be cached
	 */
	public void insert(K key, V object);
	
	public V query(K key);
	
	public boolean contains(K key);
	
	public V remove(K key);
	
	public void flush();
	
	public int size();
	
}
