package org.coffeeshop.swing.figure;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

public class PathFigure extends VectorFigure {

	private String name;

	private GeneralPath path = new GeneralPath();

	private int width = 0, height = 0;

	private Color color;
	
	public PathFigure(String name, Color color) {
		this.name = name;
		this.color = color;
	}

	public void clear() {
		synchronized (path) {
			path.reset();
			width = 0;
			height = 0;
		}
	}

	public void appendPoint(float x, float y) {
		synchronized (path) {
			if (path.getCurrentPoint() == null)
				path.moveTo(x, y);
			else
				path.lineTo(x, y);
			width = Math.max(width, (int) Math.round(Math.ceil(x)));
			height = Math.max(height, (int) Math.round(Math.ceil(y)));
		}
	}

	@Override
	protected void paintGeometry(Graphics2D g, float scale,
			FigureObserver observer) {
		g.setStroke(new BasicStroke(1 / scale));
		g.setColor(color);
		synchronized (path) {
			g.draw(path);
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
