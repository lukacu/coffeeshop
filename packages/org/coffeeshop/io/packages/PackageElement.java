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

package org.coffeeshop.io.packages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Provides not yet complete representation of a package element i.e. file in
 * the Java package organisation structure
 * 
 * @author lukacu
 * @since CoffeeShop 1.0
 */

public class PackageElement {

	private String _name, _pack, _archive;
	private Class<?> _neighbor = null;
	
	/**
	 * Constructs a new package element.
	 * 
	 * @param name name of the element
	 * @param pack package 
	 * @param archive archive in in which the element is located
	 */
	public PackageElement(String name, String pack, String archive) {
		this._name = name;
		this._pack = pack;
		this._archive = archive;
	}

	/**
	 * Constructs a new package element.
	 * 
	 * @param name name of the element
	 * @param neighborClass the class that shares the package with the
	 * element
	 */
	public PackageElement(String name, Class<?> neighborClass) {
		this._name = name;
		this._neighbor = neighborClass;
		this._pack = "";
		this._archive = neighborClass.getPackage().getName();
	}	
	
	/**
	 * Returns the name of the element
	 * 
	 * @return the name of the element
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Returns the name of the package
	 * 
	 * @return the name of the package
	 */
	public String getPackage() {
		return _pack;
	}
	
	/**
	 * Returns the name of the archive
	 * 
	 * @return the name of the archive
	 */
	public String getArchive() {
		return _archive;
	}	
	
	/**
	 * Returns the input stream that can be used to read the data of
	 * the element
	 * 
	 * @return input stream
	 * @throws IOException If the element is not found
	 */
	public InputStream getStream() throws IOException {
		if (_neighbor != null)
			return _neighbor.getResourceAsStream(_name);		
		
		File a = new File(_archive);
		if (a.isDirectory()) {
			File f = new File(a.getAbsolutePath() + File.separator +  
					_pack.replace('.', File.separatorChar) + File.separator + _name);
			if (!f.exists())
				throw new IOException("Element not found in directory " + _archive);
			FileInputStream in = new FileInputStream(f);
			return in;
		}
		else {
		    JarFile jar = new JarFile(_archive);
		    ZipEntry entry = jar.getEntry(_pack.replace('.', '/') + '/' + _name);
	        if (entry == null) 
	            throw new IOException("Element not found in Jar file " + _archive);      
	        return jar.getInputStream(entry);
		}
	}
	
}
