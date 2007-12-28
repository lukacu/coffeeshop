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

import java.math.BigDecimal;

/**
 * A {@link org.coffeeshop.string.parsers.StringParser} for parsing BigDecimals. 
 * The parse() method delegates the actual parsing to BigDecimal's constructor.
 * 
 * @author Luka Cehovin
 * @see org.coffeeshop.string.parsers.StringParser
 * @see java.math.BigDecimal
 * @since CoffeeShop Core 1.0
 */
public class BigDecimalStringParser implements StringParser {

	private static final BigDecimalStringParser INSTANCE = new BigDecimalStringParser();

	/** Returns a {@link BigDecimalStringParser}.
	 *
	 * <p>Convenient access to the only instance returned by
	 * this method is available through
	 * {@link org.coffeeshop.arguments.Arguments#BIGDECIMAL_PARSER}.
	 *  
	 * @return a {@link BigDecimalStringParser}.
	 */
	
    public static BigDecimalStringParser getParser() {
		return INSTANCE; 
	}

    /**
     * Parses the specified argument into a BigDecimal.  This method simply
     * delegates the parsing to <code>new BigDecimal(String)</code>.  
     * If BigDecimal throws a NumberFormatException, it is encapsulated 
     * into a ParseException and re-thrown.
     *
     * @param arg the argument to parse
     * @return a BigDecimal object with the value contained in the specified
     * argument.
     * @throws ParseException if <code>new BigDecimal(arg)</code> throws a
     * NumberFormatException.
     * @see BigDecimal
     * @see org.coffeeshop.string.parsers.StringParser#parse(String)
     */
    public Object parse(String arg) throws ParseException {
        BigDecimal result = null;
        try {
            result = new BigDecimal(arg);
        } catch (NumberFormatException e) {
            throw (
                new ParseException(
                    "Unable to convert '" + arg + "' to a BigDecimal.",
                    e));
        }
        return (result);
    }
}
