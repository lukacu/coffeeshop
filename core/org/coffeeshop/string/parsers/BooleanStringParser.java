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
 * <p>A {@link org.coffeeshop.string.parsers.StringParser} for parsing Booleans.</p>
 *
 * <p>When parsing, the following arguments are interpreted as TRUE:
 * <ul>
 *         <li>null</i>
 *         <li>"t" (case-insensitive)</li>
 *         <li>"true" (case-insensitive)</li>
 *         <li>"y" (case-insensitive)</li>
 *         <li>"yes" (case-insensitive)</li>
 *         <li>"1"</li>
 * </ul>
 * <p>The following arguments are interpreted as FALSE:
 * <ul>
 *         <li>"f" (case-insensitive)</li>
 *         <li>"false" (case-insensitive)</li>
 *         <li>"n" (case-insensitive)</li>
 *         <li>"no" (case-insensitive)</li>
 *         <li>"0"</li>
 * </ul>
 * 
 * <p>All other input values throw a ParseException.
 * 
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see org.coffeeshop.string.parsers.StringParser
 * @see java.lang.Boolean
 */
public class BooleanStringParser implements StringParser {

	private static final BooleanStringParser INSTANCE = new BooleanStringParser();	

	/** Returns a {@link BooleanStringParser}.
	 * 
	 * <p>Convenient access to the only instance returned by
	 * this method is available through
	 * {@link org.coffeeshop.arguments.Arguments#BOOLEAN_PARSER}.
	 *  
	 * @return a {@link BooleanStringParser}.
	 */

    public static BooleanStringParser getParser() {
		return INSTANCE;
	}

    /**
     * Converts the specified argument into a Boolean.
     *
     * <p>When parsing, the following arguments are interpreted as TRUE:
     * <ul>
     *         <li>null</i>
     *         <li>"t" (case-insensitive)</li>
     *         <li>"true" (case-insensitive)</li>
     *         <li>"y" (case-insensitive)</li>
     *         <li>"yes" (case-insensitive)</li>
     *         <li>"1"</li>
     * <ul>
     * <p>The following arguments are interpreted as FALSE:
     * <ul>
     *         <li>"f" (case-insensitive)</li>
     *         <li>"false" (case-insensitive)</li>
     *         <li>"n" (case-insensitive)</li>
     *         <li>"no" (case-insensitive)</li>
     *         <li>"0"</li>
     * </ul>
     * 
     * <p>All other input throws a ParseException.
     * @param arg the argument to convert to a Boolean.
     * @return a Boolean as described above.
     * @throws ParseException if none of the above cases are matched.
     */
    public Object parse(String arg) throws ParseException {
        boolean result = false;
        if ((arg == null)
            || arg.equalsIgnoreCase("t")
            || arg.equalsIgnoreCase("true")
            || arg.equalsIgnoreCase("y")
            || arg.equalsIgnoreCase("yes")
            || arg.equals("1")) {
            result = true;
        } else if (
            arg.equalsIgnoreCase("f")
                || arg.equalsIgnoreCase("false")
                || arg.equalsIgnoreCase("n")
                || arg.equalsIgnoreCase("no")
                || arg.equals("0")) {
            result = false;
        } else {
            throw (
                new ParseException(
                    "Unable to convert '" + arg + "' to a boolean value."));
        }
        return (new Boolean(result));
    }
}
