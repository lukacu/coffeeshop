package org.coffeeshop.swing.figure;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

public class RectanglesFigure extends VectorFigure {

	private static final Color DEFAULT_COLOR = Color.WHITE;
	
	private ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
	
	private ArrayList<Color> color = new ArrayList<Color>();
	
	private int width, height;
	
	private String name;
	
	public RectanglesFigure(String name, int width, int height) {
		this.width = width;
		this.height = height;
		this.name = name;
	}

	public void add(int x, int y, int width, int height) {
		
		add(new Rectangle(x, y, width, height), null);
		
	}
	
	public void add(int x, int y, int width, int height, Color c) {
		
		add(new Rectangle(x, y, width, height), c);
		
	}
	
	public void add(Rectangle r) {
		
		add(r, null);
		
	}
	
	public void add(Rectangle r, Color c) {
	
		if (r == null)
			return;
		
		if (c == null)
			c = DEFAULT_COLOR;
		
		synchronized (rectangles) {
			rectangles.add(r);
			color.add(c);			
		}
		
	}
	
	@Override
	protected void paintGeometry(Graphics2D g, float scale,
			FigureObserver observer) {
		synchronized (rectangles) {
			
			for (int i = 0; i < rectangles.size(); i++) {
				
				g.setColor(color.get(i));
				g.draw(rectangles.get(i));

			}
		}
	
	}

	public int getHeight(FigureObserver observer) {
		return height;
	}

	public String getName() {
		return name;
	}

	public int getWidth(FigureObserver observer) {
		return width;
	}


}
