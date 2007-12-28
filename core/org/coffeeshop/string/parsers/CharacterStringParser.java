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
 * A {@link org.coffeeshop.string.parsers.StringParser} for parsing Characters.  The parse() method requires an
 * argument of length exactly
 * equal to 1 in order to perform the conversion; otherwise, a ParseException
 * is thrown.
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see org.coffeeshop.string.parsers.StringParser
 * @see java.lang.Character
 */
public class CharacterStringParser implements StringParser {

	private static final CharacterStringParser INSTANCE = new CharacterStringParser();	

	/** Returns a {@link CharacterStringParser}.
	 * 
	 * <p>Convenient access to the only instance returned by
	 * this method is available through
	 * {@link org.coffeeshop.arguments.Arguments#CHARACTER_PARSER}.
	 *  
	 * @return a {@link CharacterStringParser}.
	 */

    public static CharacterStringParser getParser() {
		return INSTANCE;
	}

	/**
     * Creates a new CharacterStringParser.
     * @deprecated Use {@link #getParser()} or, even better, {@link org.coffeeshop.arguments.Arguments#CHARACTER_PARSER}.
     */
    public CharacterStringParser() {
        super();
    }

    /**
     * Parses the specified argument into a Character.  The conversion is
     * performed by
     * checking that the specified argument is exactly 1 character long, then
     * encapsulating
     * that char in a Character object.  If the specified argument is not
     * exactly 1 character long,
     * a ParseException is thrown.
     *
     * @param arg the argument to parse
     * @return a Character object with the value contained in the specified
     * argument.
     * @throws ParseException if ( (arg==null) || (arg.length()!=1) )
     * @see java.lang.Character
     * @see org.coffeeshop.string.parsers.StringParser#parse(String)
     */
    public Object parse(String arg) throws ParseException {
        if ((arg == null) || (arg.length() != 1)) {
            throw (
                new ParseException(
                    "Unable to convert '" + arg + "' to a Character."));
        }
        return (new Character(arg.charAt(0)));
    }
}
