package org.coffeeshop.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

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
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.coffeeshop.awt.StackLayout;
import org.coffeeshop.awt.StackLayout.Orientation;
import org.coffeeshop.dialogs.OrganizedSettings;
import org.coffeeshop.dialogs.SettingsGroup;
import org.coffeeshop.dialogs.SettingsMap;
import org.coffeeshop.dialogs.SettingsNode;
import org.coffeeshop.dialogs.SettingsValue;
import org.coffeeshop.settings.CachedSettings;
import org.coffeeshop.settings.PrefixProxySettings;
import org.coffeeshop.settings.Settings;
import org.coffeeshop.settings.SettingsChangedEvent;
import org.coffeeshop.settings.SettingsListener;
import org.coffeeshop.settings.Value;
import org.coffeeshop.string.StringUtils;
import org.coffeeshop.string.parsers.BooleanStringParser;
import org.coffeeshop.string.parsers.BoundedIntegerStringParser;
import org.coffeeshop.string.parsers.EnumeratedStringParser;
import org.coffeeshop.string.parsers.EnumeratedSubsetStringParser;
import org.coffeeshop.string.parsers.IntegerStringParser;
import org.coffeeshop.string.parsers.ParseException;
import org.coffeeshop.string.parsers.StringParser;
import org.coffeeshop.string.parsers.StringStringParser;

public class SettingsPanel extends ScrollablePanel implements SettingsEditor {
	
	public class ValueProxy extends Value {
		
		private String name;
		
		private String defaultValue;
		
		private ValueProxy(String name, String defaultValue) {
			
			this.name = name;
			this.defaultValue = defaultValue;
			
		}
				
		public void setValue(String value) {
			
			settings.setString(name, value);
			
			if (strategy == CommitStrategy.ALWAYS)
				settings.commit();
			
		}

		@Override
		public String getValue() {
			return settings.getString(name, defaultValue);
		}
		
	}
	
	public static interface SettingsRenderer {
		
		public JComponent renderComponent(ValueProxy value);
		
		public void updateComponent(JComponent component);
		
	}
	
	private static interface SettingsComponent {
		
		public void update();
		
	}
	
	private static class CustomSettingsComponent implements SettingsComponent {
		
		private JComponent component;
		
		private SettingsRenderer renderer;

		public CustomSettingsComponent(SettingsRenderer renderer, ValueProxy value) {
			super();
			this.component = renderer.renderComponent(value);
			this.renderer = renderer;
		}
		
		public void update() {
			
			renderer.updateComponent(component);
			
		}
		
	}
	
	private class SettingsPanelListener implements SettingsListener {

		@Override
		public void settingsChanged(SettingsChangedEvent e) {
		
			SettingsComponent component = components.get(e.getKey());
			
			if (component == null) 
				return;

			component.update();
	
		}
		
	}
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, SettingsComponent> components = new HashMap<String, SettingsComponent>();

	private Vector<SettingsEditor> children = new Vector<SettingsEditor>();
	
	private CachedSettings settings;
	
	private SettingsListener listener = new SettingsPanelListener();
	
	private CommitStrategy strategy;
	
	private class IntegerEditor extends JSpinner implements ChangeListener, SettingsComponent {

		private static final long serialVersionUID = 1L;
		
		private ValueProxy value;

		public IntegerEditor(ValueProxy value) {
			super();
			this.value = value;
			update();
			addChangeListener(this);
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() != this)
				return;

			try {
				value.setValue(getValue().toString());
			} catch (RuntimeException ex) {
				update();
			}
		}

