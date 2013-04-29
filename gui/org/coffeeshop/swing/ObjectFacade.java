package org.coffeeshop.swing;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public interface ObjectFacade  {

	public String getDisplayString();
	
	public static class FacadeListCellRenderer extends DefaultListCellRenderer {
	
		private static final long serialVersionUID = 1L;
	
		@Override
		public Component getListCellRendererComponent(JList<? extends Object> list,
				Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
			if (value instanceof ObjectFacade) 
				return super.getListCellRendererComponent(list, ((ObjectFacade) value).getDisplayString(),
						index, isSelected, cellHasFocus);
			
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
		}

	}
	
	//public static class FacadeComboBoxCellRenderer extends DefaultCo
}
