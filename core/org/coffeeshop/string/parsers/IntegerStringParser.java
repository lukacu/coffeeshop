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
 * A {@link org.coffeeshop.string.parsers.StringParser} for parsing Integers.  The parse() method delegates the actual
 * parsing to Integer.decode(String).
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see org.coffeeshop.string.parsers.StringParser
 * @see java.lang.Integer
 */
public class IntegerStringParser implements StringParser {
	
	private static final IntegerStringParser INSTANCE = new IntegerStringParser();	

	/** Returns a {@link IntegerStringParser}.
	 * 
	 * <p>Convenient access to the only instance returned by
	 * this method is available through
	 * {@link org.coffeeshop.arguments.Arguments#INTEGER_PARSER}.
	 *  
	 * @return a {@link IntegerStringParser}.
	 */
    public static IntegerStringParser getParser() {
		return INSTANCE;
	}


    /**
     * Parses the specified argument into an Integer.  This method delegates
     * the parsing to <code>Integer.decode(arg)</code>.  If
     * <code>Integer.decode()</code> throws a
     * NumberFormatException, it is encapsulated into a ParseException and
     * re-thrown.
     *
     * @param arg the argument to parse
     * @return an Integer object with the value contained in the specified
     * argument.
     * @throws ParseException if <code>Integer.decode(arg)</code> throws a
     * NumberFormatException.
     * @see java.lang.Integer
     * @see org.coffeeshop.string.parsers.StringParser#parse(String)
     */
    public Object parse(String arg) throws ParseException {
        Integer result = null;
        try {
            result = Integer.decode(arg);
        } catch (NumberFormatException nfe) {
            throw (
                new ParseException(
                    "Unable to convert '" + arg + "' to an Integer.",
                    nfe));
        }
        return (result);
    }
}
