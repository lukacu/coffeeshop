package org.coffeeshop.swing;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.coffeeshop.awt.StackLayout;
import org.coffeeshop.awt.StackLayout.Orientation;
import org.coffeeshop.dialogs.OrganizedSettings;
import org.coffeeshop.swing.SettingsPanel;

public class SimpleDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private OrganizedSettings settings;
	
	private SettingsPanel panel;
	
	private Action ok = new AbstractAction("OK") {
		
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
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
	
	public SimpleDialog(Frame owner, String title) {
		
		super(owner, title);
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		
		getContentPane().setLayout(new BorderLayout());
		
	
		JPanel buttons = new JPanel(new StackLayout(Orientation.HORIZONTAL, 10, 10));

		buttons.add(new JButton(ok));
		buttons.add(new JButton(cancel));
		
		buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		getContentPane().add(BorderLayout.SOUTH, buttons);

	}
	
	@Override
	public void setVisible(boolean b) {

		if (b) {
			
			if (panel != null) 
				getContentPane().remove(panel);
			
			panel = new SettingsPanel(settings);
			
			getContentPane().add(panel);
		}
		
		super.setVisible(b);
	}
	
	public void addMultichoice(String key, String title, Collection<?> choices) {
		
		//settings.attachValue(key, title, new EnumeratedStringParser(validOptionValues, caseSensitive, checkOptionChars))
		
		
		
	}
	/*
	public Object get(String key) {
		
		return settings.get(key);
		
	}
*/
}
