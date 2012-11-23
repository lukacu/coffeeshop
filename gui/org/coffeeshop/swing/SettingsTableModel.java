package org.coffeeshop.swing;

import java.util.Collections;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.coffeeshop.settings.Settings;
import org.coffeeshop.settings.SettingsChangedEvent;
import org.coffeeshop.settings.SettingsListener;

public class SettingsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] columnNames = new String[] { "Key", "Value" };

	private Settings settings;

	private Vector<String> keys = new Vector<String>();
	
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

		return keys.size();
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}

	public Class<?> getColumnClass(int column) {
		return String.class;
	}

	@Override
	public Object getValueAt(int row, int col) {

		if (col == 0) {
			
			return keys.get(row);
			
		} else {
			
			String key = keys.get(row);
			
			return settings.getString(key, null);
			
		}

	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		if (columnIndex != 1 || aValue == null || !(aValue instanceof String))
			return;
		
		if (rowIndex < 0 || rowIndex >= keys.size())
			return;
		
		String key = keys.get(rowIndex);
		
		settings.setString(key, (String) aValue);
		
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 1;
	}
}

