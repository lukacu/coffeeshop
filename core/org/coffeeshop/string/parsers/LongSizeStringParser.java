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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** A {@link org.coffeeshop.string.parsers.StringParser} that lets the user specify sizes with an optional unit.
 * 
 * <P>This parser will parse its argument using {@link #parseSize(CharSequence)}.
 * 
 * @author Sebastiano Vigna
 * @see org.coffeeshop.string.parsers.IntSizeStringParser
 */

public class LongSizeStringParser implements StringParser {

	/** The regular expression used to parse the size specification. */
	private static final Pattern PARSE_SIZE_REGEX = Pattern.compile( "((#|0x|0X)?[0-9A-Fa-f]+)([KMGTP]i?)?" );

	/** The big integer used to check that the result is smaller than {@link Long#MAX_VALUE}. */
	private static final BigInteger LONG_MAX_VALUE = new BigInteger( Long.toString( Long.MAX_VALUE ) );
	
	/** A map from units to their size. */
	private static final HashMap<String, Long> UNIT2SIZE = new HashMap<String, Long>();

	/** The only instance of this parser. Aliased to <code>JSAP.LONG_SIZE_PARSER</code>. */
	final static LongSizeStringParser INSTANCE = new LongSizeStringParser();
	
	private LongSizeStringParser() {}
	
	/** Returns the only instance of a {@link LongSizeStringParser}.
	 * 
	 * <p>Convenient access to the only instance returned by
	 * this method is available through
	 * {@link org.coffeeshop.arguments.Arguments#LONGSIZE_PARSER}.
	 *  
	 * @return the only instance of a {@link LongSizeStringParser}.
	 */
	public static LongSizeStringParser getParser() {
		return INSTANCE;
	}
	
	
	public Object parse( String arg ) throws ParseException {
		return new Long( parseSize( arg ) );
	}
	
	static {
		UNIT2SIZE.put( "K", new Long( (long)1E3 ) );
		UNIT2SIZE.put( "M", new Long( (long)1E6 ) );
		UNIT2SIZE.put( "G", new Long( (long)1E9 ) );
		UNIT2SIZE.put( "T", new Long( (long)1E12 ) );
		UNIT2SIZE.put( "P", new Long( (long)1E15 ) );
		UNIT2SIZE.put( "Ki", new Long( 1L << 10 ) );
		UNIT2SIZE.put( "Mi", new Long( 1L << 20 ) );
		UNIT2SIZE.put( "Gi", new Long( 1L << 30 ) );
		UNIT2SIZE.put( "Ti", new Long( 1L << 40 ) );
		UNIT2SIZE.put( "Pi", new Long( 1L << 50 ) );
	}

	/** Parses a size specified using units (e.g., K, Ki, M, Mi,&hellip;).
	 * 
	 * <P>The argument must be in the form <var>number</var> [<var>unit</var>] (with
	 * no space inbetween). <var>number</var> is anything accepted by {@link Long#decode(java.lang.String)},
	 * which allows, besides decimal numbers, hexadecimal numbers prefixed by <code>0x</code>, <code>0X</code> or <code>#</code>,
	 * and octal numbers prefixed by <code>0</code>.
	 * <var>unit</var> may be one of K (10<sup>3</sup>), Ki (2<sup>10</sup>), M (10<sup>6</sup>), Mi (2<sup>20</sup>), 
	 * G (10<sup>9</sup>), Gi (2<sup>30</sup>), T (10<sup>12</sup>), Ti (2<sup>40</sup>), 
	 * P (10<sup>15</sup>), Pi (2<sup>50</sup>). Thus, for instance, <samp>1Ki</samp> is
	 * 1024, whereas <samp>9M</samp> is nine millions and <code>0x10Pi</code> is 18014398509481984. 
	 * Note that in the number part case does not matter, but in the unit part it does.
	 * 
	 * @param s a size specified as above.
	 * @return the corresponding unitless size.
	 * @throws ParseException if <code>s</code> is malformed, <var>number</var> is negative or
	 * the resulting size is larger than {@link Long#MAX_VALUE}.
	 */
		
	public static long parseSize( final CharSequence s ) throws ParseException {
		final Matcher m = PARSE_SIZE_REGEX.matcher( s );
		if ( ! m.matches() ) throw new ParseException( "Invalid size specification '" + s + "'." );
		
		final String unit = m.group( 3 );

		BigInteger unitSize = BigInteger.ONE;

		if ( unit != null ) {
			Long unitSizeObj = (Long)UNIT2SIZE.get( unit );
			if ( unitSizeObj == null ) throw new ParseException( "Invalid unit specification '" + unit + "'." );
			unitSize = new BigInteger( unitSizeObj.toString() );
		}

		final String number = m.group( 1 );
		final Long size;

		try {
			size = Long.decode( number );
			if ( size.longValue() < 0 ) throw new ParseException( "Sizes cannot be negative." );
		}
		catch( NumberFormatException e ) {
			throw new ParseException( "Invalid number '" + number + "'." );
		}
		
		BigInteger result = new BigInteger( size.toString() ).multiply( unitSize );
		
		if ( result.compareTo( LONG_MAX_VALUE ) > 0 ) throw new ParseException( "Size '" + s + "' is too big." );
		
		return Long.parseLong( result.toString() );
	}
}
