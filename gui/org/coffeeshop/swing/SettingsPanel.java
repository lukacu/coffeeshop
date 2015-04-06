package org.coffeeshop.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import org.coffeeshop.awt.StackLayout;
import org.coffeeshop.awt.StackLayout.Orientation;
import org.coffeeshop.dialogs.OrganizedSettings;
import org.coffeeshop.dialogs.SettingsGroup;
import org.coffeeshop.dialogs.SettingsMap;
import org.coffeeshop.dialogs.SettingsNode;
import org.coffeeshop.dialogs.SettingsValue;
import org.coffeeshop.settings.PrefixProxySettings;
import org.coffeeshop.settings.PropertiesSettings;
import org.coffeeshop.settings.Settings;
import org.coffeeshop.string.StringUtils;
import org.coffeeshop.string.parsers.BooleanStringParser;
import org.coffeeshop.string.parsers.BoundedIntegerStringParser;
import org.coffeeshop.string.parsers.EnumeratedStringParser;
import org.coffeeshop.string.parsers.EnumeratedSubsetStringParser;
import org.coffeeshop.string.parsers.IntegerStringParser;
import org.coffeeshop.string.parsers.ParseException;
import org.coffeeshop.string.parsers.StringParser;
import org.coffeeshop.string.parsers.StringStringParser;

public class SettingsPanel extends ScrollablePanel {

	public interface SettingsRenderer {
		
		public JComponent renderComponent(String name, Settings settings);
		
	}
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, JComponent> components = new HashMap<String, JComponent>();

	private PropertiesSettings temporary = new PropertiesSettings();
	
	private Settings settings;
	
	private OrganizedSettings structure;
	
	private class ChangeListenerSpinner implements ChangeListener {

		private String key;
		private int defaultValue; 
		private JSpinner spinner;
		
		public ChangeListenerSpinner(String key, JSpinner spinner, int defaultValue) {
			this.key = key;
			this.defaultValue = defaultValue;
			this.spinner = spinner;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() != spinner)
				return;

