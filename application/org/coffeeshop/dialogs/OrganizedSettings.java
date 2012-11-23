package org.coffeeshop.dialogs;

import java.util.Iterator;
import java.util.Vector;

import org.coffeeshop.string.parsers.StringParser;

public class OrganizedSettings implements SettingsGroup {

	private String title;
	
	//private HashMap<String, SettingsValue> values = new HashMap<String, SettingsValue>();
	
	private Vector<SettingsNode> nodes = new Vector<SettingsNode>();
	
	private class InternalSettingsGroup implements SettingsGroup {

		private String title;
		
		private Vector<SettingsNode> nodes = new Vector<SettingsNode>();
		
		public InternalSettingsGroup(String title) {
			this.title = title;
		}

		@Override
		public SettingsValue attachValue(String name, String title,
				StringParser type, String value) {

			SettingsValue v = new SettingsValue(name, title, type, value);
			nodes.add(v);
			
			return v;
		}
		
		@Override
		public SettingsValue attachValue(String name, String title, StringParser type) {
			
			return attachValue(name, title, type, null);
			
		}

		@Override
		public SettingsGroup createSubgroup(String title) {
			InternalSettingsGroup g = new InternalSettingsGroup(title);
			nodes.add(g);
			return g;
		}

		@Override
		public String getTitle() {
			return title;
		}


		@Override
		public Iterator<SettingsNode> iterator() {
			return nodes.iterator();
		}

		@Override
		public SettingsMap attachMap(String namespace, String title) {
			SettingsMap v = new SettingsMap(title, namespace);
			nodes.add(v);
			
			return v;
		}
		
	}
	
	public OrganizedSettings(String title) {
		this.title = title;
	}

	@Override
	public SettingsValue attachValue(String name, String title,
			StringParser type, String uvalue) {

		SettingsValue v = new SettingsValue(name, title, type, uvalue);
		nodes.add(v);
		
		return v;
	}
	
	@Override
	public SettingsValue attachValue(String name, String title, StringParser type) {
		return attachValue(name, title, type, null);
	}


	@Override
	public SettingsGroup createSubgroup(String title) {
		InternalSettingsGroup g = new InternalSettingsGroup(title);
		nodes.add(g);
		return g;
	}

	
	
	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public Iterator<SettingsNode> iterator() {
		return nodes.iterator();
	}
	
	@Override
	public SettingsMap attachMap(String namespace, String title) {

		SettingsMap v = new SettingsMap(title, namespace);
		nodes.add(v);
		
		return v;
	}

}
