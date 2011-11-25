package org.coffeeshop.swing.figure;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public interface Figure {

	public int getWidth(FigureObserver observer);
	
	public int getHeight(FigureObserver observer);
	
	public void paint(Graphics2D g, Rectangle2D figureSize, Rectangle windowSize, FigureObserver observer);
	
	public String getName();
	
}
