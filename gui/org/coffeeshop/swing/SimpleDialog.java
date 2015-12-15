package org.coffeeshop.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.coffeeshop.awt.StackLayout;
import org.coffeeshop.awt.StackLayout.Orientation;
import org.coffeeshop.dialogs.OrganizedSettings;
import org.coffeeshop.settings.Settings;

public class SimpleDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private boolean confirm = false;
	
	private SettingsPanel panel;
	
	private Action ok = new AbstractAction("OK") {
		
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			panel.commit();
			
			confirm = true;
			SimpleDialog.this.setVisible(false);
			
		}
	};
	
	private Action cancel = new AbstractAction("Cancel") {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			SimpleDialog.this.setVisible(false);
			
		}
	};

	public SimpleDialog(Frame owner, String title, SettingsPanel panel) {
		
		super(owner, title, true);

		getContentPane().setLayout(new BorderLayout());
		
		JPanel buttons = new JPanel(new StackLayout(Orientation.HORIZONTAL, 10, 10));

		buttons.add(new JButton(ok));
		buttons.add(new JButton(cancel));
		
		buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		getContentPane().add(buttons, BorderLayout.SOUTH);

		this.panel = panel;
		
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		getContentPane().add(panel, BorderLayout.CENTER);

		setMinimumSize(new Dimension(400, 400));
		
		pack();
		
		centerToOwner();
		
	}
	
	public SimpleDialog(Frame owner, Settings settings, OrganizedSettings structure) {
		
		this(owner, structure.getTitle(), new SettingsPanel(settings, structure) {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void build(OrganizedSettings settings) {
				setLayout(new BorderLayout());
				
				add(buildGroup(settings, false), BorderLayout.CENTER);
			}
			
		});
		
	}
	
	public boolean showDialog() {
		confirm = false;
		setVisible(true);
		return confirm;
	}
	
	public void centerToOwner() {
		
		Dimension size = getSize();
		
		Rectangle reference = null;
		
		if (getOwner() != null) {
			
			reference = getOwner().getBounds();
			
		} else {

			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
	
			reference = ge.getDefaultScreenDevice().getDefaultConfiguration()
					.getBounds();

		}

		setLocation((reference.width - size.width) / 2 + reference.x, (reference.height - size.height) / 2 + reference.y);

	}
}
