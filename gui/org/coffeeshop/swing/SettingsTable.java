package org.coffeeshop.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.AbstractTableModel;

import org.coffeeshop.settings.Settings;
import org.coffeeshop.settings.SettingsChangedEvent;
import org.coffeeshop.settings.SettingsListener;

class SettingsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] columnNames = new String[] { "Key", "Value" };

	private Settings settings;

	private Vector<String> keys = new Vector<String>();
	
	private boolean placeholderVisible = false;
	
	public SettingsTableModel(Settings settings) {
		
		this.settings = settings;
		
		keys.addAll(settings.getKeys());
		
		Collections.sort(keys);
		
		settings.addSettingsListener(new SettingsListener() {
			
			@Override
			public void settingsChanged(SettingsChangedEvent e) {
				
				int row = keys.indexOf(e.getKey());
				
				if (row > -1) {

					if (e.getValue() == null) {
						
						keys.remove(row);
						fireTableRowsDeleted(row, row);
						
					} else {
					
						fireTableRowsUpdated(row, row);
					}

				} else {

					keys.add(e.getKey());

					fireTableRowsInserted(keys.size()-1, keys.size()-1);
					
				}

			}
			
		});
		
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {		
		return placeholderVisible ? keys.size() + 1 : keys.size();
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}

	public Class<?> getColumnClass(int column) {
		return String.class;
	}

	@Override
	public Object getValueAt(int row, int col) {

		if (row >= keys.size()) 
			return "";
		
		if (col == 0) {
			
			return keys.get(row);
			
		} else {
						
			String key = keys.get(row);
			
			return settings.getString(key, null);
			
		}

	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		if (aValue == null || !(aValue instanceof String))
			return;
		
		if (placeholderVisible && rowIndex == keys.size()) {
			
			if (columnIndex == 0) {
				String key = ((String) aValue).trim();
				hidePlaceholder();

				if (!key.isEmpty())
					settings.setString(key, settings.getString(key, ""));
				
			}
			
		}
				
		if (columnIndex != 1 || rowIndex < 0 || rowIndex >= keys.size())
			return;
		
		String key = keys.get(rowIndex);
		
		settings.setString(key, (String) aValue);
		
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		
		if (placeholderVisible && rowIndex == keys.size()) return true;
		
		return columnIndex == 1;
	}
	
	public void showPlaceholder() {
		
		if (placeholderVisible) return;
		
		placeholderVisible = true;
		
		fireTableRowsInserted(keys.size(), keys.size());
		
	}

	public void hidePlaceholder() {
		
		if (!placeholderVisible) return;
		
		placeholderVisible = false;
		
		fireTableRowsDeleted(keys.size(), keys.size());
		
	}
	
	public void removeValues(int [] rows) {
		
		hidePlaceholder();
		
		Vector<String> removeKeys = new Vector<String>();
		
		for (int i : rows) {
			
			if (i < 0 || i >= keys.size()) continue;
			
			removeKeys.add(keys.get(i));
			
		}
		
		for (String key : removeKeys) {
			settings.remove(key);
		}
		
	}
	
}

public class SettingsTable extends JPanel {

	private static final long serialVersionUID = 1L;

	private Action add = new ToolTipAction("Add",
			ImageStore.getIcon("add-16.png")) {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {

			model.showPlaceholder();
			table.editCellAt(table.getRowCount()-1, 0);

		}
	};
	
	private Action remove = new ToolTipAction("Remove",
			ImageStore.getIcon("delete-16.png")) {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {

			int[] selection = table.getSelectedRows();
			
			if (selection == null || selection.length == 0)
				return;
			
			model.removeValues(selection);
		}
	};
	
	private JTable table;
	
	private SettingsTableModel model;
	
	public SettingsTable(Settings settings) {
		
		super(new BorderLayout());
		
		table = new JTable(new SettingsTableModel(settings));
		model = (SettingsTableModel) table.getModel();
		
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		JToolBar bar = new JToolBar();
		bar.setFloatable(false);
		
		bar.add(add);
		bar.add(remove);
		
		add(bar, BorderLayout.NORTH);
		
		setPreferredSize(new Dimension(200, 200));
		
	}
	
	
}
