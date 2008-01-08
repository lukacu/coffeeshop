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
 * 
 */

package org.coffeeshop.swing;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.coffeeshop.io.Files;
import org.coffeeshop.string.StringUtils;

/**
 * A class exists in Java 1.6 SE API, but it is provided here for
 * 1.5 compatibility.
 * 
 * @author Luka Cehovin
 *
 */
public class FileNameExtensionFilter extends FileFilter {
	
	private String description, extension;
	
	public FileNameExtensionFilter(String description, String extension) {
		this.description = description;
		this.extension = extension;
	}
	
	
	@Override
	public boolean accept(File arg0) {

		int i = arg0.getName().lastIndexOf(".");
		
		if (i == -1)
			return StringUtils.empty(extension);
		
		String e = arg0.getName().substring(i+1);
		
		if (e.length() == 0)
			return StringUtils.empty(extension);
		
		return e.equalsIgnoreCase(extension) ;

	}

	@Override
	public String getDescription() {
		return description;
	}

}
