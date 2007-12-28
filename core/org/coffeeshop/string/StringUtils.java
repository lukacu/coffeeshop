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

package org.coffeeshop.string;

import java.util.List;
import java.util.regex.*;

/**
 * Miscellaneous string manipulation methods
 * 
 * Class is a member of CoffeeShop Core package.
 * 
 * @author Luka Cehovin
 * @author Mitar
 * 
 * @since CoffeeShop Core 1.0
 */
public final class StringUtils {
	
	//TODO this has to be done better
	public static int TAB_WIDTH = 4;
	
	private static final Pattern tabPattern = Pattern.compile("\\t");
	
	/**
	 * Repeat string sequence n times
	 * 
	 * @param str string to be repeated
	 * @param n how many times to repeat it
	 * @return result string
	 */
	public static final String repetition(String str, int n) {
		if (n <= 0) return "";
		
		StringBuilder sb = new StringBuilder(str.length() * n);
		sb.append(str);
		for (int i = 1; (i * 2) <= n; i *= 2) sb.append(sb);
		for (int i = sb.length(); i < n; i++) sb.append(str);
		
		return sb.toString();
	}
	
	/**
	 * Convert tabs (\t) to spaces
	 * 
	 * @param str original string
	 * @return result string
	 */
	public static final String tabsToSpaces(String str) {
		int tabwidth = TAB_WIDTH;
		
		Matcher matcher = tabPattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		int lastend = 0;
		while (matcher.find()) {
			// Calculates the number of space characters needed for the replacement
			int n = (tabwidth - (sb.length() + (matcher.start() - lastend)) % tabwidth);
			
			// Replaces the tab character
			matcher.appendReplacement(sb, repetition(" ", n));
			
			lastend = matcher.end();
		}
		matcher.appendTail(sb);
		
		return sb.toString();
	}
	
	/**
	 * Finds out if two strings are the same. This is a convenience method
	 * that also handles situations where one or both of the arguments are
	 * <tt>null</tt>.
	 * 
	 * @param a first argument
	 * @param b second argument
	 * @return <code>true</code> if strings are the same or both <code>null</code>, <code>false</code> otherwise.
	 */
	public static final boolean same(String a, String b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;
		return (a.compareTo(b) == 0);		
	}
	
	/**
	 * If the refference to the string is actually <code>null</code> this method
	 * converts it to empty string.
	 * 
	 * @param s a possibly <code>null</code> reference to a string
	 * @return string that can be the same as the input string or empty string
	 */
    public static String noNull(String s) {
        return (s == null ? "" : s);
    }

    /**
     * Checks it the string is empty. String is empty if the reference is <code>null</code>
     * or the length of a trimmed string is 0.
     * 
     * @param s
     * @return
     */
    public static boolean empty(String s) {
    	
    	if (s == null)
    		return true;
    	
    	if (s.trim().length() == 0)
    		return true;
    	
    	return false;
    }
    
    /**
     * Checks if the string is composed only of letters or
     * numbers. It depends on the method isLetterOrDigit from
     * the {@link Character} class.
     * 
     * @param s the string to be checked
     * @return <code>true</code> if the string is composed only 
     * of letters or numbers, <code>false</code> otherwise.
     */
    public static boolean isAlphanumerical(String s) {
    	if (empty(s))
    		return true;
    	
    	for (int i = 0; i < s.length(); i++) {
    		if (!Character.isLetterOrDigit(s.charAt(i)))
    			return false;
    	}
    	
    	return true;
    	
    }
    
    public static boolean isInteger(String s) {
    	
    	if (empty(s))
    		return false;
    	
    	try {
    		Integer.parseInt(s);
        	
        	return true;
    	} catch (NumberFormatException e) {
    		return false;
    	}
    }
    
    public static boolean isFloatingPointNumber(String s) {
    	
    	if (empty(s))
    		return false;
    	
    	try {
    		Double.parseDouble(s);
        	
        	return true;
    	} catch (NumberFormatException e) {
    		return false;
    	}
    }
    
    public static String replaceAll(String original, String regexWhat, String with) {
    	
		Pattern p = Pattern.compile(regexWhat);
		
		Matcher m = p.matcher(original);
		
		return m.replaceAll(with);
    	
    }
    
    public static String padRight(String s, int padCount) {
        if (padCount < 0) {
            throw (new IllegalArgumentException("padCount must be >= 0"));
        }
        StringBuffer buf = new StringBuffer(noNull(s));
        for (int i = 0; i < padCount; ++i) {
            buf.append(' ');
        }
        return (buf.toString());
    }

    public static String padRightToWidth(String s, int desiredWidth) {
        String result = noNull(s);
        if (result.length() < desiredWidth) {
            result = padRight(result, desiredWidth - result.length());
        }
        return (result);
    }

    public static List<String> wrapToList(String s, int width) {
    	List<String> result = new java.util.LinkedList<String>();
        if ((s != null) && (s.length() > 0)) {
            StringBuffer buf = new StringBuffer();
            int lastSpaceBufIndex = -1;
            for (int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                if (c == '\n') {
                    result.add(buf.toString());
                    buf.setLength(0);
                    lastSpaceBufIndex = -1;
                } else {
                    if (c == ' ') {
                        if (buf.length() >= width - 1) {
                            result.add(buf.toString());
                            buf.setLength(0);
                            lastSpaceBufIndex = -1;
                        }
                        if (buf.length() > 0) {
                            lastSpaceBufIndex = buf.length();
                            buf.append(c);
                        }
                    } else {
                    	if (buf.length() >= width) {
                            if (lastSpaceBufIndex != -1) {
                                result.add(buf.substring(0, lastSpaceBufIndex));
                                buf.delete(0, lastSpaceBufIndex + 1);
                                lastSpaceBufIndex = -1;
                            }
                        }
                        buf.append(c);
                    }
                }
            }
            if (buf.length() > 0) {
                result.add(buf.toString());
            }
        }
        return (result);
    }
}
