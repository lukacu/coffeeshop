package org.coffeeshop.dialogs;

import org.coffeeshop.application.Application;

public interface ApplicationDialogsListener {

	public void openAbout(Application application);
	
	public void openPreferences(Application application);
	
	public void closeAbout(Application application);
	
	public void closePreferences(Application application);
	
}
