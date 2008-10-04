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

package org.coffeeshop;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * This class provides some static constats that can be
 * used to identify the CoffeeShop version.
 * 
 * @author Luka Cehovin
 * @since CoffeeShop Core 1.0
 */
public class Coffeeshop {

	public static class PackageInfo {
		
		private int buildNumber;

		private String packageLocation;
		
		public int getBuildNumber() {
			return buildNumber;
		}

		public String getLocation() {
			return packageLocation;
		}
		
		private PackageInfo(int buildNumber, String packageLocation) {
			this.buildNumber = buildNumber;
			this.packageLocation = packageLocation;
			
		}
		
		private PackageInfo() {
			this.buildNumber = -1;
			this.packageLocation = null;
		}

	}
	
	private static final String[][] packages = new String[][] {
		{"core", "org.coffeeshop.Coffeeshop"},
		{"application", "org.coffeeshop.application.Application"},
		{"gui", "org.coffeeshop.awt.Images"}
	};
	
	/**
	 * Name: CoffeeShop
	 */
	public static final String NAME = "CoffeeShop";
	
	/**
	 * Version number as a string. Useful for displaying
	 * @deprecated Use {@link Coffeeshop#getCoffeeshopInfo()} from now on.
	 */
	public static final String VERSION = "1.0";
	
	/**
	 * Version number as an integer. Useful for comparing.
	 * @deprecated Use {@link Coffeeshop#getCoffeeshopInfo()} from now on.
	 */
	public static final int VERSION_NUMBER = 1;
	
	public static Map<String, PackageInfo> getCoffeeshopInfo() {
		
		HashMap<String, PackageInfo> info = new HashMap<String, PackageInfo>();
		
		for (int i = 0; i < packages.length; i++) {
		
			String packageName = packages[i][0];
			String packageClassName = packages[i][1];
			
			Class<?> packageClass;
			try {
				
				packageClass = Class.forName(packageClassName);
				
				URL location = packageClass.getProtectionDomain().getCodeSource().getLocation();
				
				if(location.getPath().endsWith(".jar")) {
					
					JarInputStream jar = new JarInputStream(location.openStream());
					
					Manifest manifest = jar.getManifest();
					
					int buildNumber = -1;
					
					try {
					
						buildNumber = Integer.parseInt(manifest.getMainAttributes().getValue("Implementation-Build"));
					
					} catch (NumberFormatException e) {
				
					}	
					
					info.put(packageName, new PackageInfo(buildNumber, location.getPath()));
					
				} else {
					info.put(packageName, new PackageInfo());
				}
				
				
			} catch (ClassNotFoundException e) {

			} catch (IOException e) {

				info.put(packageName, new PackageInfo());
				
			}		
		
		}
		
		return info;
	}
	
	
}
