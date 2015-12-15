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


/**
 * This object describes the settings change event.
 * 
 * @author luka
 * @since CoffeeShop 1.0
 * @see SettingsListener
 */
public class SettingsChangedEvent extends Value {

	private String key, value, oldValue;
	
	private Settings settings;

	/**
	 * Creates new object.
	 * 
	 * @param key
	 *            key part of the key-value pair
	 * @param settings
	 *            settings object
	 * @param value
	 *            value part of the key-value pair
	 * @param oldValue
	 *            value that has been replaced
	 */
	public SettingsChangedEvent(Settings settings, String key, String value, String oldValue) {
		this.settings = settings;
		this.key = key;
		this.value = value;
		this.oldValue = oldValue;
	}
	
	/**
	 * Gets key part
	 * 
	 * @return key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Gets old value
	 * 
	 * @return old value
	 */
	public String getOldValue() {
		return oldValue;
	}

	/**
	 * Gets settings object
	 * 
	 * @return settings object
	 * @see PropertiesSettings
	 */
	public Settings getSettings() {
		return settings;
	}

	/**
	 * Gets new value
	 * 
	 * @return new value
	 */
	public String getValue() {
		return value;
	}

}
