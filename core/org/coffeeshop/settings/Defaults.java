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

import java.util.HashMap;
import java.util.Set;

// TODO: javadoc
public class Defaults extends AbstractReadonlySettings implements SettingsMetadata {

	protected class DefaultElement {
		
		private String value, description;
		
		public String getDescription() {
			return description;
		}
		
		public String getValue() {
			return value;
		}
		
		public DefaultElement(String value, String description) {
			if (value == null)
				throw new IllegalArgumentException("Must define default value");

			this.value = value;
			this.description =  description;
		}
		
	}

	protected HashMap<String, DefaultElement> map = new HashMap<String, DefaultElement>();
	
	public Defaults(ReadableSettings parent) {
		super(parent);
	}
	
	public boolean addDefaultElement(String key, String value, String description) {
		
		return addDefaultElement(key, new DefaultElement(value, description));

	}
	
	protected boolean addDefaultElement(String key, DefaultElement e) {
		
		if (map.containsKey(key))
			return false;
		
		map.put(key, e);
		
		return true;
	}
	
	protected String getProperty(String key) {
		DefaultElement e = map.get(key);
		if (e == null) return null;
		return e.value;
	}

	public Set<String> getKeys() {
		
		Set<String> keys = map.keySet();
		
		return keys;
	}

	@Override
	public String getComment(String key) {
		return null;
	}

	@Override
	public String getTitle(String key) {
		DefaultElement e = map.get(key);
		
		if (e == null)
			return null;
		
		return e.description;
	}
	
}
