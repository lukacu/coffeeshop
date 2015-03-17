package org.coffeeshop.swing;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.Action;

import org.coffeeshop.string.StringUtils;

public class ActionSetManager {

	private Hashtable<String, Set<Action>> sets = new Hashtable<String, Set<Action>>();
	
	public void add(String name, Action ... actions) {
		
		if (StringUtils.empty(name)) 
			throw new IllegalArgumentException("Name is empty");
		
		if (sets.containsKey(name)) {
			Set<Action> set = sets.get(name);
			set.addAll(Arrays.asList(actions));			
		} else {
			Set<Action> set = new HashSet<Action>(Arrays.asList(actions));			
			sets.put(name, set);			
		}
	
	}	
	
	public Action add(Action action, String name) {
		
		if (StringUtils.empty(name)) 
			throw new IllegalArgumentException("Name is empty");
		
		if (sets.containsKey(name)) {
			Set<Action> set = sets.get(name);
			set.add(action);			
		} else {
			Set<Action> set = new HashSet<Action>();
			set.add(action);
			sets.put(name, set);			
		}
		
		return action;
	}
	
	public void enableAll() {
		enable(collectAll());
	}
	
	public void disableAll() {		
		disable(collectAll());
	}
	
	public void enableSet(String name) {
		Set<Action> set = sets.get(name);

		if (set == null)
			return;
		
		enable(set);
	}
	
	public void disableSet(String name) {
		Set<Action> set = sets.get(name);

		if (set == null)
			return;
		
		disable(set);
	}
	
	public void enableAllBut(String name) {
		Set<Action> set = sets.get(name);

		if (set == null)
			throw new IllegalArgumentException("Unknown group name");
		
		Collection<Action> all = collectAll();
		
		all.removeAll(set);
		
		enable(all);
	}
	
	public void disableAllBut(String name) {
		Set<Action> set = sets.get(name);

		if (set == null)
			throw new IllegalArgumentException("Unknown group name");
		
		Collection<Action> all = collectAll();
		
		all.removeAll(set);
		
		disable(all);
	}
	
	private Collection<Action> collectAll() {
		
		Enumeration<Set<Action>> sets =  this.sets.elements();
		
		HashSet<Action> actions = new HashSet<Action>();
		
		while (sets.hasMoreElements()) {
			Set<Action> set = sets.nextElement();
			actions.addAll(set);
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
