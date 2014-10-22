package org.coffeeshop.settings;

public class PrefixProxySettings extends ProxySettings {

	private String prefix;
	
	public PrefixProxySettings(Settings parent, String prefix) {
		super(parent);
		this.prefix = prefix;		
	}

	@Override
	protected String internalKey(String key) {
		return prefix + key;
	}

	@Override
	protected String externalKey(String key) {
		if (key.startsWith(prefix)) 
			return key.substring(prefix.length());
		else return null;
	}

}
