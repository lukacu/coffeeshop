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
 * A {@link org.coffeeshop.string.parsers.StringParser} for parsing Longs.  The parse() method delegates the actual
 * parsing to <code>Long.decode(String)</code>.
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see org.coffeeshop.string.parsers.StringParser
 * @see java.lang.Long
 */
public class LongStringParser implements StringParser {
	
	private static final LongStringParser INSTANCE = new LongStringParser();	

	/** Returns a {@link LongStringParser}.
	 * 
	 * <p>Convenient access to the only instance returned by
	 * this method is available through
	 * {@link org.coffeeshop.arguments.Arguments#LONG_PARSER}.
	 *  
	 * @return a {@link LongStringParser}.
	 */
    public static LongStringParser getParser() {
		return INSTANCE;
	}

    /**
     * Parses the specified argument into a Long.  This method delegates
     * the parsing to <code>Long.decode(arg)</code>.  If
     * <code>Long.decode()</code> throws a
     * NumberFormatException, it is encapsulated into a ParseException and
     * re-thrown.
     *
     * @param arg the argument to parse
     * @return a Long object with the value contained in the specified argument.
     * @throws ParseException if <code>Long.decode(arg)</code> throws a
     * NumberFormatException.
     * @see java.lang.Long
     * @see org.coffeeshop.string.parsers.StringParser#parse(String)
     */
    public Object parse(String arg) throws ParseException {
        Long result = null;
        try {
            result = Long.decode(arg);
        } catch (NumberFormatException e) {
            throw (
                new ParseException(
                    "Unable to convert '" + arg + "' to a Long.",
                    e));
        }
        return (result);
    }
}
