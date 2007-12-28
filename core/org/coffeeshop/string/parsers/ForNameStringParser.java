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

import java.lang.reflect.Method;



/** A {@link org.coffeeshop.string.parsers.StringParser} that passes the
 * argument to a static method of signature <code>forName(String)</code> of a specified class.
 * 
 * <P>Note that, for instance, this parser can be used with {@link java.lang.Class} (resulting in a 
 * string parser identical to {@link org.coffeeshop.string.parsers.ClassStringParser}),
 * but also {@link java.nio.charset.Charset}, and more generally, any class using the <code>forName(String)</code>
 * convention.
 *
 * @author Sebastiano Vigna
 */

public class ForNameStringParser implements StringParser {

	/** The class array describing the parameters (a string) of <code>forName</code>. */
	private final static Class[] PARAMETERS = new Class[] { String.class };
	
	/** The class given to the constructor. */
	private final Class c;
	/** The <code>forName(String)</code> static method of {@link #klass}. */
	private final Method forName;

	private ForNameStringParser( final Class<?> c ) throws SecurityException, NoSuchMethodException {
		this.c = c;
		forName = c.getMethod( "forName", PARAMETERS );
	}
	
	/** Returns a class <code>forName()</code> string parser.
	 *
	 * <p>When required to parse an argument, the returned string parser will return the
	 * object obtain by means of a call to a static method of <code>klass</code> of signature
	 * <code>forName(String)</code>.
	 *  
	 * @param klass a class with a static method of signature <code>forName(String)</code>.
	 */
	
	public static ForNameStringParser getParser( final Class c ) throws SecurityException, NoSuchMethodException {
		return new ForNameStringParser( c );
	}
	
	public Object parse( String arg ) throws ParseException {
		try {
			return forName.invoke( c, new Object[] { arg } );
		}
		catch ( Exception e ) {
			throw new ParseException ( e );
		}
	}
}
