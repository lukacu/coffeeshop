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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.coffeeshop.string.StringUtils;

/**
 * Miscellaneous file/path manipulation methods. Mostly recursive functions
 * that ease the work on many files/directories.
 * 
 * Class is a member of CoffeeShop Core package.
 * 
 * @author Luka Cehovin
 * @since CoffeeShop Core 1.0
 */
public class Files {
	
	/**
	 * Returs a subdirectory with a desired name. The method can also create the 
	 * non-existing directory if desired so.
	 * 
	 * @param dir root dir
	 * @param subDirName subdirectory path
	 * @param makeNew should the method create the directory 
	 * @return
	 */
	public static File subdirectory(File dir, String subDirName, boolean makeNew) {

		if (StringUtils.empty(subDirName))
			return dir;
		
		File subDir = new File(dir, subDirName);
		
		if (makeNew && !subDir.exists())
			makeDirectory(subDir);
		
		return subDir;
	}
	
	/**
	 * Creates a directory. If it is necessary the method will allso create
	 * all the patent directories that do not exist. 
	 * 
	 * @param directory
	 * @return the new directory
	 */
	public static File makeDirectory(File directory) {
		
		if (directory.exists())
			return directory;
		
		File parent = directory.getParentFile();
		
		if (!parent.exists())
			makeDirectory(parent);
		
		directory.mkdir();
		
		return directory;
		
	}
	
	/**
	 * A proxy for {@link File} object constructor that verifies if the
	 * input directory exists and that the input string is not empty.
	 * 
	 * @param dir parent directory
	 * @param fileName new name
	 * @return new file object
	 * @throws FileNotFoundException if the parent directory does not exist
	 * @throws IllegalArgumentException if the file name is empty or <code>null</code>.
	 */
	public static File join(File dir, String fileName) throws FileNotFoundException {
		
		if (!dir.exists())
			throw new FileNotFoundException("Directory does not exist: " + dir);
		
		if (StringUtils.empty(fileName))
			throw new IllegalArgumentException("Empty subdirectory name");
		
		File f = new File(dir, fileName);
		
		return f;
	}
	
	/**
	 * A proxy method for {@link Files#join(String, String, char)} that
	 * uses {@link File#separatorChar} as separatos symbol.
	 * 
	 * @param root the first part of path
	 * @param appendix the second part of path
	 * @return the joined path
	 * @see File#separatorChar
	 * @see Files#join(String, String, char)
	 */
	public static String join(String root, String appendix) {
		
		return join(root, appendix, File.separatorChar);

	}

	/**
	 * Joins two path elements so that there remains only one path
	 * separator on the join no matter what the input elements are.
	 * 
	 * 
	 * Examples (using '/' as separator):
	 * <ul>
	 * <li>"/aaa/bbb" + "ccc/ddd" = "/aaa/bbb/ccc/ddd"</li>
	 * <li>"/aaa/bbb/" + "ccc/ddd" = "/aaa/bbb/ccc/ddd"</li>
	 * <li>"/aaa/bbb/" + "/ccc/ddd" = "/aaa/bbb/ccc/ddd"</li>
	 * </ul>
	 * 
	 * @param root the first part of path
	 * @param appendix the second part of path
	 * @param separator the separators symbol
	 * @return the joined path
	 */
	public static String join(String root, String appendix, char separator) {
		
		if (StringUtils.empty(root))
			return appendix;

		if (StringUtils.empty(appendix))
			return root;

		if (root.charAt(root.length() - 1) == separator) 
			return appendix.charAt(0) == separator ? root + appendix.substring(1) : root + appendix;

		return appendix.charAt(0) == separator ? root + appendix : root + String.valueOf(separator) + appendix;

	}
	
	/**
	 * Just a proxy method for delete(File) method.
	 * 
	 * @see Files#delete(File)
	 * @param path the file to be deleted.
	 * @return <code>true</code> if completely successful (all files deleted),
	 * <code>false</code> otherwise.
	 */
	public static boolean delete(String path) {
		
		File f = new File(path);
		
		return delete(f);
		
	}
	
