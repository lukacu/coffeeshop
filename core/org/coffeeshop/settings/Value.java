package org.coffeeshop.settings;

public abstract class Value {

	public abstract String getValue();
	
	public boolean getBoolean() throws NumberFormatException {
        String s = getValue();
        if (s == null) {
            throw new NumberFormatException();
        }

        return Boolean.parseBoolean(s);
	}

	public double getDouble() throws NumberFormatException {
        String s = getValue();
        if (s == null) {

        	
            throw new NumberFormatException();
        } 
        
        return Double.parseDouble(s);
	}

	public float getFloat() throws NumberFormatException {
        String s = getValue();
        if (s == null) {

        	
            throw new NumberFormatException();
        }
        
        return Float.parseFloat(s);
	}

	public long getLong() throws NumberFormatException {
        String s = getValue();
        if (s == null) {
            throw new NumberFormatException();
        }
        
        return Long.parseLong(s);
	}

	public int getInt() throws NumberFormatException {
        String s = getValue();
        if (s == null) {
            throw new NumberFormatException();
        }
        
        return Integer.parseInt(s);
	}
	
	public String getString() throws SettingsNotFoundException {
        return getValue();
	}    
 
	public int getInt(int def) {
		
		try {
			
			return getInt();
			
		} 
		catch (NumberFormatException e) {}
		catch (SettingsNotFoundException e) {}

		return def;
		
	}
	
	public long getLong(long def) {
		
		try {
			
			return getLong();
			
		}
		catch (NumberFormatException e) {}
		catch (SettingsNotFoundException e) {}

		return def;
		
	}
	
	public double getDouble(double def) {
		
		try {
			
			return getDouble();
			
		}
		catch (NumberFormatException e) {}
		catch (SettingsNotFoundException e) {}

		return def;
		
	}
	
	public float getFloat(float def) {
		try {
			
			return getFloat();
			
		} 
		catch (NumberFormatException e) {}
		catch (SettingsNotFoundException e) {}

		return def;
	}
	
	public boolean getBoolean(boolean def) {
		try {
			
			return getBoolean();
			
		} 
		catch (NumberFormatException e) {}
		catch (SettingsNotFoundException e) {}

		return def;
		
	}
	
	public String getString(String def) {
		try {
			
			return getString();
			
		} 
		catch (NumberFormatException e) {}
		catch (SettingsNotFoundException e) {}

		return def;
		
	}
	
}
