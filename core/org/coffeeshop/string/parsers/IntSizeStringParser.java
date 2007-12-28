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


/** A {@link org.coffeeshop.string.parsers.StringParser} that works like {@link LongSizeStringParser}, but
 * additionally checks that the result is not larger than {@link Integer#MAX_VALUE}.
 * 
 * @author Sebastiano Vigna
 */

public class IntSizeStringParser implements StringParser {

	/** The only instance of this parser. Aliased to <code>JSAP.INT_SIZE_PARSER</code>. */
	final static IntSizeStringParser INSTANCE = new IntSizeStringParser();
	
	private IntSizeStringParser() {}
	
	
	/** Returns the only instance of an {@link IntSizeStringParser}.
	 * 
	 * <p>Convenient access to the only instance returned by
	 * this method is available through
	 * {@link org.coffeeshop.arguments.Arguments#INTSIZE_PARSER}.
	 *  
	 * @return the only instance of an {@link IntSizeStringParser}.
	 */
	public static IntSizeStringParser getParser() {
		return INSTANCE;
	}

	public Object parse( String arg ) throws ParseException {
		final long size = LongSizeStringParser.parseSize( arg );
		if ( size > Integer.MAX_VALUE ) throw new ParseException( "Integer size '" + arg + "' is too big." );
		return new Integer( (int)size );
	}
}
