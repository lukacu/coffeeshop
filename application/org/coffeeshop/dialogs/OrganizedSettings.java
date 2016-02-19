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
		
		private String prefix;
		
		private Vector<SettingsNode> nodes = new Vector<SettingsNode>();

		public InternalSettingsGroup(String title, String prefix) {
			this.title = title;
			this.prefix = prefix;
		}		
		
		public InternalSettingsGroup(String title) {
			this(title, "");
		}

		@Override
		public SettingsValue attachValue(String name, String title,
				StringParser type, Object value) {

			String def = value == null ? "" : value.toString();
			
			SettingsValue v = new SettingsValue(prefix + name, title, type, def);
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
			SettingsMap v = new SettingsMap(title, prefix + namespace);
			nodes.add(v);
			
			return v;
		}
		
	}
	
	public OrganizedSettings(String title) {
		this.title = title;
	}

	@Override
	public SettingsValue attachValue(String name, String title,
			StringParser type, Object uvalue) {

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

	public SettingsGroup createSubgroup(String title, String prefix) {
		InternalSettingsGroup g = new InternalSettingsGroup(title, prefix);
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
