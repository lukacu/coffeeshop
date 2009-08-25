package org.coffeeshop.swing.figure;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class GridFigure implements Figure {

	private AffineTransform imageToScreen = new AffineTransform();
	
	public int density = 20;
	
	public Color color = new Color(100, 100, 100, 100);
	
	public GridFigure() {

	}

	public final void paint(Graphics2D g, Rectangle2D figureSize,
			Rectangle windowSize, FigureObserver observer) {

		g.setClip(windowSize.x, windowSize.y, windowSize.width,
				windowSize.height);

		float scale = (float) windowSize.width
				/ (float) figureSize.getWidth();

		float offsetX = (float) figureSize.getX() * scale
				- (float) windowSize.x;
		float offsetY = (float) figureSize.getY() * scale
				- (float) windowSize.y;

		g.setStroke(new BasicStroke(1 / scale));
		
		imageToScreen.setTransform(scale, 0, 0, scale, -offsetX,
				-offsetY);
		
		AffineTransform oldT = g.getTransform();
		AffineTransform newT = new AffineTransform(oldT);
		//newT.concatenate(imageToScreen);
		g.setTransform(newT);
		
		/*AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
		g.setComposite(composite);*/
		//g.setColor(new Color(100, 100, 100, 100));
		g.setColor(Color.GRAY);
		//double precision = Math.floor(Math.log10(scale));
		
		//System.out.println(precision);
		
		int startX = windowSize.x + density - windowSize.x % density;
		int startY = windowSize.y + density - windowSize.y % density;
		
		for (int i = startX; i < windowSize.x + windowSize.width; i+=density) {
			
			g.drawLine(i, windowSize.y, i, windowSize.y + windowSize.height);
			
		}
		
		for (int i = startY; i < windowSize.y + windowSize.height; i+=density) {
			
			g.drawLine(windowSize.x, i, windowSize.x + windowSize.width, i);
			
		}
		
		
		g.setTransform(oldT);
		
	}

	public int getHeight(FigureObserver observer) {
		return -1;
	}

	public String getName() {
		return "Grid";
	}

	public int getWidth(FigureObserver observer) {
		return -1;
	}

}
