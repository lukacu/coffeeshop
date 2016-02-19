/*
 * License:
 * 
 * 
 * 
 * Copyright (C) 2008 Luka Cehovin
 * 
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 2 of the License, or (at 
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * http://www.opensource.org/licenses/gpl-license.php
 * 
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.coffeeshop.settings;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.io.*;

/**
 * Represents a group of name=value settings. Class is builded upon
 * java.util.Properties class and provides additional convenience
 * methods for accessing different types of values.
 * 
 * @author Luka Cehovin
 * @since CoffeeShop Core 1.0
 * @see java.util.Properties
 * @see PropertiesSettings
 */
public class PropertiesSettings extends Settings implements StoreableSettings {
	
	/** The Constant COMMENT. */
	private static final String COMMENT = "Coffeeshop settings file";
	
	/** The Constant serialVersionUID. */
	static final long serialVersionUID = 1;
	
    /** The modified flag. If true at least one setting was modified. */
	private boolean modified;
    
    /** The storage. */
    private Properties storage;
    
    private Vector<SettingsStorageListener> listeners = new Vector<SettingsStorageListener>();
    
    /**
     * Construct a new empty <code>Settings</code> object.
     * 
     */
    public PropertiesSettings() {
        super(null);
        
    	storage = new Properties();
    	
        modified = false;
    }
    
    /**
     * Construct a new empty <code>Settings</code> object with a parent settings storage.
     * 
     * @param parent the parent
     */
    public PropertiesSettings(ReadableSettings parent) {
        super(parent);
        
    	storage = new Properties();
    	
        modified = false;
    }
    
    /**
     * Construct a new <code>Settings</code> object with data loaded from a file.
     * 
     * @param fileName the file name
     */
    public PropertiesSettings(File fileName) throws IOException {
        super(null);
        
        if (fileName == null || !fileName.exists())
        	throw new IOException("File does not exist");
        
    	storage = new Properties();
    	
    	loadSettings(fileName);
    	
        modified = false;
    }
    
    /**
     * Construct a new <code>Settings</code> object with data loaded from a string.
     * 
     * @param settings Settings as string
     */
    public PropertiesSettings(String settings) throws IOException {
        super(null);
        
        if (settings == null)
        	throw new NullPointerException();
        
    	storage = new Properties();
    	
    	loadSettings(settings);
    	
        modified = false;
    }
    
    /**
     * Toggle internal modified flag to <code>true</code>.
     */    
    public void touch() {
        modified = true;
    }
    
     
    protected String setProperty(String key, String value) {
        modified = true;
       
        if (value == null) {
        
        	remove(key);
        	
        }
        
        String oldValue = (String)storage.setProperty(key, value);
        
        notifySettingsChanged(key, oldValue, value);
        
        return (String)storage.setProperty(key, value);
     
    }
        
    /**
     * Loads settings from string.
     * 
     * @param settings as a string
     * @throws IOException 
     * 
     */
    public void loadSettings(String settings) throws IOException {
    	
    	notifySettingsBeforeLoad();
    	
    	StringReader in = new StringReader(settings);
        storage.load(in);
        in.close();
        
        notifySettingsAfterLoad();
    }

    /**
     * Loads settings from file.
     * 
     * @param fileName target file
     * 
     * @see Properties#load(java.io.InputStream)
     */
    public void loadSettings(File fileName) throws IOException {

    	notifySettingsBeforeLoad();
    	
    	FileInputStream in = new FileInputStream(fileName);
        storage.load(in);
        in.close();
        
        notifySettingsAfterLoad();
    }

    /**
     * Loads settings from input stream.
     * 
     * @param stream input stream
     * 
     * @see Properties#load(java.io.InputStream)
     */
    public void loadSettings(InputStream stream) throws IOException {
    	
    	notifySettingsBeforeLoad();

        storage.load(stream);
        
        notifySettingsAfterLoad();
    }
    
    /**
     * Store settings.
     * 
     * @param fileName the file name
     * 
     * @return true, if successful
     * @throws IOException if IO exception occurs
     */
    public void storeSettings(File fileName) throws IOException {

    	notifySettingsBeforeStore();
    	
		FileOutputStream out = new FileOutputStream(fileName);
		
		storage.store(out, COMMENT);
		
		out.close();
		
		notifySettingsAfterStore();
    		
    }
    
	/* (non-Javadoc)
	 * @see org.coffeeshop.settings.WriteableSettings#isModified()
	 */
	public boolean isModified() {
		return modified;
	}

	/* (non-Javadoc)
	 * @see org.coffeeshop.settings.AbstractSettings#getProperty(java.lang.String)
	 */
	protected String getProperty(String key) {
		return storage.getProperty(key);
	}

	/* (non-Javadoc)
	 * @see org.coffeeshop.settings.WriteableSettings#remove(java.lang.String)
	 */
	public void remove(String key) {
		if (storage.containsKey(key)) {
			notifySettingsChanged(key, storage.getProperty(key), null);		
			storage.remove(key); 
		}
	}
    
	/* (non-Javadoc)
	 * @see org.coffeeshop.settings.ReadableSettings#getKeys()
	 */
	public Set<String> getKeys() {
		
		Set<String> keys = new HashSet<String>();
		
		for (Object o : storage.keySet()) {
			keys.add((String)o);
		}
		
		return keys;
		
	}
	
    /**
     * Adds settings listener to the internal list. Listener is subscribed to
     * the events of this settings object.
     * 
     * @param l the listener object
     */
    public void addSettingsStorageListener(SettingsStorageListener l) {
    	if (l == null)
    		throw new IllegalArgumentException("Argument is null");
    	
    	listeners.add(l);
    }
    
    /**
     * Removes listener form the subscribers list.
     * 
     * @param l the listener object
     */
    public void removeSettingsStorageListener(SettingsStorageListener l) {
    	if (l == null)
    		throw new IllegalArgumentException("Argument is null");
    	
    	listeners.remove(l);
    }
    
    protected void notifySettingsBeforeStore() {

        for (SettingsStorageListener listener : listeners) {
        	listener.beforeStore(this);
        }
    	
    }
    
    protected void notifySettingsAfterStore() {

        for (SettingsStorageListener listener : listeners) {
        	listener.afterStore(this);
        }
    	
    }
    
    protected void notifySettingsBeforeLoad() {

        for (SettingsStorageListener listener : listeners) {
        	listener.beforeLoad(this);
        }
    	
    }
    
    protected void notifySettingsAfterLoad() {

        for (SettingsStorageListener listener : listeners) {
        	listener.afterLoad(this);
        }
    	
    }

}
