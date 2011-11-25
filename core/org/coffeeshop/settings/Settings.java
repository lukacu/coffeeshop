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
 * @see Settings
 */
public class Settings extends AbstractSettings {
	
	/** The Constant COMMENT. */
	private static final String COMMENT = "Coffeeshop settings file";
	
	/** The Constant serialVersionUID. */
	static final long serialVersionUID = 1;
	
    /** The modified flag. If true at least one setting was modified. */
	private boolean modified;
    
    /** The listeners. */
    private Vector<SettingsListener> listeners = new Vector<SettingsListener>();

    /** The storage. */
    private Properties storage;
    
    /**
     * Construct a new empty <code>Settings</code> object with a parent settings storage.
     * 
     * @param parent the parent
     */
    public Settings(ReadableSettings parent) {
        super(parent);
        
    	storage = new Properties();
    	
        modified = false;
    }
    
    /**
     * Construct a new <code>Settings</code> object with a parent settings storage.
     * 
     * @param fileName the file name
     */
    public Settings(File fileName) throws IOException {
        super(null);
        
        if (fileName == null || !fileName.exists())
        	throw new IOException("File does not exist");
        
    	storage = new Properties();
    	
    	loadSettings(fileName);
    	
        modified = false;
    }
    
    /**
     * Toggle internal modified flag to <code>true</code>.
     */    
    public void touch() {
        modified = true;
    }
    
    /**
     * Adds settings listener to the internal list. Listener is subscibed to
     * the events of this settings object.
     * 
     * @param l the listener object
     */
    public void addSettingsListener(SettingsListener l) {
    	if (l == null)
    		throw new IllegalArgumentException("Argument is null");
    	
    	listeners.add(l);
    }
    
    /**
     * Removes listener form the subsribers list.
     * 
     * @param l the listener object
     */
    public void removeSettingsListener(SettingsListener l) {
    	if (l == null)
    		throw new IllegalArgumentException("Argument is null");
    	
    	listeners.remove(l);
    }    
    
    /**
     * Notify on store.
     */
    private void notifyStore() {
    	
    	for (SettingsListener l : listeners) {
    			l.storeSettings(this);
    	}
    	
    }
      
    protected String setProperty(String key, String value) {
        modified = true;
       
        String oldValue = (String)storage.setProperty(key, value);
        
        SettingsChangedEvent e = new SettingsChangedEvent(this, key, value, oldValue);
        
        for (SettingsListener listener : listeners) {
        	listener.settingsChanged(e);
        }
        
        return (String)storage.setProperty(key, value);
    }
        
    /**
     * Loads settings from file.
     * 
     * @param fileName target file
     * 
     * @return <code>true</code> if successful, <code>false</code> otherwise
     * 
     * @deprecated Use {@link #loadSettings(File)} instead.
     */
    public boolean loadSettings(String fileName) {
    	try {
    		loadSettings(new File(fileName));
    		return true;
    	} catch (IOException e) {
    		return false;
    	}
    }

    /**
     * Loads settings from file.
     * 
     * @param fileName target file
     * 
     * @return <code>true</code> if successful, <code>false</code> otherwise
     * 
     * @see Properties#load(java.io.InputStream)
     */
    public void loadSettings(File fileName) throws IOException {

    	FileInputStream in = new FileInputStream(fileName);
        storage.load(in);
        in.close();
        
    }
    
    /**
     * Convenience method for loading settings from a data stream.
     * 
     * @param source data stream
     * 
     * @return <code>true</code> if successful, <code>false</code> otherwise
     * 
     * @see Properties#load(java.io.InputStream)
     * 
     */
    public boolean loadSettings(InputStream source) {
        try {
        	storage.load(source);
            source.close();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Store settings.
     * 
     * @param fileName the file name
     * 
     * @return true, if successful
     * @deprecated Use {@link #storeSettings(File)} instead.
     */
    public boolean storeSettings(String fileName) {
    	try {
    		storeSettings(new File(fileName));
    		return true;
    	} catch (IOException e) {
    		return false;
    	}
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
    	notifyStore();
    	
		FileOutputStream out = new FileOutputStream(fileName);
		
		storage.store(out, COMMENT);
		
		out.close();
    		
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
		storage.remove(key); 
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
		
}
