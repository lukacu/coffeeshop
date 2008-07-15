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

import java.util.Set;


/**
 * 
 * @author Luka Cehovin
 * @since CoffeeShop Core 1.0
 * @see java.util.Properties
 * @see Settings
 * @see ReadableSettings
 */
public abstract class AbstractSettings implements ReadableSettings {
	
	static final long serialVersionUID = 2342341;
	
    private ReadableSettings parent;
    
    /**
     * Construct new empty <code>Settings</code> object.
     *
     * @param a application object
     */
    public AbstractSettings(ReadableSettings parent) {
        super();

    	this.parent = parent;

    }
  
    protected abstract String getProperty(String key);

	public boolean getBoolean(String key) throws SettingsNotFoundException, NumberFormatException {
        String s = getProperty(key); 
        if (s == null) {
            if (parent != null)
            	return parent.getBoolean(key);
        	
            throw new SettingsNotFoundException(key);
        }

        return Boolean.parseBoolean(s);
	}

	public double getDouble(String key) throws SettingsNotFoundException, NumberFormatException {
        String s = getProperty(key);
        if (s == null) {
            if (parent != null)
            	return parent.getDouble(key);
        	
            throw new SettingsNotFoundException(key);
        } 
        
        return Double.parseDouble(s);
	}

	public float getFloat(String key) throws SettingsNotFoundException, NumberFormatException {
        String s = getProperty(key);
        if (s == null) {
            if (parent != null)
            	return parent.getFloat(key);
        	
            throw new SettingsNotFoundException(key);
        }
        
        return Float.parseFloat(s);
	}

	public long getLong(String key) throws SettingsNotFoundException, NumberFormatException {
        String s = getProperty(key);
        if (s == null) {
            if (parent != null)
            	return parent.getLong(key);
        	
            throw new SettingsNotFoundException(key);
        }
        
        return Long.parseLong(s);
	}

	public int getInt(String key) throws SettingsNotFoundException, NumberFormatException {
        String s = getProperty(key);
        if (s == null) {
            if (parent != null)
            	return parent.getInt(key);
        	
            throw new SettingsNotFoundException(key);
        }
        
        return Integer.parseInt(s);
	}
	
	public String getString(String key) throws SettingsNotFoundException {
        String s = getProperty(key);
        if (s == null) {
            if (parent != null)
            	return parent.getString(key);
        	
            throw new SettingsNotFoundException(key);
        }
        
        return s;
	}    
 
	public boolean containsKey(String key) {
		return getProperty(key) != null;
	}
	
	public Set<String> getAllKeys() {
		
		Set<String> keys = getKeys();
		
		if (parent != null)
			keys.addAll(parent.getAllKeys());
		
		return keys;
		
	}
}
