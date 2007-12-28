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
package org.coffeeshop.settings;

import java.util.Properties;
import java.util.Vector;
import java.io.*;

import org.coffeeshop.Coffeeshop;

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
public class Settings extends AbstractSettings implements WriteableSettings {
	
	private static final String COMMENT = Coffeeshop.NAME + " " + Coffeeshop.VERSION + " managed settings file";
	
	// Serializable interface version or something
	static final long serialVersionUID = 1;
	
	// At least one setting was modified...
    private boolean modified;
    
    private Vector<SettingsListener> listeners = new Vector<SettingsListener>();

    private Properties storage;
    
    /**
     * Construct new empty <code>Settings</code> object.
     *
     * @param a application object
     */
    public Settings(ReadableSettings parent) {
        super(parent);
        
    	storage = new Properties();
    	
        modified = false;
    }
    
    /**
     * Toggle internal modified flag to <code>true</code>. 
     *
     */    
    public void touch() {
        modified = true;
    }
    
    /**
     * Adds settings listener to the internal list. Listener is subscibed to
     * the events of this settings object.
     * 
     * @param l
     */
    public void addSettingsListener(SettingsListener l) {
    	if (l == null)
    		throw new IllegalArgumentException("Argument is null");
    	
    	listeners.add(l);
    }
    
    /**
     * Removes listener form the subsribers list.
     * 
     * @param l
     */
    public void removeSettingsListener(SettingsListener l) {
    	if (l == null)
    		throw new IllegalArgumentException("Argument is null");
    	
    	listeners.remove(l);
    }    
    
    private void notifyStore() {
    	
    	for (SettingsListener l : listeners) {
    			l.storeSettings(this);
    	}
    	
    }
    
    /**
     * Overrides method of <code>java.util.Properties</code> just to
     * turn on modified flag.
     * 
     * @see Properties#setProperty(java.lang.String, java.lang.String)
     * 
     */    
    private String setProperty(String key, String value) {
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
     * 
     * @param fileName target file
     * @return <code>true</code> if successful, <code>false</code> otherwise
     * @see Properties#load(java.io.InputStream)
     */
    public boolean loadSettings(String fileName) {
        try {
            FileInputStream in = new FileInputStream(fileName);
            storage.load(in);
            in.close();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Convenience method for loading settings from a data stream
     * 
     * 
     * @param source data stream
     * @return <code>true</code> if successful, <code>false</code> otherwise
     * @see Properties#load(java.io.InputStream)
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

    public boolean storeSettings(String fileName) {
    	notifyStore();
    	
    	try {
    		FileOutputStream out = new FileOutputStream(fileName);
    		
    		storage.store(out, COMMENT);
    		
    		out.close();
    		
    	} catch (IOException e) {
    		return false;
    	}
    	
    	return true;
    }
    
    
	public boolean isModified() {
		return modified;
	}

	public void setDouble(String key, double value) {
		setProperty(key, String.valueOf(value));
		
	}

	public void setFloat(String key, float value) {
		setProperty(key, String.valueOf(value));
		
	}

	public void setInt(String key, int value) {
		setProperty(key, String.valueOf(value));
		
	}

	public void setLong(String key, long value) {
		setProperty(key, String.valueOf(value));
		
	}

	public void setBoolean(String key, boolean value) {
		setProperty(key, String.valueOf(value));
		
	}
	
	public void setString(String key, String value) {
		setProperty(key, value);
		
	}

	protected String getProperty(String key) {
		return storage.getProperty(key);
	}

	public void remove(String key) {
		storage.remove(key);
	}
    
}
