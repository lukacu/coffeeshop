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

import java.io.File;

/**
 * A StringParser for parsing {@link File} objects.  The parse() method
 * delegates the actual
 * parsing to <code>new File(String)</code>.  If <code>new File(String)</code>
 * throws a NullPointerException, it is encapsulated in a ParseException and
 * re-thrown.
 *
 * @author Luka Cehovin
 * @see org.coffeeshop.string.parsers.StringParser
 * @see java.net.URL
 */
public class FileStringParser implements StringParser {

	private boolean mustExist = false;
	private boolean mustBeDirectory = false;
	private boolean mustBeFile = false;

	/**
	 * Creates a new file parser
	 * 
	 * @param mustExist
	 * @param mustBeDirectory
	 * @param mustBeFile
	 */
	public FileStringParser(boolean mustExist, boolean mustBeDirectory, boolean mustBeFile) {
		super();
		this.mustExist = mustExist;
		this.mustBeDirectory = mustBeDirectory;
		this.mustBeFile = mustBeFile;
	}

	/**
	 * Parses the specified argument into a File.  This method delegates the
	 * actual
	 * parsing to <code>new File(arg)</code>.  If <code>new File(arg)</code>
	 * throws a NullPointerException, it is encapsulated in a ParseException
	 * and re-thrown.
	 *
	 * @param arg the argument to parse
	 * @return a File as specified by arg.
	 * @throws ParseException if <code>new File(arg)</code> throws a
	 * NullPointerException.
	 * @see java.io File
	 * @see org.coffeeshop.string.parsers.StringParser#parse(String)
	 */
	public Object parse(String arg) throws ParseException {
		File result = null;
		try {
			result = new File(arg);

            if (mustExist && !result.exists()) {
                throw (new ParseException(result + " does not exist."));
            }
			if (mustBeDirectory && result.exists() && !result.isDirectory()) {
				throw (new ParseException(result + " is not a directory."));
			}
			if (mustBeFile && result.exists() && result.isDirectory()) {
				throw (new ParseException(result + " is not a file."));
			}
		} catch (NullPointerException e) {
			throw (
				new ParseException(
					"No File given to parse",
					e));
		}
		return (result);
	}
}
