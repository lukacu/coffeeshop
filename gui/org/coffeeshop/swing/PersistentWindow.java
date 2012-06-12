package org.coffeeshop.swing;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JFrame;

import org.coffeeshop.application.Application;
import org.coffeeshop.settings.Settings;
import org.coffeeshop.settings.WriteableSettings;
import org.coffeeshop.string.StringUtils;

public abstract class PersistentWindow extends JFrame {

	public static interface PersistentWindowsListener {

		public void onPersistentWindowOpened(PersistentWindow window);

		public void onPersistentWindowClosed(PersistentWindow window);

	}

	private static boolean exitOnAllClosed = true;

	public static boolean willExitOnAllClosed() {
		return exitOnAllClosed;
	}

	public static void setExitOnAllClosed(boolean exitOnAllClosed) {
		PersistentWindow.exitOnAllClosed = exitOnAllClosed;
	}

	public static int getPersistentWindowsCount() {

		return openedWindows.size();

	}

	private static final long serialVersionUID = 1L;

	private static Vector<PersistentWindowsListener> listeners = new Vector<PersistentWindow.PersistentWindowsListener>();

	public static void addPersistentWindowsListener(PersistentWindowsListener l) {

		synchronized (listeners) {

			listeners.add(l);

		}

	}

	public static void removePersistentWindowsListener(
			PersistentWindowsListener l) {

		synchronized (listeners) {

			listeners.remove(l);

		}
	}

	private static HashSet<PersistentWindow> openedWindows = new HashSet<PersistentWindow>();

	private String persistId;

	public PersistentWindow(String id, String title) {

		super(title);

		this.persistId = id;

		if (StringUtils.empty(id))
			throw new IllegalArgumentException("ID must not be null");

		setSize(800, 600);
		center();
		
		defaultState();

		try {

			// TODO: split the Application from PersistentWindow
			Settings settings = Application.getApplicationSettings();

			Point loc = this.getLocation();
			Dimension dim = this.getSize();
			int state = this.getExtendedState();

			loc.x = settings.getInt(persistId + ".x", loc.x);
			loc.y = settings.getInt(persistId + ".y", loc.y);
			dim.width = settings.getInt(persistId + ".width", dim.width);
			dim.height = settings.getInt(persistId + ".height", dim.height);
			state = settings.getInt(persistId + ".state", state);

			this.setLocation(loc);
			this.setSize(dim);
			this.setExtendedState(state);

		} catch (RuntimeException e) {
		}

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {

				synchronized (openedWindows) {

					openedWindows.add(PersistentWindow.this);

				}

				synchronized (listeners) {

					for (PersistentWindowsListener l : listeners) {

						l.onPersistentWindowOpened(PersistentWindow.this);

					}

				}

			}

			public void windowClosing(WindowEvent e) {
				try {

					Settings settings = Application.getApplicationSettings();

					Point loc = getLocation();
					Dimension dim = getSize();
					int state = getExtendedState();

					settings.setInt(persistId + ".x", loc.x);
					settings.setInt(persistId + ".y", loc.y);
					settings.setInt(persistId + ".width", dim.width);
					settings.setInt(persistId + ".height", dim.height);
					settings.setInt(persistId + ".state", state);

					try {
						saveState(persistId, settings, true);
					} catch (Exception ex) {
					}
				} catch (RuntimeException ex) {
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {
				synchronized (openedWindows) {

					openedWindows.remove(PersistentWindow.this);

					synchronized (listeners) {

						for (PersistentWindowsListener l : listeners) {

							l.onPersistentWindowClosed(PersistentWindow.this);

						}

					}

					if (openedWindows.isEmpty() && exitOnAllClosed)
						System.exit(0);
				}
			}
		});

	}

	protected abstract void defaultState();

	protected void saveState(String persistId, WriteableSettings settings,
			boolean closing) {

	};

	public void center() {

		Dimension w = getSize();

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();

		Rectangle r = ge.getDefaultScreenDevice().getDefaultConfiguration()
				.getBounds();

		Dimension d = new Dimension(r.width, r.height);

		setLocation((d.width - w.width) / 2, (d.height - w.height) / 2);

	}

}
