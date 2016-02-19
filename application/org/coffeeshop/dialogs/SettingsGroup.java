package org.coffeeshop.dialogs;

import org.coffeeshop.string.parsers.StringParser;

public interface SettingsGroup extends Iterable<SettingsNode>, SettingsNode {

	public SettingsGroup createSubgroup(String title);

	public SettingsValue attachValue(String name, String title, StringParser type, Object value);
	
	public SettingsValue attachValue(String name, String title, StringParser type);

	public SettingsMap attachMap(String namespace, String title);
}
