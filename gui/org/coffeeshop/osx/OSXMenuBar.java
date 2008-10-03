package org.coffeeshop.osx;

import org.coffeeshop.application.Application;
import org.coffeeshop.dialogs.ApplicationDialogs;

public class OSXMenuBar extends ApplicationDialogs {

    private OSXApplicationProxy osxApplication;

    private boolean about, preferences;
    
    public class OSXApplicationListenerImpl implements OSXApplicationListener {

		public void handleAbout(OSXApplicationEvent event) {
			
			event.setHandled(true);
			if (OSXMenuBar.this.about)
				fireOpenAbout();
		}

		public void handleOpenApplication(OSXApplicationEvent event) {

		}

		public void handleOpenFile(OSXApplicationEvent event) {

		}

		public void handlePreferences(OSXApplicationEvent event) {
			event.setHandled(true);
			if (OSXMenuBar.this.preferences)
				fireOpenPreferences();
		}

		public void handlePrintFile(OSXApplicationEvent event) {

		}

		public void handleQuit(OSXApplicationEvent event) {

		}

		public void handleReopenApplication(OSXApplicationEvent event) {

		}
		
	};
    
    public OSXMenuBar(Application application, boolean about, boolean preferences) {
    	
    	super(application);

    	try {
    		osxApplication = OSXApplicationProxy.getInstance();
    	} catch (RuntimeException e) {
    		
    	}

    	this.about = about;
    	this.preferences = preferences;
    	
    	if (about)
    		osxApplication.addAboutMenuItem();
    	
    	if (preferences)
    		osxApplication.addPreferencesMenuItem();
    	
    	osxApplication.addApplicationListener(new OSXApplicationListenerImpl());
    	
    	
    }
	
}
