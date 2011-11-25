package org.coffeeshop.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.coffeeshop.awt.StackLayout;
import org.coffeeshop.awt.StackLayout.Orientation;
import org.coffeeshop.dialogs.OrganizedSettings;
import org.coffeeshop.dialogs.SettingsGroup;
import org.coffeeshop.dialogs.SettingsNode;
import org.coffeeshop.dialogs.SettingsValue;
import org.coffeeshop.settings.Settings;
import org.coffeeshop.settings.SettingsChangedEvent;
import org.coffeeshop.settings.SettingsListener;
import org.coffeeshop.string.StringUtils;
import org.coffeeshop.string.parsers.BoundedIntegerStringParser;
import org.coffeeshop.string.parsers.EnumeratedStringParser;
import org.coffeeshop.string.parsers.IntegerStringParser;
import org.coffeeshop.string.parsers.StringParser;

public class SettingsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, JComponent> components = new HashMap<String, JComponent>();
	
	private OrganizedSettings settings;
	
	private class ChangeListenerSpinner implements ChangeListener {

		private String key;
		private JSpinner spinner;
		
		public ChangeListenerSpinner(String key, JSpinner spinner) {
			this.key = key;
			this.spinner = spinner;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() != spinner)
				return;

			try {
				settings.setString(key, spinner.getValue().toString());
			} catch (RuntimeException ex) {
				spinner.setValue(settings.getString(key));
			}
		}
		
	}
	
	private class ChangeListenerSlider implements ChangeListener {

		private String key;
		private JSlider slider;
		
		public ChangeListenerSlider(String key, JSlider slider) {
			this.key = key;
			this.slider = slider;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() != slider)
				return;

			if (slider.getValueIsAdjusting())
				return;
			try {
				settings.setInt(key, slider.getValue());
			} catch (RuntimeException ex) {
				slider.setValue(settings.getInt(key));
			}
		}
		
	}
	
	private class ChangeListenerCombo implements ItemListener {

		private String key;
		private JComboBox combo;
		
		public ChangeListenerCombo(String key, JComboBox combo) {
			this.key = key;
			this.combo = combo;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource() != combo)
				return;

			try {
				settings.setString(key, combo.getSelectedItem().toString());
			} catch (RuntimeException ex) {
				combo.setSelectedItem(settings.getString(key));
			}
		}
		
	}
	
	
	public SettingsPanel(OrganizedSettings settings) {
		super();
		
		this.settings = settings;
		
		build(settings);
		
		settings.addSettingsListener(new SettingsListener() {

			@Override
			public void settingsChanged(SettingsChangedEvent e) {
				
				
				
			}

			@Override
			public void storeSettings(Settings s) {
			}
			
		});
	}
	
	private void build(OrganizedSettings settings) {
		
		//setLayout(new StackLayout(Orientation.HORIZONTAL));
		setLayout(new BorderLayout());
		
		add(buildGroup(settings), BorderLayout.CENTER);
		
	}
	
	private JComponent buildGroup(SettingsGroup group) {
		
		JPanel panel = new JPanel(new StackLayout(Orientation.VERTICAL, 2, 5, true));
		
		if (!StringUtils.empty(group.getTitle())) {
			panel.setBorder(new TitledBorder(group.getTitle()));
		}
		
		for (SettingsNode n : group) {
			
			if (n instanceof SettingsGroup) {
				JComponent c = buildGroup((SettingsGroup) n);
				panel.add(c);
				continue;
			}
			
			JComponent c = buildComponent((SettingsValue) n);
			panel.add(c);
		}
		
		return panel;
	}
	
	private JComponent buildComponent(SettingsValue value) {

		StringParser p = value.getParser();
		
		if (p instanceof IntegerStringParser) {
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.WEST);
			
			JSpinner spinner = new JSpinner(new SpinnerNumberModel());
			
			spinner.setValue(settings.getInt(value.getName()));
			
			spinner.setValue(settings.getString(value.getName()));
			
			spinner.addChangeListener(new ChangeListenerSpinner(value.getName(), spinner));
			
			components.put(getName(), spinner);
			
			panel.add(spinner, BorderLayout.EAST);
			
			return panel;
		}
		
		if (p instanceof BoundedIntegerStringParser) {
			
			BoundedIntegerStringParser bi = (BoundedIntegerStringParser) p;
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);
			
			int val = Math.min(bi.getMax(), Math.max(bi.getMin(), settings.getInt(value.getName())));
			
			JSlider slider = new JSlider(bi.getMin(), bi.getMax(), val);
			
			slider.addChangeListener(new ChangeListenerSlider(value.getName(), slider));
			
			slider.setValue(settings.getInt(value.getName()));
			
			components.put(getName(), slider);
			
			panel.add(slider, BorderLayout.SOUTH);
			
			return panel;
		}		
		
		if (p instanceof EnumeratedStringParser) {
			
			EnumeratedStringParser bi = (EnumeratedStringParser) p;
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(new JLabel(value.getTitle()), BorderLayout.NORTH);
			
			JComboBox combo = new JComboBox(bi.getValues());
			
			combo.addItemListener(new ChangeListenerCombo(value.getName(), combo));
			
			combo.setSelectedItem(settings.getString(value.getName()));
			
			components.put(getName(), combo);
			
			panel.add(combo, BorderLayout.SOUTH);
			
			return panel;
		}	
		
		throw new RuntimeException("Unsupported value type");
		
	}
	

	
}
