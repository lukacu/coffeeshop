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

package org.coffeeshop.external;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.URL;


/**
 * This class provides basic information about the operating system that the
 * application is running on.
 * 
 * This class is a member of CoffeeShop Core package.
 * 
 * @author Luka Cehovin
 * @since CoffeeShop Core 1.0
 * 
 */
public class OperatingSystem {

	/**
	 * Enumerated identifiers for the operating systems. <b>Note:</b> Those
	 * operating systems are currently supported. You can add support for new
	 * ones.
	 * 
	 * @author lukacu
	 * 
	 */
	public static enum OperatingSystemType {
		UNKNOWN, LINUX, WINDOWS, OSX
	}

	private static OperatingSystemType system;

	/**
	 * Static constructor of this class. This code is executed when this class
	 * is accessed for the first time.
	 */
	static {
		system = OperatingSystemType.UNKNOWN;
		String os = System.getProperty("os.name");

		if (os.toLowerCase().indexOf("linux") > -1)
			system = OperatingSystemType.LINUX;
		if (os.toLowerCase().indexOf("windows") > -1)
			system = OperatingSystemType.WINDOWS;
		if (os.toLowerCase().indexOf("mac") > -1)
			system = OperatingSystemType.OSX;

	}

	/**
	 * Returns the operating system type that the application is running on.
	 * 
	 * @return Enumerated OS type
	 * @see OperatingSystemType
	 */
	public static OperatingSystemType getOperatingSystemType() {
		return system;
	}

	/**
	 * This method returns the maximum amount of memory that can be used by Java
	 * Virtual Machine.
	 * 
	 * @return the number of bytes or -1 if the information is undefined.
	 */
	public static long getMemoryLimit() {

		MemoryMXBean memStatus = ManagementFactory.getMemoryMXBean();
		MemoryUsage usage = memStatus.getHeapMemoryUsage();
		return usage.getMax();

	}

	/**
	 * This method returns the amount of memory that is used by Java Virtual
	 * Machine.
	 * 
	 * @return the number of bytes used.
	 */
	public static long getMemoryUsed() {

		MemoryMXBean memStatus = ManagementFactory.getMemoryMXBean();
		MemoryUsage usage = memStatus.getHeapMemoryUsage();
		return usage.getUsed();

	}

	/**
	 * Open an url using the default system handler
	 * 
	 * @param url
	 * @return
	 */
	public static boolean open(URL url) {
		if (url == null)
			throw new IllegalArgumentException("Null argument");
		
		return true;
	}
	
	/**
	 * This method returns the number of CPUs in the machine.
	 * 
	 * @return the number of CPUs in the machine
	 */
	public static int getProcessorCount() {
		
		return Runtime.getRuntime().availableProcessors();
		
	}
	
	public static File getSystemTemporaryDirectory() throws IOException {
		String tmpRoot = null;
		
		switch (OperatingSystem.getOperatingSystemType()) {
		case LINUX:
		case OSX:
			tmpRoot = "/tmp/";
			break;

		case WINDOWS:
			tmpRoot = "C:\\Temp\\";
			
			break;
		default:
			throw new IOException("This operating system is not supported.");
		}
		
		File f = new File(tmpRoot); 
		if (!f.exists())
			throw new IOException("Cannot locate temporary directory " + tmpRoot);
		
		if (!f.isDirectory())
			throw new IOException("Not a directory: " + tmpRoot);
			
		if (!f.canWrite())
			throw new IOException("Not writeable: " + tmpRoot);
		
		return f;
		
	}
}
