package org.coffeeshop.cache;

import java.util.Vector;

public abstract class AbstractCache<K, V> implements Cache<K, V> {

	private Vector<CacheListener> listeners = new Vector<CacheListener>();

	public void addCacheListener(CacheListener l) {

		synchronized (listeners) {

			listeners.add(l);

		}

	}

	public void removeCacheListener(CacheListener l) {

		synchronized (listeners) {

			listeners.remove(l);

		}

	}

	protected void fireCacheUpdate() {

		synchronized (listeners) {

			for (CacheListener l : listeners) {

				l.cacheUpdated(this);

			}

		}

	}

}
