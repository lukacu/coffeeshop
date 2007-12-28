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
 * A {@link org.coffeeshop.string.parsers.StringParser} for parsing Floats.  The <code>parse()</code> method delegates
 * the actual
 * parsing to <code>new Float(String)</code>.  If <code>new Float(String)</code>
 * throws a NumberFormatException, it
 * is encapsulated in a ParseException and re-thrown.
 *
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see org.coffeeshop.string.parsers.StringParser
 * @see java.lang.Float
 */
public class FloatStringParser implements StringParser {

	private static final FloatStringParser INSTANCE = new FloatStringParser();	

	/** Returns a {@link FloatStringParser}.
	 * 
	 * <p>Convenient access to the only instance returned by
	 * this method is available through
	 * {@link org.coffeeshop.arguments.Arguments#FLOAT_PARSER}.
	 *  
	 * @return a {@link FloatStringParser}.
	 */

    public static FloatStringParser getParser() {
		return INSTANCE;
	}

    /**
     * Parses the specified argument into a Float.  This method delegates the
     * actual
     * parsing to new Float(String).  If new Float(String) throws a
     * NumberFormatException, it
     * is encapsulated in a ParseException and re-thrown.
     *
     * @param arg the argument to parse
     * @return a Float object with the value contained in the specified
     * argument.
     * @throws ParseException if <code>new Float(arg)</code> throws a
     * NumberFormatException.
     * @see java.lang.Float
     * @see org.coffeeshop.string.parsers.StringParser#parse(String)
     */
    public Object parse(String arg) throws ParseException {
        Float result = null;
        try {
            result = new Float(arg);
        } catch (NumberFormatException e) {
            throw (
                new ParseException(
                    "Unable to convert '" + arg + "' to a Float.",
                    e));
        }
        return (result);
    }
}
