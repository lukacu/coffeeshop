package org.coffeeshop.swing.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;

import org.coffeeshop.io.Files;
import org.coffeeshop.swing.ActionSetManager;
import org.coffeeshop.swing.ImageStore;
import org.coffeeshop.swing.ToolTipAction;
import org.coffeeshop.swing.figure.ButtonAction;
import org.coffeeshop.swing.figure.FigureObserver;
import org.coffeeshop.swing.figure.FigurePanel;
import org.coffeeshop.swing.figure.FigurePanel.Button;
import org.coffeeshop.swing.figure.ImageFigure;

public class MaskEditor extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private FigurePanel panel;

	private ImageMaskFigure mask = null;
	
	private File file = null;
	
	private ActionSetManager actionManager = new ActionSetManager();
	
	private class ToggleButton extends JToggleButton {

		private static final long serialVersionUID = 1L;

		private ToggleButton(Action a, ButtonGroup grp) {
			super(a);
			setText("");
			if (grp != null)
				grp.add(this);
		}
		
	}
	
	private Action saveSelection = new ToolTipAction("Save selection", "mask-16.png") {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (file == null && mask != null)
				return;
			File masked = Files.changeExtension(file, "masked.png");

			BufferedImage img = mask.getMasked();
			
			try {
				ImageIO.write(img, "png", masked);
			} catch (IOException e1) {

				e1.printStackTrace();
			}
			
			
		}
	};
	
	private Action modeReplace = new ToolTipAction("Replace selection", "selection-replace-16.png") {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			
			if (mask != null)
				mask.mode = MaskMode.REPLACE;

		}
	};

	private Action modeAdd = new ToolTipAction("Add to selection", "selection-add-16.png") {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			
			if (mask != null)
				mask.mode = MaskMode.ADD;

		}
	};
	
	private Action modeIntersect = new ToolTipAction("Intersect with selection", "selection-intersect-16.png") {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			
			if (mask != null)
				mask.mode = MaskMode.INTERSECT;

		}
	};
	
	class ImageTransferHandler extends TransferHandler {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent,
		 *      java.awt.datatransfer.DataFlavor[])
		 */
		public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
			for (int i = 0; i < arg1.length; i++) {
				DataFlavor flavor = arg1[i];
				if (flavor.getRepresentationClass().equals(String.class))
					return true;
				if (flavor.getRepresentationClass().equals(URL.class))
					return true;
				if (flavor.getRepresentationClass().equals(List.class))
					return true;
				//System.err.println("canImport: Rejected Flavor: " + flavor);
			}
			// Didn't find any that match, so:
			return false;
		}

		/**
		 * Do the actual import.
		 * 
		 * @see javax.swing.TransferHandler#importData(javax.swing.JComponent,
		 *      java.awt.datatransfer.Transferable)
		 */
		public boolean importData(JComponent comp, Transferable t) {
			DataFlavor[] flavors = t.getTransferDataFlavors();
			for (int i = 0; i < flavors.length; i++) {
				DataFlavor flavor = flavors[i];
				
				URL url = null;
				try {
					if (flavor.getRepresentationClass().equals(List.class)) {
						List<?> l = (List<?>) t.getTransferData(flavor);
						if (l.size() == 0)
							return false;
						
						Object o = l.get(0);
						if (o.getClass().equals(String.class))
							try {
								url = new URL((String) o);
							} catch (MalformedURLException ex) {
								return false;
							}
						
						if (o.getClass().equals(URL.class))
							url = (URL) o;
						
						if (o.getClass().equals(File.class))
							url = ((File) o).toURI().toURL();
					}	
					
					if (flavor.getRepresentationClass().equals(String.class)) {
						String fileOrURL = (String) t.getTransferData(flavor);
						try {
							url = new URL(fileOrURL);
						} catch (MalformedURLException ex) {
							return false;
						}

					} 
					
					if (flavor.getRepresentationClass().equals(URL.class)) {
						url = (URL) t.getTransferData(flavor);
					}
				
					if (url != null) {
						
						try {
							
							file = new File(url.toURI());
							
							Image img = ImageIO.read(url);
							
							mask = new ImageMaskFigure(img);

							panel.setFigure(mask);
							
							panel.setButtonAction(Button.LEFT, mask);
							
							actionManager.enableAll();
							
							return true;
						} catch (IOException e) {
							e.printStackTrace();}
						catch (URISyntaxException e) {e.printStackTrace();}
						
						return false;
						
					}
					
					
				} 
				catch (IOException ie) {}
				catch (UnsupportedFlavorException ue) {}
				catch (ClassCastException ce) {}
			}
			return false;
		}
	}
	
	private KeyEventDispatcher keystrokes = new KeyEventDispatcher() {

		public boolean dispatchKeyEvent(KeyEvent e) {
			
		/*	if (e.getID() != KeyEvent.)
				return true;
			*/
			if (e.getKeyCode() != KeyEvent.VK_ESCAPE)
				return true;
			
			if (mask != null)
				mask.cancelInput();
			
			return false;
		}

	};
	
	public static enum MaskMode {REPLACE, ADD, INTERSECT}
	
	private class ImageMaskFigure extends ImageFigure implements ButtonAction {

		private int stage = 0;
		
		private Vector<Point> points = new Vector<Point>();
		
		private MaskMode mode = MaskMode.REPLACE;
		
		private Area mask = null;
		
		private Paint maskPaint = new Color(200, 255, 200, 150);
		
		private AffineTransform imageToScreen = new AffineTransform();
		
		public ImageMaskFigure(Image image) {
			super(image);
			
		}

		public Object onClick(FigurePanel source, Point position, int clicks,
				int modifiers) {
			
			switch(stage) {
			case 0: 
				if (!source.isFigurePointInFigure(position))
					return null;
				points.add(position);
				stage = 1;
				break;
			
			case 1: 
				if (!source.isFigurePointInFigure(position))
					return null;
				if (clicks == 1 || points.size() < 3)
					points.add(position);
				else {
					
					Polygon poly = new Polygon();
					
					for (Point p : points) {
						poly.addPoint(p.x, p.y);
					}
					
					if (mask == null)
						mask = new Area(poly);
					else {
						switch(mode) {
						case REPLACE:
							mask = new Area(poly);
							break;
						case ADD: 
							mask.add(new Area(poly));
							break;
						case INTERSECT:
							mask.intersect(new Area(poly));
							break;
						
						}
					}
					points.clear();
					stage = 0;
				}
				break;
			
			}
			
			source.repaint();
			return null;
		}
		
		public void cancelInput() {
			
			if (!points.isEmpty()) {
				points.clear();
				panel.repaint();
			}
			
		}
		
		public void paint(Graphics2D g, Rectangle figureSize, Rectangle windowSize, FigureObserver observer) {
			
			super.paint(g, figureSize, windowSize, observer);
			
			if (mask != null || !points.isEmpty()) {
				
				//g.setClip(windowSize.x, windowSize.y, windowSize.width, windowSize.height);
				
				float scale = (float)windowSize.width / (float)figureSize.width;
				
				float offsetX = (float)figureSize.x * scale - (float)windowSize.x;
				float offsetY = (float)figureSize.y * scale - (float)windowSize.y;
		
				imageToScreen.setTransform(scale, 0, 0, scale, -offsetX, -offsetY);
				AffineTransform old = g.getTransform();
				old.concatenate(imageToScreen);
				g.setTransform(old);
				
			}
			
			
			if (mask != null) {
				
				g.setPaint(maskPaint);
				
				g.fill(mask);
				
			}
			
			if (!points.isEmpty()) {
				
				Point p1 = points.firstElement();
				
				g.drawOval(p1.x - 3, p1.y - 3, 6, 6);
				
				for (int i = 1; i < points.size(); i++) {
					Point p2 = points.elementAt(i);
					g.drawOval(p2.x - 3, p2.y - 3, 6, 6);
					g.drawLine(p1.x, p1.y, p2.x, p2.y);
					p1 = p2;
				}
				
			}
		}
		
		public BufferedImage getMasked() {
			
			Image original = getImage();
			
			BufferedImage masked = new BufferedImage(original.getWidth(null), original.getHeight(null), BufferedImage.TYPE_INT_RGB); 
			
			Graphics2D g = (Graphics2D) masked.getGraphics();
			
			g.drawImage(original, 0, 0, null);
			
			if (mask == null)
				return masked;
			
			Area a = new Area(new Rectangle(0, 0, masked.getWidth(null), masked.getHeight(null)));
			
			a.subtract(mask);
			
			g.setColor(new Color(0, 0, 0));
			
			g.fill(a);
			
			return masked;
			
		}

		public void onPress(FigurePanel source, Point position, int modifiers) {
			// TODO Auto-generated method stub
			
		}

		public void onRelease(FigurePanel source, Point position, int modifiers) {
			// TODO Auto-generated method stub
			
		}
		
		
	};
	

	public MaskEditor() {
		super("Mask editor");
		
		ImageStore.registerAnchorClass(MaskEditor.class);
		
		setLayout(new BorderLayout());
		
		panel = new FigurePanel();
		
		setIconImage(ImageStore.getImage("image-16.png"));
		
		add(panel, BorderLayout.CENTER);
		
		setSize(500, 500);
		
		panel.setTransferHandler(new ImageTransferHandler());
		
		add(initMenu(), BorderLayout.NORTH);
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keystrokes);
		
		actionManager.disableAll();
		
	}
	
	private JToolBar initMenu() {

		JToolBar menu = new JToolBar(JToolBar.HORIZONTAL);
		
		menu.setFloatable(false);

		ButtonGroup modes = new ButtonGroup();
		
		menu.add(new ToggleButton(modeReplace, modes));
		menu.add(new ToggleButton(modeAdd, modes));
		menu.add(new ToggleButton(modeIntersect, modes));
		menu.addSeparator();
		menu.add(saveSelection);
		
		actionManager.newSet("selection.mode", modeReplace, modeAdd, modeIntersect);
		
		actionManager.newSet("save", saveSelection);
		
		return menu;
		
	}
	
	public static void main(String[] args) {
		
		MaskEditor a = new MaskEditor();
		a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		a.setVisible(true);
		
	}

}

