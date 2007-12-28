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
 * Interface responsible for converting Strings into Objects. Each implementation of
 * StringParser is capable of parsing a String into a different class of object. 
 *  
 * @author Luka Cehovin
 */
public interface StringParser {

    /**
     * Parses the specified argument into an Object of the appropriate type.
     * If the specified argument cannot be converted into the desired 
     * Object, a ParseException should be thrown.<br>
     * <br>
     * <b>Note:</b> this method MAY BE CALLED with a <b>null</b> argument.
     * Take this into consideration when subclassing!
     * 
     * @param arg the argument to convert to an Object of class appropriate to
     * the StringParser subclass.
     * @return the Object resulting from the parsed argument.
     * @throws ParseException if the specified argument cannot be parsed.
     */
    public Object parse(String arg) throws ParseException;

}
