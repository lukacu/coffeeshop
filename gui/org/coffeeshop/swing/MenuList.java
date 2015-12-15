package org.coffeeshop.swing;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.coffeeshop.Callback;
import org.coffeeshop.ReferenceCollection;

public class MenuList extends JMenu {

	private static final long serialVersionUID = 1L;

	private ListModel model;
	
	private Vector<JMenuItem> items = new Vector<JMenuItem>();
	
	private ReferenceCollection<Callback> listeners = new ReferenceCollection<Callback>();
	
	private class ItemAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private Object object;
		
		public ItemAction(Object object) {
			super(object.toString());
			this.object = object;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			for (Callback l : listeners) {
				l.callback(MenuList.this, object);
			}
			
		}
		
	}
	
	public MenuList(String title, ListModel model) {
		
		super(title);
		
		this.model = model;
		
		
		
		model.addListDataListener(new ListDataListener() {
			
			@Override
			public void intervalRemoved(ListDataEvent e) {
				 updateMenu();
			}
			
			@Override
			public void intervalAdded(ListDataEvent e) {
				 updateMenu();
			}
			
			@Override
			public void contentsChanged(ListDataEvent e) {
				 updateMenu();
			}
		});
		
		updateMenu();
	}
	
	private void updateMenu() {
		
		for (JMenuItem item : items) {
			remove(item);
		}
		items.clear();
		
		for (int i = 0; i < model.getSize(); i++) {
			JMenuItem item = new JMenuItem(new ItemAction(model.getElementAt(i)));
			items.add(item);
			add(item);
		}
		
	}
	
	public void addSelectionListener(Callback l) {
		listeners.add(l);
	}
	
	public void removeSelectionListener(Callback l) {
		listeners.remove(l);
	}
}
