package org.coffeeshop.swing.figure;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.Scrollable;
import javax.swing.SpringLayout;

/**
 * StripBoard is a Swing component that is used to display strip images. It
 * provides userfriendly navigation mechanism.
 * 
 * @author lukacu
 * @since WebStrips 0.1
 */
public class FigurePanel extends JComponent implements Scrollable {

	public static final long serialVersionUID = 1;

	public static enum Button {NONE, LEFT, MIDDLE, RIGHT}
	
	private static final Dimension MINIMUM_SIZE = new Dimension(400, 100);

	private static final int SCROLLBAR_EXTENT = 10;

	public static final WheelAction scrollAction = new WheelAction() {

		public void onWheel(FigurePanel source, Point position, Point vector,
				int modifiers) {
			
			if (source == null)
				return;
			
			if (vector.x != 0 && source.horizontal.isEnabled())
				source.horizontal.setValue(source.horizontal.getValue()
						+ vector.x);

			if (source.vertical.isEnabled()) {
				source.vertical.setValue(source.vertical.getValue()
						+ vector.y);
			} else {
				if (vector.x == 0 && vector.y != 0 && source.horizontal.isEnabled())
					source.horizontal.setValue(source.horizontal.getValue()
							+ vector.y);
				
			}
		}

	};
	
	public static final MoveAction dragAction = new MoveAction() {

		private Cursor cursor = new Cursor(Cursor.MOVE_CURSOR);
		
		public Cursor onMove(FigurePanel source, Point from, Point to,
				int modifiers) {

			if (source == null)
				return cursor;
			
			if (source.horizontal.isEnabled()) {
				int move = (int)Math.round((double)(from.x - to.x) * source.getZoom());
				source.horizontal.setValue(source.horizontal.getValue() + move);
			}
			if (source.vertical.isEnabled()) {
				int move = (int)Math.round((double)(from.y - to.y) * source.getZoom());
				source.vertical.setValue(source.vertical.getValue() + move);
			}

			return cursor;
		}

	};

	public static final WheelAction zoomAction = new WheelAction() {

		public void onWheel(FigurePanel source, Point position, Point vector,
				int modifiers) {
			
			
			if (source == null)
				return;
			
			double zoom = source.getZoom();
			
			double delta = (vector.y < 0) ? -0.1 : 0.1;	
			if (zoom < 1) {
				zoom = (1 / zoom) - delta;
				source.setZoom(1 / zoom);
			} else {
				source.setZoom(zoom + delta);
			}
			
		}

	};
	
	
	// internal controls

	private FigureContainer container;

	private JScrollBar vertical = new JScrollBar(JScrollBar.VERTICAL),
			horizontal = new JScrollBar(JScrollBar.HORIZONTAL);

	private SpringLayout layout = new SpringLayout();

	private double wheelSensitivity = 4;

	// button actions

	private ButtonAction noButtonAction = null;
	
	private ButtonAction leftButtonAction = null;

	private ButtonAction rightButtonAction = null;

	private ButtonAction middleButtonAction = null;

	private MoveAction noMoveAction = null;
	
	private MoveAction leftMoveAction = null;

	private MoveAction rightMoveAction = null;

	private MoveAction middleMoveAction = dragAction;

	private WheelAction noWheelAction = scrollAction;
	
	private WheelAction leftWheelAction = null;

	private WheelAction rightWheelAction = zoomAction;

	private WheelAction middleWheelAction = null;
	
	private double maxZoom = 10, minZoom = 0.1;
	
