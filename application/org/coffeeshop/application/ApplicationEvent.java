package org.coffeeshop.application;

public class ApplicationEvent {

	private Application application;
	
	private boolean handled = false;
	
	private Object source;

	public ApplicationEvent(Application application, Object source) {
		super();
		this.application = application;
		this.source = source;
	}

	public boolean isHandled() {
		return handled;
	}

	public void handle() {
		this.handled = true;
	}

	public Application getApplication() {
		return application;
	}

	public Object getSource() {
		return source;
	}
	
	
}
