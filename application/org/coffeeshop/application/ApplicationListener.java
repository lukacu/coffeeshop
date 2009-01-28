package org.coffeeshop.application;

public interface ApplicationListener {

	public void initalize(Application application);
	
	public void openAbout(Application application);
	
	public void openPreferences(Application application);
	
	public void cleanup(Application application);
	
}
