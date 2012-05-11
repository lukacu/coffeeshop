package org.coffeeshop.swing;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import org.coffeeshop.application.Application;
import org.coffeeshop.settings.Settings;
import org.coffeeshop.settings.SettingsNotFoundException;
import org.coffeeshop.swing.Splash.SplashController;

public abstract class History implements SplashController {

	private int size;
	
	private LinkedList<File> history = new LinkedList<File>();

	public History(int size) {
		
		this.size = size;
		Settings settings = Application.getApplicationSettings();
		
		TreeSet<File> history = new TreeSet<File>();
		
		for (int i = 0; i < size; i++) {
			
			try {
				File f = new File(settings.getString("history." + i));

				if (f.exists())
					history.add(f);

			} catch (SettingsNotFoundException e) {}
			
		}
	
		this.history.addAll(history);
		
	}
	
	@Override
	public Object[] items() {
		return history.toArray(new File[history.size()]);
	}

	public void add(File path) {
		if (!path.exists())
			return;
		
		Iterator<File> itr = history.iterator();
		
		while (itr.hasNext()) {
			File f = itr.next();
			
			if (f.compareTo(path) == 0)
				itr.remove();
		}
		
		
		history.addFirst(path);
		
		if (history.size() > size)
			history.removeLast();
		
		Settings settings = Application.getApplicationSettings();
		
		for (int i = 0; i < Math.min(size, history.size()); i++) {
			
			try {
				settings.setString("history." + i, history.get(i).getAbsolutePath());
				
			} catch (SettingsNotFoundException e) {}
			
		}
		
	}
}
