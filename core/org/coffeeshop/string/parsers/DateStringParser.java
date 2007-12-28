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

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * A {@link org.coffeeshop.string.parsers.StringParser} for parsing java.util.Date objects.  By default, arguments
 * are parsed using the
 * java.text.SimpleDateFormat for the default locale.  The format can be
 * overridden using this StringParser's
 * setProperties() method, supplying a java.util.Properties object with a
 * property key named "format".
 * The value associated with the "format" property is used to create a new
 * java.text.SimpleDateFormat
 * to parse the argument.
 * 
 * <p>A ParseException is thrown if a SimpleDateFormat cannot be constructed with
 * the specified format, or if the SimpleDateFormat throws a
 * java.text.ParseException during parsing.
 * 
 *
 * @author Luka Cehovin
 * @see org.coffeeshop.string.parsers.StringParser
 * @see java.util.Date
 * @see java.text.SimpleDateFormat
 */
public class DateStringParser implements StringParser {

    /**
     * The SimpleDateFormat used to do the parsing.
     */
    private SimpleDateFormat format = null;

    /** Returns a {@link DateStringParser}.
	 * 
	 * @return a {@link DateStringParser}.
	 */
    
	public DateStringParser(SimpleDateFormat format) {
		if (format == null)
			throw new IllegalArgumentException("Null format object");
		
		this.format = format;
	}

    /**
     * Parses the specified argument using either the java.text.SimpleDateFormat
     * for the current locale
     * (by default) or a java.text.SimpleDateFormat as defined by this
     * PropertyStringParser's "format"
     * property.
     *
     * If the specified argument cannot be parsed by the current format, a
     * ParseException is thrown.
     *
     * @param arg the argument to convert to a Date.
     * @return a Date as described above.
     * @throws ParseException if the specified argument cannot be parsed by the
     * current format..
     */
    public Object parse(String arg) throws ParseException {
        Date result = null;
        try {
            result = format.parse(arg);
        } catch (java.text.ParseException e) {
            throw (
                new ParseException(
                    "Unable to convert '" + arg + "' to a Date.",
                    e));
        }
        return (result);
    }

}
