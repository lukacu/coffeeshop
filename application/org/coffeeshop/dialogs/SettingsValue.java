package org.coffeeshop.dialogs;

import org.coffeeshop.string.parsers.StringParser;

public class SettingsValue implements SettingsNode {

	private String name, title;
	
	private StringParser parser;

	public SettingsValue(String name, String title, StringParser parser) {
		super();
		this.name = name;
		this.parser = parser;
		this.title = title;
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

}
