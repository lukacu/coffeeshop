package org.coffeeshop.osx;

public interface OSXApplicationListener {
	    public void handleAbout(OSXApplicationEvent event);
	    public void handleOpenApplication(OSXApplicationEvent event);
	    public void handleOpenFile(OSXApplicationEvent event);
	    public void handlePreferences(OSXApplicationEvent event);
	    public void handlePrintFile(OSXApplicationEvent event);
	    public void handleQuit(OSXApplicationEvent event);
	    public void handleReopenApplication(OSXApplicationEvent event);
}
