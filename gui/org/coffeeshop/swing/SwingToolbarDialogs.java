package org.coffeeshop.swing;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JToolBar;

import org.coffeeshop.application.Application;
import org.coffeeshop.dialogs.ApplicationDialogs;

public class SwingToolbarDialogs extends ApplicationDialogs {

	private JToolBar toolbar = new JToolBar();
	
	public SwingToolbarDialogs(Application application, Icon aboutIcon, Icon preferencesIcon) {
		super(application);
		
		toolbar.setFloatable(false);
		
		if (aboutIcon != null)
			toolbar.add(new ToolTipAction("About", aboutIcon) {
			
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					fireOpenAbout();
				}
			});
		
		if (preferencesIcon != null)
			toolbar.add(new ToolTipAction("Preferences", preferencesIcon) {
			
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					fireOpenPreferences();
				}
			});
	}

	public JToolBar getToolBar() {
		return toolbar;
	}
	
}
