package org.coffeeshop.dialogs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.coffeeshop.settings.ReadableSettings;
import org.coffeeshop.settings.Settings;
import org.coffeeshop.settings.SettingsMetadata;
import org.coffeeshop.string.parsers.ParseException;
import org.coffeeshop.string.parsers.StringParser;

public class OrganizedSettings extends Settings implements SettingsGroup {

	private String title;
	
	private HashMap<String, SettingsValue> values = new HashMap<String, SettingsValue>();
	
	private Vector<SettingsNode> nodes = new Vector<SettingsNode>();
	
	private ReadableSettings parent;
	
	private class InternalSettingsGroup implements SettingsGroup {

		private String title;
		
		private Vector<SettingsNode> nodes = new Vector<SettingsNode>();
		
		public InternalSettingsGroup(String title) {
			this.title = title;
		}

		@Override
		public SettingsValue attachValue(String name, String title,
				StringParser type, String value) {
			if (values.containsKey(name))
				throw new RuntimeException("Name already taken");
			
			SettingsValue v = new SettingsValue(name, title, type, value);
			values.put(name, v);
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
		public SettingsValue attachValue(String name, StringParser type) {
			
			if (parent == null || (!(parent instanceof SettingsMetadata)))
				throw new RuntimeException("Unable to retrieve settings metadata from parent");
			
			String title = ((SettingsMetadata) parent).getTitle(name);
			
			if (title == null)
				throw new RuntimeException("Unable to retrieve settings metadata from parent");
			
			return attachValue(name, title, type);
		}
		
	}
	
	public OrganizedSettings(ReadableSettings parent, String title) {
		super(parent);
		this.parent = parent;
		this.title = title;
	}
/*
	private void buildFromDescription(String description) {
		
		StringTokenizer lines = new StringTokenizer(description, "\n");
		
		while (lines.hasMoreElements()) {
		
			String element = lines.nextToken();
			
			if (StringUtils.empty(element))
				continue;
			
			
		
		}
		
	}
	*/
	@Override
	protected String setProperty(String key, String value) {
		
		if (values.containsKey(key)) {
			
			SettingsValue v = values.get(key);
			
			try {
				v.getParser().parse(value);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		
		}

		return super.setProperty(key, value);
	}

	@Override
	protected String getProperty(String key) {
		
		String val = super.getProperty(key);

		if (val == null) {

			SettingsValue value = values.get(key);
			
			if (value == null || value.getDefault() == null)
				return null;
			
			return value.getDefault();
			
		} else return val;
		
	}
	
	@Override
	public SettingsValue attachValue(String name, String title,
			StringParser type, String value) {
		if (values.containsKey(name))
			throw new RuntimeException("Name already taken");
		
		SettingsValue v = new SettingsValue(name, title, type, value);
		values.put(name, v);
		nodes.add(v);
		
		return v;
	}
	
	@Override
	public SettingsValue attachValue(String name, String title, StringParser type) {
		return attachValue(name, title, type, null);
	}

	@Override
	public SettingsValue attachValue(String name, StringParser type) {
		
		if (parent == null || (!(parent instanceof SettingsMetadata)))
			throw new RuntimeException("Unable to retrieve settings metadata from parent");
		
		String title = ((SettingsMetadata) parent).getTitle(name);
		
		if (title == null)
			throw new RuntimeException("Unable to retrieve settings metadata from parent");
		
		return attachValue(name, title, type);
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

	public Object get(String key) {
		
		String raw = getString(key);
		
		SettingsValue sv = values.get(key);
		
		if (sv == null || sv.getParser() == null)
			return raw;
		
		try {
			return sv.getParser().parse(raw);
		} catch (ParseException e) {
			return null;
		}
		
	}
	
}