	// mouse events handler
	private class MouseHandler implements MouseMotionListener, MouseListener,
			MouseWheelListener {

		private Point history = new Point();

		private boolean dragging = false;

		public void mouseClicked(MouseEvent e) {

			Point p = container.pointScreenToFigure(e.getPoint());

			if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
				if (leftButtonAction != null)
					leftButtonAction.onClick(FigurePanel.this, p,
							e.getClickCount(), e.getModifiersEx());
				return;
			}
			if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
				if (rightButtonAction != null)
				rightButtonAction.onClick(FigurePanel.this, p, e
						.getClickCount(), e.getModifiersEx());
				return;
			}
			if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0) {
				if (middleButtonAction != null)
				middleButtonAction.onClick(FigurePanel.this, p, e
						.getClickCount(), e.getModifiersEx());
				return;
			}
		}

		public void mouseDragged(MouseEvent e) {

			Point from = container.pointScreenToFigure(history);
			Point to = container.pointScreenToFigure(e.getPoint());

			Cursor cursor = null;
			
			if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
				if (leftMoveAction != null)
				cursor = leftMoveAction.onMove(FigurePanel.this, from, to, e
						.getModifiersEx());
			} else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
				if (rightMoveAction != null)
					cursor = rightMoveAction.onMove(FigurePanel.this, from, to, e
						.getModifiersEx());
			} else if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0) {
				if (middleMoveAction != null)
					cursor = middleMoveAction.onMove(FigurePanel.this, from, to, e
						.getModifiersEx());
			}

			history.x = e.getX();
			history.y = e.getY();

			if (cursor != null && container.getCursor() != cursor) {
				container.setCursor(cursor);
			} else if (cursor == null) container.setCursor(null);
			
			if (!dragging) {
				dragging = true;
			}

		}

		public void mouseMoved(MouseEvent e) {

			if (noMoveAction != null) {
			
				Point from = container.pointScreenToFigure(history);
				Point to = container.pointScreenToFigure(e.getPoint());
	
				Cursor cursor = noMoveAction.onMove(FigurePanel.this, from, to, e
						.getModifiersEx());
				
				if (cursor != null && container.getCursor() != cursor) {
					container.setCursor(cursor);
				} else if (cursor == null) container.setCursor(null);
				
			}
			
			history.x = e.getX();
			history.y = e.getY();

			if (dragging) {
				dragging = false;
			}
		}

		public void mouseEntered(MouseEvent arg0) {
			history.x = arg0.getX();
			history.y = arg0.getY();
		}

		public void mouseExited(MouseEvent arg0) {
		}

		public void mousePressed(MouseEvent e) {
			Point p = container.pointScreenToFigure(e.getPoint());

			if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
				if (leftButtonAction != null)
					leftButtonAction.onPress(FigurePanel.this, p,
							e.getModifiersEx());
				return;
			}
			if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
				if (rightButtonAction != null)
				rightButtonAction.onPress(FigurePanel.this, p, e.getModifiersEx());
				return;
			}
			if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0) {
				if (middleButtonAction != null)
				middleButtonAction.onPress(FigurePanel.this, p, e.getModifiersEx());
				return;
			}
		}

		public void mouseReleased(MouseEvent e) {
			Point p = container.pointScreenToFigure(e.getPoint());

			if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
				if (leftButtonAction != null)
					leftButtonAction.onRelease(FigurePanel.this, p,
							e.getModifiersEx());
				return;
			}
			if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
				if (rightButtonAction != null)
				rightButtonAction.onRelease(FigurePanel.this, p, e.getModifiersEx());
				return;
			}
			if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0) {
				if (middleButtonAction != null)
				middleButtonAction.onRelease(FigurePanel.this, p, e.getModifiersEx());
				return;
			}
		}

		public void mouseWheelMoved(MouseWheelEvent e) {
			
			if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {

				if (e.getSource() == container) {
				
					Point p = container.pointScreenToFigure(e.getPoint());
					Point vector = new Point();
					
					if (e.isShiftDown()) {
						vector.x = (int) Math.round((double) e.getUnitsToScroll()
								* wheelSensitivity);
					} else {
						vector.y = (int) Math.round((double) e.getUnitsToScroll()
								* wheelSensitivity);
					}
					
					if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
						if (leftWheelAction != null)
							leftWheelAction.onWheel(FigurePanel.this, p, vector, e
								.getModifiersEx());
					} else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
						if (rightWheelAction != null)
							rightWheelAction.onWheel(FigurePanel.this, p, vector, e
								.getModifiersEx());
					} else if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0) {
						if (middleWheelAction != null)
							middleWheelAction.onWheel(FigurePanel.this, p, vector, e
								.getModifiersEx());
					} else {
						if (noWheelAction != null)
							noWheelAction.onWheel(FigurePanel.this, p, vector, e
								.getModifiersEx());
					}

				
				return;
				}
				
				// if the event was trigerred by _horizontal component and
				// that component is enabled, we move horizontal scrollbar
				if (e.getComponent() == horizontal && horizontal.isEnabled()) {
					if (horizontal.isEnabled())
						horizontal.setValue(horizontal.getValue()
								+ (int) Math.round((double) e
										.getUnitsToScroll()
										* wheelSensitivity));
				} else {
					// if vertical scrollbar is disabled and the horisontal
					// isn't, we move horizontal scrollbar
					if (e.getComponent() == vertical &&  vertical.isEnabled()) {
						if (vertical.isEnabled())
							vertical.setValue(vertical.getValue()
									+ (int) Math.round((double) e
											.getUnitsToScroll()
											* wheelSensitivity));
					} else {
						return;
					}
				}
			}
		}

	};

	/**
	 * Constructs a new strip board component
	 * 
	 * @param gc
	 *            window graphical configuration (used to create appropriate
	 *            images for buttons)
	 */
	public FigurePanel() {

		setLayout(layout);

		vertical.setVisibleAmount(SCROLLBAR_EXTENT);
		horizontal.setVisibleAmount(SCROLLBAR_EXTENT);

		container = new FigureContainer();

		add(vertical);
		add(horizontal);
		add(container);

		layout.putConstraint(SpringLayout.EAST, vertical, 0, SpringLayout.EAST,
				this);

		layout.putConstraint(SpringLayout.SOUTH, vertical, -horizontal
				.getPreferredSize().height, SpringLayout.SOUTH, this);

		layout.putConstraint(SpringLayout.NORTH, vertical, 0,
				SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.EAST, horizontal, -vertical
				.getPreferredSize().width, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.WEST, horizontal, 0,
				SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.SOUTH, horizontal, 0,
				SpringLayout.SOUTH, this);

		layout.putConstraint(SpringLayout.EAST, container, 0,
				SpringLayout.WEST, vertical);

		layout.putConstraint(SpringLayout.WEST, container, 0,
				SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.SOUTH, container, 0,
				SpringLayout.NORTH, horizontal);

		layout.putConstraint(SpringLayout.NORTH, container, 0,
				SpringLayout.NORTH, this);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				readjustScrollbars();
				repaint();
			}
		});

		horizontal.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (horizontal.isEnabled())
					container.setOffsetX(-e.getValue());
			}
		});

		vertical.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (vertical.isEnabled())
					container.setOffsetY(-e.getValue());
			}
		});

		MouseHandler mouse = new MouseHandler();

		KeyAdapter keys = new KeyAdapter() {

			public void keyPressed(KeyEvent e) {

			}

		};

		container.addMouseListener(mouse);
		container.addMouseMotionListener(mouse);
		vertical.addMouseWheelListener(mouse);
		horizontal.addMouseWheelListener(mouse);
		container.addMouseWheelListener(mouse);
		
		addKeyListener(keys);

	}

	public void configure(Map<String, String> parameters) {
		System.out.println(minZoom + " " + maxZoom);
		String s = parameters.get("magnification.max");
		if (s != null) {
			try {
				maxZoom = Double.parseDouble(s);
			} catch (NumberFormatException e) {}
		}
		
		s = parameters.get("magnification.min");

		if (s != null) {
			try {
				minZoom = Double.parseDouble(s);
			} catch (NumberFormatException e) {}
		}

		if (minZoom > maxZoom) {
			double tmp = minZoom;
			minZoom = maxZoom;
			maxZoom = tmp;
		}
		
		double zoom = getZoom();
		s = parameters.get("magnification");
		if (s != null) {
			try {
				zoom = Double.parseDouble(s);
			} catch (NumberFormatException e) {}
		}

		setZoom(zoom);
	}
	
	@Override
	public void setLayout(LayoutManager mgr) {
		if (mgr != layout)
			throw new IllegalArgumentException(
					"FigurePanel already has a layout manager");
		super.setLayout(mgr);
	}

	@Override
	public Dimension getMinimumSize() {
		return MINIMUM_SIZE;
	}

	public void setFigure(Figure figure) {
		container.setFigure(figure);

		resetScrollbars();

	}

	public Figure getFigure() {
		return container.getFigure();
	}

	/**
	 * Resets the zoom level
	 * 
	 */
	public void normalZoom() {

		container.setZoom(1);

		readjustScrollbars();
	}

	/**
	 * Set a new magnification value
	 * 
	 * @param zoom
	 *            new magnification value (1 is normal size, 2 is two times
	 *            bigger than normal)
	 */
	public void setZoom(double zoom) {

		container.setZoom(zoom);

		readjustScrollbars();
	}

	/**
	 * Get the current zoom value
	 * 
	 * @return current zoom value
	 */
	public double getZoom() {

		return container.getZoom();

	}

	/**
	 * Resets the scrollbars to (0, 0)
	 * 
	 */
	private void resetScrollbars() {
		container.recalculate();

		if (container.getMaxOffsetY() <= container.getMinOffsetY()) {
			vertical.setEnabled(false);
			vertical.setValues(0, SCROLLBAR_EXTENT, 0, 0);
		} else {
			vertical.setEnabled(true);
			vertical.setValues(0, SCROLLBAR_EXTENT, container.getMinOffsetY(), container.getMaxOffsetY());
		}

		if (container.getMaxOffsetX() <= container.getMinOffsetX()) {
			horizontal.setEnabled(false);
			horizontal.setValues(0, SCROLLBAR_EXTENT, 0, 0);
		} else {
			horizontal.setEnabled(true);
			horizontal.setValues(0, SCROLLBAR_EXTENT, container.getMinOffsetX(), container.getMaxOffsetX());
		}
	}

	/**
	 * Readjusts the scrollbars to the figure.
	 * 
	 */
	private void readjustScrollbars() {
		container.recalculate();

		if (container.getMaxOffsetY() <= container.getMinOffsetY()) {
			vertical.setEnabled(false);
			vertical.setValues(0, SCROLLBAR_EXTENT, 0, 0);
		} else {
			float oldWidth = vertical.getMaximum() - vertical.getMinimum();
			float newWidth = container.getMaxOffsetY() - container.getMinOffsetY();
			
			int newValue = Math.round((float) vertical.getValue()
					* (newWidth / oldWidth));

			vertical.setEnabled(true);
			vertical.setValues(newValue, SCROLLBAR_EXTENT, container.getMinOffsetY(), container.getMaxOffsetY());
		
		}

		if (container.getMaxOffsetX() <= container.getMinOffsetX()) {
			horizontal.setEnabled(false);
			horizontal.setValues(0, SCROLLBAR_EXTENT, 0, 0);
		} else {		
			
			float oldWidth = horizontal.getMaximum() - horizontal.getMinimum();
			float newWidth = container.getMaxOffsetX() - container.getMinOffsetX();
			
			int newValue = Math.round((float) horizontal.getValue()
					* (newWidth / oldWidth));

			horizontal.setEnabled(true);
			horizontal.setValues(newValue, SCROLLBAR_EXTENT, container.getMinOffsetX(), container.getMaxOffsetX());
			
		}

	}

	/**
	 * ImageContainer is a panel that displays the image according to the offset
	 * and similar parameters.
	 * 
	 * @author lukacu
	 * @since WebStrips 0.1
	 */
	class FigureContainer extends JPanel implements FigureObserver, FigureListener {

		/**
		 * Serialization...
		 */
		public static final long serialVersionUID = 1;

		private double zoom = 1;

		private Figure figure;

		private int borderWidth = 0;
		
		/**
		 * Constructs a new component.
		 * 
		 */
		public FigureContainer() {

			setDoubleBuffered(true); // double buffering is enabled
			setBackground(Color.WHITE); // default background color is white
			repaint();
			setToolTipText(" ");
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			// Utilities.setupRenderingHints((Graphics2D) g);

			//((Graphics2D) g).setTransform(identity);
			
			g.setColor(getBackground());

			Rectangle r = getBounds();

			// fill the background
			g.fillRect(r.x, r.y, r.width, r.height);

			// nothing to do if there is no figure to display
			if (figure == null)
				return;

			Rectangle figureSize = new Rectangle(figureOffsetX, figureOffsetY, figureWidth,
					figureHeight);
			
			Rectangle windowSize = new Rectangle(0, 0, getWidth(), getHeight());

			windowSize = figureSize.intersection(windowSize);

			figureSize.x = windowSize.x;
			figureSize.y = windowSize.y;
			figureSize.width = windowSize.width;
			figureSize.height = windowSize.height;

			figure.paint((Graphics2D) g, rectangleScreenToFigure(figureSize), windowSize, this);
			

		}

		public synchronized void setFigure(Figure figure) {
			
			if (this.figure != null) 
				this.figure.removeFigureListener(this);
			
			this.figure = figure;

			if (this.figure != null)
				this.figure.addFigureListener(this);
			
			recalculate();

			repaint();
		}

		public Figure getFigure() {
			return figure;
		}

		private int figureWidth, figureHeight, figureOffsetX, figureOffsetY, minOffsetX, maxOffsetX,
			minOffsetY, maxOffsetY;

		/**
		 * Returns a horizontal offset of the image.
		 * 
		 * @return
		 */
		public int getOffsetX() {
			return figureOffsetX;
		}

		/**
		 * Returns a vertical offset of the image.
		 * 
		 * @return
		 */
		public int getOffsetY() {
			return figureOffsetY;
		}

		
		public void setOffsets(int oX, int oY) {
			figureOffsetX = Math.min(maxOffsetX, Math.max(oX, minOffsetX));
			figureOffsetY = Math.min(maxOffsetY, Math.max(oY, minOffsetY));
			recalculate();
			repaint();
		}

		
		/**
		 * Sets a horizontal offset of the image
		 * 
		 * @param offset
		 */
		public void setOffsetX(int offset) {
			figureOffsetX = offset;
			recalculate();
			repaint();
		}

		/**
		 * Sets a vertical offset of the image
		 * 
		 * @param offset
		 */
		public void setOffsetY(int offset) {
			figureOffsetY = offset;
			recalculate();
			repaint();
		}

		public int getMinOffsetX() {
			return minOffsetX;
		}
		
		public int getMaxOffsetX() {
			return maxOffsetX;
		}
		
		public int getMinOffsetY() {
			return minOffsetY;
		}
		
		public int getMaxOffsetY() {
			return maxOffsetY;
		}
		
		/**
		 * Returns the width of the image
		 * 
		 * @return width of the image
		 */
		public int getFigureWidth() {
			if (figure == null)
				return 0;
			return figureWidth;
		}

		/**
		 * Returns the height of the image
		 * 
		 * @return height of the image
		 */
		public int getFigureHeight() {
			if (figure == null)
				return 0;
			return figureHeight;
		}

		/**
		 * Get the magnification factor for this image container.
		 * 
		 * @return
		 */
		public double getZoom() {
			return zoom;
		}

		/**
		 * Set the magnification factor for this image container.
		 * 
		 * @param zoom
		 *            magnification factor. Value 1 is the original size.
		 */
		public void setZoom(double zoom) {
			this.zoom = Math.max(minZoom, Math.min(maxZoom, zoom));

			recalculate();

			repaint();

		}

		public void figureUpdated(Figure figure) {

			if (this.figure != figure)
				return;

			recalculate();

			repaint();
		}

		public void recalculate() {

			if (this.figure != figure) {
				minOffsetX = 0;
				maxOffsetX = 0;
				minOffsetY = 0;
				maxOffsetY = 0;
				
				return;
			}
				
			if (figure != null) {
				figureWidth = (int) Math.round((double) figure.getWidth(this)
						* this.zoom);
				figureHeight = (int) Math.round((double) figure.getHeight(this)
						* this.zoom);
				
				int zBorder = (int) Math.round((double)borderWidth * this.zoom);
				minOffsetX = - zBorder;
				maxOffsetX = (figureWidth + 2 * zBorder) - getWidth();
				minOffsetY = - zBorder;
				maxOffsetY = (figureHeight + 2 * zBorder) - getHeight();
				
				// calculate the offset of the image
				figureOffsetX = (minOffsetX < maxOffsetX) ? figureOffsetX
						: ((getWidth() - figureWidth) / 2);
				figureOffsetY = (minOffsetY < maxOffsetY) ? figureOffsetY
						: ((getHeight() - figureHeight) / 2);

			}

		}
		
		public Point pointFigureToScreen(Point src) {

			if (src == null)
				return null;

			return new Point((int) Math.round((double) (src.x * this.zoom) + figureOffsetX),
					(int) Math.round((double) (src.y * this.zoom) + figureOffsetY));

		}

		
		public Point pointScreenToFigure(Point src) {

			if (src == null)
				return null;

			return new Point((int) Math.round((double) (src.x - figureOffsetX) / this.zoom),
					(int) Math.round((double) (src.y - figureOffsetY) / this.zoom));

		}
		
		protected void rectangleFigureToScreen(Rectangle src) {

			src.x = (int) Math.round((double) (src.x * this.zoom)
					+ figureOffsetX);
			
			src.y = (int) Math.round((double) (src.y * this.zoom)
					+ figureOffsetX);

			src.width = (int) Math.round((double) (src.width) * this.zoom);

			src.height = (int) Math.round((double) (src.height) * this.zoom);
			
		}
		
		protected Rectangle2D rectangleScreenToFigure(Rectangle src) {

			return new Rectangle2D.Double((double) (src.x - figureOffsetX) / this.zoom,
					(double) (src.y  - figureOffsetY) / this.zoom,
					(double) (src.width / this.zoom),
					(double) (src.height / this.zoom));
			
		}


		public boolean isFigurePointInFigure(Point p) {
			
			if (figure == null || p == null)
				return false;
			
			if (p.x < 0 || p.x >= figure.getWidth(null))
				return false;

			if (p.y < 0 || p.y >= figure.getHeight(null))
				return false;
			
			return true;
			
		}
		
		public String getToolTipText(MouseEvent e) {
			if (noButtonAction != null) {
			
				Point p = pointScreenToFigure(e.getPoint());
				
				Object o = noButtonAction.onClick(FigurePanel.this, p, 0, e.getModifiersEx());
				
				if (o == null)
					return null;
				
				return o.toString();
			}
			
			return FigurePanel.this.getToolTipText();
		}
		
		public Point getToolTipLocation(MouseEvent e) {
			return e.getPoint();
			
		}
	}
	
	public Point pointScreenToFigure(Point src) {

		return container.pointScreenToFigure(src);

	}

	public Point pointFigureToScreen(Point src) {

		return container.pointFigureToScreen(src);

	}

	public void setButtonAction(Button b, ButtonAction h) {

		if (b == null)
			b = Button.NONE;
		
		switch (b) {
		case NONE: 
			noButtonAction = h;
			break;
		case LEFT:
			leftButtonAction = h;
			break;
		case MIDDLE:
			middleButtonAction = h;
			break;
		case RIGHT:
			rightButtonAction = h;
			break;
		}
	}

	public void setMoveAction(Button b, MoveAction h) {

		if (b == null)
			b = Button.NONE;
		
		switch (b) {
		case NONE: 
			noMoveAction = h;
			break;
		case LEFT:
			leftMoveAction = h;
			break;
		case MIDDLE:
			middleMoveAction = h;
			break;
		case RIGHT:
			rightMoveAction = h;
			break;
		}
	}
	
	public void setWheelAction(Button b, WheelAction h) {

		if (b == null)
			b = Button.NONE;
		
		switch (b) {
		case NONE: 
			noWheelAction = h;
			break;
		case LEFT:
			leftWheelAction = h;
			break;
		case MIDDLE:
			middleWheelAction = h;
			break;
		case RIGHT:
			rightWheelAction = h;
			break;
		}
	}
	
	public void setBackground(Color bg) {
		
		super.setBackground(bg);
		container.setBackground(bg);
	}
	
	public double getWheelSensitivity() {
		return wheelSensitivity;
	}

	public boolean isFigurePointInFigure(Point p) {
		
		return container.isFigurePointInFigure(p);
	
	}
	
	
	/**
	 * Sets the wheel sensitivity.
	 * 
	 * @param speed
	 *            the floating point value that will be bounded between 0.01 and
	 *            100
	 */
	public void setWheelSensitivity(double speed) {
		wheelSensitivity = Math.max(0.01, Math.min(100, speed));
	}

	/**
	 * @see Scrollable#getPreferredScrollableViewportSize()
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return container.getPreferredSize();
	}

	/**
	 * @see Scrollable#getScrollableUnitIncrement(Rectangle, int, int)
	 */
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 1;
	}

	/**
	 * @see Scrollable#getScrollableBlockIncrement(Rectangle, int, int)
	 */
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 1;
	}

	/**
	 * @see Scrollable#getScrollableTracksViewportWidth()
	 */
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	/**
	 * @see Scrollable#getScrollableTracksViewportHeight()
	 */
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

}