			try {
				temporary.setString(key, spinner.getValue().toString());
			} catch (RuntimeException ex) {
				spinner.setValue(temporary.getInt(key, defaultValue));
			}
		}
		
	}
	
	private class ChangeListenerSlider implements ChangeListener {

		private String key;
		private int defaultValue; 
		private JSlider slider;
		private JLabel label;
		
		public ChangeListenerSlider(String key, JSlider slider, JLabel label, int defaultValue) {
			this.key = key;
			this.slider = slider;
			this.defaultValue = defaultValue;
			this.label = label;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() != slider)
				return;

			if (slider.getValueIsAdjusting())
				return;
			
			try {
				temporary.setInt(key, slider.getValue());
			} catch (RuntimeException ex) {
				slider.setValue(temporary.getInt(key, defaultValue));
			}
			
			label.setText(Integer.toString(slider.getValue()));
			
		}
		
	}
	
	private class ChangeListenerCheckbox implements ChangeListener {

		private String key;
		private boolean defaultValue; 
		private JCheckBox box;
		
		public ChangeListenerCheckbox(String key, JCheckBox box, boolean defaultValue) {
			this.key = key;
			this.box = box;
			this.defaultValue = defaultValue;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() != box)
				return;

			try {
				temporary.setBoolean(key, box.isSelected());
			} catch (RuntimeException ex) {
				box.setSelected(temporary.getBoolean(key, defaultValue));
			}
		}
		
	}
	
	private class ChangeListenerCombo implements ItemListener {

		private String key;
		private JComboBox<Object> combo;
		
		public ChangeListenerCombo(String key, JComboBox<Object> combo) {
			this.key = key;
			this.combo = combo;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource() != combo)
				return;

			try {
				Object obj = combo.getSelectedItem();
				if (obj != null)
					temporary.setString(key, combo.getSelectedItem().toString());
			} catch (RuntimeException ex) {
				combo.setSelectedItem(temporary.getString(key, null));
			}
		}
		
	}
	
	private class ChangeListenerList implements ListSelectionListener {

		private String key;
		private JList<Object> list;
		private String separator;
		
		public ChangeListenerList(String key, JList<Object> list, String separator) {
			this.key = key;
			this.list = list;
			this.separator = separator;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			
			if (e.getSource() != list)
				return;
			
			int[] selection = list.getSelectedIndices();
			
			String value = "";
			
			if (selection.length > 0) {
				value = list.getModel().getElementAt(selection[0]).toString();
		
				
				for (int i = 1; i < selection.length; i++) {
					value += separator + list.getModel().getElementAt(selection[i]).toString();
				}
			}
			
			try {
				temporary.setString(key, value);
			} catch (RuntimeException ex) {
				//list.setSelectedItem(settings.getString(key));
			}
		}
		
	}
	
	
	private class ChangeListenerTextField implements DocumentListener {

		private String key;
		private JTextComponent field;
		private boolean multiline;
		
		public ChangeListenerTextField(String key, JTextComponent field, boolean multiline) {
			this.key = key;
			this.field = field;
			this.multiline = multiline;
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			
			try {
				update(field.getText());
			} catch (RuntimeException ex) {}
			
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			try {
				update(field.getText());
			} catch (RuntimeException ex) {}
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			try {
				update(field.getText());
			} catch (RuntimeException ex) {}
		}
		
		private void update(String text) {
			
			String value;
			if (multiline) {
				
				value = text.replace("\\", "\\\\").replace("\n", "\\n");
				
			} else {
				
				value = text.replace("\n", "");
				
			}
			
			temporary.setString(key, value);
			
		}
		
	}
	
	public SettingsPanel(Settings settings, OrganizedSettings structure) {
		super();
		
		this.settings = settings;
		
		this.structure = structure;
		
		cache(structure);
				
		build(structure);
		
	}
	
	public void commit() {
		
		for (String key : temporary.getKeys()) {
			
			settings.setString(key, temporary.getString(key));
			
		}

	}
	
	private void cache(SettingsGroup group) {
		
			for (SettingsNode n : group) {
			
				if (n instanceof SettingsGroup) {
					cache((SettingsGroup) n);
					continue;
				} else if (n instanceof SettingsValue) {
					String key = ((SettingsValue)n).getName();
					if (settings.containsKey(key)) {
						temporary.setString(key, settings.getString(key));
					} else {
						temporary.setString(key, ((SettingsValue)n).getDefault());
					}
				}
				
			}
	}
	
	protected void build(OrganizedSettings settings) {
		
		setLayout(new BorderLayout());
		
		add(buildGroup(settings, true), BorderLayout.CENTER);
		
	}
	
	protected JComponent buildGroup(SettingsGroup group, boolean border) {
		
		JPanel groupPanel = new JPanel(new StackLayout(Orientation.VERTICAL, 2, 5, true));
		
		if (!StringUtils.empty(group.getTitle()) && border) {
			groupPanel.setBorder(new TitledBorder(group.getTitle()));
		}
		
		for (SettingsNode n : group) {
			
			if (n instanceof SettingsGroup) {
				JComponent c = buildGroup((SettingsGroup) n, true);
				groupPanel.add(c);
				continue;
			}
			
			if (n instanceof SettingsMap) {
				SettingsMap map = (SettingsMap) n;
				JPanel panel = new JPanel(new BorderLayout());
				panel.add(new JLabel(map.getTitle()), BorderLayout.NORTH);
				panel.add(new JScrollPane(
						new SettingsTable(new PrefixProxySettings(temporary,
								map.getNamespace()))), BorderLayout.CENTER);
				groupPanel.add(panel);
				continue;
			}
			
			JComponent c = buildComponent((SettingsValue) n);
			groupPanel.add(c);
		}
		
		return groupPanel;
	}
	
	private JComponent buildComponent(SettingsValue value) {

		StringParser p = value.getParser();
		
		if (p instanceof SettingsRenderer) {
			
			return ((SettingsRenderer) p).renderComponent(getName(), temporary);
			
		}
		
		if (p instanceof IntegerStringParser) {
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.WEST);
			
			JSpinner spinner = new JSpinner(new SpinnerNumberModel());
			
			spinner.setValue(temporary.getInt(value.getName(), 0));
			
			//spinner.setValue(settings.getString(value.getName()));
			
			spinner.addChangeListener(new ChangeListenerSpinner(value.getName(), spinner, 0));
			
			components.put(getName(), spinner);
			
			panel.add(spinner, BorderLayout.EAST);
			
			return panel;
		}
		
		if (p instanceof BooleanStringParser) {
			
			JCheckBox box = new JCheckBox(value.getTitle());

			box.setSelected(temporary.getBoolean(value.getName(), false));
			
			box.addChangeListener(new ChangeListenerCheckbox(value.getName(), box, false));

			components.put(getName(), box);

			return box;
		}
		
		if (p instanceof BoundedIntegerStringParser) {
			
			BoundedIntegerStringParser bi = (BoundedIntegerStringParser) p;
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);

			int defval = 0;
			
			if (bi.getMin() > 0)
				defval = bi.getMin();

			if (bi.getMax() < 0)
				defval = bi.getMax();
			
			int val = Math.min(bi.getMax(), Math.max(bi.getMin(), temporary.getInt(value.getName(), defval)));

			JSlider slider = new JSlider(bi.getMin(), bi.getMax(), val);
			
			JLabel current = new JLabel(Integer.toString(val));
			
			slider.addChangeListener(new ChangeListenerSlider(value.getName(), slider, current, defval));
			
			components.put(getName(), slider);
			
			panel.add(slider, BorderLayout.CENTER);
			
			current.setMinimumSize(new Dimension(50, 1));
			
			panel.add(current, BorderLayout.EAST);
			
			return panel;
		}		
				
		if (p instanceof EnumeratedSubsetStringParser) {
			
			EnumeratedSubsetStringParser bi = (EnumeratedSubsetStringParser) p;
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);
			
			final JList<Object> list = new JList<Object>(bi.getValues());
			
			list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			
			list.addListSelectionListener(new ChangeListenerList(value.getName(), list, bi.getSeparator()));
			
			list.setCellRenderer(new ObjectFacade.FacadeListCellRenderer());
			
			list.setSelectionModel(new DefaultListSelectionModel() {

				private static final long serialVersionUID = 1L;

				@Override
				public void setSelectionInterval(int index0, int index1) {
					if (list.isSelectedIndex(index0)) {
						list.removeSelectionInterval(index0, index1);
					} else {
						list.addSelectionInterval(index0, index1);
					}
				}
			});
			
			try {
				
				@SuppressWarnings("unchecked")
				Set<Object> subset = (Set<Object>) bi.parse(temporary.getString(value.getName(), ""));
					
				Object[] values = bi.getValues();
				if (!subset.isEmpty()) {
					int[] indices = new int[subset.size()];
					int j = 0;
					
					for (int i = 0; i < values.length; i++) {
				
						if (subset.contains(values[i])) {
							indices[j++] = i;
						}
					}
					
					list.setSelectedIndices(indices);
					
				}
			} catch (ParseException e) {

			}
			
			components.put(getName(), list);
			
			panel.add(new JScrollPane(list), BorderLayout.SOUTH);
			
			return panel;
		}
		
		if (p instanceof EnumeratedStringParser) {
			
			EnumeratedStringParser bi = (EnumeratedStringParser) p;
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);
			
			JComboBox<Object> combo = new JComboBox<Object>(bi.getValues());
			
			combo.setRenderer(new ObjectFacade.FacadeListCellRenderer());
			
			combo.addItemListener(new ChangeListenerCombo(value.getName(), combo));
			
			combo.setSelectedIndex(bi.findValue(temporary.getString(value.getName(), value.getDefault())));			
			
			components.put(getName(), combo);
			
			panel.add(combo, BorderLayout.SOUTH);
			
			return panel;
		}	
		
		if (p instanceof StringStringParser && ((StringStringParser) p).isMultiline()) {
			
			StringStringParser bi = (StringStringParser) p;
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);
			
			JTextArea area = new JTextArea((String)bi.parse(temporary.getString(value.getName(), value.getDefault())));
			
			area.getDocument().addDocumentListener(new ChangeListenerTextField(value.getName(), area, true));
			
			components.put(getName(), area);
			
			panel.add(new JScrollPane(area), BorderLayout.CENTER);

			return panel;
		}
		
	/*	if (p instanceof FileStringParser) {
			
			
			
			
		}*/
		
		JPanel panel = new JPanel(new BorderLayout());
		
		panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);
		
		JTextField line = new JTextField(temporary.getString(value.getName(), value.getDefault()));
		
		line.getDocument().addDocumentListener(new ChangeListenerTextField(value.getName(), line, false));
		
		components.put(getName(), line);
		
		panel.add(line, BorderLayout.SOUTH);
		
		return panel;
		
	}
	
	private void registerSettingsComponent(String name, Component component) {
		
	}
	
}
