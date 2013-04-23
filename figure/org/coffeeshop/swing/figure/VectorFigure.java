package org.coffeeshop.swing.figure;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.coffeeshop.swing.viewers.FigureViewer;
import org.coffeeshop.swing.viewers.Viewable;

public abstract class VectorFigure extends AbstractFigure implements Viewable {

	private AffineTransform imageToScreen = new AffineTransform();

	public final void paint(Graphics2D g, Rectangle2D figureSize,
			Rectangle windowSize, FigureObserver observer) {

		g.setClip(windowSize.x, windowSize.y, windowSize.width,
				windowSize.height);

		float scaleX = (float) windowSize.width
				/ (float) figureSize.getWidth();

		float scaleY = (float) windowSize.height
				/ (float) figureSize.getHeight();
		
		float offsetX = (float) figureSize.getX() * scaleX
				- (float) windowSize.x;
		float offsetY = (float) figureSize.getY() * scaleY
				- (float) windowSize.y;

		imageToScreen.setTransform(scaleX, 0, 0, scaleY, -offsetX,
				-offsetY);
		
		AffineTransform oldT = g.getTransform();
		AffineTransform newT = new AffineTransform(oldT);
		newT.concatenate(imageToScreen);
		g.setTransform(newT);
		
		paintGeometry(g, (scaleX + scaleY) / 2, observer);
		
		g.setTransform(oldT);
		
	}
	
	protected abstract void paintGeometry(Graphics2D g, float scale, FigureObserver observer);
	
	public boolean view(Map<String, String> parameters) {
		String title = parameters.get("title");
		if (title == null) {
			title = getName();
		} 
		
		FigureViewer viewer = new FigureViewer(title, this);
		viewer.setVisible(true);	
		
		return true;
	}
}
