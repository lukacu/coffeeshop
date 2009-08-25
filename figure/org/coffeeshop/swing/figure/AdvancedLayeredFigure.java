package org.coffeeshop.swing.figure;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

public class AdvancedLayeredFigure extends LayeredFigure {

	private Point2D[] offsets;
	
	private float[] alpha;
	
	public AdvancedLayeredFigure(String name, Figure ... layers) {
		
		super(name, layers);
		
		offsets = new Point2D.Double[layers.length];
		for (int i = 0; i < offsets.length; i++) {
			offsets[i] = new Point2D.Double(0, 0);
		}
		
		
		alpha = new float[layers.length];
		Arrays.fill(alpha, 1);
		
	}
	
	public AdvancedLayeredFigure(Figure ... layers) {
		
		this(null, layers);
		
	}
		
	public int getHeight(FigureObserver observer) {
	
		int height = 0;
		
		for (int i = 0; i < figures.length; i++) {
			
			height = Math.max(height, (int) Math.round(figures[i].getHeight(observer) + offsets[i].getY()));
			
		}

		return height;
	}

	public int getWidth(FigureObserver observer) {
		int width = 0;
		
		for (int i = 0; i < figures.length; i++) {
			width = Math.max(width, (int) Math.round(figures[i].getWidth(observer) + offsets[i].getY()));
			
		}

		return width;
	}

	public void paint(Graphics2D g, Rectangle2D figureSize, Rectangle windowSize,
			FigureObserver observer) {
		
		Rectangle2D transformed = new Rectangle2D.Double();

		for (int i = 0; i < figures.length; i++) {
			if (!hidden[i]) {
				
				if (alpha[i] < 1){
					AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha[i]);		
					
					g.setComposite(composite);
				} else g.setComposite(AlphaComposite.Src);
				
				//g.setTransform(AffineTransform.getTranslateInstance(offsets[i].x, offsets[i].y));
				
				transformed.setRect(figureSize.getX() - offsets[i].getX(), 
						figureSize.getY() - offsets[i].getY(), figureSize.getWidth(), figureSize.getHeight());

				figures[i].paint(g, transformed, windowSize, observer);
				
				
			}
		}

	}

	public void setOffset(Figure layer, double x, double y) {
		
		int i = getIndex(layer);

		if (i == -1)
			return;
		
		offsets[i].setLocation(x, y);
	}
	
	public void setOffset(Figure layer, Point offset) {
		
		setOffset(layer, offset.x, offset.y);

	}
	
	public Point2D getOffset(Figure layer) {
		
		int i = getIndex(layer);

		if (i == -1)
			return null;
		
		return (Point2D) offsets[i].clone();
	}
	
	public void setTransparency(Figure layer, float alpha) {
		
		int i = getIndex(layer);

		if (i == -1)
			return;
		
		this.alpha[i] = Math.min(1, Math.max(0, alpha));

	}
	
	public float getTransparency(Figure layer) {
		
		int i = getIndex(layer);

		if (i == -1)
			return 0;
		
		return this.alpha[i];

	}
	
	public int size() {
		return figures.length;
	}
	
	public boolean isHidden(int layer) {
		try {
			return hidden[layer];
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}
	
	public void hide(int layer) {
		try {
			hidden[layer] = true;
		} catch (ArrayIndexOutOfBoundsException e) {}
	}
	
	public void show(int layer) {
		try {
			hidden[layer] = false;
		} catch (ArrayIndexOutOfBoundsException e) {}
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
	
}
