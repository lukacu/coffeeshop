package org.coffeeshop.swing;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

public abstract class ToolTipAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public ToolTipAction(String title, Icon icon) {
		super(title, icon);
		if (icon != null)
			this.putValue(Action.SHORT_DESCRIPTION, title);
	}

	public ToolTipAction(String title, String iconId) {
		super(title, ImageStore.getIcon(iconId));
		if (this.getValue(Action.SMALL_ICON) != null)
			this.putValue(Action.SHORT_DESCRIPTION, title);
	}
	
	public ToolTipAction(String title) {
		super(title);
	}
	
	public void setIcon(Icon icon) {
		this.putValue(Action.SMALL_ICON, icon);
		if (this.getValue(Action.SMALL_ICON) == null)
			this.putValue(Action.SHORT_DESCRIPTION, this.getValue(Action.NAME));
	}
	
	public void setTitle(String title) {
		if (this.getValue(Action.SMALL_ICON) != null)
			this.putValue(Action.SHORT_DESCRIPTION, title);
		else
			this.putValue(Action.NAME, title);
	}
}
