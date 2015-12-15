package org.coffeeshop.settings;

public class FilterProxySettings extends ProxySettings {

	public static interface SettingsFilter {
		
		public boolean filter(String key);
		
	}
	
	private SettingsFilter filter;
	
	public FilterProxySettings(Settings parent, SettingsFilter filter) {
		super(parent);
		this.filter = filter;
	}

	@Override
	protected String internalKey(String key) {
		return key;
	}
	
	@Override
	protected String externalKey(String key) {
		if (filter.filter(key))
			return key;
		else
			return null;
	}
	
}
