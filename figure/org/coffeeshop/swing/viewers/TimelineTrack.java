package org.coffeeshop.swing.viewers;

import java.awt.Color;
import java.awt.geom.Point2D;

import org.coffeeshop.swing.figure.PlotObject;

public interface TimelineTrack {

	public static interface TimelineMarker {
		
		public static enum MarkerShape {CIRCLE, RECTANGLE, DIAMOND, TRIANGLE};
		
		public String getName();
		
		public Color getColor();
		
	}
	
	public void addObject(PlotObject object);
	
	public void removeObject(PlotObject object);	
	
	public String getName();
	
	public String getToolTip(Point2D point);
	
}
