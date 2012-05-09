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

import java.util.Date;

/**
 * Provides a default log format for the Logger class.
 * 
 * This class is a member of CoffeeShop Core package.
 * 
 * @author Luka Cehovin
 * @since CoffeeShop Core 1.0
 * @see org.coffeeshop.log.LogFormat
 * @see org.coffeeshop.log.Logger
 */
class DefaultFormat implements LogFormat {

	/**
	 * @see LogFormat#formatReport(int, String)
	 */
	public String formatReport(int channel, String message) {
		
		return String.format("[%1$ty/%1$tm/%1$td %1$tT] ", new Date()) + message;
	}

	/**
	 * @see LogFormat#formatThrowable(int, Throwable)
	 */
	public String formatThrowable(int channel, Throwable throwable) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("[%1$ty/%1$tm/%1$td %1$tT] ", new Date()));
		
		sb.append("Thrown object type: " + throwable.getClass().getCanonicalName());
		
		internalFormatThrowable(sb, throwable);

		return sb.toString();
	}
	
	private void internalFormatThrowable(StringBuilder sb, Throwable throwable) {
		
		if (throwable.getMessage() != null) {
			sb.append("\n                    ");
			sb.append("Message: " + throwable.getMessage());
		}
		
		sb.append("\n                    ");
		
		sb.append("Stack trace: ");
		
		StackTraceElement[] e = throwable.getStackTrace();
		
		for (int i = 0; i < e.length; i++) {
			sb.append("\n                      ");
			sb.append(e[i]);
		}
		
		if (throwable.getCause() != null) {
			Throwable cause = throwable.getCause();
			sb.append("\nCaused by: " + cause.getClass().getCanonicalName());
			internalFormatThrowable(sb, cause);
		}
	}
	
}
