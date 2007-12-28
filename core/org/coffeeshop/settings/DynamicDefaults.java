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
 * DynamicDefaults is a wrapper for any settings class that implements the {@link ReadableSettings}
 * interface. It provides getter methods similar to those of {@link ReadableSettings} but with 
 * the second argument that specifies which value to return if the wrapped settings class does not
 * include the value for the key or if it cannot be converted to the appropriate format.
 * 
 * @author Luka Cehovin
 * @see ReadableSettings
 *
 */
public class DynamicDefaults {

	private ReadableSettings settings;
	
	public DynamicDefaults(ReadableSettings settings) {
		this.settings = settings;
	}
	
	public int getInt(String key, int def) {
		
		try {
			
			return settings.getInt(key);
			
		} 
		catch (NumberFormatException e) {}
		catch (SettingsNotFoundException e) {}

		return def;
		
	}
	
	public long getLong(String key, long def) {
		
		try {
			
			return settings.getLong(key);
			
		} 
		catch (NumberFormatException e) {}
		catch (SettingsNotFoundException e) {}

		return def;
		
	}
	
	public double getDouble(String key, double def) {
		
		try {
			
			return settings.getDouble(key);
			
		} 
		catch (NumberFormatException e) {}
		catch (SettingsNotFoundException e) {}

		return def;
		
	}
	
	public float getFloat(String key, float def) {
		try {
			
			return settings.getFloat(key);
			
		} 
		catch (NumberFormatException e) {}
		catch (SettingsNotFoundException e) {}

		return def;
	}
	
	public boolean getBoolean(String key, boolean def) {
		try {
			
			return settings.getBoolean(key);
			
		} 
		catch (NumberFormatException e) {}
		catch (SettingsNotFoundException e) {}

		return def;
		
	}
	
	
	public String getString(String key, String def) {
		try {
			
			return settings.getString(key);
			
		} 
		catch (SettingsNotFoundException e) {}

		return def;
	}
}
