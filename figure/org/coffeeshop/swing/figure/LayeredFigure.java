package org.coffeeshop.swing.figure;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Vector;

import org.coffeeshop.swing.viewers.LayeredFigureViewer;
import org.coffeeshop.swing.viewers.Viewable;

public class LayeredFigure extends AbstractFigure implements Viewable {
	
	protected Vector<Figure> figures = new Vector<Figure>();
	
	protected Vector<Boolean> hidden = new Vector<Boolean>();
	
	protected String name;
	
	public LayeredFigure(String name, Figure ... layers) {

		for (Figure figure : layers) {
			if (figure != null)
				add(figure);
		}
		
		this.name = name;
		
		if (name == null)
			this.name = "Layered Figure (" + figures.size() + " layers)";
		
	}
	
	public void add(Figure figure) {
		figures.add(figure);
		hidden.add(false);
		
	}
	
	public LayeredFigure(Figure ... layers) {
		
		this(null, layers);
		
	}
	
	protected int getIndex(Figure figure) {
		
		for (int i = 0; i < figures.size(); i++)
			if (figures.get(i) == figure)
				return i;
		
			return -1;
			
	}
	
	protected Vector<Figure> flatten(Figure[] figures) {
				
		Vector<Figure> result = new Vector<Figure>();
		
		for (int i = 0; i < figures.length; i++) {
			Figure figure = figures[i];
			if (figure == null)
				continue;
			
			if (figure instanceof LayeredFigure) {
				
				for (Figure f : ((LayeredFigure) figure).figures) 
					result.add(f);
				
			} else result.add(figure);

		}

		return result;
		
	}
	
	public int getHeight(FigureObserver observer) {
	
		int height = 0;
		
		for (Figure figure : figures) {
			
			height = Math.max(height, figure.getHeight(observer));
			
		}

		return height;
	}

	public int getWidth(FigureObserver observer) {
		int width = 0;
		
		for (Figure figure : figures) {
			width = Math.max(width, figure.getWidth(observer));
			
		}

		return width;
	}

	public void paint(Graphics2D g, Rectangle2D figureSize, Rectangle windowSize,
			FigureObserver observer) {

		for (int i = 0; i < figures.size(); i++) {
			if (!hidden.get(i))
				figures.get(i).paint(g, figureSize, windowSize, observer);	
		}

	}

	public int size() {
		return figures.size();
	}
	
	public void setVisible(Figure layer, boolean visible) {
		
		int i = getIndex(layer);

		setVisible(i, visible);

	}
	
	public void setVisible(int i, boolean visible) {
		
		if (i == -1)
			return;
		
		this.hidden.set(i, !visible);

	}
	
	public boolean isVisible(Figure layer) {
		
		int i = getIndex(layer);

		return isVisible(i);

	}
	
	public boolean isVisible(int i) {
		
		if (i == -1)
			return false;
		
		return !this.hidden.get(i);

	}
	
	public Figure getFigure(int i) {
		return figures.get(i);
	}
	
	
	public String getName() {
		return name;
	}
	
	public String getName(int layer) {
		try {
			return figures.get(layer).getName();
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public String getName(Figure figure) {
		int i = getIndex(figure);

		if (i == -1)
			return "";
			
		return figures.get(i).getName();

	}
	
	public boolean view(Map<String, String> parameters) {

		String title = parameters.get("title");
		if (title == null) {
			title = getName();
		} 
		
		LayeredFigureViewer viewer = new LayeredFigureViewer(title, this);
		viewer.setVisible(true);	
		
		return true;
	}
	
	
}
