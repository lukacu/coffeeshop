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

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A {@link org.coffeeshop.string.parsers.StringParser} for parsing java.net.InetAddress objects.  The parse() method
 * delegates the actual
 * parsing to <code>InetAddress.getByName(String)</code>.  If
 * <code>InetAddress.getByName()</code>
 * throws an UnknownHostException, it is encapsulated in a ParseException and
 * re-thrown.
 *
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see org.coffeeshop.string.parsers.StringParser
 * @see java.net.InetAddress
 */
public class InetAddressStringParser implements StringParser {

	private static final InetAddressStringParser INSTANCE = new InetAddressStringParser();	

	/** Returns a {@link InetAddressStringParser}.
	 * 
	 * <p>Convenient access to the only instance returned by
	 * this method is available through
	 * {@link org.coffeeshop.arguments.Arguments#INETADDRESS_PARSER}.
	 *  
	 * @return a {@link InetAddressStringParser}.
	 */

    public static InetAddressStringParser getParser() {
		return INSTANCE;
	}

    /**
     * Parses the specified argument into an InetAddress.  This method
     * delegates the actual
     * parsing to <code>InetAddress.getByName(arg)</code>.  If
     * <code>InetAddress.getByName(arg)</code>
     * throws an UnknownHostException, it is encapsulated in a ParseException
     * and re-thrown.
     *
     * @param arg the argument to parse
     * @return an InetAddress object representing the specified address.
     * @throws ParseException if <code>InetAddress.getByName(arg)</code> throws
     * an UnknownHostException.
     * @see java.net InetAddress
     * @see org.coffeeshop.string.parsers.StringParser#parse(String)
     */
    public Object parse(String arg) throws ParseException {
        InetAddress result = null;
        try {
            result = InetAddress.getByName(arg);
        } catch (UnknownHostException e) {
            throw (new ParseException("Unknown host: " + arg, e));
        }
        return (result);
    }
}
