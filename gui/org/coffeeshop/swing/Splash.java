
package org.coffeeshop.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.coffeeshop.awt.StackLayout;
import org.coffeeshop.awt.StackLayout.Orientation;
import org.coffeeshop.swing.ImagePanel;

public class Splash {

	private Window window;
	
	private Object result = null;
	
	private class Window extends JFrame {
		
		public static final long serialVersionUID = 1;
		
		private Action exit = new AbstractAction("Exit") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
									
				closeWithResult(null);

			}
		};
		
		public Window(Image splashImage, boolean horizontal) {
		
			super();
		
			setUndecorated(true);
			
			setResizable(false);
			
			JPanel root = new JPanel(new BorderLayout());

			root.add(new ImagePanel(splashImage), BorderLayout.CENTER);
			
			JPanel sidebar = new JPanel(new BorderLayout());
			
			JComponent sidebarComponent = createSidebarComponent();
			
			/*if (orientation == Orientation.VERTICAL) {
				//sidebar.setPreferredSize(new Dimension(400, splashImage.getHeight(null)));
			} else  {
				sidebar.setPreferredSize(new Dimension(splashImage.getWidth(null), 200));
			}*/
			
			JPanel buttons = new JPanel(new StackLayout(horizontal ? 
					Orientation.VERTICAL : Orientation.HORIZONTAL, 5, 5, horizontal));

			Color background = getBackground();
			
			if (System.getProperty("splash.background") != null) {
				background = Color.decode(System.getProperty("splash.background"));
			}

			sidebar.setBackground(background);
			
			if (sidebarComponent != null) {
				sidebarComponent.setBackground(background);
				sidebar.add(sidebarComponent, BorderLayout.CENTER);
			}
			
			buttons.setBackground(background);

			List<Action> actions = createActions();

			if (actions != null && actions.size() > 0) {
				actions.add(exit);
				
				for (Action action : actions) {
					JButton button = new JButton(action);
					
					//button.setBackground(background);
					//button.setBorder(null);
					buttons.add(button);
				}

				buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				sidebar.add(buttons, BorderLayout.SOUTH);
				root.add(sidebar, horizontal ? BorderLayout.EAST : BorderLayout.SOUTH);
				
			}
	
			setContentPane(root);
			
			if (!horizontal) {
				setMinimumSize(new Dimension(-1, splashImage.getHeight(null)));
			} else  {
				setMinimumSize(new Dimension(splashImage.getWidth(null), -1));
				
			}
			
			pack();
			
			Dimension w = getSize();
			
			Rectangle r = MouseInfo.getPointerInfo().getDevice().getDefaultConfiguration().getBounds();
			
			setLocation(r.x + (r.width - w.width) / 2, r.y + (r.height - w.height) / 2);
			
		}

	}
	
	public Splash(String title, Image image) {

		window = new Window(image, image.getWidth(null) < image.getHeight(null));
		
		window.setTitle(title);
		
		Image icon = ImageStore.getImage("icon.png", "icon-16.png", "icon-32.png", "icon-46.png");
		
		if (icon != null)
			window.setIconImage(icon);
		
	}
	
	public final Object show() {

		window.setVisible(true);
		
		synchronized (window) {
			try {
				window.wait();
			} catch (InterruptedException e) {
				window.setVisible(false);
				return null;
			}
		}
		
		window.setVisible(false);
		
		return result;
	}
	
	protected final void closeWithResult(Object result) {
		
		this.result = result;
		
		synchronized (window) {
			window.notifyAll();
		}
		
	}
	
	protected final void close() {
		
		synchronized (window) {
			window.notifyAll();
		}
		
	}
	
	protected JComponent createSidebarComponent() {
		
		return null;
		
	}
	
	protected List<Action> createActions() {
		
		return null;
		
	}
	
}
