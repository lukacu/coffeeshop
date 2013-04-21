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
 * 
 */
public class FigurePanel extends JComponent implements Scrollable {

	public static final long serialVersionUID = 1;

	public static enum Alignment {TOP_LEFT, BOTTOM_LEFT, CENTER_LEFT, TOP_CENTER, 
		CENTER, BOTTOM_CENTER, TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT}
	
	public static enum Button {NONE, LEFT, MIDDLE, RIGHT}
	
	private static final Dimension MINIMUM_SIZE = new Dimension(400, 100);

	private static final int SCROLLBAR_EXTENT = 10;

	public static final WheelAction scrollAction = new WheelAction() {

		public void onWheel(FigurePanel source, Point position, Point vector,
				int modifiers) {
			
			if (source == null)
				return;
			
			if (vector.x != 0 && source.horizontalScrollbar.isEnabled())
				source.horizontalScrollbar.setValue(source.horizontalScrollbar.getValue()
						+ vector.x);

			if (source.verticalScrollbar.isEnabled()) {
				source.verticalScrollbar.setValue(source.verticalScrollbar.getValue()
						+ vector.y);
			} else {
				if (vector.x == 0 && vector.y != 0 && source.horizontalScrollbar.isEnabled())
					source.horizontalScrollbar.setValue(source.horizontalScrollbar.getValue()
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
			
			if (source.horizontalScrollbar.isEnabled()) {
				int move = (int)Math.round((double)(from.x - to.x) * source.getZoom());
				source.horizontalScrollbar.setValue(source.horizontalScrollbar.getValue() + move);
			}
			if (source.verticalScrollbar.isEnabled()) {
				int move = (int)Math.round((double)(from.y - to.y) * source.getZoom());
				source.verticalScrollbar.setValue(source.verticalScrollbar.getValue() + move);
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

	private JScrollBar verticalScrollbar = new JScrollBar(JScrollBar.VERTICAL),
			horizontalScrollbar = new JScrollBar(JScrollBar.HORIZONTAL);

	private Alignment alignment = Alignment.CENTER;
	
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
				if (e.getComponent() == horizontalScrollbar && horizontalScrollbar.isEnabled()) {
					if (horizontalScrollbar.isEnabled())
						horizontalScrollbar.setValue(horizontalScrollbar.getValue()
								+ (int) Math.round((double) e
										.getUnitsToScroll()
										* wheelSensitivity));
				} else {
					// if vertical scrollbar is disabled and the horisontal
					// isn't, we move horizontal scrollbar
					if (e.getComponent() == verticalScrollbar &&  verticalScrollbar.isEnabled()) {
						if (verticalScrollbar.isEnabled())
							verticalScrollbar.setValue(verticalScrollbar.getValue()
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
	 * 
	 */
	public FigurePanel() {

		setLayout(layout);

		verticalScrollbar.setVisibleAmount(SCROLLBAR_EXTENT);
		horizontalScrollbar.setVisibleAmount(SCROLLBAR_EXTENT);

		container = new FigureContainer();

		add(verticalScrollbar);
		add(horizontalScrollbar);
		add(container);

		layout.putConstraint(SpringLayout.EAST, verticalScrollbar, 0, SpringLayout.EAST,
				this);

		layout.putConstraint(SpringLayout.SOUTH, verticalScrollbar, -horizontalScrollbar
				.getPreferredSize().height, SpringLayout.SOUTH, this);

		layout.putConstraint(SpringLayout.NORTH, verticalScrollbar, 0,
				SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.EAST, horizontalScrollbar, -verticalScrollbar
				.getPreferredSize().width, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.WEST, horizontalScrollbar, 0,
				SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.SOUTH, horizontalScrollbar, 0,
				SpringLayout.SOUTH, this);

		layout.putConstraint(SpringLayout.EAST, container, 0,
				SpringLayout.WEST, verticalScrollbar);

		layout.putConstraint(SpringLayout.WEST, container, 0,
				SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.SOUTH, container, 0,
				SpringLayout.NORTH, horizontalScrollbar);

		layout.putConstraint(SpringLayout.NORTH, container, 0,
				SpringLayout.NORTH, this);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				readjustScrollbars();
				repaint();
			}
		});

		horizontalScrollbar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (horizontalScrollbar.isEnabled())
					container.setOffsetX(-e.getValue());
			}
		});

		verticalScrollbar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (verticalScrollbar.isEnabled())
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
		verticalScrollbar.addMouseWheelListener(mouse);
		horizontalScrollbar.addMouseWheelListener(mouse);
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

	@Override
	public void revalidate() {
		container.recalculate(container.figure);
		super.revalidate();
	}
	
	public Alignment getAligmnent() {
		return alignment;
	}

	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
		this.revalidate();
	}
	
	
	/**
	 * Resets the scrollbars to (0, 0)
	 * 
	 */
	private void resetScrollbars() {
		container.recalculate(container.figure);

		if (container.getMaxOffsetY() <= container.getMinOffsetY()) {
			verticalScrollbar.setEnabled(false);
			verticalScrollbar.setValues(0, SCROLLBAR_EXTENT, 0, 0);
		} else {
			verticalScrollbar.setEnabled(true);
			verticalScrollbar.setValues(0, SCROLLBAR_EXTENT, container.getMinOffsetY(), container.getMaxOffsetY());
		}

		if (container.getMaxOffsetX() <= container.getMinOffsetX()) {
			horizontalScrollbar.setEnabled(false);
			horizontalScrollbar.setValues(0, SCROLLBAR_EXTENT, 0, 0);
		} else {
			horizontalScrollbar.setEnabled(true);
			horizontalScrollbar.setValues(0, SCROLLBAR_EXTENT, container.getMinOffsetX(), container.getMaxOffsetX());
		}
	}

	/**
	 * Readjusts the scrollbars to the figure.
	 * 
	 */
	private void readjustScrollbars() {
		container.recalculate(container.figure);

		if (container.getMaxOffsetY() <= container.getMinOffsetY()) {
			verticalScrollbar.setEnabled(false);
			verticalScrollbar.setValues(0, SCROLLBAR_EXTENT, 0, 0);
		} else {
			float oldWidth = verticalScrollbar.getMaximum() - verticalScrollbar.getMinimum();
			float newWidth = container.getMaxOffsetY() - container.getMinOffsetY();
			
			int newValue = Math.round((float) verticalScrollbar.getValue()
					* (newWidth / oldWidth));

			verticalScrollbar.setEnabled(true);
			verticalScrollbar.setValues(newValue, SCROLLBAR_EXTENT, container.getMinOffsetY(), container.getMaxOffsetY());
		
		}

		if (container.getMaxOffsetX() <= container.getMinOffsetX()) {
			horizontalScrollbar.setEnabled(false);
			horizontalScrollbar.setValues(0, SCROLLBAR_EXTENT, 0, 0);
		} else {		
			
			float oldWidth = horizontalScrollbar.getMaximum() - horizontalScrollbar.getMinimum();
			float newWidth = container.getMaxOffsetX() - container.getMinOffsetX();
			
			int newValue = Math.round((float) horizontalScrollbar.getValue()
					* (newWidth / oldWidth));

			horizontalScrollbar.setEnabled(true);
			horizontalScrollbar.setValues(newValue, SCROLLBAR_EXTENT, container.getMinOffsetX(), container.getMaxOffsetX());
			
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
			
			recalculate(figure);

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
			recalculate(figure);
			repaint();
		}

		
		/**
		 * Sets a horizontal offset of the image
		 * 
		 * @param offset
		 */
		public void setOffsetX(int offset) {
			figureOffsetX = offset;
			recalculate(figure);
			repaint();
		}

		/**
		 * Sets a vertical offset of the image
		 * 
		 * @param offset
		 */
		public void setOffsetY(int offset) {
			figureOffsetY = offset;
			recalculate(figure);
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

			recalculate(figure);

			repaint();

		}

		public void figureUpdated(Figure figure) {

			if (this.figure != figure)
				return;

			recalculate(figure);

			repaint();
		}

		public void recalculate(Figure figure) {

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
				if (minOffsetX >= maxOffsetX)
					switch (alignment) {
					case TOP_CENTER:
					case BOTTOM_CENTER:
					case CENTER:
						figureOffsetX = ((getWidth() - figureWidth) / 2);
						break;
					case TOP_RIGHT:
					case CENTER_RIGHT:
					case BOTTOM_RIGHT:
						figureOffsetX = (getWidth() - figureWidth);
						break;
					case TOP_LEFT:
					case CENTER_LEFT:
					case BOTTOM_LEFT:
						figureOffsetX = 0;
						break;
					}
						
				
				if (minOffsetY >= maxOffsetY)
					switch (alignment) {
					case CENTER_RIGHT:
					case CENTER_LEFT:
					case CENTER:
						figureOffsetY = ((getHeight() - figureHeight) / 2);
						break;
					case BOTTOM_RIGHT:
					case BOTTOM_CENTER:
					case BOTTOM_LEFT:
						figureOffsetY = (getHeight() - figureHeight);
						break;
					case TOP_LEFT:
					case TOP_RIGHT:
					case TOP_CENTER:
						figureOffsetY = 0;
						break;
					}

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
