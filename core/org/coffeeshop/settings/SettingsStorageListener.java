package org.coffeeshop.settings;

public interface SettingsStorageListener {

	public void beforeStore(Settings settings);
	
	public void afterStore(Settings settings);
	
	public void beforeLoad(Settings settings);
	
	public void afterLoad(Settings settings);
	
}
