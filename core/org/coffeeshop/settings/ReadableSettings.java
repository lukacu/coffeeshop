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
 * @see AbstractSettings
 */
public interface ReadableSettings {

	/**
	 * 
	 * 
	 * @param key
	 * @return
	 */
	public int getInt(String key) throws SettingsNotFoundException, NumberFormatException;
	
	public long getLong(String key) throws SettingsNotFoundException, NumberFormatException;
	
	public double getDouble(String key) throws SettingsNotFoundException, NumberFormatException;
	
	public float getFloat(String key) throws SettingsNotFoundException, NumberFormatException;
	
	public boolean getBoolean(String key) throws SettingsNotFoundException, NumberFormatException;
	
	public String getString(String key) throws SettingsNotFoundException;
	  
	public int getInt(String key, int def);
	
	public long getLong(String key, long def);
	
	public double getDouble(String key, double def);
	
	public float getFloat(String key, float def);
	
	public boolean getBoolean(String key, boolean def);
	
	public String getString(String key, String def);
	  
	public boolean containsKey(String key);
	
	public Set<String> getKeys();
	
	public Set<String> getAllKeys();
	
}