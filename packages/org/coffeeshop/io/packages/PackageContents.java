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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**

 * A <code>PackageContents</code> lists the contents of a particular Java <code>Package</code>.
 * 
 * Notice: Java package is an abstract container that can span through
 * multiple archives (directories or jar files). Though this class provides
 * methods for listing 
 * This depends on the scanned archives.
 * 
 * @see Package
 * @author lukacu
 * @since CoffeeShop 1.0
 * @version 1.0
 */
public class PackageContents {
 
	private static final String CLASS_PATH = System.getProperty("java.class.path");
	private static final String PATH_SEPARATOR = System.getProperty("path.separator");
	private Vector<PackageElement> elements = new Vector<PackageElement>();
	private Vector<String> subpackages = new Vector<String>();
	/**
	 * Lists the contents of the <code>Package</code> pack. The package must be in the
	 * class-path (see <code>System.getProperty(String)</code>).
	 * @param pack The <code>Package</code> to list the contents of.
	 * @see System#getProperty(java.lang.String)
	 */
	public PackageContents(String pack) {
		//split the class-path into tokens
		StringTokenizer tokens = new StringTokenizer(CLASS_PATH, PATH_SEPARATOR);
		while (tokens.hasMoreTokens()) {
			extractContents(pack, tokens.nextToken());
		}
	} //end constructor

	/**
	 * Lists the contents of the <code>Package</code> pack. The package must be in the
	 * class-path (see <code>System.getProperty(String)</code>).
	 * @param pack The <code>Package</code> to list the contents of.
	 * @see System#getProperty(java.lang.String)
	 */
	public PackageContents(String pack, String[] classpath) {
		for (String path : classpath) {
			extractContents(pack, path);
		}
	} //end constructor	
	
	/**
	 * Lists the contents of the <code>Package</code> pack. The package must be in the
	 * class-path (see <code>System.getProperty(String)</code>).
	 * @param pack The <code>Package</code> to list the contents of.
	 * @param classpath The directory or jar file to check for the package.
	 * @see System#getProperty(java.lang.String)
	 */
	public PackageContents(String pack, String classpath) throws Exception {
		if (!extractContents(pack, classpath))
			throw new Exception("No contents found");
	} //end constructor	
		
	
	/**
	 * Lists the contents of the <code>Package</code> pack. The package must be in the
	 * class-path (see <code>System.getProperty(String)</code>).
	 * @param pack The <code>Package</code> to list the contents of.
	 * @param classpath The directory or jar file to check for the package.
	 * @see System#getProperty(java.lang.String)
	 */
	public boolean extractContents(String pack, String classpath) {
		File file = new File(classpath); 
		if (file.exists()) {
			//if the file is a directory, check it
			if (file.isDirectory()) {
				return checkDirectoryContents(file, pack);
			}
			else { //assume the file is a JAR and check it
				try {
					return checkJARFileContents(file, pack);
				} catch (Exception e) {}
			}
		}
		return false;
	} //end extractContents		
	
	/**
	 * Checks the contents of the specified directory for <code>File</code>s that are
	 * members of the package.
	 * @param directory The directory to check for package members.
	 * @param pack The <code>Package</code> to display the contents of.
	 */
	private boolean checkDirectoryContents(File directory, String pack) {
		//check the directory for sub-directories and class files
		File pkgDir = new File(directory.getAbsolutePath() + File.separator +  pack.replace('.', File.separatorChar));
		
		if (!(pkgDir.exists() && pkgDir.isDirectory())) return false;
		
		File[] files = pkgDir.listFiles();
		for (File file : files) {
			//if the file exists
			if (file.exists()) {
				//if the file is a directory, check the contents of that directory
				if (file.isDirectory()) {
					subpackages.add(file.getName());
				}
				else {
					this.elements.add(new PackageElement(file.getName(), pack, directory.getAbsolutePath()));
				}
			}
		}
		return true;
	} //end checkDirectoryContents
 
	/**
	 * Checks the contents of the JAR file for <code>Class</code>es that are members of
	 * the package.
	 * @param jarFile The JAR file to check for package members.
	 * @param pack The <code>Package</code> to display the contents of. Null if an external
	 * JAR is being checked.
	 * @throws Exception If an error occurs when attempting to access the JAR file.
	 */
	private boolean checkJARFileContents(File jarFile, String packageName) throws Exception {
        String pkgPath = packageName.replace('.', '/');
		//open the JAR file and begin reading its contents
        InputStream is = new BufferedInputStream(new FileInputStream(jarFile));
        JarInputStream jis = new JarInputStream(is);
        JarEntry entry;
        while ((entry = jis.getNextJarEntry()) != null) {
        	try {
        		if (entry.isDirectory()) {
        			if (entry.getName().startsWith(pkgPath))
        				subpackages.add(entry.getName());
        		}
        		else {
        			if (entry.getName().startsWith(pkgPath))
        				elements.add(new PackageElement(entry.getName(), packageName, jarFile.getAbsolutePath()));
        		}
            } catch (Exception e) {
            	//don't do anything - we want to keep looping
            }
        }
        return true;
	} //end checkJARFileContents
 
	/**
	 * Returns the contents of package, i.e. array of instances of 
	 * <code>PackageElement</code> that represent files in this package.
	 * 
	 * Notice: Java package is an abstract container that can span through
	 * multiple archives (directories or jar files). Files listed by this
	 * function may not be all the files that are actualy in this package.
	 * This depends on the scanned archives.
	 * 
	 * @return array of package elements
	 */
	public PackageElement[] getElements() {
		PackageElement[] r = new PackageElement[elements.size()];
		for(int i = 0; i < elements.size(); i++) {
			r[i] = elements.elementAt(i);
		}
		return r;
	} 
 
	/**
	 * Lists subpackages of this package.  
	 * 
	 * Notice: Java package is an abstract container that can span through
	 * multiple archives (directories or jar files). Packages listed by this
	 * function may not be all the packages that are actualy in this package.
	 * This depends on the scanned archives.
	 * 
	 * <code>PackageElement</code> that represent files in this package.
	 * @return array of package elements
	 */
	public String[] getSubpackages() {
		String[] r = new String[subpackages.size()];
		for(int i = 0; i < subpackages.size(); i++) {
			r[i] = subpackages.elementAt(i);
		}
		return r;
	} 
	
	/**
	 * 
	 * @return number of elements in this package.
	 */
	public int getElementCount() {
		return elements.size();
	}
	
	public PackageElement getElement(String name) {
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).getName().compareTo(name) == 0)
				return elements.get(i);
		}
		return null;
	}
} //end class
