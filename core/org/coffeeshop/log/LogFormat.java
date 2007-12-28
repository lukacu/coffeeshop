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

package org.coffeeshop.log;

/**
 * Classes that want to provide a custom way of displaying log messages must
 * implement this interface
 * 
 * @author Luka Cehovin
 * @since CoffeeShop Core 1.0
 * @see org.coffeeshop.log.Logger
 */
public interface LogFormat {

	/**
	 * Format a message to be displayed by the logger
	 * 
	 * @param channel channel that the message has been sent to
	 * @param message message string
	 * @return formatted message
	 */
	public String formatReport(int channel, String message);
	
	/**
	 * Formats a throwable based message (exception or error) to be 
	 * displayed by the logger
	 * 
	 * @param channel channel that the message has been sent to
	 * @param t throwable object
	 * @return formatted message
	 */
	public String formatThrowable(int channel, Throwable t);
	
}
