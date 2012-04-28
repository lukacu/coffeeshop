package org.coffeeshop.swing;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ImageStore {

	private static Hashtable<String, Image> storage = new Hashtable<String, Image>();
	
	private static HashSet<Class<?>> anchors = new HashSet<Class<?>>();
	
	static {
		ImageStore.registerAnchorClass(ImageStore.class);
	}
	
	public static void registerAnchorClass(Class<?> anchor) {
		anchors.add(anchor);
	}
	
	
	public static Icon getIcon(String name) {
		
		Image img = getImage(name);
		
		if (img != null)
			return new ImageIcon(img);
		
		return null;
	}
	
	public static Image getImage(String name) {
		Image i = storage.get(name);
		
		if (i != null)
			return i;
		
		for (Class<?> cls : anchors) {
		
			try {

				InputStream str = cls.getResourceAsStream(name);
				
				if (str == null)
					continue;
				
				Image img = ImageIO.read(str);
				
				if (img == null)
					return null;
				
				storage.put(name, img);
				
				return img;
				
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (NullPointerException e) {
	
			}
		}
		return null;
	}
	
}