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
 * An exception indicating that a required parameter was missing from the
 * supplied arguments and defaults.
 *
 * @see FlaggedOption#setAllowMultipleDeclarations(boolean)
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see org.coffeeshop.arguments.Option#required()
 */
public class RequiredParameterMissingException extends ArgumentsException {

	private static final long serialVersionUID = 1L;
	
	/**
     * The unique ID of the parameter that was missing.
     */
    private String id = null;

    /**
     * Creates a new RequiredParameterMissingException referencing the
     * specified parameter.
     * @param paramID the unique ID of the parameter that was missing.
     */
    public RequiredParameterMissingException(String paramID) {
        super("Parameter '" + paramID + "' is required.");
        this.id = paramID;
    }

    /**
     * Returns the unique ID of the parameter that was missing.
     * @return the unique ID of the parameter that was missing.
     */
    public String getID() {
        return (this.id);
    }

}
