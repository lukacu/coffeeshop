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
 * SettingsListener can be used to monitor Settings object operations
 * 
 * @author lukacu
 * @since CoffeeShop 1.0
 * @see org.coffeeshop.settings.Settings
 *
 */
public interface SettingsListener {

	/**
	 * Called before settings are saved to file if any last minute
	 * updating is needed.
	 * 
	 * @param s settings object
	 */
	public void storeSettings(Settings s);
	
	/**
	 * Called when specific settings value is changed
	 * @param e event object
	 * @see SettingsChangedEvent
	 */
	public void settingsChanged(SettingsChangedEvent e);
		
}
