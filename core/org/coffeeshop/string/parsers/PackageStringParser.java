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
 * A {@link org.coffeeshop.string.parsers.StringParser} for parsing Packages.  The parse() method delegates the actual
 * parsing to <code>Package.getPackage(String)</code>, and returns the resulting
 * Package object.
 * If <code>Package.getPackage()</code> returns null, a ParseException is
 * thrown.
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see org.coffeeshop.string.parsers.StringParser
 * @see java.lang.Package
 */
public class PackageStringParser implements StringParser {
	
	private static final PackageStringParser INSTANCE = new PackageStringParser();	

	/** Returns a {@link PackageStringParser}.
	 * 
	 * <p>Convenient access to the only instance returned by
	 * this method is available through
	 * {@link org.coffeeshop.arguments.Arguments#PACKAGE_PARSER}.
	 *  
	 * @return a {@link PackageStringParser}.
	 */
    public static PackageStringParser getParser() {
		return INSTANCE;
	}

    /**
     * Parses the specified argument into a Package object.  This method
     * delegates the
     * parsing to <code>Package.getPackage(String)</code>, and returns the
     * resulting Package object.
     * If <code>Package.getPackage()</code> returns null, a ParseException is
     * thrown.
     *
     * @param arg the argument to parse
     * @return a Package object representing the specified package.
     * @throws ParseException if <code>Package.getPackage(arg)</code> returns
     * null.
     * @see java.lang.Package
     * @see org.coffeeshop.string.parsers.StringParser#parse(String)
     */
    public Object parse(String arg) throws ParseException {
        Package result = Package.getPackage(arg);
        if (result == null) {
            throw (
                new ParseException("Unable to locate Package '" + arg + "'."));
        }
        return (result);
    }
}
