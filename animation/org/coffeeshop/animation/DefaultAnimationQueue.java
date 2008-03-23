package org.coffeeshop.animation;


public class DefaultAnimationQueue extends AnimationQueue {

	private static AnimationQueue queue = null;
	
	/**
	 * This static method returns the only instance of AnimationQueue class. If needed
	 * an instance is created.
	 * 
	 * @return The only instance of AnimationQueue class.
	 */
	public static AnimationQueue getDefaultAnimationQueue() {
		
		if (queue == null) {
			
			queue = new DefaultAnimationQueue(20);
			
		}
		
		return queue;
		
	}
	
	
	private AnimationThread thread = null;
	
	private long frameLength;
	
	private class AnimationThread extends Thread {
		
		public void run() {
			
			frameLength = 1000L / fps;
			
			while (true) {
			
				long startFrame = System.currentTimeMillis();
				
				doFrame();
				
				long sleepTime = frameLength - (System.currentTimeMillis() - startFrame);
				
				if (sleepTime > 0) {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
			
		}
		
	}
	
	private DefaultAnimationQueue(int fps) {
		
		super(fps);
		
		thread = new AnimationThread();
		
		thread.start();
		
	}
	
}
