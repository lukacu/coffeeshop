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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.coffeeshop.external.OperatingSystem;
import org.coffeeshop.string.StringUtils;

/**
 * TempDirectory represents a temporary directory created in a platform dependant
 * manner that will be in most cases deleted at the end of application execution 
 * (except probably in case of some kind of JVM failure). The class provides methods
 * for random file name generation.
 * 
 * Class is a member of CoffeeShop Core package.
 * 
 * Related system properties:
 * <code>org.coffeeshop.io.temp_rand_len</code> - number of random generated 
 * characters in random file names. Min: 5, Max: 255, Default: 10
 * <code>org.coffeeshop.io.temp_delete</code> - delete temporary derectory by
 * default (if not told otherwise). Default: true. 
 * 
 * 
 * @author lukacu
 * @since CoffeeShop Core 1.0
 */
public class TempDirectory {

	private static int RANDOM_APPENDIX_LENGTH = 10;
	
	private static boolean DEFAULT_DELETE_ON_EXIT = true;
	
	private static class Deleter extends Thread {
		
		private ArrayList<TempDirectory> dirList = new ArrayList<TempDirectory>();
		
	    public synchronized void add(TempDirectory dir) {
	    	if (!dirList.contains(dir))
	    		dirList.add(dir);
	    }
	    
	    public synchronized void remove(TempDirectory dir) {
	    	dirList.remove(dir);
	    }
	    
	    public void run() {
	        synchronized (this)
	        {
	            Iterator<TempDirectory> iterator = dirList.iterator();
	            while (iterator.hasNext())
	            {
	                File dir = iterator.next().handle;
	                Files.delete(dir);
	                iterator.remove();
	            }
	        }
	    }
	}
	
    private static Deleter deleter;
    
    static {
        deleter = new Deleter();
        Runtime.getRuntime().addShutdownHook(deleter);
        
        String len = System.getProperty("org.coffeeshop.io.temp_rand_len", "10");
        
        String del = System.getProperty("org.coffeeshop.io.temp_delete", "true");
        
        try {
        	RANDOM_APPENDIX_LENGTH = Math.min(255, Math.max(5, Integer.parseInt(len)));
        } catch (NumberFormatException e) {
        	
        }
        
        DEFAULT_DELETE_ON_EXIT = Boolean.getBoolean(del.toLowerCase());
        
    }
	
    private File handle;
    
    /**
     * Creates a new temporary directory using the prefix paramerer as name.
     * The root directory used depends on the operation system. (/tmp in case 
     * of Linux and OSX and C:\Temp in case of Windows) 
     * 
     * @param prefix the name of the directory
     * @throws IOException if unable to create the directory or the main
     * temporary directory is not writeable.
     */
	public TempDirectory(String prefix) throws IOException {
		
		String tmpRoot = null;
		
		switch (OperatingSystem.getOperatingSystemType()) {
		case LINUX:
		case OSX:
			tmpRoot = "/tmp/";
			break;

		case WINDOWS:
			tmpRoot = "C:\\Temp\\";
			
			break;
		default:
			throw new IOException("This operating system is not supported.");
		}
		
		File root = new File(tmpRoot);
		
		if (!root.canWrite())
			throw new IOException("Unable to write directory: " + tmpRoot + ".");
		
		handle = randomFile(root, prefix);
		
		if (!handle.mkdir())
			throw new IOException("Unable to create temporary directory.");
		
		if (DEFAULT_DELETE_ON_EXIT)
			deleter.add(this);
		
	}
	
	private String randomString(int len) {
		
		StringBuffer b = new StringBuffer();
		
		for (int i = 0; i < len; i++) {
			int r = (int) Math.round(Math.random() * ('z' - 'a'));
			
			b.append((char) ('a' + r));
		}
		
		return b.toString();
	}
	
	private String randomName(String prefix) {
		if (StringUtils.empty(prefix))
			return randomString(RANDOM_APPENDIX_LENGTH);
		else
			return prefix + "-" + randomString(RANDOM_APPENDIX_LENGTH);
	}
	
	private File randomFile(File dir, String prefix) {
	
		while (true) {
			
			File file = new File(dir, randomName(prefix));
			
			if (!file.exists())
				return file;
		}

	}
	
	/**
	 * Marks the directory for automatic deletion.
	 * 
	 * @param d <code>true</code> if the directory should be deleted
	 * on exit of <code>false</code> otherwise.
	 */
	public void deleteOnExit(boolean d) {
		if (d)
			// check Deleter thread for details
			deleter.add(this);
		else
			deleter.remove(this);
	}
	
	/**
	 * Generates a random file handle and returns it. The
	 * file generated does not exist in the directory.
	 * 
	 * @return file handle
	 */
	public synchronized File tempFileName() {
		return tempFileName(null);
	}

	/**
	 * Generates a random file handle using prefix and returns it. The
	 * file generated does not exist in the directory.
	 * 
	 * @param prefix prefix (the first, fixed part) for the name
	 * @return file handle
	 */
	public synchronized File tempFileName(String prefix) {
		return randomFile(handle, prefix);
	}
	
	/**
	 * Returns the full path of the directory. 
	 * 
	 * @return the full path of the directory.
	 */
	public String getPath() {
		return handle.getAbsolutePath();
	}
	
	/**
	 * Delete the content of the directory.
	 *
	 */
	public void clear() {
        Files.delete(handle);
	}
	
}
