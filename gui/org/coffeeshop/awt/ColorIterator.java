package org.coffeeshop.awt;

import java.awt.Color;
import java.util.Iterator;

public abstract class ColorIterator implements Iterator<Color> {

	public static class HueColorIterator extends ColorIterator {
	
		private float hue = 0;

		private float step = 1;
		
		@Override
		public boolean hasNext() {
			return step > 0.001;
		}

		@Override
		public Color next() {

			if (!hasNext()) return null;

			Color color = new Color(Color.HSBtoRGB(hue, 1, 1));
			
			hue += 0.5;
			
			if (hue >= 1) {
				
				hue += step - 1;
				
				if (hue >= 0.5) {
					
					step /= 2;
					hue = step / 2;
					
				}

			}
			
			return color;
			
		}
		
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Removing colors not supported");
	}

}
