
package org.coffeeshop.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;

import org.coffeeshop.awt.StackLayout;
import org.coffeeshop.swing.ImageStore;

public class StatusBar extends JComponent {

	public static final long serialVersionUID = 1;
	
	public static final int CLEANUP_DELAY = 3000;

	public enum MessageType {INFO, WARNING, ERROR}
	
	private JLabel message;
	
	private JProgressBar progress;
	
	private Timer taskTimer = new Timer();
	
	private TimerTask cleanupTask = null;
	
	private JPanel widgets = null;
	
	JToolBar tools = new JToolBar(JToolBar.HORIZONTAL);
	
	private Hashtable<String, JLabel> labels = new Hashtable<String, JLabel>();
	
	private class CleanupTask extends TimerTask {
		
		public void run() {

			synchronized (message) {
				
				message.setText(" ");
				message.setIcon(null);
				

			}
		}
		
	};
	
	/**
	 * Creates new status bar component
	 *
	 */
	public StatusBar() {
		super();
		
		setLayout(new BorderLayout(3, 3));
		
		setBorder(BorderFactory.createEmptyBorder(4, 2, 2, 2));
		
		message = new JLabel(" ");
		
		message.setIconTextGap(10);
		
		progress = new JProgressBar();
		
		progress.setPreferredSize(new Dimension(150, 20));
		
		progress.setVisible(false);
		
		add(message, BorderLayout.CENTER);
		
		widgets = new JPanel(new StackLayout(StackLayout.Orientation.HORIZONTAL, 5, 0));
		
		widgets.add(progress);
		
		widgets.add(tools);
		
		tools.setFloatable(false);
		
		add(widgets, BorderLayout.EAST);
		
	}
	
	public void setMessage(String msg, MessageType type) {
		
		switch (type) {
		case INFO:
			setMessage(msg, ImageStore.getIcon("information-16.png"));
			break;
		case WARNING:
			setMessage(msg, ImageStore.getIcon("warning-16.png"));
			break;
		case ERROR:
			setMessage(msg, ImageStore.getIcon("error-16.png"));
			break;
		}
	}
	
	/**
	 * Sets new message to be displayed and resets the erase counter.
	 * 
	 * @param msg new message
	 */
	public void setMessage(String msg, Icon icon) {
		
		synchronized (message) {
			
			message.setIcon(icon);
			
			if (msg == null || msg.length() == 0) {
				message.setText(" ");
				return;
			}
				
			message.setText(msg);
			
			
			if (cleanupTask != null) {
				cleanupTask.cancel();
			}
			
			cleanupTask = new CleanupTask();
			
			try {
				taskTimer.schedule(cleanupTask, CLEANUP_DELAY);
				
			}
			catch (IllegalStateException e) {}
		}
		
		revalidate();
	}

	/**
	 * Sets the status of the progress bar. The value of the parameter <tt>p</tt>
	 * has three meanings:
	 * <ul>
	 * <li>less than 0: the progress bar is not visible</li>
	 * <li>between 0 and 100: the progress bar is visible and shows the progress 
	 * according to the value</li>
	 * <li>above 100: the progress bar is visible, but the value is not known</li>
	 * </ul>
	 * 
	 * @param p
	 */
	public void setProgress(int p) {
		if (p < 0) {
			progress.setVisible(false);
			progress.setValue(0);
			return;
		}
		
		if (p > 100) {
			progress.setIndeterminate(true);
			return;
		}
		
		progress.setVisible(true);
		progress.setIndeterminate(false);
		
		progress.setValue(p);
		
	}

	public void setProgress(String s) {
		
		if (s != null) {
			progress.setStringPainted(true);
			progress.setString(s);
		} else
			progress.setStringPainted(false);
		
	}
	
	public void setLabel(String name, String text) {
		
		JLabel label = labels.get(name);
		
		if (label == null) {
			label = new JLabel(text);
			
			widgets.add(label);
			
			labels.put(name, label);
			return;
		}
		
		label.setText(text);
		
	}

	public void addAction(Action action) {
		tools.add(action);
	}
}