		@Override
		public void update() {
			setValue(value.getInt());
		}
		
	}
	
	private class BoundedIntegerEditor extends JPanel implements ChangeListener, SettingsComponent {

		private static final long serialVersionUID = 1L;
		
		private ValueProxy value;
		private JSlider slider;
		private JLabel label;
		
		public BoundedIntegerEditor(BoundedIntegerStringParser parser, ValueProxy value) {
			
			super(new BorderLayout());
			
			this.value = value;
			
			slider = new JSlider(parser.getMin(), parser.getMax(), parser.getMin());
			
			label = new JLabel();
			
			add(slider, BorderLayout.CENTER);
			
			label.setMinimumSize(new Dimension(50, 1));
			
			add(label, BorderLayout.EAST);
			
			update();
			
			slider.addChangeListener(this);
			
		}
		
		
		@Override
		public void update() {
			
			int val = Math.min(slider.getMaximum(), Math.max(slider.getMinimum(), value.getInt()));

			slider.setValue(val);
			label.setText(Integer.toString(slider.getValue()));
			
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() != this)
				return;

			if (slider.getValueIsAdjusting())
				return;
			
			value.setValue(Integer.toString(slider.getValue()));
			label.setText(value.getString());
			
		}
		
	}
	
	private class BooleanEditor extends JCheckBox implements ChangeListener, SettingsComponent {
		
		private static final long serialVersionUID = 1L;
		
		private ValueProxy value;
		
		public BooleanEditor(String name, ValueProxy value) {
			
			super(name);
			this.value = value;
			update();
			addChangeListener(this);
		}
		
		@Override
		public void update() {
			
			setSelected(value.getBoolean());
			
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() != this)
				return;

			value.setValue(Boolean.toString(isSelected()));
			
		}
		
	}
	
	private class EnumeratedSubsetEditor extends JList<Object> implements ListSelectionListener, SettingsComponent {

		private static final long serialVersionUID = 1L;
		
		private ValueProxy value;
		private EnumeratedSubsetStringParser parser;
		
		public EnumeratedSubsetEditor(EnumeratedSubsetStringParser parser, ValueProxy value) {
			super(parser.getValues());
			this.parser = parser;
			this.value = value;
			
			setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			
			addListSelectionListener(this);
			
			setCellRenderer(new ObjectFacade.FacadeListCellRenderer());
			
			setSelectionModel(new DefaultListSelectionModel() {

				private static final long serialVersionUID = 1L;

				@Override
				public void setSelectionInterval(int index0, int index1) {
					if (isSelectedIndex(index0)) {
						removeSelectionInterval(index0, index1);
					} else {
						addSelectionInterval(index0, index1);
					}
				}
			});
			
			update();
		}
		
		@Override
		public void update() {

			try {
				
				@SuppressWarnings("unchecked")
				Set<Object> subset = (Set<Object>) parser.parse(value.getString());
					
				Object[] values = parser.getValues();
				if (!subset.isEmpty()) {
					int[] indices = new int[subset.size()];
					int j = 0;
					
					for (int i = 0; i < values.length; i++) {
				
						if (subset.contains(values[i])) {
							indices[j++] = i;
						}
					}
					
					setSelectedIndices(indices);
					
				}
			} catch (ParseException e) {

			}
			
			
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			
			if (e.getSource() != this)
				return;
			
			int[] selection = getSelectedIndices();
			
			String encoded = "";
			
			if (selection.length > 0) {
				encoded = getModel().getElementAt(selection[0]).toString();
		
				
				for (int i = 1; i < selection.length; i++) {
					encoded += parser.getSeparator() + getModel().getElementAt(selection[i]).toString();
				}
			}
			
			value.setValue(encoded);

		}
		
	}
	
	private class EnumeratedEditor extends JComboBox<Object> implements ItemListener, SettingsComponent {

		private static final long serialVersionUID = 1L;
		
		private ValueProxy value;
		private EnumeratedStringParser parser;
		
		public EnumeratedEditor(EnumeratedStringParser parser, ValueProxy value) {
			super(parser.getValues());
			setRenderer(new ObjectFacade.FacadeListCellRenderer());
			
			this.value = value;
			this.parser = parser;
			update();
			
			addItemListener(this);
		}
		
		@Override
		public void update() {
	
			setSelectedIndex(parser.findValue(value.getString()));			
			
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource() != this)
				return;

				Object obj = getSelectedItem();
				if (obj != null)
					value.setValue(getSelectedItem().toString());

		}
		
		
	}

	private class MultilineTextEditor extends JTextArea implements DocumentListener, SettingsComponent {

		private static final long serialVersionUID = 1L;
		
		private ValueProxy value;
		private StringStringParser parser;
		
		public MultilineTextEditor(StringStringParser parser, ValueProxy value) {
			super();
			this.value = value;
			this.parser = parser;
			
			update();
			getDocument().addDocumentListener(this);
			
		}
		
		@Override
		public void update() {
			
			setText((String) parser.parse(value.getString()));
			
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			
			try {
				update(getText());
			} catch (RuntimeException ex) {}
			
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			try {
				update(getText());
			} catch (RuntimeException ex) {}
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			try {
				update(getText());
			} catch (RuntimeException ex) {}
		}
		
		private void update(String text) {
			
			value.setValue(text.replace("\\", "\\\\").replace("\n", "\\n"));
								
		}
		
		
	}
	
	private class TextEditor extends JTextField implements DocumentListener, SettingsComponent {

		private static final long serialVersionUID = 1L;
		
		private ValueProxy value;

		public TextEditor(ValueProxy value) {
			
			super();
			this.value = value;

			update();
			getDocument().addDocumentListener(this);
			
		}
		
		@Override
		public void update() {
			
			setText(value.getString());
			
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			
			try {
				update(getText());
			} catch (RuntimeException ex) {}
			
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			try {
				update(getText());
			} catch (RuntimeException ex) {}
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			try {
				update(getText());
			} catch (RuntimeException ex) {}
		}
		
		private void update(String text) {
			
			value.setValue(text.replace("\n", ""));
		
		}
		
		
	}
	
	public SettingsPanel(Settings settings, OrganizedSettings structure, CommitStrategy strategy) {
		
		super();
		
		this.settings = new CachedSettings(settings);
		
		this.strategy = strategy;
				
		build(structure);
		
		settings.addSettingsListener(listener);
	}
	
	public SettingsPanel(Settings settings, OrganizedSettings structure) {
		this(settings, structure, CommitStrategy.MANUAL);
		
	}
	
	public void commit() {
		
		settings.commit();

		for (SettingsEditor child : children)
			child.commit();
		
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
						new SettingsTable(new PrefixProxySettings(settings,
								map.getNamespace()), strategy)), BorderLayout.CENTER);
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
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);
			
			CustomSettingsComponent component = new CustomSettingsComponent(((SettingsRenderer) p), new ValueProxy(value.getName(), value.getDefault()));
				
			panel.add(component.component, BorderLayout.CENTER);
			
			registerComponent(value.getName(), component);
			
			return panel;
		}
		
		if (p instanceof IntegerStringParser) {
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.WEST);
			
			IntegerEditor component = new IntegerEditor(new ValueProxy(value.getName(), value.getDefault()));
			
			panel.add(component, BorderLayout.EAST);
			
			registerComponent(value.getName(), component);
			
			return panel;
		}
		
		if (p instanceof BooleanStringParser) {
			
			BooleanEditor editor = new BooleanEditor(value.getTitle(), new ValueProxy(value.getName(), value.getDefault()));
			
			registerComponent(value.getName(), editor);

			return editor;
		}
		
		if (p instanceof BoundedIntegerStringParser) {
			
			BoundedIntegerStringParser bi = (BoundedIntegerStringParser) p;
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);

			BoundedIntegerEditor editor = new BoundedIntegerEditor(bi, new ValueProxy(value.getName(), value.getDefault()));
			
			registerComponent(value.getName(), editor);
			
			panel.add(editor, BorderLayout.CENTER);
			
			return panel;
		}		
				
		if (p instanceof EnumeratedSubsetStringParser) {
			
			EnumeratedSubsetStringParser bi = (EnumeratedSubsetStringParser) p;
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);
			
			EnumeratedSubsetEditor editor = new EnumeratedSubsetEditor(bi, new ValueProxy(value.getName(), value.getDefault()));
			
			registerComponent(value.getName(), editor);
			
			panel.add(new JScrollPane(editor), BorderLayout.SOUTH);
			
			return panel;
		}
		
		if (p instanceof EnumeratedStringParser) {
			
			EnumeratedStringParser bi = (EnumeratedStringParser) p;
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);
			
			EnumeratedEditor editor = new EnumeratedEditor(bi, new ValueProxy(value.getName(), value.getDefault()));
		
			registerComponent(value.getName(), editor);
			
			panel.add(editor, BorderLayout.SOUTH);
			
			return panel;
		}	
		
		if (p instanceof StringStringParser && ((StringStringParser) p).isMultiline()) {
			
			StringStringParser bi = (StringStringParser) p;
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);
			
			MultilineTextEditor editor = new MultilineTextEditor(bi, new ValueProxy(value.getName(), value.getDefault()));
			
			registerComponent(value.getName(), editor);
			
			panel.add(new JScrollPane(editor), BorderLayout.CENTER);

			return panel;
		}
		
		JPanel panel = new JPanel(new BorderLayout());
		
		panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);
		
		TextEditor editor = new TextEditor(new ValueProxy(value.getName(), value.getDefault()));

		registerComponent(value.getName(), editor);
		
		panel.add(editor, BorderLayout.SOUTH);
		
		return panel;
		
	}
	
	private void registerComponent(String name, SettingsComponent component) {
		
		components.put(name, component);
		
		JComponent target = null;
		
		if (component instanceof CustomSettingsComponent) {
			
			target = ((CustomSettingsComponent) component).component;
			
		} else {
			
			target = ((JComponent) component);
			
		}
		
		
		target.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				
				if (strategy == CommitStrategy.FOCUS)
					commit();
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {

			}
			
		});
		
	}
	
}
