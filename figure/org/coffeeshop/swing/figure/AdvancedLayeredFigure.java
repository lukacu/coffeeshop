package org.coffeeshop.swing.figure;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import org.coffeeshop.swing.viewers.Viewable;

public class AdvancedLayeredFigure extends LayeredFigure implements Viewable {

	protected Vector<Float> alpha = new Vector<Float>();
	
	protected Vector<Point2D> offsets = new Vector<Point2D>(); 
	
	public AdvancedLayeredFigure(String name, Figure ... layers) {
		
		super(name, layers);

	}

	@Override
	public void add(Figure figure) {
		if (figure == null)
			throw new IllegalArgumentException("Figure must not be null");
		
		super.add(figure);
		
		alpha.add(1.0f);
		offsets.add(new Point2D.Double(0, 0));
	}
	
	public AdvancedLayeredFigure(Figure ... layers) {
		
		this(null, layers);
		
	}
		
	public int getHeight(FigureObserver observer) {
	
		int height = 0;
		
		for (int i = 0; i < figures.size(); i++) {
			
			height = Math.max(height, (int) Math.round(figures.get(i).getHeight(observer) + offsets.get(i).getY()));
			
		}

		return height;
	}

	public int getWidth(FigureObserver observer) {
		int width = 0;
		
		for (int i = 0; i < figures.size(); i++) {
			width = Math.max(width, (int) Math.round(figures.get(i).getWidth(observer) + offsets.get(i).getX()));
			
		}

		return width;
	}

	public void paint(Graphics2D g, Rectangle2D figureSize, Rectangle windowSize,
			FigureObserver observer) {
		
		Rectangle2D transformed = new Rectangle2D.Double();

		for (int i = 0; i < figures.size(); i++) {
			if (!hidden.get(i)) {
				
				if (alpha.get(i) < 1){
					AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha.get(i));		
					
					g.setComposite(composite);
				} else g.setComposite(AlphaComposite.Src);
				
				//g.setTransform(AffineTransform.getTranslateInstance(offsets[i].x, offsets[i].y));
				
				transformed.setRect(figureSize.getX() - offsets.get(i).getX(), 
						figureSize.getY() - offsets.get(i).getY(), figureSize.getWidth(), figureSize.getHeight());

				figures.get(i).paint(g, transformed, windowSize, observer);
				
				
			}
		}

	}

	public void setOffset(Figure layer, double x, double y) {
		
		int i = getIndex(layer);

		if (i == -1)
			return;
		
		offsets.get(i).setLocation(x, y);
	}
	
	public void setOffset(Figure layer, Point offset) {
		
		setOffset(layer, offset.x, offset.y);

	}
	
	public Point2D getOffset(Figure layer) {
		
		int i = getIndex(layer);

		if (i == -1)
			return null;
		
		return (Point2D) offsets.get(i).clone();
	}
	
	public void setTransparency(Figure layer, float alpha) {
		
		int i = getIndex(layer);

		if (i == -1)
			return;
		
		this.alpha.set(i, Math.min(1, Math.max(0, alpha)));

	}
	
	public float getTransparency(Figure layer) {
		
		int i = getIndex(layer);

		if (i == -1)
			return 0;
		
		return this.alpha.get(i);

	}

	public boolean remove(Figure layer) {

		int i = getIndex(layer);

		if (i == -1)
			return false;
		
		super.remove(layer);
		
		alpha.remove(i);
		offsets.remove(i);
		
		return true;
		

	}
	
}
