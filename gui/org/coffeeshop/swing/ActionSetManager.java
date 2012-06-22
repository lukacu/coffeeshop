package org.coffeeshop.swing;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.Action;

import org.coffeeshop.string.StringUtils;

public class ActionSetManager {

	private Hashtable<String, ActionSet> sets = new Hashtable<String, ActionSet>();
	
	public ActionSet newSet(String name, Action ... actions) {
		
		if (sets.containsKey(name) || StringUtils.empty(name)) 
			throw new IllegalArgumentException("Name already exists or empty");
		
		ActionSet set = new ActionSet(actions);
		
		sets.put(name, set);
		
		return set;
	}	
	
	public void enableAll() {
		enable(collectAll());
	}
	
	public void disableAll() {		
		disable(collectAll());
	}
	
	public void enableSet(String name) {
		ActionSet set = sets.get(name);

		if (set == null)
			throw new IllegalArgumentException("Unknown group name");
		
		enable(set.getAll());
	}
	
	public void disableSet(String name) {
		ActionSet set = sets.get(name);

		if (set == null)
			throw new IllegalArgumentException("Unknown group name");
		
		disable(set.getAll());
	}
	
	public void enableAllBut(String name) {
		ActionSet set = sets.get(name);

		if (set == null)
			throw new IllegalArgumentException("Unknown group name");
		
		Collection<Action> all = collectAll();
		
		all.removeAll(set.getAll());
		
		enable(all);
	}
	
	public void disableAllBut(String name) {
		ActionSet set = sets.get(name);

		if (set == null)
			throw new IllegalArgumentException("Unknown group name");
		
		Collection<Action> all = collectAll();
		
		all.removeAll(set.getAll());
		
		disable(all);
	}
	
	private Collection<Action> collectAll() {
		
		Enumeration<ActionSet> sets =  this.sets.elements();
		
		HashSet<Action> actions = new HashSet<Action>();
		
		while (sets.hasMoreElements()) {
			ActionSet set = sets.nextElement();
			actions.addAll(set.getAll());
		}
		
		return actions;
		
	}
	
	private void enable(Collection<Action> actions) {
		for (Action a : actions) {
			a.setEnabled(true);
		}
	}
	
	private void disable(Collection<Action> actions) {
		for (Action a : actions) {
			a.setEnabled(false);
		}
	}
	
}
