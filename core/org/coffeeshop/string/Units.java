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
package org.coffeeshop.string;

/**
 * Static class <code>Units</code> provides convenient methods for 
 * converting and presenting units.
 * 
 * Class is a member of CoffeeShop Core package.
 * 
 * @author Luka Cehovin
 * @author Mitar
 * @since CoffeeShop Core 1.0
 */

public class Units {

	/**
	 * Kilobites abbreviation
	 */
	public static String KILOBYTES = "kB";
	/**
	 * Megabytes abbreviation
	 */
	public static String MEGABYTES = "MB";
	/**
	 * Kibibites abbreviation
	 */
	public static String KIBIBYTES = "KiB";
	/**
	 * Mebibytes abbreviation
	 */
	public static String MEBIBYTES = "MiB";
	
	/**
	 * Converts bytes to kilobytes string representation with optional two
	 * decimal digits.
	 * 
	 * @param bytes
	 * @param decimals show/hide decimal digits
	 * @return kilobytes string representation
	 */	
	public static String getKilobytes(long bytes, boolean decimals) {
	    if (decimals) {
		return String.format("%.2f %s", (bytes / 1000.0), KILOBYTES);
	    }
	    else {
		return String.format("%.0f %s", (bytes / 1000.0), KILOBYTES);
	    }
	}

	/**
	 * Converts bytes to kibibytes string representation with optional two
	 * decimal digits. See http://en.wikipedia.org/wiki/Kibibyte for info
	 * on kibibyte unit.
	 * 
	 * @param bytes
	 * @param decimals show/hide decimal digits
	 * @return kibibytes string representation
	 */	
	public static String getKibibytes(long bytes, boolean decimals) {
	    if (decimals) {
		return String.format("%.2f %s", (bytes / 1024.0), KIBIBYTES);
	    }
	    else {
		return String.format("%.0f %s", (bytes / 1024.0), KIBIBYTES);
	    }
	}
	
	/**
	 * Converts bytes to mebibytes string representation with optional two
	 * decimal digits. See http://en.wikipedia.org/wiki/Mebibyte for info
	 * on mebibyte unit.
	 * 
	 * @param bytes
	 * @param decimals show/hide decimal digits
	 * @return mebibytes string representation 
	 */	
	public static String getMebibytes(long bytes, boolean decimals) {
	    if (decimals) {
		return String.format("%.2f %s", (bytes / 1048576.0), MEBIBYTES);
	    }
	    else {
		return String.format("%.0f %s", (bytes / 1048576.0), MEBIBYTES);
	    }
	}	

	/**
	 * Converts bytes to megabytes string representation with optional two
	 * decimal digits. 
	 * 
	 * @param bytes
	 * @param decimals show/hide decimal digits
	 * @return megabytes string representation 
	 */	
	public static String getMegabytes(long bytes, boolean decimals) {
	    if (decimals) {
		return String.format("%.2f %s", (bytes / 1000000.0), MEGABYTES);
	    }
	    else {
		return String.format("%.0f %s", (bytes / 1000000.0), MEGABYTES);
	    }
	}	
	
}
