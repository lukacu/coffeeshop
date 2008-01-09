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
 */

package org.coffeeshop.awt;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.coffeeshop.io.Streams;

/**
 * This class provides some methods for easier loading of Images in AWT.
 * 
 * 
 * @author lukacu
 * @since CoffeeShop 1.0
 */
public class Images {

	/**
	 * Loads image from input stream.
	 * <b>Note:</b> This method loads the image completely. It waits 
	 * untill the image is loaded so there is no need for ImageObservers 
	 * later.
	 * 
	 * @param stream the input stream
	 * @return an completely loaded Image object
	 * @throws IOException if error occurs while loading
	 */
	public static Image loadImageFromStream(InputStream stream) 
		throws IOException {
		
		/*Image img = Toolkit.getDefaultToolkit().createImage(
				Streams.getStreamAsByteArray(stream));*/
		
		return ImageIO.read(stream);
		
		/*ImageWaiter waiter = new ImageWaiter(img);
		
		if (!waiter.waitForImage()) {
			throw new IOException("Error occured during image loading");
		}*/
		
		//return img;
	}
	
	/**
	 * Loads image from the specified URL.
	 * <b>Note:</b> This method loads the image completely. It waits 
	 * until the image is loaded so there is no need for ImageObservers 
	 * later.
	 * 
	 * @param url URL of the image
	 * @return an completely loaded Image object
	 * @throws IOException if error occurs while loading
	 * @throws MalformedURLException if the URL provided is not correctly constructed.
	 */
	public static Image loadImageFromURL(String url) 
		throws IOException, MalformedURLException {
		
		URL location = new URL(url);
		
		return ImageIO.read(location);
		
		/*Image img = Toolkit.getDefaultToolkit().createImage(location);
		
		ImageWaiter waiter = new ImageWaiter(img);
		
		if (!waiter.waitForImage()) {
			throw new IOException("Error occured during image loading");
		}
		
		return img;*/
	}

	/**
	 * Loads image from the specified filename.
	 * <b>Note:</b> This method loads the image completely. It waits 
	 * until the image is loaded so there is no need for ImageObservers 
	 * later.
	 * 
	 * @param file filename of the image
	 * @return an completely loaded Image object
	 * @throws IOException if error occurs while loading
	 * @throws FileNotFoundException if the file does not exist.
	 */
	public static Image loadImageFromFile(String file) 
		throws IOException, FileNotFoundException {
		
		File f = new File(file);
		
		if (!f.exists())
			throw new FileNotFoundException(file + " not found");
		
		return ImageIO.read(f);
		
		/*Image img = Toolkit.getDefaultToolkit().createImage(file);
		
		ImageWaiter waiter = new ImageWaiter(img);
		
		if (!waiter.waitForImage()) {
			throw new IOException("Error occured during image loading");
		}
		
		return img;*/
	}
	
	/**
	 * Loads image from a byte array.
	 * <b>Note:</b> This method loads the image completely. It waits 
	 * untill the image is loaded so there is no need for ImageObservers 
	 * later.
	 * 
	 * @param array input byte array
	 * @return an completely loaded Image object
	 * @throws IOException if error occurs while loading
	 */
	public static Image loadImageFromArray(byte[] array) 
		throws IOException {
		
		return ImageIO.read(new ByteArrayInputStream(array));
		/*Image img = Toolkit.getDefaultToolkit().createImage(array);
		
		ImageWaiter waiter = new ImageWaiter(img);
		
		if (!waiter.waitForImage()) {
			throw new IOException("Error occured during image loading");
		}
		
		return img;*/
	}
	
}
