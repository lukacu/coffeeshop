package org.coffeeshop.swing.figure;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.coffeeshop.swing.viewers.FigureViewer;
import org.coffeeshop.swing.viewers.Viewable;

public class ImageFigure extends AbstractFigure implements Viewable {

	protected Image image;

	protected String name;

	private class ImageFigureObserver implements ImageObserver {

		FigureObserver observer;

		public ImageFigureObserver(FigureObserver observer) {
			this.observer = observer;
		}

		public boolean imageUpdate(Image img, int infoflags, int x, int y,
				int width, int height) {
			if (observer != null)
				observer.figureUpdated(ImageFigure.this);

			return false;
		}

	}

	public ImageFigure(Image image) {

		this(image, "Image Figure");

	}

	public ImageFigure(Image image, String name) {

		if (image == null)
			throw new IllegalArgumentException("Null argument");

		this.image = image;
		this.name = name;
	}

	public ImageFigure(File file) throws IOException {

		Image image = ImageIO.read(file);

		if (image == null)
			throw new IllegalArgumentException("Null argument");

		this.image = image;
		this.name = file.getName();
	}

	public Image getImage() {
		return image;
	}

	public int getHeight(FigureObserver observer) {
		return image.getHeight(new ImageFigureObserver(observer));
	}

	public int getWidth(FigureObserver observer) {
		return image.getWidth(new ImageFigureObserver(observer));
	}

	public void paint(Graphics2D g, Rectangle2D figureSize,
			Rectangle windowSize, FigureObserver observer) {

		g.drawImage(image, windowSize.x, windowSize.y, windowSize.x
				+ windowSize.width, windowSize.y + windowSize.height,
				(int) figureSize.getMinX(), (int) figureSize.getMinY(),
				(int) figureSize.getMaxX(), (int) figureSize.getMaxY(),
				new ImageFigureObserver(observer));

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
