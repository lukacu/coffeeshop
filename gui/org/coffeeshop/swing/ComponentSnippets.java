package org.coffeeshop.swing;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ComponentSnippets {

	public static JPanel transparentPanel() {
		
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		return panel;
		
	}
	
	public static JButton flatButton(JButton button) {

		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);
		
		return button;
	}
	
}
