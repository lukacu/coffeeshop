package org.coffeeshop.settings;

import java.util.HashSet;
import java.util.Set;

public class PrefixProxySettings extends AbstractSettings {

	private String prefix;
	
	private AbstractSettings parent;
	
	public PrefixProxySettings(AbstractSettings parent, String prefix) {
		super(parent);
		this.prefix = prefix;
		this.parent = parent;
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
		parent.remove(this.prefix + key);
	}

	@Override
	public Set<String> getKeys() {
		
		Set<String> filtered = new HashSet<String>();
		
		Set<String> full = parent.getKeys();
		
		for (String key : full) {
			
			if (key.startsWith(prefix)) {
				filtered.add(key.substring(prefix.length()));
			}
			
		}
		
		return filtered;
	}

	@Override
	protected String setProperty(String key, String value) {
		return parent.setProperty(this.prefix + key, value);
	}

	@Override
	protected String getProperty(String key) {
		return parent.getProperty(this.prefix + key);
	}

}
