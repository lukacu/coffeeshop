package org.coffeeshop.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.coffeeshop.application.Application;
import org.coffeeshop.settings.Settings;
import org.coffeeshop.settings.SettingsNotFoundException;

public abstract class History extends Splash {

	private LinkedList<File> history = new LinkedList<File>();

	private JList list;
			
	public History(String title, Image image) {
		
		super(title, image);
		
		Settings settings = Application.getApplicationSettings();
		
		TreeSet<File> history = new TreeSet<File>();
		
		int i = 0;
		
		while (true) {
			
			try {
				File f = new File(settings.getString("history." + i));

				if (f.exists())
					history.add(f);

			} catch (SettingsNotFoundException e) {
				break;
			}
			
			i++;
		}
	
		this.history.addAll(history);
		
		list.setListData(new Vector<File>(history));
		
	}
	
	public void add(File path) {
		
		if (!path.exists())
			return;
		
		Iterator<File> itr = history.iterator();
		
		while (itr.hasNext()) {
			File f = itr.next();
			
			if (f.compareTo(path) == 0)
				itr.remove();
		}
		
		
		history.addFirst(path);

		Settings settings = Application.getApplicationSettings();
		
		for (int i = 0; i < history.size(); i++) {
			
			try {
				settings.setString("history." + i, history.get(i).getAbsolutePath());
				
			} catch (SettingsNotFoundException e) {}
			
		}
		
	}
	
	@Override
	protected JComponent createSidebarComponent() {
		
		list = new JList();
		
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (e.getValueIsAdjusting())
					return;
				
				closeWithResult(list.getSelectedValue());

			}
		});
		
		list.setCellRenderer(new ListCellRenderer() {
			
			@Override
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				File file = (File) value;
				JLabel label = new JLabel(file.getName());
				label.setToolTipText(file.getAbsolutePath());
				return label;
			}
		});
		
		JScrollPane listpane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		listpane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20), listpane.getBorder()));
		
		listpane.setPreferredSize(new Dimension(400, 200));
		
		return listpane;

	}
	
	@Override
	protected List<Action> createActions() {
		
		Vector<Action> actions = new Vector<Action>();
		
		actions.add(new AbstractAction("Browse") {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent arg0) {
						
						closeWithResult(browse());
						
					}
				}
		);
		
		return actions;
	}
	
	protected abstract File browse();
	
}
