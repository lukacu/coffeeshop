package org.coffeeshop.swing;

public interface SettingsEditor {

	public static enum CommitStrategy {MANUAL, FOCUS, ALWAYS};
	
	public void commit();
	
}
