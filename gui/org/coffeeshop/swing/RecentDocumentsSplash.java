package org.coffeeshop.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public abstract class RecentDocumentsSplash extends Splash {

	private JList list = new JList();
	
	public RecentDocumentsSplash(String title, Image image, RecentDocuments history) {
		super(title, image);

		list.setModel(history);
	}

	@Override
	protected JComponent createSidebarComponent() {

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
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				File file = (File) value;
				JLabel label = new JLabel(file.getName());
				label.setToolTipText(file.getAbsolutePath());
				return label;
			}
		});

		JScrollPane listpane = new JScrollPane(list,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		listpane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(20, 20, 20, 20),
				listpane.getBorder()));

		listpane.setPreferredSize(new Dimension(400, 200));

		return listpane;

	}

	protected abstract File browse();
	
}
