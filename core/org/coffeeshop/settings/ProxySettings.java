package org.coffeeshop.settings;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

class ProxyWeakSettingsListener implements SettingsListener {
	
	WeakReference<Settings> reference;
	
	public ProxyWeakSettingsListener(Settings proxy) {
		
		reference = new WeakReference<Settings>(proxy);
		
	}
	
	
	@Override
	public void settingsChanged(SettingsChangedEvent e) {
		
		Settings settings = reference.get();
		
		if (settings == null) return;
		
		settings.notifySettingsChanged(e.getKey(), e.getOldValue(), e.getValue());
		
	}
		
}

public class ProxySettings extends Settings {

	private Settings parent;
	
	public ProxySettings(Settings parent) {
		super(parent);
		this.parent = parent;
		
		if (this.parent != null)
			this.parent.addSettingsListener(new ProxyWeakSettingsListener(this));
		
	}

	protected String internalKey(String key) {
		return key;
	}
	
	protected String externalKey(String key) {
		return key;
	}
	
	@Override
	public void touch() {
		parent.touch();
	}

	@Override
	public boolean isModified() {
		return parent.isModified();
	}

	@Override
	public void remove(String key) {
		
		parent.remove(internalKey(key));
		
	}

	@Override
	public Set<String> getKeys() {
		
		Set<String> filtered = new HashSet<String>();
		
		Set<String> full = parent.getAllKeys();
		
		for (String key : full) {
			
			String eKey = externalKey(key);
			
			if (eKey != null)
				filtered.add(eKey);
			
		}
		
		return filtered;
	}

	@Override
	public Set<String> getAllKeys() {
		return getKeys();
	}
	
	@Override
	protected String setProperty(String key, String value) {
		if (value == null) {
			remove(key);
			return null;
		}
		
		String old = parent.setProperty(internalKey(key), value);
		
		return old;
	}

	@Override
	protected String getProperty(String key) {
		return parent.getProperty(internalKey(key));
	}

	@Override
	protected void notifySettingsChanged(String key, String oldValue,
			String newValue) {

		String eKey = externalKey(key);

		if (eKey != null)
			super.notifySettingsChanged(eKey, oldValue, newValue);
	}
	
}