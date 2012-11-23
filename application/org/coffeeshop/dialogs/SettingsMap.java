package org.coffeeshop.dialogs;

public class SettingsMap implements SettingsNode {

	private String title, namespace;
	
	public SettingsMap(String title, String namespace) {
		super();
		this.title = title;
		this.namespace = namespace;
	}

	@Override
	public String getTitle() {

		return title;
	}

	public String getNamespace() {
		return namespace;
	}

}
