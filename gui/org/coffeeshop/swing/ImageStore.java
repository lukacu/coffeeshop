package org.coffeeshop.swing;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ImageStore {

	public interface ImageProvider {
		
		public Image loadImage(String name);
		
	}
	
	private static class ResourceImageProvider implements ImageProvider {

		private Class<?> anchor;
		
		public ResourceImageProvider(Class<?> anchor) {
			this.anchor = anchor;
		}
		
		@Override
		public Image loadImage(String name) {
			
			try {

				InputStream str = anchor.getResourceAsStream(name);
				
				if (str == null)
					return null;
				
				Image img = ImageIO.read(str);
				
				if (img == null)
					return null;
				
				return img;
				
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (NullPointerException e) {
				return null;
			}
		}
		
	}
	
	
	private static Hashtable<String, Image> storage = new Hashtable<String, Image>();
	
	private static List<ImageProvider> providers = new Vector<ImageProvider>();
	
	static {
		
		ImageStore.registerAnchorClass(ImageStore.class);
		
	}
	
	public static void registerAnchorClass(Class<?> anchor) {
		
		providers.add(new ResourceImageProvider(anchor));
		
	}
	
	public static void registerImageProvider(ImageProvider provider) {
		
		providers.add(provider);
	}
	
	public static Icon getIcon(String name) {
		
		Image img = getImage(name);
		
		if (img != null)
			return new ImageIcon(img);
		
		return null;
	}

	public static Icon getIcon(String ... names) {
		
		Image img = getImage(names);
		
		if (img != null)
			return new ImageIcon(img);
		
		return null;
	}
	
	public static Image getImage(String ... names) {
		
		for (String name : names) {
			
			Image i = getImage(name);
			if (i != null)
				return i;
			
		}
		
		return null;
	}
	
	public static Image getImage(String name) {
		Image i = storage.get(name);
		
		if (i != null)
			return i;
		
		for (ImageProvider provider : providers) {
		
			try {

				Image img = provider.loadImage(name);
				
				if (img == null)
					continue;
				
				storage.put(name, img);
				
				return img;
							
			} catch (NullPointerException e) {
	
			}
		}
		return null;
	}
	
}