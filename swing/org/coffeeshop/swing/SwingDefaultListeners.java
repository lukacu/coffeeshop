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

package org.coffeeshop.swing;

import java.awt.Cursor;

import javax.swing.JComponent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.coffeeshop.external.Browser;

public class SwingDefaultListeners {


	private static HyperlinkListener hyperlinkListener;
	
	public static HyperlinkListener getDefaultHyperlinkListener() {
		
		if (hyperlinkListener == null)
			hyperlinkListener = new HyperlinkListener() {
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) 
						Browser.openURL(e.getURL().toString());
					else if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
						((JComponent) e.getSource()).setCursor(new Cursor(Cursor.HAND_CURSOR));
					} else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
						((JComponent) e.getSource()).setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					}
				}
			};
		
		return hyperlinkListener;
	}
	
}
