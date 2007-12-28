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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A utility class to allow lookups of parameter IDs by short flag or long flag.
 * This class is used by DefaultSource in order to populate Defaults objects.
 *
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see org.coffeeshop.arguments.Flagged
 * @see org.coffeeshop.arguments.DefaultSource
 * @see org.coffeeshop.arguments.Defaults
 */
class IDMap {

    /**
     * A list of the unique IDs of all the parameters stored in this IDMap.
     */
    private List<String> ids = null;

    /**
     * A Map associating short flags with parameter IDs.
     */
    private Map<Character, String> byShortFlag = null;

    /**
     * A Map associating long flags with parameter IDs.
     */
    private Map<String, String> byLongFlag = null;

    /**
     * Creates a new IDMap.
     * @param ids a List of the unique IDs of all the parameters to store
     * in this IDMap.
     * @param byShortFlag a Map with keys equal to the short flags of the
     * parameters (as Character objects),
     * and values equal to the unique IDs of the parameters associated with
     * those short flags.
     * @param byLongFlag a Map with keys equal to the long flags of the
     * parameters (as Strings),
     * and values equal to the unique IDs of the parameters associated with
     * those short flags.
     */
    public IDMap(List<String> ids, Map<Character, String> byShortFlag, Map<String, String> byLongFlag) {
        this.ids = new java.util.ArrayList<String>(ids);
        this.byShortFlag = new java.util.HashMap<Character, String>(byShortFlag);
        this.byLongFlag = new java.util.HashMap<String, String>(byLongFlag);
    }

    /**
     * Returns an Iterator over all parameter IDs stored in this IDMap.
     * @return an Iterator over all parameter IDs stored in this IDMap.
     * @see java.util.Iterator
     */
    public Iterator idIterator() {
        return (ids.iterator());
    }

    /**
     * Returns true if the specified ID is stored in this IDMap, false if not.
     * @param id the id to search for in this IDMap
     * @return true if the specified ID is stored in this IDMap, false if not.
     */
    public boolean idExists(String id) {
        return (ids.contains(id));
    }

    /**
     * Returns the unique ID of the parameter with the specified short flag, or
     * null if the specified short flag is not defined in this IDMap.
     * @param c the short flag to search for in this IDMap.
     * @return the unique ID of the parameter with the specified short flag, or
     * null if the specified short flag is not defined in this IDMap.
     */
    public String getIDByShortFlag(Character c) {
        return ((String) byShortFlag.get(c));
    }

    /**
     * Returns the unique ID of the parameter with the specified short flag, or
     * null if the specified short flag is not defined in this IDMap.
     * @param c the short flag to search for in this IDMap.
     * @return the unique ID of the parameter with the specified short flag, or
     * null if the specified short flag is not defined in this IDMap.
     */
    public String getIDByShortFlag(char c) {
        return (getIDByShortFlag(new Character(c)));
    }

    /**
     * Returns the unique ID of the parameter with the specified long flag, or
     * null if the specified long flag is not defined in this IDMap.
     * @param s the long flag to search for in this IDMap.
     * @return the unique ID of the parameter with the specified long flag, or
     * null if the specified long flag is not defined in this IDMap.
     */
    public String getIDByLongFlag(String s) {
        return ((String) byLongFlag.get(s));
    }
}
