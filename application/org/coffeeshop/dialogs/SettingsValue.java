package org.coffeeshop.dialogs;

import org.coffeeshop.settings.Settings;
import org.coffeeshop.settings.SettingsNotFoundException;
import org.coffeeshop.string.parsers.ParseException;
import org.coffeeshop.string.parsers.StringParser;

public class SettingsValue implements SettingsNode {

	private String name, title, def;
	
	private StringParser parser;

	public SettingsValue(String name, String title, StringParser parser, Object def) {
		super();
		this.name = name;
		this.parser = parser;
		this.title = title;
		this.def = def == null ? "" : def.toString();
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public StringParser getParser() {
		return parser;
	}
	
	public Object parse(String raw) throws ParseException {
		if (parser == null)
			return raw;
		
		return parser.parse(raw);
	}
	
	public Object parse(Settings settings) throws ParseException {
		
		try {
		
			return parse(settings.getString(name));
		
		} catch (SettingsNotFoundException e) {
			return null;
		}
	}
	
	public String getDefault() {
		return def;
	}

}
