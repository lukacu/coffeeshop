package org.coffeeshop.swing.figure;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

public class LayeredFigure implements Figure {

	protected Figure[] figures;
	
	protected boolean[] hidden;
	
	protected String name;
	
	public LayeredFigure(String name, Figure ... layers) {
		
		figures = flatten(layers);
		
		hidden = new boolean[figures.length];
		
		this.name = name;
		
		Arrays.fill(hidden, false);
		
		if (name == null)
			this.name = "Layered Figure (" + figures.length + " layers)";
		
	}
	
	public LayeredFigure(Figure ... layers) {
		
		this(null, layers);
		
	}
	
	protected int getIndex(Figure figure) {
		
		for (int i = 0; i < figures.length; i++)
			if (figures[i] == figure)
				return i;
		
			return -1;
			
	}
	
	protected Figure[] flatten(Figure[] figures) {
		
		int count = 0;
		
		for (int i = 0; i < figures.length; i++) {
			Figure figure = figures[i];

			if (figure == null)
				continue;
			
			if (figure instanceof LayeredFigure) {
				count += ((LayeredFigure) figure).size();
			} else count += 1;

		}
		
		Figure[] result = new Figure[count];
		
		int pos = 0;
		
		for (int i = 0; i < figures.length; i++) {
			Figure figure = figures[i];
			if (figure == null)
				continue;
			
			if (figure instanceof LayeredFigure) {
				System.arraycopy(((LayeredFigure) figure).figures, 0, result, pos, 
						((LayeredFigure) figure).figures.length);
				
				pos += ((LayeredFigure) figure).size();
			} else result[pos++] = figure;

		}

		return result;
		
	}
	
	public int getHeight(FigureObserver observer) {
	
		int height = 0;
		
		for (int i = 0; i < figures.length; i++) {
			
			height = Math.max(height, figures[i].getHeight(observer));
			
		}

		return height;
	}

	public int getWidth(FigureObserver observer) {
		int width = 0;
		
		for (int i = 0; i < figures.length; i++) {
			width = Math.max(width, figures[i].getWidth(observer));
			
		}

		return width;
	}

	public void paint(Graphics2D g, Rectangle2D figureSize, Rectangle windowSize,
			FigureObserver observer) {
		

		for (int i = 0; i < figures.length; i++) {
			if (!hidden[i])
				figures[i].paint(g, figureSize, windowSize, observer);
			
		}

	}

	public int size() {
		return figures.length;
	}
	
	public void setVisible(Figure layer, boolean visible) {
		
		int i = getIndex(layer);

		if (i == -1)
			return;
		
		this.hidden[i] = !visible;

	}
	
	public boolean isVisible(Figure layer) {
		
		int i = getIndex(layer);

		if (i == -1)
			return false;
		
		return !this.hidden[i];

	}
	
	/**
	 * @deprecated
	 * @param layer
	 * @return
	 */
	public boolean isHidden(int layer) {
		try {
			return hidden[layer];
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}
	
	/**
	 * @deprecated
	 * @param layer
	 * @return
	 */
	public void hide(int layer) {
		try {
			hidden[layer] = true;
		} catch (ArrayIndexOutOfBoundsException e) {}
	}
	
	/**
	 * @deprecated
	 * @param layer
	 * @return
	 */
	public void show(int layer) {
		try {
			hidden[layer] = false;
		} catch (ArrayIndexOutOfBoundsException e) {}
	}

	public Figure getFigure(int index) {
		try {
			return figures[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getName(int layer) {
		try {
			return figures[layer].getName();
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public String getName(Figure figure) {
		int i = getIndex(figure);

		if (i == -1)
			return "";
			
		return figures[i].getName();

	}

}
