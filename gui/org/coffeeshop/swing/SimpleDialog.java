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
	
	private Action ok = new AbstractAction("OK") {
		
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
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
	
	public SimpleDialog(Frame owner, Settings settings, OrganizedSettings structure) {
		
		super(owner, structure.getTitle(), true);

		getContentPane().setLayout(new BorderLayout());
		
		JPanel buttons = new JPanel(new StackLayout(Orientation.HORIZONTAL, 10, 10));

		buttons.add(new JButton(ok));
		buttons.add(new JButton(cancel));
		
		buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		getContentPane().add(buttons, BorderLayout.SOUTH);

		SettingsPanel panel = new SettingsPanel(settings, structure);;
		
		getContentPane().add(panel, BorderLayout.CENTER);

		pack();
		
		centerToOwner();
		
	}
	
	public boolean showDialog() {
		confirm = false;
		setVisible(true);
		return confirm;
	}
	
	public void centerToOwner() {
		
		Dimension w = getSize();
		
		Rectangle reference = null;
		
		if (getOwner() != null) {
			reference = getOwner().getBounds();
		} else {

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();

		reference = ge.getDefaultScreenDevice().getDefaultConfiguration()
				.getBounds();

		}

		setLocation((reference.width - w.width - reference.x) / 2 + reference.x, (reference.height - w.height - reference.y) / 2 + reference.y);

	}
}
