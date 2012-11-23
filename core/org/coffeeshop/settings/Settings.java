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

import java.util.Vector;

/**
 * 
 * @author Luka Cehovin
 * @since CoffeeShop Core 1.0
 * @see java.util.Properties
 * @see PropertiesSettings
 * @see ReadableSettings
 */
public abstract class Settings extends AbstractReadonlySettings implements WriteableSettings {
	
	static final long serialVersionUID = 2342341;

    protected abstract String setProperty(String key, String value);

	/* (non-Javadoc)
	 * @see org.coffeeshop.settings.WriteableSettings#setDouble(java.lang.String, double)
	 */
	public void setDouble(String key, double value) {
		setProperty(key, String.valueOf(value));
		
	}

	/* (non-Javadoc)
	 * @see org.coffeeshop.settings.WriteableSettings#setFloat(java.lang.String, float)
	 */
	public void setFloat(String key, float value) {
		setProperty(key, String.valueOf(value));
		
	}

	/* (non-Javadoc)
	 * @see org.coffeeshop.settings.WriteableSettings#setInt(java.lang.String, int)
	 */
	public void setInt(String key, int value) {
		setProperty(key, String.valueOf(value));
		
	}

	/* (non-Javadoc)
	 * @see org.coffeeshop.settings.WriteableSettings#setLong(java.lang.String, long)
	 */
	public void setLong(String key, long value) {
		setProperty(key, String.valueOf(value));
		
	}

	/* (non-Javadoc)
	 * @see org.coffeeshop.settings.WriteableSettings#setBoolean(java.lang.String, boolean)
	 */
	public void setBoolean(String key, boolean value) {
		setProperty(key, String.valueOf(value));
		
	}
	
	/* (non-Javadoc)
	 * @see org.coffeeshop.settings.WriteableSettings#setString(java.lang.String, java.lang.String)
	 */
	public void setString(String key, String value) {
		setProperty(key, value);
		
	}

	
	public Settings(ReadableSettings parent) {
		super(parent);
	}

    /** The listeners. */
    private Vector<SettingsListener> listeners = new Vector<SettingsListener>();
	
    /**
     * Adds settings listener to the internal list. Listener is subscribed to
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
     * Removes listener form the subscribers list.
     * 
     * @param l the listener object
     */
    public void removeSettingsListener(SettingsListener l) {
    	if (l == null)
    		throw new IllegalArgumentException("Argument is null");
    	
    	listeners.remove(l);
    }   
    
    protected void notifySettingsChanged(String key, String oldValue, String newValue) {
    	
        SettingsChangedEvent e = new SettingsChangedEvent(this, key, newValue, oldValue);
        
        for (SettingsListener listener : listeners) {
        	listener.settingsChanged(e);
        }
    	
    }
    
}
