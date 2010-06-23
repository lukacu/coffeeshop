/* THIS FILE IS A MEMBER OF THE COFFEESHOP LIBRARY
 * 
 * License:
 * 
 * Coffeeshop is a conglomerate of handy general purpose Java classes.  
 * 
 * Copyright (C) 2006-2008 Luka Cehovin
 * This library is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation; either version 2.1 of 
 *  the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 *  GNU Lesser General Public License for more details. 
 *  
 *  http://www.opensource.org/licenses/lgpl-license.php
 *  
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the 
 *  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA 
 * 
 */

package org.coffeeshop.application;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.coffeeshop.arguments.Arguments;
import org.coffeeshop.arguments.ArgumentsException;
import org.coffeeshop.arguments.ArgumentsResult;
import org.coffeeshop.arguments.FlaggedOption;
import org.coffeeshop.arguments.Parameter;
import org.coffeeshop.arguments.Switch;
import org.coffeeshop.arguments.UnflaggedOption;
import org.coffeeshop.external.OperatingSystem;
import org.coffeeshop.io.TempDirectory;
import org.coffeeshop.log.Logger;
import org.coffeeshop.settings.ReadableSettings;
import org.coffeeshop.settings.Settings;
import org.coffeeshop.settings.SettingsNotFoundException;
import org.coffeeshop.string.parsers.StringParser;

/**
 * This is the main application class that an application should override to use
 * CoffeeShop toolbox.
 * 
 * 
 * @author luka
 * @since CoffeeShop 1.0
 */
public abstract class Application {

	public interface SettingsSetter {
		
		public boolean addDefaultElement(String key, String value, String description, String shortFlag, String longFlag, StringParser parser);
		
		public boolean addDefaultElement(String key, String value, String description);
		
	}
	
	/**
	 * Generates a suitable storage directory path according to the current user
	 * and the underlying operating system.
	 * 
	 * @param a
	 *            the application descriptor
	 * @return the suitable storage directory path according to the current user
	 *         and the underlying operating system.
	 */
	public static File applicationStorageDirectory(Application a) {
		return OperatingSystem.getSystemConfigurationDirectory();
	}
	
	private class ApplicationSettingsImpl extends Settings implements SettingsSetter {

		private static final String PASSTHROUGH_ARGUMENTS_ID = "__coffeeshop.passthrough";
		
		private class DefaultElement {
			
			public String value, description;
			
			public DefaultElement(String value, String description) {
				if (value == null)
					throw new IllegalArgumentException("Must define default value");

				this.value = value;
				this.description =  description;
			}
			
		}
		
		private class ArgumentElement extends DefaultElement {
			
			private String longFlag = null;
			
			private char shortFlag = 0;
			
			private StringParser parser;
			
			public ArgumentElement(String value, String description, String shortFlag, String longFlag, StringParser parser) {
				super(value, description);
				
				if (shortFlag != null) {
					if (shortFlag.length() != 1)
						throw new IllegalArgumentException("Short flag must be only one character or null");
					
					this.shortFlag = shortFlag.charAt(0);
				}
				this.longFlag = longFlag;
				
				if (shortFlag == null && longFlag == null)
					throw new IllegalArgumentException("At least one flag type must be set");
				
				
				this.parser = parser;
			}
			
			
		}
		
		protected HashMap<String, DefaultElement> map = new HashMap<String, DefaultElement>();
		
		private boolean modified = false;
		
		private Arguments argumentsParser = null;
		
		private ArgumentsResult reconfigured;
		
		private Settings storage = null;
		
		public ApplicationSettingsImpl(Application a) {
			super((ReadableSettings)null);
			this.storage = Application.getApplication().getSettingsManager().getSettings(Application.getApplication().getUnixName() + ".ini", null);
		}
		
		public boolean addDefaultElement(String key, String value, String description, String shortFlag, String longFlag, StringParser parser) {
			
			return addDefaultElement(key, new ArgumentElement(value, description, shortFlag, longFlag, parser));
		}

		public boolean addDefaultElement(String key, String value, String description) {
			
			return addDefaultElement(key, new DefaultElement(value, description));

		}
				
		protected boolean addDefaultElement(String key, DefaultElement e) {
			
			if (key.equals(PASSTHROUGH_ARGUMENTS_ID))
				throw new IllegalArgumentException("This is the only illegal key and you must use this one!?");
			
			if (map.containsKey(key))
				return false;
			
			map.put(key, e);
			
			return true;
		}
		
		public String[] parseArguments(String[] args) throws ArgumentsException {
			
			if (argumentsParser == null || modified) {
				
				argumentsParser = new Arguments();
				
				Set<String> keys = map.keySet();
				
				Switch help = new Switch("help").setLongFlag("help").setShortFlag('h');
		    	help.setHelp("Display this message and exit.");
				
		    	argumentsParser.registerParameter(help);
		    	
				for (String key : keys) {
					
					DefaultElement d = map.get(key);
					
					if (!(d instanceof ArgumentElement))
						continue;
					
					Parameter p = makeParameter(key, (ArgumentElement)d);
					
					argumentsParser.registerParameter(p);
					
				}
				
				UnflaggedOption passthrough = new UnflaggedOption(PASSTHROUGH_ARGUMENTS_ID);
				passthrough.setGreedy(true);
				
				argumentsParser.registerParameter(passthrough);
				
			}
			
			reconfigured = argumentsParser.parse(args);
			
			if (!reconfigured.success()) {
				
				Iterator<?> i = reconfigured.getErrorMessageIterator();
				
				for (; i.hasNext(); ) {
					System.out.println((String) i.next());
				}
				
				System.exit(5000);
				
			}
			
			if (reconfigured.getBoolean("help")) {
				System.out.println(argumentsParser.getHelp());
				System.exit(0);
			}
			
			return reconfigured.getStringArray(PASSTHROUGH_ARGUMENTS_ID);
			
		}
		
