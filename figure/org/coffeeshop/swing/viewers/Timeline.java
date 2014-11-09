package org.coffeeshop.swing.viewers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.beans.Transient;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.coffeeshop.awt.Colors;
import org.coffeeshop.settings.PropertiesSettings;
import org.coffeeshop.settings.Settings;
import org.coffeeshop.settings.SettingsChangedEvent;
import org.coffeeshop.settings.SettingsListener;
import org.coffeeshop.swing.figure.AdvancedLayeredFigure;
import org.coffeeshop.swing.figure.ButtonAction;
import org.coffeeshop.swing.figure.FigureObserver;
import org.coffeeshop.swing.figure.FigurePanel;
import org.coffeeshop.swing.figure.FigurePanel.Alignment;
import org.coffeeshop.swing.figure.FigurePanel.Button;
import org.coffeeshop.swing.figure.LayeredFigure;
import org.coffeeshop.swing.figure.MoveAction;
import org.coffeeshop.swing.figure.PlotFigure;
import org.coffeeshop.swing.figure.PlotObject;
import org.coffeeshop.swing.figure.VectorFigure;
import org.coffeeshop.swing.figure.WheelAction;

public class Timeline extends JPanel {

	public static interface TimelineListener {

		public void positionChanged(Timeline timeline, int frame);

		public void selectionChanged(Timeline timeline, int begin, int end);

	}

	private static final long serialVersionUID = 1L;

	private Vector<TimelineListener> listeners = new Vector<TimelineListener>();

	private int selectionOrigin = -1;
	
	private double maxZoom = 10, minZoom = 0.1;

	private double zoom = 1;

	private Color backgroundColor, ticksColor, cursorColor, selectionColor;

	private boolean selectionEnabled;

	protected int height;

	protected int width;

	private float timescaleHeight = 0;
	
	public abstract class TimelineFigure extends VectorFigure {

		@Override
		public int getHeight(FigureObserver observer) {
			return height;
		}

		@Override
		public int getWidth(FigureObserver observer) {
			return (int) (width * zoom);
		}

	}

	public class CursorFigure extends TimelineFigure {

		private int position = 0;

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = Math.min(width - 1, Math.max(0, position));
		}

		@Override
		public String getName() {
			return "Cursor";
		}

