package org.coffeeshop.swing;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import org.coffeeshop.dialogs.OrganizedSettings;
import org.coffeeshop.dialogs.SettingsGroup;
import org.coffeeshop.settings.PrefixProxySettings;
import org.coffeeshop.settings.Settings;
import org.coffeeshop.string.parsers.ParseException;

public class KeyBindingsEditor extends SettingsPanel {

	private static final long serialVersionUID = 1L;

	private InputMap inputs;

	private Settings settings;
	
	public KeyBindingsEditor(Settings settings, ActionMap actions, InputMap inputs) {
		super(settings, (OrganizedSettings) generate(actions, new OrganizedSettings("Keybingins")));
		this.settings = settings;
		this.inputs = inputs;
	}
	
	public KeyBindingsEditor(Settings settings, ActionMap actions, InputMap inputs, String prefix) {
		this(new PrefixProxySettings(settings, prefix), actions, inputs);
	}

	@Override
	public void commit() {
		super.commit();
		
		install(inputs, settings);
	}
	
	public static SettingsGroup generate(ActionMap actions, SettingsGroup group) {
		
		for (Object i : actions.allKeys()) {
			
			Action action = actions.get(i);
			
			group.attachValue((String)i, (String)action.getValue(Action.NAME), KeyStrokeStringParser.getParser());
			
		}

		return group;
	}
	
	public static void install(InputMap map, Settings settings) {
		
		for (String i : settings.getAllKeys()) {
			
			try {
				KeyStroke stroke = (KeyStroke) KeyStrokeStringParser.getParser().parse(settings.getString(i, null));
				
				map.put(stroke, i);

			} catch (ParseException e) {
				continue;
			}

		}
	
	}
	
}
