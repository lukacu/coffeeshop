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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * Layaut that puts every component to the center of the parent container.
 * It is intended for containers that include only one component.
 * 
 * @author luka
 * @since CoffeeShop 1.0
 * @see LayoutManager
 *
 */
public class CenterLayout implements LayoutManager {

	/**
	 * @see LayoutManager#addLayoutComponent(String, Component)
	 */
	public void addLayoutComponent(String arg0, Component arg1) {
	}

	/**
	 * Place each component to the center of the container
	 * 
	 * @see LayoutManager#layoutContainer(Container)
	 */
	public void layoutContainer(Container arg0) {

		Dimension base = arg0.getSize();

		for (int i = 0; i < arg0.getComponentCount(); i++) {

			Component c = arg0.getComponent(i);

			Dimension d = c.getPreferredSize();

			c.setBounds(Math.max(0, (base.width - d.width) / 2), Math.max(0,
					(base.height - d.height) / 2), Math
					.min(d.width, base.width), Math.min(d.height, base.height));
		}

	}

	/**
	 * Minimum size of a container is the maximum minimum size
	 * of its components (calculated for each dimension separately)
	 * 
	 * @see LayoutManager#minimumLayoutSize(Container)
	 */
	public Dimension minimumLayoutSize(Container arg0) {
		Dimension r = new Dimension();

		for (int i = 0; i < arg0.getComponentCount(); i++) {

			Component c = arg0.getComponent(i);

			Dimension d = c.getMaximumSize();

			r.height = Math.max(r.height, d.height);

			r.width = Math.max(r.width, d.width);

		}
		return r;
	}

	/**
	 * Prefered size of a container is the maximum prefered size
	 * of its components (calculated for each dimension separately)
	 * 
	 * @see LayoutManager#preferredLayoutSize(Container)
	 */
	public Dimension preferredLayoutSize(Container arg0) {
		Dimension r = new Dimension();

		for (int i = 0; i < arg0.getComponentCount(); i++) {

			Component c = arg0.getComponent(i);

			Dimension d = c.getPreferredSize();

			r.height = Math.max(r.height, d.height);

			r.width = Math.max(r.width, d.width);

		}
		return r;
	}

	/**
	 * @see LayoutManager#removeLayoutComponent(Component)
	 */
	public void removeLayoutComponent(Component arg0) {	}

}