		@Override
		protected void paintGeometry(Graphics2D g, float scale,
				FigureObserver observer) {

			g.setColor(cursorColor);

			int x = (int) (position * scale * zoom);

			g.drawLine(x, 0, x, (int) height);
			
			timescaleHeight = g.getFont().getLineMetrics("0", g.getFontRenderContext()).getHeight() + 10;
			
			double tickResolution = bestTickResolution(scale) * 10;
			g.setColor(ticksColor);
			
			g.fillRect(0, 0, (int)(width * zoom), (int) timescaleHeight);
			
			g.setColor(cursorColor);
			
			double tickPosition = 0;

			while (tickPosition < width) {

				g.drawString(Integer.toString((int) tickPosition), (int) (tickPosition * zoom), 10);
				
				tickPosition += tickResolution;
			}
			
			
		}

	}

	public class SelectionFigure extends TimelineFigure {

		private int selectionStart, selectionEnd;

		@Override
		public String getName() {
			return "Selection";
		}

		public int getSelectionStart() {
			return selectionStart;
		}

		public void setSelectionStart(int selectionStart) {
			this.selectionStart = Math.min(width - 1,
					Math.max(0, selectionStart));
			
			this.selectionEnd = this.selectionStart;
		}

		public int getSelectionEnd() {
			return selectionEnd;
		}

		public void setSelectionEnd(int selectionEnd) {
			selectionEnd = Math.min(width - 1, Math.max(0, selectionEnd));
			
			if (selectionEnd < this.selectionStart)
				this.selectionStart = selectionEnd;
			else
				this.selectionEnd = selectionEnd;
		}

		@Override
		protected void paintGeometry(Graphics2D g, float scale,
				FigureObserver observer) {

			g.setColor(backgroundColor);

			g.fillRect(0, 0, (int) (width * zoom), height);

			if (selectionEnd <= selectionStart)
				return;

			g.setColor(selectionColor);

			g.fillRect((int) (selectionStart * zoom), 0,
					(int) ((selectionEnd - selectionStart) * zoom), height);

		}

	}

	public class TimeUnitsFigure extends TimelineFigure {

		@Override
		public String getName() {
			return "Time Units";
		}

		@Override
		protected void paintGeometry(Graphics2D g, float scale,
				FigureObserver observer) {

			double tickResolution = bestTickResolution(scale);

			g.setColor(ticksColor);
			double tickPosition = 0;
			int height = getHeight(observer);

			while (tickPosition < width && tickPosition < width) {

				g.drawLine((int) (tickPosition * zoom), 0,
						(int) (tickPosition * zoom), height);

				tickPosition += tickResolution;
			}

		}

	}

	private class PlotTrack extends PlotFigure implements TimelineTrack, SettingsListener {

		private double maxHeight = 0; 
		
		public PlotTrack(String name) {
			super(name);
			
			maxHeight = settings.getDouble(name + "@max", 0);
			
			setTitle(settings.getString(name + "@title", name));
		}
		
		@Override
		public void addObject(PlotObject object) {

			if (object == null || containsObject(object)) {
				
				updateTrackLayout();
				
				setCustomWidth(getOriginalWidth() * getZoom());
				
				timeline.repaint();
				
				return;
			}

			super.addObject(object);
				
			if (size() == 1) {
				
				container.add(this);
				settings.addSettingsListener(this);
				updateTrackLayout();
				
				setCustomWidth(getOriginalWidth() * getZoom());
				
			}
			
			timeline.repaint();
		}

		@Override
		public void removeObject(PlotObject object) {
			super.removeObject(object);

			if (object != null && size() == 0) {
				
				container.remove(this);
				settings.removeSettingsListener(this);
				updateTrackLayout();
				
			}
			
			timeline.repaint();
		}

		@Override
		public void settingsChanged(SettingsChangedEvent e) {
			
			if (!e.getKey().startsWith(getName()))
				return;
			
			String key = e.getKey();
			
			if (key == getName() + "@max") {
				maxHeight = e.getDouble(0);
				recalculateBounds();
			} else if (key == getName() + "@title")
				setTitle(e.getString(getName()));
			
			container.remove(this);
			updateTrackLayout();
			
		}
		
		@Override
		protected void recalculateBounds() {
			
			super.recalculateBounds();
			
			if (maxHeight != 0)
				bounds.setRect(bounds.getX(), bounds.getCenterY(), bounds.getWidth(), maxHeight);
			
		}

		@Override
		public String getToolTip(Point2D point) {
			
			for (PlotObject obj : this) {
				String text = obj.getToolTip(point);
				if (text != null) return text;
			}
			
			return null;
		}
		
	}

	private double bestTickResolution(double scale) {

		double d = scale * zoom;

		if (d > 5)
			return scale;

		if (d * 5 > 5)
			return scale * 5;

		if (d * 10 > 5)
			return scale * 10;

		if (d * 10 > 5)
			return scale * 10;

		return scale * 100;

	}
	
	public void setZoom(double zoom) {

		this.zoom = Math.max(minZoom, Math.min(maxZoom, zoom));

		for (PlotTrack track : tracks)
			track.setCustomWidth(track.getOriginalWidth() * this.zoom);

		timeline.revalidate();

		Point spos = timeline.pointFigureToScreen(new Point((int) (width * zoom), 0));
		
		if (spos.x < timeline.getViewportWidth()) {
			
			timeline.scrollToView(new Point((int) (width * zoom), 0), Alignment.TOP_RIGHT);
			
		}
		
		repaint();

	}

	public double getZoom() {

		return zoom;

	}

	public synchronized void setPosition(int position) {

		cursor.setPosition(position);

		Point spos = timeline.pointFigureToScreen(new Point((int) (cursor
				.getPosition() * zoom), 0));

		if (spos.x < 0)
			timeline.scrollToView(new Point(
					(int) (cursor.getPosition() * zoom), 0),
					Alignment.TOP_RIGHT);
		else if (spos.x > timeline.getViewportWidth())
			timeline.scrollToView(new Point(
					(int) (cursor.getPosition() * zoom), 0), Alignment.TOP_LEFT);

		synchronized (listeners) {

			for (TimelineListener l : listeners) {
				l.positionChanged(this, cursor.getPosition());
			}

		}

		timeline.repaint();
	}

	public synchronized int getPosition() {

		return cursor.getPosition();

	}

	private void updateColorTheme(Color base) {

		backgroundColor = Colors.changeBrightness(base, 0.7);

		ticksColor = Colors.changeBrightness(base, 0.67);

		cursorColor = Colors.changeBrightness(base, 0.2);

		selectionColor = Colors.changeBrightness(base, 0.8);

	}

	private void updateTrackLayout() {
		
		double total = timeline.getViewportHeight() - timescaleHeight;
		
		double current = timescaleHeight;
		
		for (PlotTrack track : tracks) {
			
			if (track.isEmpty()) continue;
			
			container.setOffset(track, 0, current);
			
			track.setCustomHeight(total / container.size());
			
			current += total / container.size();
		}
		
	}
	
	private void updateLength() {
		
		boolean stretch = getZoom() == minZoom;

		minZoom = timeline.getViewportWidth() / (float) width;

		setZoom(stretch ? minZoom : getZoom());
		
		if (!stretch) {
			
			Point spos = timeline.pointFigureToScreen(new Point((int) (width * zoom), 0));
			
			if (spos.x < timeline.getViewportWidth()) {
				
				timeline.scrollToView(new Point((int) (width * zoom), 0), Alignment.TOP_RIGHT);
				
			}
			
			
		}
		
	}
	
	private FigurePanel timeline;

	private CursorFigure cursor = new CursorFigure();

	private SelectionFigure selection = new SelectionFigure();

	private TimeUnitsFigure units = new TimeUnitsFigure();

	private AdvancedLayeredFigure container = new AdvancedLayeredFigure();
	
	private Vector<PlotTrack> tracks = new Vector<PlotTrack>();
	
	private Settings settings;

	public Timeline(int length) {
		this(length, null);
	}
	
	public Timeline(int length, Settings settings) {

		super(new BorderLayout());

		this.settings = settings == null ? new PropertiesSettings() : settings;
		
		width = Math.max(1, length);

		timeline = new FigurePanel();

		timeline.setAlignment(Alignment.TOP_LEFT);
		timeline.setScrollbarVisible(false);

		LayeredFigure layers = new LayeredFigure(selection, units, container,
				cursor);

		timeline.setFigure(layers);

		timeline.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {

				height = timeline.getViewportHeight();

				updateTrackLayout();
				
				updateLength();
				
				timeline.revalidate();
			}

		});

		timeline.setWheelAction(Button.NONE, new WheelAction() {

			@Override
			public void onWheel(FigurePanel source, Point position,
					Point vector, int modifiers) {

				if (source == null)
					return;

				if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0) {

					double zoom = getZoom();

					double delta = (vector.x < 0) ? -0.1 : 0.1;

					if (zoom < 1) {
						zoom = (1 / zoom) - delta;
						setZoom(1 / zoom);
					} else {
						setZoom(zoom + delta);
					}

				} else {

					int move = (modifiers & KeyEvent.CTRL_DOWN_MASK) == 0 ? vector.y
							: (vector.y > 0 ? 1 : -1);

					setPosition((int) (getPosition() + move));

					timeline.repaint();
				}

			}
		});

		timeline.setButtonAction(Button.LEFT, new ButtonAction() {

			@Override
			public void onRelease(FigurePanel source, Point position,
					int modifiers) {

			}

			@Override
			public void onPress(FigurePanel source, Point position,
					int modifiers) {

			}

			@Override
			public Object onClick(FigurePanel source, Point position,
					int clicks, int modifiers) {

				if (source == null || clicks != 1)
					return null;

				if ((modifiers & MouseEvent.SHIFT_DOWN_MASK) != 0) {
					TimelineTrack track = getTrack(position, null);
					
					return null;
				}
				
				
				setPosition((int) (position.x / zoom));

				timeline.repaint();

				return null;
			}
		});

		timeline.setButtonAction(Button.RIGHT, new ButtonAction() {

			@Override
			public void onRelease(FigurePanel source, Point position,
					int modifiers) {

				selectionOrigin = -1;
				
			}

			@Override
			public void onPress(FigurePanel source, Point position,
					int modifiers) {

				if (selectionEnabled)
					selectionOrigin = ((int) (position.x / zoom));

			}

			@Override
			public Object onClick(FigurePanel source, Point position,
					int clicks, int modifiers) {
				
				if (selectionEnabled) {
					clearSelection();
				}
				
				return null;
			}
		});
		
		timeline.setButtonAction(Button.NONE, new ButtonAction() {

			@Override
			public void onRelease(FigurePanel source, Point position,
					int modifiers) {

			}

			@Override
			public void onPress(FigurePanel source, Point position,
					int modifiers) {

			}

			@Override
			public Object onClick(FigurePanel source, Point position,
					int clicks, int modifiers) {
				Point2D within = new Point2D.Double();
				
				TimelineTrack track = getTrack(position, within);
				
				if (track == null)
					return null;
				
				String text = track.getToolTip(within);
				
				return text;
			}
		});
		
		timeline.setMoveAction(Button.NONE, new MoveAction() {

			@Override
			public Cursor onMove(FigurePanel source, Point from, Point to,
					int modifiers) {
				return new Cursor(Cursor.DEFAULT_CURSOR);
			}
			
			
		});

		timeline.setMoveAction(Button.RIGHT, new MoveAction() {

			@Override
			public Cursor onMove(FigurePanel source, Point from, Point to,
					int modifiers) {

				if (selectionEnabled && selectionOrigin > -1) {
					
					int frame = (int) (to.x / zoom);
					
					if (frame < selectionOrigin) {
						setSelection(frame, selectionOrigin);
					} else if (frame > selectionOrigin) {
						setSelection(selectionOrigin, frame);
					}
		
					timeline.repaint();
					
				}

				return new Cursor(Cursor.DEFAULT_CURSOR);
			}
		});

		timeline.setMoveAction(Button.MIDDLE, new MoveAction() {

			@Override
			public Cursor onMove(FigurePanel source, Point from, Point to,
					int modifiers) {

				int vel = to.x - from.x;

				if (Math.abs(vel) < 2)
					return new Cursor(Cursor.DEFAULT_CURSOR);

				timeline.scrollRelativeScreen(new Point(-vel, 0));

				return new Cursor(Cursor.HAND_CURSOR);
			}
		});

		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		updateColorTheme(getBackground());

		setSelectionEnabled(true);

		add(timeline, BorderLayout.CENTER);
	}

	
	
	public boolean isSelectionEnabled() {
		
		return selectionEnabled;
		
	}

	public void setSelectionEnabled(boolean selectionEnabled) {
		
		this.selectionEnabled = selectionEnabled;
		
	}

	public synchronized void clearSelection() {

		setSelection(cursor.getPosition(), cursor.getPosition());

	}

	public synchronized void setSelection(int beginFrame, int endFrame) {

		if (endFrame < beginFrame) {
			clearSelection();
			return;
		}

		selection.setSelectionStart(beginFrame);
		selection.setSelectionEnd(endFrame);

		repaint();

		synchronized (listeners) {

			for (TimelineListener l : listeners) {
				l.selectionChanged(this, selection.getSelectionStart(),
						selection.getSelectionEnd());
			}

		}
	}

	public synchronized boolean isSelectionValid() {
		return selection.getSelectionStart() < selection.getSelectionEnd();
	}

	public synchronized int getSelectionStart() {
		return selection.getSelectionStart();
	}

	public synchronized int getSelectionEnd() {
		return selection.getSelectionEnd();
	}

	public void addTimelineListener(TimelineListener l) {
		
		synchronized (listeners) {
			listeners.add(l);
		}
		
	}

	public void removeTimelineListener(TimelineListener l) {
		
		synchronized (listeners) {
			listeners.remove(l);
		}
		
	}

	public TimelineTrack getTrack(String name) {
		
		for (TimelineTrack track : tracks) {
			if (track.getName().equals(name))
				return track;
		}
		
		PlotTrack track = new PlotTrack(name);

		tracks.add(track);
		
		updateTrackLayout();
		
		updateLength();
		
		timeline.revalidate();
		
		return track;
		
	}
	
	@Override
	@Transient
	public Dimension getMinimumSize() {
		return new Dimension(width, (int)timescaleHeight + container.size() * 10);
	}
	
	@Override
	@Transient
	public Dimension getPreferredSize() {
		return new Dimension(width, (int)timescaleHeight + container.size() * 60);
	}
	
	public void setLength(int length) {
		
		this.width = Math.max(1, length);
		
		updateLength();
		updateTrackLayout();

		timeline.revalidate();
		
	}
	
	public int getLength() {
		
		return width;
		
	}
	
	public TimelineTrack getTrack(Point p, Point2D within) {
		
		Point spos = timeline.pointScreenToFigure(p);
		
		spos.x /= zoom;
		
		for (PlotTrack track : tracks) {
			Point2D offset = container.getOffset(track);
			
			if (offset == null)
				continue;
			
			double x = spos.x - offset.getX();
			double y = spos.y - offset.getY();
			
			if (y > 0 && y < track.getCustomHeight()) {
				if (within != null)
					within.setLocation(x, y);
				
				return track;
			}
		
		}
		
		return null;
	}
	
}
