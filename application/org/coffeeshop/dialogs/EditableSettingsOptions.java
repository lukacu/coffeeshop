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

package org.coffeeshop.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.coffeeshop.string.parsers.StringParser;

public class EditableSettingsOptions {

	private String title, description;
	
	private StringParser parser;

	private boolean enabled = true;
	
	private ArrayList<String> helpers = new ArrayList<String>();

	public EditableSettingsOptions(String title, String description, StringParser parser, boolean enabled) {
		super();
		this.title = title;
		this.description = description;
		this.parser = parser;
		this.enabled = enabled;
	}

	public void addHelper(String helper) {
		
		if (helpers.contains(helper))
			return;
		
		helpers.add(helper);
		
	}
	
	public String getDescription() {
		return description;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public List<String> getHelpers() {
		return helpers;
	}

	public StringParser getParser() {
		return parser;
	}

	public String getTitle() {
		return title;
	}
	
	
	
}
