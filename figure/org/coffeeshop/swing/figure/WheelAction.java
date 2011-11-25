package org.coffeeshop.swing.figure;

import java.awt.Point;
import java.util.EventListener;

public interface WheelAction extends EventListener {

	public void onWheel(FigurePanel source, Point position, Point vector, int modifiers);
	
}
