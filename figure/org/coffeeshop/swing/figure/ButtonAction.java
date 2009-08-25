package org.coffeeshop.swing.figure;

import java.awt.Point;
import java.util.EventListener;

public interface ButtonAction extends EventListener {

	public Object onClick(FigurePanel source, Point position, int clicks, int modifiers);
	
	public void onPress(FigurePanel source, Point position, int modifiers);
	
	public void onRelease(FigurePanel source, Point position, int modifiers);
	
}
