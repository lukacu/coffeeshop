package org.coffeeshop.swing.figure;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface PlotObject {
	
	public String getName();
	
	public void paint(Graphics2D g, float scale, AffineTransform pretransform);
	
	public Rectangle2D getBounds();
	
	public String getToolTip(Point2D point);
	
}