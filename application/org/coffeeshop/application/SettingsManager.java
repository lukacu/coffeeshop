/* THIS FILE IS A MEMBER OF THE COFFEESHOP LIBRARY
 * 
 * License:
 * 
 * Coffeeshop is a conglomerate of handy general purpose Java classes.  
 * 
 * Copyright (C) 2006-2008 Luka Cehovin
 * This library is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation; either version 2.1 of 
 *  the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 *  GNU Lesser General Public License for more details. 
 *  
 *  http://www.opensource.org/licenses/lgpl-license.php
 *  
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the 
 *  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA 
 * 
 */

package org.coffeeshop.application;

import java.io.*;
import java.util.*;

import org.coffeeshop.settings.ReadableSettings;
import org.coffeeshop.settings.PropertiesSettings;
import org.coffeeshop.string.StringUtils;

/**
 * This static class keeps track of all the opened settings files that are 
 * represented as <code>Settings</code> objects. It provides methods for
 * loading these files in the context of an application and then 
 * later storing them back.
 * 
 * @author lukacu
 * @since CoffeeShop 1.0
 * @see org.coffeeshop.settings.PropertiesSettings
 */
public class SettingsManager {
    
	// settings directory
    private File settingsDirectory = null;
    
    // setting directory exists flag
    private boolean hasDirectory = false;
    
    // hash map that stores opened files
    private HashMap<String, PropertiesSettings> files; 
    
	private static class SettingsAutosave extends Thread {
		
		private ArrayList<SettingsManager> dirList = new ArrayList<SettingsManager>();
		
	    public synchronized void add(SettingsManager dir) {
	    	if (!dirList.contains(dir))
	    		dirList.add(dir);
	    }
	    
	    public void run() {
	        synchronized (this)
	        {
	            Iterator<SettingsManager> iterator = dirList.iterator();
	            while (iterator.hasNext()) {
	            	try {
	            		iterator.next().storeAllSettings();
	            	} catch (FileNotFoundException e) {
	            		Application.getApplicationLogger().report(e);
	            	}
	            }
	        }
	    }
	}
    
    private static SettingsAutosave autosave;
    
    static {
        autosave = new SettingsAutosave();
        Runtime.getRuntime().addShutdownHook(autosave);        
    }
	
    /**
     * Initalization of SettingsManager
     * 
     *  @param a application object
     */
    public SettingsManager(Application a) {
    	
    	this(a, true);
    }
    
    /**
     * Initalization of SettingsManager
     * 
     *  @param a application object
     *  @param autosave settings should be automatically saved if application
     *  exits 
     */
    public SettingsManager(Application a, boolean autosave) {
    	
    	if (a == null)
    		throw new IllegalArgumentException("Application must not be null");
    	
        // constucts settings directory path
		settingsDirectory = Application.applicationStorageDirectory(a);
		// attempt to check for the directory and possibly create it 
		hasDirectory = createRepository(); 
		files = new HashMap<String, PropertiesSettings>();
		
		if (autosave)
			SettingsManager.autosave.add(this);
		
    }
    
    /**
     * Attempts to check for the directory and possibly create it.
     * 
     * @return <code>true</code> if successful (directory exists), 
     * <code>false</code> otherwise.
     */
    private boolean createRepository() {
        File path = settingsDirectory;
        if (!path.exists()) {
        	try {
            path.mkdir();
        	}
        	catch (SecurityException e) {
        		return false;
        	}
            return true;
        } 
        return true;
    }
    
    /**
     * Provides a <code>Settings</code> object associated with the provided 
     * file. The algorythm is as follows:  
     * 1. if object like that exists in the list of managed object return that
     * object
     * 2. if we cannot load the file return empty object (added to the list)
     * 3. otherwise return object with loaded settings (added to the list)
     * 
     * @param fileName storage file for these settings
     * @return <code>true</code> if successful, 
     * <code>false</code> if the file already has an object attached.
     */  
    public PropertiesSettings getSettings(String fileName, ReadableSettings parent) {
    	// search for existing settings object
        PropertiesSettings result = (PropertiesSettings)files.get(fileName);
        if (result != null) return result;
        
        result = new PropertiesSettings(parent);
        files.put(fileName, result);
        // if the settings directory does not exists, we can not load
        // settings ... therefore return empty object

        if (hasDirectory) 
        	try {
        		result.loadSettings(new File(settingsDirectory, fileName));
        	} catch (IOException e) {}
        return result;
    }
    
    /**
     * @param fileName storage file for these settings
     * @return <code>true</code> if successful, 
     * <code>false</code> if the file already has an object attached.
     * @see SettingsManager#getSettings(String)
     */  
    public PropertiesSettings getSettings(String fileName) {
    	
    	return getSettings(fileName, null);
    }
    
    /**
     * Add a Settings object to the SettingsManager list.
     * 
     * This method is not realy meant to be used outside this class and
     * will be probaly be transformed to private method in the future.
     * 
     * @param fileName storage file for these settings
     * @param settings storage object
     * @return <code>true</code> if successful, 
     * <code>false</code> if the file already has an object attached.
     */    
    public boolean addSettings(String fileName, PropertiesSettings settings) {
        PropertiesSettings result = (PropertiesSettings)files.get(fileName);
        if (result != null) return false;
        
        files.put(fileName, settings);
        settings.touch();
        return true;
    }
    
    /**
     * Stores all the files managed by the SettingsManager
     * 
     * @return <code>true</code> if successful, <code>false</code> if at 
     * least one file could not be stored.
     * @throws FileNotFoundException if the settings directory could not be created
     * beacuse of security reasons
     */  
    public boolean storeAllSettings() throws FileNotFoundException {
        Object s[] = files.keySet().toArray();
        boolean result = true;
        
        for(int i = 0; i < s.length; i++) {
            result &= storeSettings((String)s[i]);
        }
        return result;
    }
    
    /**
     * Stores settings to the given file (if souch file was ever opened) 
     * 
     * This method is not realy meant to be used outside this class and
     * will be probaly be transformed to private method in the future.
     * 
     * @param fileName
     * @return <code>true</code> if successful, <code>false</code> if not.
     * @throws FileNotFoundException if the settings directory could not be created
     * beacuse of security reasons
     */  
    public boolean storeSettings(String fileName) throws FileNotFoundException {
    	if (!hasDirectory)
    		throw new FileNotFoundException("Settings directory does not exists");
        PropertiesSettings result = (PropertiesSettings)files.get(fileName);
        if (result != null) {
        	try {
        		result.storeSettings(new File(settingsDirectory, fileName));
        		return true;
        	} catch (IOException e) {
        		return false;
        	}
        }
        else return false;
    }
 
    
    public File storageFileName(String name) {
    	if (StringUtils.empty(name))
    		return null;
    	
    	return new File(settingsDirectory, name);
    }
    
    
    
}