		protected String getProperty(String key) {
			if (reconfigured != null) {
				
				Object o = reconfigured.getObject(key);
				
				if (o != null)
					return o.toString();
			}

			if (storage == null) return null;
			try {
				String s = storage.getString(key);
				return s;
			} catch (SettingsNotFoundException e) {}
			
			DefaultElement e = map.get(key);
			if (e == null) return null;
			return e.value;
		}
		
		private Parameter makeParameter(String name, ArgumentElement e) {
			
			if (e.parser == null) {
				
				Switch s = new Switch(name);
				
				if (e.description != null)
					s.setHelp(e.description);
				if (e.longFlag != null)
					s.setLongFlag(e.longFlag);
				if (e.shortFlag != 0)
					s.setShortFlag(e.shortFlag);
				
				return s;
				
			} else {
				
				FlaggedOption o = new FlaggedOption(name);
				
				if (e.description != null)
					o.setHelp(e.description);
				if (e.longFlag != null)
					o.setLongFlag(e.longFlag);
				if (e.shortFlag != 0)
					o.setShortFlag(e.shortFlag);
				
				o.setStringParser(e.parser);
				
				return o;
			}
			
		}
		
		public boolean isModified() {
			return storage.isModified();
		}

		public void setDouble(String key, double value) {
			storage.setDouble(key, value);
			
		}

		public void setFloat(String key, float value) {
			storage.setFloat(key, value);
		}

		public void setInt(String key, int value) {
			storage.setInt(key, value);
			
		}

		public void setLong(String key, long value) {
			storage.setLong(key, value);
			
		}

		public void setBoolean(String key, boolean value) {
			storage.setBoolean(key, value);
			
		}
		
		public void setString(String key, String value) {
			storage.setString(key, value);
			
		}
		
	    public void touch() {
	        storage.touch();
	    }

		public void remove(String key) {
			storage.remove(key);
			
		}
	}
	
	private static Application application = null;
	
	private String name;
	
	private SettingsManager settingsManager;
	
	private Logger logger;
	
	private ApplicationSettingsImpl settings;
	
	private String[] arguments;
	
	private TempDirectory temp = null;
	
	/**
	 * Get the current application object
	 * 
	 * @return current application object
	 */
	public static final Application getApplication() {
		
		if (application == null)
			throw new RuntimeException("No application object created.");
		
		return application;
	}
	
	public static final Logger getApplicationLogger() {
		return getApplication().getLogger();
	}
	
	public static final Settings getApplicationSettings() {
		return getApplication().getSettings();
	}
	
	/**
	 * Constructs new application object
	 * 
	 * @param name applicaton name (only literals and numerals are accepted)
	 * @param arguments command line arguments
	 */
	public Application(String name, String[] arguments) throws ArgumentsException {
		
		if (application != null)
			throw new RuntimeException("Only one application object allowed.");
		
		application = this;
		
		if (name.matches("[^A-Za-z0-9]"))
			throw new IllegalArgumentException(
					"Name of the application must contain only alphanumerical characters");
		
		this.name = name;
		
		settingsManager = new SettingsManager(this);
		
		processArguments(arguments);
		
	}
	
	/**
	 * Get applicaton name
	 * 
	 * @return application name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get application UNIX style name (lowercase)
	 * 
	 * @return UNIX style name
	 */
	public final String getUnixName() {
		return name.toLowerCase();
	}
	
	/**
	 * Get application settings manager 
	 * 
	 * @return settings manager
	 * @see SettingsManager
	 */
	public final SettingsManager getSettingsManager() {
		return settingsManager;
	}
	
	/**
	 * Get application logger
	 * 
	 * @return application logger
	 * @see Logger
	 */
	public Logger getLogger() {
		
		if (logger == null)
			logger = new Logger();
		
		return logger;
	}
	
	public final String[] getArguments() {
		return arguments;
	}
	
	public final Settings getSettings() {
		return settings;
	}
	
	/**
	 * Get application short description
	 * 
	 * @return short application description
	 */
	public abstract String getShortDescription();
	
	
	protected abstract void defineDefaults(SettingsSetter setter);
	
	private final void processArguments(String[] arguments) throws ArgumentsException {

		ApplicationSettingsImpl a = new ApplicationSettingsImpl(this);
		
		defineDefaults(a);
		
		this.arguments = a.parseArguments(arguments);
		
		settings = a;
	}
	
	public TempDirectory getTempDirectory() {
		if (temp == null)
			temp = new TempDirectory(getUnixName());
		
		return temp;
	}
	
	/**
	 * Get application long description
	 * 
	 * @return long application description
	 */
	public abstract String getLongDescription();
	
	@Override
	public String toString() {
		return "Application: " + getName();
	}
	
}