	/**
	 * Deletes the file. If the file is a directory then the method
	 * first recursively deletes its content.
	 * 
	 * @param f the file to be deleted.
	 * @return <code>true</code> if completely successful (all files deleted),
	 * <code>false</code> otherwise.
	 */
	public static boolean delete(File f) {

		if (!f.exists())
			return false;
		
		if (!f.isDirectory())
			return f.delete();
		
		File[] files = f.listFiles();
		
		boolean result = true;
		
		for (int i = 0; i < files.length; i++) 
			result &= delete(files[i]);
		
		if (!result)
			return false;
		
		return f.delete();
	}
	
	/**
	 * Change extension (the part after the last dot) with another.
	 * 
	 * Example:
	 *  input "foo.txt", "dat"
	 *  output "foo.dat"
	 * 
	 * @param f
	 * @param newExtension
	 * @return
	 */
	public static File changeExtension(File f, String newExtension) {
		
		String path = f.getAbsolutePath();
		
		int i = path.lastIndexOf(".");
		
		if (i == -1)
			return new File(path + "." + newExtension);
		
		return new File(path.substring(0, i+1) + newExtension);
		
	}

	/**
	 * Returns a file object without extension part (the part after
	 * the last dot).
	 * 
	 * Example: 
	 * 	input "foo.txt"
	 * 	output "foo"
	 * 
	 * @param f source file object
	 * @return extensionless file object
	 */
	public static File removeExtension(File f) {
		
		String path = f.getAbsolutePath();
		
		int i = path.lastIndexOf(".");
		
		if (i == -1)
			return new File(path);
		
		return new File(path.substring(0, i));
		
	}
	
	/**
	 * Break a path down into individual elements and add to a list.
	 * 
	 * Example : if a path is /a/b/c/d.txt, the breakdown will be a list
	 * object of [d.txt,c,b,a] (currently {@link ArrayList} is used).
	 * 
	 * Note: this function if probably not very fast so it should not be used too often.
	 * 
	 * @param f input file
	 * @return a List collection with the individual elements of the path in reverse order
	 */
	public static List<String> getPathAsList(File f) {
		List<String> l = new ArrayList<String>();
		File r;
		
		try {
			r = f.getCanonicalFile();
			while(r != null) {
				l.add(r.getName());
				r = r.getParentFile();
			}
		}
		catch (IOException e) {
			l = null;
		}
		return l;
	}

	/**
	 * Calculate a string representing the relative path of
	 * path with respect to root path
	 * 
	 * @param root root path
	 * @param path path of file
	 */
	private static String matchPathLists(List<String> root, List<String> path) {
		int i;
		int j; 
		String s;
		// start at the beginning of the lists
		// iterate while both lists are equal
		s = "";
		i = root.size()-1;
		j = path.size()-1;

		// first eliminate common root
		while((i >= 0)&&(j >= 0)&&(root.get(i).equals(path.get(j)))) {
			i--;
			j--;
		}

		// for each remaining level in the home path, add a ..
		for(;i>=0;i--) {
			s += ".." + File.separator;
		}

		// for each level in the file path, add the path
		for(;j>=1;j--) {
			s += path.get(j) + File.separator;
		}

		// file name
		if (j > -1)
			s += path.get(j);
		return s;
	}

	/**
	 * Get relative path of File 'f' with respect to 'home' directory
	 * example : home = /a/b/c
	 *           f    = /a/d/e/x.txt
	 *           s = getRelativePath(home,f) = ../../d/e/x.txt
	 *           
	 * @param root base path, should be a directory, not a file, or it 
	 * does not make sense
	 * @param path file to generate path for
	 * @return path from root to path as a string
	 */
	public static String getRelativePath(File root, File path){
		List<String> homelist;
		List<String> filelist;
		String s;

		homelist = getPathAsList(root);
		filelist = getPathAsList(path);
		s = matchPathLists(homelist, filelist);

		return s;
	}
	

	
}
