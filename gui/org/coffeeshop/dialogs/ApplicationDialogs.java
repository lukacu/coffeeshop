package org.coffeeshop.dialogs;

import java.util.Vector;

import org.coffeeshop.application.Application;

public abstract class ApplicationDialogs {

	private Vector<ApplicationDialogsListener> listeners = new Vector<ApplicationDialogsListener>();
	
	private Application application;
	
	public ApplicationDialogs(Application application) {
		
		this.application = application;
		
	}
	
	public void addApplicationDialogsListener(ApplicationDialogsListener listener) {
		
		listeners.add(listener);
		
	}
	
	public void removeApplicationDialogsListener(ApplicationDialogsListener listener) {
	
		listeners.remove(listeners);
		
	}
	
	protected void fireOpenAbout() {
		
		for(ApplicationDialogsListener l : listeners) {
			try {
				l.openAbout(application);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void fireOpenPreferences() {
		
		for(ApplicationDialogsListener l : listeners) {
			try {
				l.openPreferences(application);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void fireCloseAbout() {
		
		for(ApplicationDialogsListener l : listeners) {
			try {
				l.closeAbout(application);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void fireClosePreferences() {
		
		for(ApplicationDialogsListener l : listeners) {
			try {
				l.closePreferences(application);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
