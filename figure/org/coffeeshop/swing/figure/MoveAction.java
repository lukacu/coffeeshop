package org.coffeeshop.swing.figure;

import java.awt.Cursor;
import java.awt.Point;
import java.util.EventListener;

public interface MoveAction extends EventListener {

	public Cursor onMove(FigurePanel source, Point from, Point to, int modifiers);
	
}
