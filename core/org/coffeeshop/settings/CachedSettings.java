package org.coffeeshop.settings;

import java.util.HashMap;
import java.util.Map.Entry;

import org.coffeeshop.string.StringUtils;

public class CachedSettings extends ProxySettings {

	private HashMap<String, String> cache = new HashMap<String, String>();

	public CachedSettings(Settings parent) {
		super(parent);
	}
 
	@Override
	protected void parentUpdated(SettingsChangedEvent e) {
		
		if (!cache.containsKey(e.getKey()))
			super.parentUpdated(e);
		
	}
	
	@Override
	protected String setProperty(String key, String value) {

		String old = null;
		
		if (!cache.containsKey(key)) {
			old = super.getProperty(key);
		} else {
			old = cache.get(key);
		}
		
		cache.put(key, value);
		
		return old;
	}
	
	public void commit() {
		
		for (Entry<String, String> e : cache.entrySet()) {

			if (StringUtils.same(e.getValue(), super.getProperty(e.getValue())))
				continue;
			
			super.setProperty(e.getKey(), e.getValue());
			
		}
		
		cache.clear();
		
	}

	@Override
	public boolean isModified() {
		return !cache.isEmpty();
	}
	
	@Override
	public void touch() {
		commit();
	}
	
}
