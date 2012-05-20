package org.coffeeshop.swing;

import java.io.File;
import java.util.LinkedList;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

import org.coffeeshop.settings.AbstractSettings;
import org.coffeeshop.settings.SettingsNotFoundException;

public class RecentDocuments extends AbstractListModel {

	private static final long serialVersionUID = 1L;

	private LinkedList<File> documents = new LinkedList<File>();

	private AbstractSettings settings;
	
	private boolean validate = true;
	
	private String base;
	
	public RecentDocuments(AbstractSettings settings, String base) {

		this.settings = settings;
		
		this.base = base;
		
		TreeSet<File> history = new TreeSet<File>();

		int i = 0;

		while (true) {

			try {
				File f = new File(settings.getString(base + i));

				if (!validate || f.exists())
					history.add(f);

			} catch (SettingsNotFoundException e) {
				break;
			}

			i++;
		}

		this.documents.addAll(history);

		push();
		
	}

	public void addDocument(File path) {

		if (validate && !path.exists())
			return;

		int i;
		for (i = 0; i < documents.size(); i++) {
			File f = documents.get(i);
			if (f.compareTo(path) == 0) {
				documents.remove(i);
				break;
			}
		}

		documents.addFirst(path);

		push();
		
		fireContentsChanged(this, 0, getSize());

	}

	private void push() {
		
		int i;
		
		for (i = 0; i < documents.size(); i++) {

			try {
				settings.setString(base + i, documents.get(i)
						.getAbsolutePath());

			} catch (SettingsNotFoundException e) {
			}

		}
		
		while (true) {

			if (settings.containsKey(base + i))
				settings.remove(base + i);
			else break;
			
			i++;
		}
		
	}
	
	@Override
	public Object getElementAt(int arg0) {
		return documents.get(arg0);
	}

	@Override
	public int getSize() {
		return documents.size();
	}



}
