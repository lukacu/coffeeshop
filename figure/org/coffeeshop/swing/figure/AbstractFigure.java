package org.coffeeshop.swing.figure;

import java.util.Vector;

public abstract class AbstractFigure implements Figure {

	private Vector<FigureListener> listeners = new Vector<FigureListener>();
	
	@Override
	public void addFigureListener(FigureListener listener) {
		synchronized (listeners) {
			if (listener != null)
				listeners.add(listener);	
		}
	}
	
	@Override
	public void removeFigureListener(FigureListener listener) {
		synchronized (listeners) {
			if (listener != null)
				listeners.remove(listener);	
		}
	}
	
	protected void notifyOnUpdate() {
		
		synchronized (listeners) {
			
			for (FigureListener listener : listeners) {
				
				listener.figureUpdated(this);
				
			}
			
		}
		
		
	}
	
}
