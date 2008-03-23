package org.coffeeshop.animation;

import java.util.ArrayList;


/**
 * AnimationQueue represents a core animation thread. Only one instance of this class can
 * exist in an application and it is created on demand.
 * 
 * @author lukacu
 *
 */
public abstract class AnimationQueue implements Executor {

	protected int fps = 10;
	
	private ArrayList<Animation> activeAnimations = new ArrayList<Animation>();
	
	private ArrayList<Animation> pendingAnimations = new ArrayList<Animation>();
	
	private ArrayList<Animation> remove = new ArrayList<Animation>();

	public AnimationQueue(int fps) {
		
		this.fps = fps;
		
	}
	
	protected void doFrame() {
		
		for (Animation a : activeAnimations) {
			
			if (!a.frame())
				remove.add(a);
		}
		
		activeAnimations.removeAll(remove);
		remove.clear();
	
		if (!pendingAnimations.isEmpty()) {
			synchronized (pendingAnimations) {
				
				activeAnimations.addAll(pendingAnimations);
				pendingAnimations.clear();
				
			}
			
			
		}
	}
	
	public void addAnimation(Animation a) {
		
		synchronized (pendingAnimations) {
			
			pendingAnimations.add(a);
			
		}
		
	}
	
}
