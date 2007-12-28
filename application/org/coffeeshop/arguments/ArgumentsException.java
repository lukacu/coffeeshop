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
package org.coffeeshop.arguments;

/**
 * The base class for all of argument exceptions.  A ArgumentException can
 * encapsulate another Exception, which can be obtained via the getRootCause()
 * method.  This is useful in cases where subclasses might need to
 * throw an exception that the argument engine is not expecting, such as an 
 * IOException while loading a DefaultSource. The subclass can in these cases throw a new
 * Argument Exception encapsulating the IOException.
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see org.coffeeshop.arguments.Flagged
 * @see org.coffeeshop.arguments.Option
 */
public class ArgumentsException extends Exception {

    /**
	 * Serialization...
	 */
	private static final long serialVersionUID = 7548514260144509896L;

	/**
     * Creates a new JSAPException.
     */
    public ArgumentsException() {
        super();
    }

    /**
     * Creates a new JSAPException with the specified message.
     * @param msg the message for this JSAPException.
     */
    public ArgumentsException(String msg) {
        super(msg);
    }

    /**
     * Creates a new JSAPException encapsulating the specified Throwable.
     * @param cause the Throwable to encapsulate.
     */
    public ArgumentsException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new JSAPException with the specified message encapsulating
     * the specified Throwable.
     * @param msg the message for this JSAPException.
     * @param cause the Throwable to encapsulate.
     */
    public ArgumentsException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
