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
 * A class for aggregating exceptions thrown by JSAP's parsing process.  This
 * class is necessary as it is desirable to have information regarding ALL of
 * the reasons for a failed parse rather than just the FIRST reason.
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 */
public interface ExceptionMap {

    /**
     * Adds the specified exception to the exception map.  Exceptions are
     * keyed by the ID of the parameters with which they are associated.
     * "General" exceptions not associated with a particular parameter have a
     * null key.
     * @param id the unique ID of the parameter with which the specified values
     * are associated.
     * @param exception the exception to associate with the specified key.
     */
    void addException(String id, Exception exception);

    /**
     * Returns the first exception associated with the specified parameter ID.
     * "General" exceptions can be retrieved with a null id.  If no exceptions
     * are associated with the specified parameter ID, null is returned.
     * @param id the unique ID of the parameter for which the first exception
     * is requested
     * @return the first exception associated with the specified ID, or null if
     * no exceptions are associated with the specified ID.
     */
    Exception getException(String id);

    /**
     * Returns an array of ALL exceptions associated with the specified
     * parameter ID. If no exceptions are associated with the specified
     * parameter ID, an empty (zero-length) array is returned.
     * @param id the unique ID of the parameter for which the exceptions are
     * requested.
     * @return an array of ALL exceptions associated with the specified
     * parameter ID,
     * or an empty (zero-length) array if no exceptions are associated with the
     * specified parameter ID.
     */
    Exception[] getExceptionArray(String id);

}
