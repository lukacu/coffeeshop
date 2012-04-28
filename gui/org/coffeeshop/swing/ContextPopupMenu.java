package org.coffeeshop.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class ContextPopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;

	public static class ContextActionEvent extends ActionEvent {

		private Object context;

		public ContextActionEvent(Object source, int id, String command, int modifiers, Object context) {
			super(source, id, command, modifiers);
			this.context = context;
		}
		
		public ContextActionEvent(Object source, int id, String command, Object context) {
			super(source, id, command);
			this.context = context;
		}
		
		public ContextActionEvent(Object source, int id, String command, long when, int modifiers, Object context) {
			super(source, id, command, when, modifiers);
			this.context = context;
		}

		private static final long serialVersionUID = 1L;

		public Object getContext() {
			return context;
		}
		
	}
	
	private class ProxyAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		
		private Action action;
		
		public ProxyAction(Action action) {
			super();
			this.action = action;
			
		}
		
		public void addPropertyChangeListener(PropertyChangeListener listener) {
			action.addPropertyChangeListener(listener);
		}


		public Object getValue(String key) {
			return action.getValue(key);
		}


		public boolean isEnabled() {
			return action.isEnabled();
		}


		public void putValue(String key, Object value) {
			action.putValue(key, value);
		}


		public void removePropertyChangeListener(PropertyChangeListener listener) {
			action.removePropertyChangeListener(listener);
		}


		public void setEnabled(boolean b) {
			action.setEnabled(b);
		}


		public void actionPerformed(ActionEvent e) {
			
			ContextActionEvent event = new ContextActionEvent(e.getSource(), e.getID(), e.getActionCommand(),
					e.getWhen(), e.getModifiers(), context);
			
			action.actionPerformed(event);
			
		}
		
		
		
	}
	
	private Object context = null;
	
    public JMenuItem add(Action a) {
    	Action proxy = new ProxyAction(a);
    	
    	JMenuItem mi = createActionComponent(proxy);
    	mi.setAction(proxy);
        add(mi);
        return mi;
    }

    public void show(Component invoker, int x, int y) {
    	this.show(invoker, x, y, null);
    }
    
    public void show(Component invoker, int x, int y, Object context) {
    	this.context = context;
    	super.show(invoker, x, y);
    }
}
