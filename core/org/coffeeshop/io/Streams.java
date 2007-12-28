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
 */

package org.coffeeshop.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class provides static some handy static methods for streams usage.
 * 
 * @author lukacu
 * @since CoffeeShop 1.0
 */
public class Streams {

	/**
	 * Returns the contents of the input stream as byte array. This is
	 * only a convenience method that calls getStreamAsByteArray(InputStream, int)
	 * with -1 as a second parameter
	 * 
	 * @param stream
	 *            the <code>InputStream</code>
	 * @return the stream content as byte array
	 * @see Streams#getStreamAsByteArray(InputStream, int)
	 */
	public static byte[] getStreamAsByteArray(InputStream stream)
			throws IOException {
		return getStreamAsByteArray(stream, -1);
	}
	
	/**
	 * Returns the contents of the input stream as byte array.
	 * 
	 * @param stream
	 *            the <code>InputStream</code>
	 * @param length
	 *            the number of bytes to copy, if length < 0, the number is
	 *            unlimited
	 * @return the stream content as byte array
	 */
	public static byte[] getStreamAsByteArray(InputStream stream, int length)
			throws IOException {
		if (length == 0)
			return new byte[0];
		boolean checkLength = true;
		if (length < 0) {
			length = Integer.MAX_VALUE;
			checkLength = false;
		}
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

		int nextValue = stream.read();
		if (checkLength)
			length--;
		while (-1 != nextValue && length >= 0) {
			byteStream.write(nextValue);
			nextValue = stream.read();
			if (checkLength)
				length--;
		}
		byteStream.flush();

		return byteStream.toByteArray();
	}

}
