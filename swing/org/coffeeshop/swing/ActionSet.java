package org.coffeeshop.swing;

import java.util.Collection;
import java.util.Vector;

import javax.swing.Action;

public class ActionSet {

	public Vector<Action> actions = new Vector<Action>();
	
	public ActionSet(Action ... actions) {
		
		for (int i = 0; i < actions.length; i++) {
			if (actions[i] == null)
				throw new IllegalArgumentException("Null pointer");
			this.actions.add(actions[i]);
		}
		
	}

	public boolean add(Action arg0) {
		if (arg0 == null)
			throw new IllegalArgumentException("Null pointer");
		return actions.add(arg0);
	}

	public boolean addAll(ActionSet set) {
		if (set == null)
			throw new IllegalArgumentException("Null pointer");
		return actions.addAll(set.actions);
	}

	public boolean remove(Action arg0) {
		return actions.remove(arg0);
	}

	public void removeAll() {
		actions.removeAllElements();
	}
	
	public Collection<Action> getAll() {
		return actions;
	}
	
	public void enableAll() {
		
		for (Action a : actions) {
			a.setEnabled(true);
		}
		
	}
	
	public void disableAll() {
		
		for (Action a : actions) {
			a.setEnabled(false);
		}
		
	}
}
