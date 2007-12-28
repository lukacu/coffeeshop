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
 * This code is based on JSAP project from Martian Software, Inc.
 * http://www.martiansoftware.com/
 */

package org.coffeeshop.string.parsers;

/**
 * This exception can be thrown when an input string cannot be parsed
 * by an instance of {@link StringParser}. More details can be provided 
 * but it is nof mandatory to do so.
 * 
 * @author Luka Cehovin
 */
public class ParseException extends Exception {

	private static final long serialVersionUID = 1L;

	private String sourceString;
	
	private int position = -1;
	
    /**
     * Creates a new ParseException with the specified message.
     * @param msg the message for this ParseException.
     */
    public ParseException(String msg) {
        super(msg);
    }

    /**
     * Creates a new ParseException encapsulating the specified Throwable.
     * @param cause the Throwable to encapsulate.
     */
    public ParseException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new ParseException with the specified message encapsulating the
     * specified Throwable.
     * @param msg the message for this ParseException.
     * @param cause the Throwable to encapsulate.
     */
    public ParseException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Creates a new ParseException with the specified message encapsulating the
     * specified message, source string and position.
     * @param msg the message for this ParseException.
     * @param sourceString the source string
     * @param position the position in the source string.
     */
    public ParseException(String msg, String sourceString, int position) {
        super(msg);
        
        this.sourceString = sourceString;
        this.position = position;
    }

    /**
     * Returns the position in the source string relevant to this exception.
     * 
     * @return the position or -1 if it is not known.
     */
	public int getPosition() {
		return position;
	}

	/**
	 * Returns the source string relevant to this exception.
	 * 
	 * @return the string or <code>null</code> if not known.
	 */
	public String getSourceString() {
		return sourceString;
	}
}
