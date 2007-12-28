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
import java.awt.image.ImageObserver;

/**
 * This is an internal package class that is used to wait for image to
 * load completely.
 * 
 * @author lukacu
 * @since CoffeeShop 1.0
 * @see java.awt.image.ImageObserver
 */
class ImageWaiter implements ImageObserver {

	/**
	 * Loading complete
	 */
	public static final int COMPLETE = 1;
	
	/**
	 * Error
	 */
	public static final int ERRORED = 2;

	/**
	 * Loading aborted
	 */
	public static final int ABORTED = 4;

	/**
	 * Loading in progress
	 */
	public static final int LOADING = 8;

	private Image _image;

	private int _status;

	/**
	 * Constructs a new image waiter
	 * 
	 * @param img image to wait for
	 */
	public ImageWaiter(Image img) {
		_status = LOADING;
		_image = img;
	}

	/**
	 * This method returns when image is successfully loaded or an error
	 * has been encountered.
	 * 
	 * @return <code>true</code> if the image has been loaded, <code>false</code> if 
	 * there was an error.
	 */
	public boolean waitForImage() {
		if (Toolkit.getDefaultToolkit().prepareImage(_image, -1, -1, this)) {
			_status = COMPLETE;
			return true;
		}

		while (true) {
			if ((_status & LOADING) == 0) {
				return (_status == COMPLETE);
			}
		}
	}

	/**
	 * Status of the image.
	 * 
	 * @return status of the image.
	 */
	public int getStatus() {
		return _status;
	}

	/**
	 * @see ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
	 */
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int w,
			int h) {
		int s = 0;
		if ((infoflags & ERROR) != 0) {
			s = ERRORED;
		} else if ((infoflags & ABORT) != 0) {
			s = ABORTED;
		} else if ((infoflags & (ALLBITS | FRAMEBITS)) != 0) {
			s = COMPLETE;
		}
		if (s != 0 && s != _status) {
			_status = s;
		}
		return ((_status & LOADING) != 0);
	}
}
