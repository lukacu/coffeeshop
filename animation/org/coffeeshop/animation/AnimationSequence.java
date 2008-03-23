package org.coffeeshop.animation;

import java.util.Vector;


public class AnimationSequence extends Animation implements Executor {

	private Vector<Animation> sequence = new Vector<Animation>();
	
	private int current = 0;
	
	public AnimationSequence() {
		
	}
	
	public boolean frame() {
		
		if (!executing) 
			return false;
		
		executing = onFrame();
		
		return executing;
	}
	
	@Override
	protected boolean onFrame() {

		if (current >= sequence.size())
			return false;
		
		Animation a = sequence.elementAt(current);
		
		if (!a.frame()) {
			
			current++;
			
			if (current >= sequence.size())
				return false;
			
			Animation b = sequence.elementAt(current);
			
			b.execute(null);
			
			if (a.getExecutor() == b.getExecutor())
				return true;
			
			executing = false;
			
			
			
		}
		
		
		return true;
	}

	@Override
	protected void onStart() {
		current = 0;
	}

	public void addAnimation(Animation a) {

		if (a == null || a.isExecuting())
			return;

		synchronized (sequence) {			
			sequence.add(a);
		}
	}

	protected Executor getExecutor() {
		synchronized (sequence) {
			if (current >= sequence.size())
				return null;
			
			return sequence.elementAt(current).getExecutor();
		}
		
	}
}
