package org.coffeeshop.swing;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

class TableSorter implements Comparator<Integer> {
	
	private SortableTableModel model;

	Integer[] indexes;
	
	private int column;
	
	private boolean ascent;
	
	public TableSorter(SortableTableModel model, int column, boolean ascent) {
		this.model = model;
		this.ascent = ascent;
		this.column = column;
		
		indexes = new Integer[model.getRowCount()];
		
		for (int i = 0; i < indexes.length; i++)
			indexes[i] = i;
		
		resort();
		
	}

	public void resort() {
		
		Arrays.sort(indexes, this);

	}
	
	public int getColumn() {
		return column;
	}

	public boolean isAscent() {
		return ascent;
	}

	public static int compare(Number o1, Number o2) {
		double n1 = o1.doubleValue();
		double n2 = o2.doubleValue();
		if (n1 < n2) {
			return -1;
		} else if (n1 > n2) {
			return 1;
		} else {
			return 0;
		}
	}

	public static int compare(Date o1, Date o2) {
		long n1 = o1.getTime();
		long n2 = o2.getTime();
		if (n1 < n2) {
			return -1;
		} else if (n1 > n2) {
			return 1;
		} else {
			return 0;
		}
	}

	public static int compare(Boolean o1, Boolean o2) {
		boolean b1 = o1.booleanValue();
		boolean b2 = o2.booleanValue();
		if (b1 == b2) {
			return 0;
		} else if (b1) {
			return 1;
		} else {
			return -1;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compare(Integer i1, Integer i2) {

		Object o1 = model.getCell(i1, column);
		Object o2 = model.getCell(i2, column);
		int r = 0;
		if (o1 == null && o2 == null) {
			r = 0;
		} else if (o1 == null) {
			r = -1;
		} else if (o2 == null) {
			r = 1;
		} else {
			Class<?> type = model.getColumnClass(column);
			if (type.getSuperclass() == Number.class) {
				r = compare((Number) o1, (Number) o2);
			} else if (type == String.class) {
				r = ((String) o1).compareTo((String) o2);
			} else if (type == Date.class) {
				r = compare((Date) o1, (Date) o2);
			} else if (type == Boolean.class) {
				r = compare((Boolean) o1, (Boolean) o2);
			} else if (Comparable.class.isAssignableFrom(type)) {
				r = ((Comparable<Object>) o1)
						.compareTo((Comparable<Object>) o2);
			} else
				r = 0;
		}
		
		return ascent ? -r : r;
	}

}

public abstract class SortableTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private TableSorter sorter;

	public SortableTableModel() {
		
	}

	protected abstract Object getCell(int row, int column);

	protected abstract void setCell(Object value, int row, int column);

	public Object getValueAt(int row, int col) {
		int rowIndex = row;

		if (sorter != null) {
			rowIndex = sorter.indexes[row];
		}
		return getCell(rowIndex, col);
	}

	public void setValueAt(Object value, int row, int col) {
		int rowIndex = row;
		if (sorter != null) {
			rowIndex = sorter.indexes[row];
		}
		setCell(value, rowIndex, col);
	}

	public int getDataIndex(int row) {
		if (sorter != null) {
			return sorter.indexes[row];
		}
		return row;
	}

	public void sortByColumn(int column, boolean ascent) {
		if (column == -1) {
			sorter = null;
			fireTableDataChanged();
			return;
		}

		sorter = new TableSorter(this, column, ascent);
		
		fireTableDataChanged();
	}

	public int getSortedColumn() {
		
		if (sorter != null) {
			return sorter.getColumn();
		}
		else return -1;
	}
	
	public boolean isSortedAscending() {
		
		if (sorter != null) {
			return sorter.isAscent();
		}
		else return false;
	}
	
}