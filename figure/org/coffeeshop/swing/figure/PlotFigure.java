package org.coffeeshop.swing.figure;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import org.coffeeshop.swing.figure.FigureObserver;
import org.coffeeshop.swing.figure.VectorFigure;

public class PlotFigure extends VectorFigure {

	public static interface PlotObject {
		
		public String getName();
		
		public void paint(Graphics2D g, float scale, AffineTransform pretransform);
		
		public Rectangle2D getBounds();
		
	}
	
	private double customWidth = 0, customHeight = 0;
	
	protected Rectangle2D bounds = new Rectangle2D.Float();
	
	private Vector<PlotObject> objects = new Vector<PlotFigure.PlotObject>(); 
	
	private String name, title;

	private Color borderColor = Color.GRAY;
	
	public PlotFigure() {
		this(null);
		name = "Plot " + Integer.toHexString(hashCode());
	}
	
	public PlotFigure(String name) {
		this.name = name;
	}
	
	public void addObject(PlotObject object) {
		
		if (object == null) return;
		
		if (!objects.contains(object))
			objects.add(object);
		
		recalculateBounds();

	}
	
	public void removeObject(PlotObject object) {
		
		if (object == null) return;
		
		if (objects.remove(object)) {
			
			recalculateBounds();
			
		}

	}	
	
	public boolean containsObject(PlotObject object) {
		
		return objects.contains(object);
		
	}
	
	@Override
	public int getWidth(FigureObserver observer) {
		if (customWidth == 0)
			return (int) bounds.getWidth();
		else return (int) Math.abs(customWidth);
	}

	@Override
	public int getHeight(FigureObserver observer) {
		if (customHeight == 0)
			return (int) bounds.getHeight();
		else return (int) Math.abs(customHeight);
	}

	public double getOriginalWidth() {
		return bounds.getWidth();
	}
	
	public double getOriginalHeight() {
		return bounds.getHeight();
	}
	
	public double getCustomWidth() {
		return customWidth;
	}

	public void setCustomWidth(double customWidth) {
		this.customWidth = customWidth;
	}

	public double getCustomHeight() {
		return customHeight;
	}

	public void setCustomHeight(double customHeight) {
		this.customHeight = customHeight;
	}

	@Override
	public String getName() {

		return name;
		
	}

	private AffineTransform plotToFigure = new AffineTransform();

	@Override
	protected void paintGeometry(Graphics2D g, float scale,
			FigureObserver observer) {
	
		if (bounds.isEmpty()) return;
		
		double scaleX = (customWidth == 0) ? 1 : customWidth / bounds.getWidth();
		double scaleY = (customHeight == 0) ? 1 : customHeight / bounds.getHeight();
		
		double offsetX = bounds.getX();
		double offsetY = bounds.getY() - customHeight;
	
		plotToFigure.setTransform(scaleX, 0, 0, -scaleY, -offsetX,
				-offsetY);
		
		if (borderColor != null) {
			g.setColor(borderColor);
			
			g.draw(plotToFigure.createTransformedShape(bounds));
		}
		
		for (PlotObject object : objects) {
			
			object.paint(g, scale, plotToFigure);
			
		}
		
		if (title != null) {
			g.setColor(Color.GRAY);
			int height = g.getFontMetrics().getHeight();
			
			g.drawString(title, 10, height + 2);
		}
		
	}
	
	protected void recalculateBounds() {
		
		bounds = new Rectangle2D.Double();
		
		for (PlotObject object : objects) {
			
			Rectangle2D.union(bounds, object.getBounds(), bounds);
			
		}
	}
	
	public boolean isEmpty() {
		return objects.isEmpty();
	}
	
	public int size() {
		return objects.size();
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Color getBorderColor() {
		return borderColor;
	}
	
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}
	
}
