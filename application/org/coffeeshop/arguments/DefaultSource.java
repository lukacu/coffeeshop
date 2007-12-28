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
 * An interface describing an object as being able to produce a set of default
 * values.
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see org.coffeeshop.arguments.Defaults
 */
public interface DefaultSource {

    /**
     * Returns a set of default values given the configuration described by the
     * specified IDMap.  Any encountered exceptions are stored in the specified
     * JSAPResult.
     * @param idMap an IDMap containing JSAP configuration information.
     * @param exceptionMap the ExceptionMap object within which any Exceptions
     * will be returned.
     * @return a set of default values given the configuration described by the
     * specified IDMap.
     * @see org.coffeeshop.arguments.ExceptionMap#addException(String,Exception)
     */
    Defaults getDefaults(IDMap idMap, ExceptionMap exceptionMap);

}
