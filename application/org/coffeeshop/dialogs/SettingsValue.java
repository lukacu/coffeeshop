package org.coffeeshop.dialogs;

import org.coffeeshop.string.parsers.StringParser;

public class SettingsValue implements SettingsNode {

	private String name, title, def;
	
	private StringParser parser;

	public SettingsValue(String name, String title, StringParser parser, String def) {
		super();
		this.name = name;
		this.parser = parser;
		this.title = title;
		this.def = def;
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
	
	public String getDefault() {
		return def;
	}

}
