package org.coffeeshop.settings;

import java.io.File;
import java.io.IOException;

public interface StoreableSettings {

	public void loadSettings(File fileName) throws IOException;
	
	public void storeSettings(File fileName) throws IOException;
	
}
