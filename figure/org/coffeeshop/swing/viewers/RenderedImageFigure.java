package org.coffeeshop.swing.viewers;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.coffeeshop.swing.figure.Figure;
import org.coffeeshop.swing.figure.FigureObserver;

public class RenderedImageFigure implements Figure, Viewable {

	private AffineTransform imageToScreen = new AffineTransform();
	
	private RenderedImage image;
	
	private String name;
	
	public RenderedImageFigure(RenderedImage image) {
		
		this(image, "Image Figure");
		
	}
	
	public RenderedImageFigure(RenderedImage image, String name) {
		
		if (image == null)
			throw new IllegalArgumentException("Null argument");
		
		this.image = image;
		this.name = name;
		
	}
	
	public RenderedImageFigure(File file) throws IOException {
		
		BufferedImage image = ImageIO.read(file);
		
		if (image == null)
			throw new IllegalArgumentException("Null argument");
		
		this.image = image;
		this.name = file.getName();
	}

	public RenderedImage getImage() {
		return image;
	}
	
	public int getHeight(FigureObserver observer) {
		return image.getHeight();
	}

	public int getWidth(FigureObserver observer) {
		return image.getWidth();
	}

	public void paint(Graphics2D g, Rectangle2D figureSize, Rectangle windowSize, FigureObserver observer) {

		g.setClip(windowSize.x, windowSize.y, windowSize.width,
				windowSize.height);

		float scale = (float) windowSize.width
				/ (float) figureSize.getWidth();

		float offsetX = (float) figureSize.getX() * scale
				- (float) windowSize.x;
		float offsetY = (float) figureSize.getY() * scale
				- (float) windowSize.y;

		imageToScreen.setTransform(scale, 0, 0, scale, -offsetX,
				-offsetY);
		//AffineTransform old = g.getTransform();
		//old.concatenate(imageToScreen);
		//g.setTransform(old);
		
		g.drawRenderedImage(image, imageToScreen);
		
	}

	public String getName() {
		return name;
	}

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
