package org.coffeeshop.swing.figure;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class VectorObjectsFigure extends VectorFigure {

	private static final Color DEFAULT_COLOR = Color.WHITE;
	
	private static abstract class VectorObject {
		
		protected Color color;
		
		public VectorObject(Color c) {
			this.color = c;
		}
		
		public abstract void paint(Graphics2D g);
	}
	
	private static class ArrowObject extends VectorObject {
		
		private Point begin, end;
		
		public ArrowObject(Point begin, Point end, Color c) {
			super(c);
			this.begin = new Point(begin);
			this.end = new Point(end);
		}
		
		@Override
		public void paint(Graphics2D g) {
			
			g.setColor(color);
			
			g.drawLine(begin.x, begin.y, end.x, end.y);
			
		}
		
	}

	private static class CircleObject extends VectorObject {
		
		private Ellipse2D.Float shape;
		
		public CircleObject(Point2D.Float center, float radius, Color c) {
			super(c);
			
			shape = new Ellipse2D.Float(center.x - radius, center.y - radius, center.x + radius,
					center.y + radius);

		}
		
		@Override
		public void paint(Graphics2D g) {
			
			g.setColor(color);
			
			g.draw(shape);
			
		}
		
	}
	
	private static class RectangleObject extends VectorObject {
		
		private Point p1, p2, p3, p4;
		
		public RectangleObject(Point p1, Point p2, Color c) {
			super(c);
			this.p1 = new Point(p1);
			this.p2 = new Point(p1.x, p2.y);
			this.p3 = new Point(p2);
			this.p4 = new Point(p2.x, p1.y);
		}

		public RectangleObject(Point p1, Point p2, Point p3, Point p4, Color c) {
			super(c);
			this.p1 = new Point(p1);
			this.p2 = new Point(p2);
			this.p3 = new Point(p3);
			this.p4 = new Point(p4);
		}
		
		@Override
		public void paint(Graphics2D g) {
			
			g.setColor(color);
			
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
			g.drawLine(p2.x, p2.y, p3.x, p3.y);
			g.drawLine(p3.x, p3.y, p4.x, p4.y);
			g.drawLine(p4.x, p4.y, p1.x, p1.y);
			
		}
		
	}
	
	private ArrayList<VectorObject> objects = new ArrayList<VectorObject>();
	
	private int width, height;
	
	private String name;
	
	public VectorObjectsFigure(String name, int width, int height) {
		this.width = width;
		this.height = height;
		this.name = name;
	}

	
	public void addArrow(Point begin, Point end, Color c) {
	
		if (begin == null || end == null)
			return;
		
		if (c == null)
			c = DEFAULT_COLOR;
		
		synchronized (objects) {
			objects.add(new ArrowObject(begin, end, c));
		}
	}
	
	public void addRectangle(Point p1, Point p2, Color c) {
		
		if (p1 == null || p2 == null)
			return;
		
		if (c == null)
			c = DEFAULT_COLOR;
		
		synchronized (objects) {
			objects.add(new RectangleObject(p1, p2, c));
		}
	}
	
	public void addCircle(float cx, float cy, float radius, Color c) {
		
		if (radius < 0)
			return;
		
		if (c == null)
			c = DEFAULT_COLOR;
		
		synchronized (objects) {
			objects.add(new CircleObject(new Point2D.Float(cx, cy), radius, c));
		}
	}
	
	@Override
	protected void paintGeometry(Graphics2D g, float scale,
			FigureObserver observer) {
		
		g.setStroke(new BasicStroke(1/scale));
		
		synchronized (objects) {

			for (int i = 0; i < objects.size(); i++) {
				objects.get(i).paint(g);
			}
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
