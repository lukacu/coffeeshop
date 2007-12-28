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
 * A {@link org.coffeeshop.string.parsers.StringParser} for parsing Class objects.  The parse(arg) method calls
 * Class.forName(arg) and returns
 * the result.  If any exceptions are thrown by Class.forName(), they are
 * encapsulated in a ParseException
 * and re-thrown.
 * 
 * <p><b>Note:</b> The Class.forName() call attempts to load the class from the
 * same ClassLoader that loaded
 * this StringParser.
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see org.coffeeshop.string.parsers.StringParser
 * @see java.lang.Class
 */
public class ClassStringParser implements StringParser {

	private static final ClassStringParser INSTANCE = new ClassStringParser();	

	/** Returns a {@link ClassStringParser}.
	 * 
	 * <p>Convenient access to the only instance returned by
	 * this method is available through
	 * {@link org.coffeeshop.arguments.Arguments#CLASS_PARSER}.
	 *  
	 * @return a {@link ClassStringParser}.
	 */

    public static ClassStringParser getParser() {
		return INSTANCE;
	}

    /**
     * Parses the specified argument into a Class object.  This method calls
     * Class.forName(), passing
     * the specified argument as the name of the class to load, and returns
     * the resulting Class object.
     * If an exception is thrown by Class.forName(), it is encapsulated in a
     * ParseException and re-thrown.
     *
     * @param arg the argument to parse
     * @return a Class object representing the class named by the specified
     * argument.
     * @throws ParseException if <code>Class.forName(arg)</code> throws an
     * exception.
     * @see java.lang.Class
     * @see org.coffeeshop.string.parsers.StringParser#parse(String)
     */
    public Object parse(String arg) throws ParseException {
        Class result = null;
        try {
            result = Class.forName(arg);
        } catch (Exception e) {
            throw (
                new ParseException("Unable to locate class '" + arg + "'.", e));
        }
        return (result);
    }
}
