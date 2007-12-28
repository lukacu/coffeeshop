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
 * <p>Marks an argument as being "flagged" - that is, as having its value on the
 * command line preceded by an indicator.  Flagged arguments can be preceded by
 * a "short flag," a hyphen followed by a single character, or a "long flag,"
 * two hyphens followed by a word.</p>
 *
 * <p>For example, a short flag 'i' marking an option as meaning "input file"
 * might result in a command line that contains the substring "-i myfile.txt" or
 * "-i=myfile.txt", whereas the same option with a long flag of "inputfile"
 * might contain a substring such as "--inputfile myfile.txt" or
 * "--inputfile=myfile.txt"</p>
 *
 * Note that the setter methods setShortFlag() and setLongFlag() are not
 * part of this Interface.  This is because including them would prevent the
 * setShortFlag() and setLongFlag() methods in FlaggedOption and Switch from
 * returning references to themselves, which is inconvenient and inconsistent
 * with the rest of the API.
 *
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see org.coffeeshop.arguments.Switch
 * @see org.coffeeshop.arguments.FlaggedOption
 */
public interface Flagged {

    //    /**
    //     *  Sets the short flag for this object.
    //     *  @param shortFlag the new short flag for this object.
    //     *  @return the modified Flagged object.
    //     */
    //    Flagged setShortFlag(char shortFlag);

    /**
     *  Returns the short flag for this object in the form of a char.
     *  @return the short flag for this object in the form of a char.
     */
    char getShortFlag();

    /**
     *  Returns the short flag for this object in the form of a Character.
     *  @return the short flag for this object in the form of a Character.
     */
    Character getShortFlagCharacter();

    //    /**
    //     *  Sets the long flag for this object.
    //     *  @param longFlag the new long flag for this object.
    //     *  @return the modified Flagged object.
    //     */
    //    Flagged setLongFlag(String longFlag);

    /**
     *  Returns the long flag for this object.
     *  @return the long flag for this object.
     */
    String getLongFlag();

}
