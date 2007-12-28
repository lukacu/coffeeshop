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

import java.util.List;
/**
 * <p>A utility class to parse a command line contained in a single String into
 * an array of argument tokens, much as the JVM (or more accurately, your
 * operating system) does before calling your programs' <code>public static
 * void main(String[] args)</code>
 * methods.</p>
 *
 * <p>This class has been developed to parse the command line in the same way
 * that MS Windows 2000 does.  Arguments containing spaces should be enclosed
 * in quotes. Quotes that should be in the argument string should be escaped
 * with a preceding backslash ('\') character.  Backslash characters that
 * should be in the argument string should also be escaped with a preceding
 * backslash character.</p>
 *
 * Whenever <code>Arguments.parse(String)</code> is called, the specified String is
 * tokenized by this class, then forwarded to <code>Arguments.parse(String[])</code>
 * for further processing.
 *
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see org.coffeeshop.arguments.Arguments#parse(String)
 * @see org.coffeeshop.arguments.Arguments#parse(String[])
 */
class CommandLineTokenizer {

    /**
     * Hide the constructor.
     */
    private CommandLineTokenizer() {
    }

    /**
     * Goofy internal utility to avoid duplicated code.  If the specified
     * StringBuffer is not empty, its contents are appended to the resulting
     * array (temporarily stored in the specified ArrayList).  The StringBuffer
     * is then emptied in order to begin storing the next argument.
     * @param resultBuffer the List temporarily storing the resulting
     * argument array.
     * @param buf the StringBuffer storing the current argument.
     */
    private static void appendToBuffer(
        List<String> resultBuffer,
        StringBuffer buf) {
        if (buf.length() > 0) {
            resultBuffer.add(buf.toString());
            buf.setLength(0);
        }
    }

    /**
     * Parses the specified command line into an array of individual arguments.
     * Arguments containing spaces should be enclosed in quotes.
     * Quotes that should be in the argument string should be escaped with a
     * preceding backslash ('\') character.  Backslash characters that should
     * be in the argument string should also be escaped with a preceding
     * backslash character.
     * @param commandLine the command line to parse
     * @return an argument array representing the specified command line.
     */
    public static String[] tokenize(String commandLine) {
        List<String> resultBuffer = new java.util.ArrayList<String>();

        if (commandLine != null) {
            int z = commandLine.length();
            boolean insideQuotes = false;
            StringBuffer buf = new StringBuffer();

            for (int i = 0; i < z; ++i) {
                char c = commandLine.charAt(i);
                if (c == '"') {
                    appendToBuffer(resultBuffer, buf);
                    insideQuotes = !insideQuotes;
                } else if (c == '\\') {
                    if ((z > i + 1)
                        && ((commandLine.charAt(i + 1) == '"')
                            || (commandLine.charAt(i + 1) == '\\'))) {
                        buf.append(commandLine.charAt(i + 1));
                        ++i;
                    } else {
                        buf.append("\\");
                    }
                } else {
                    if (insideQuotes) {
                        buf.append(c);
                    } else {
                        if (Character.isWhitespace(c)) {
                            appendToBuffer(resultBuffer, buf);
                        } else {
                            buf.append(c);
                        }
                    }
                }
            }
            appendToBuffer(resultBuffer, buf);

        }

        String[] result = new String[resultBuffer.size()];
        return ((String[]) resultBuffer.toArray(result));
    }

}
