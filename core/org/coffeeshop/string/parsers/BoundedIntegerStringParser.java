/* THIS FILE IS A MEMBER OF THE COFFEESHOP LIBRARY
 * 
 * License:
 * 
 * Coffeeshop is a conglomerate of handy general purpose Java classes.  
 * 
 * Copyright (C) 2006-2010 Luka Cehovin
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
 */


package org.coffeeshop.string.parsers;


public class BoundedIntegerStringParser implements StringParser {
	
	private int min, max;
	
	

    public BoundedIntegerStringParser(int min, int max) {
		super();
		this.max = Math.max(max, min);
		this.min = Math.min(min, max);
	}

	public int getMin() {
		return min;
	}



	public int getMax() {
		return max;
	}

	public Object parse(String arg) throws ParseException {
        Integer result = null;
        try {
            result = Integer.decode(arg);
            
            if (result < min || result > max)
            	new ParseException("Value out of bounds");
            
        } catch (NumberFormatException nfe) {
            throw (
                new ParseException(
                    "Unable to convert '" + arg + "' to an Integer.",
                    nfe));
        }
        return (result);
    }
}
