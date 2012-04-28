
package org.coffeeshop.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.coffeeshop.awt.StackLayout;
import org.coffeeshop.awt.StackLayout.Orientation;
import org.coffeeshop.swing.ImagePanel;

public class Splash {

	public static interface SplashController {
		
		public Object[] items(Splash splash);
		
		public Object browse(Splash splash);
		
		public Image getSplashImage();
		
		public Image getIconImage();
		
	}
		
	private Window window;
	
	private Image splashImage;

	private SplashController controller;
	
	private Object choice = null;
	
	private class Window extends JFrame {
		
		public static final long serialVersionUID = 1;
		
		private Action openProject = new AbstractAction("Browse") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				choice = controller.browse(Splash.this);
				
				synchronized (Splash.this) {
					Splash.this.notifyAll();
				}
				
			}
		};
		
		private Action exit = new AbstractAction("Exit") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				synchronized (Splash.this) {
					Splash.this.notifyAll();
				}
				
			}
		};
		
		public Window(Image splashImage) {
		
			super();
		
			setUndecorated(true);
			
			setResizable(false);
			
			JPanel root = new JPanel(new StackLayout(Orientation.VERTICAL, true));

			root.add(new ImagePanel(splashImage));
			
			final JList list = new JList(controller.items(Splash.this));
			list.addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent e) {

					if (e.getValueIsAdjusting())
						return;
					
					choice = list.getSelectedValue();

					synchronized (Splash.this) {
						Splash.this.notifyAll();
					}

				}
			});
			
			JScrollPane listpane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			listpane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			listpane.setPreferredSize(new Dimension(splashImage.getWidth(null), 200));

			JPanel buttons = new JPanel(new StackLayout(Orientation.HORIZONTAL, 10, 10));

			if (System.getProperty("splash.background") != null) {
				Color c = Color.decode(System.getProperty("splash.background"));
				listpane.setBackground(c);
				buttons.setBackground(c);
			}
			
			root.add(listpane);
			
			buttons.add(new JButton(openProject));
			buttons.add(new JButton(exit));
			
			buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			root.add(buttons);
			
			setContentPane(root);
			
			setMinimumSize(new Dimension(splashImage.getWidth(null), -1));
			
			pack();
			
			Dimension w = getSize();
			
			Rectangle r = MouseInfo.getPointerInfo().getDevice().getDefaultConfiguration().getBounds();
			
			setLocation(r.x + (r.width - w.width) / 2, r.y + (r.height - w.height) / 2);
			
		}

	}
	
	private Splash(String title, SplashController controller) {
		
		this.controller = controller;
		
    	splashImage = controller.getSplashImage();

		window = new Window(splashImage);
		
		window.setTitle(title);
		
		Image icon = controller.getIconImage();
		
		if (icon != null)
			window.setIconImage(icon);
		
	}
	
	public static Object show(String title, SplashController controller) {
		
		Splash splash = new Splash(title, controller);

		splash.window.setVisible(true);
		
		synchronized (splash) {
			try {
				splash.wait();
			} catch (InterruptedException e) {
				splash.window.setVisible(false);
				return null;
			}
		}
		splash.window.setVisible(false);
		return splash.choice;
	}
		
}
