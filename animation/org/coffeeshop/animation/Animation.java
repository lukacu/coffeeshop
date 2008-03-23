package org.coffeeshop.animation;


/**
 * Animation class is a base class for all frame-based time changes that are 
 * processes using Executor. The sublasses of this class are generaly usable
 * only one time (for one change sequence) although one can also make them reusable 
 * with a bit more care. 
 * 
 * @author lukacu
 * @see AnimationQueue
 * @see Executor
 */
public abstract class Animation {

	protected boolean executing = false;
	
	/**
	 * If the animation object is not yet executing the method first calls onStart() method
	 * and then inserts animation into the animation queue provided by the getAnimationQueue()
	 * method. The status of the animation is changed to executing.
	 *
	 * @see Animation#getExecutor()
	 * @see Animation#onStart()
	 */
	public synchronized void execute() {
		
		execute(getExecutor());
		
	}
	
	/**
	 * If the animation object is not yet executing the method first calls onStart() method
	 * and then inserts animation into the animation queue provided by the argument. The
	 * status of the animation is changed to executing. This method is meant for combining
	 * multiple animations together. For normal usage use execute().
	 *
	 * @see Animation#onStart()
	 */
	protected synchronized void execute(Executor executor) {
		if (executing)
			return;
		
		onStart();
		
		if (executor != null)
			executor.addAnimation(this);
		
		executing = true;
	}
	
	/**
	 * Prematurely terminate execution of the animation object. Not that there is a chance
	 * that one more frame is executed even after calling this method.
	 * 
	 */
	public synchronized void terminate() {
		executing = false;
	}
	
	/**
	 * Tells if the animation object is currently executing.
	 * 
	 * @return <code>true</code> if the animation is executing, <code>false</code> otherwise.
	 */
	public synchronized boolean isExecuting() {
		return executing;
	}
	
	/**
	 * Does a next frame by calling the onFrame() method. This method is called by
	 * AnimationQueue to process a new frame.
	 * 
	 * @return <code>true</code> if there are more changes to come, <code>false</code> if
	 * the changes sequence has ended or the animation is not executing.
	 * @see Animation#onFrame()
	 */
	public boolean frame() {
		
		if (!executing) 
			return false;
		
		executing = onFrame();
		
		return executing;
	}
	
	/**
	 * Animation subclasses implement this method to perform frame-based changes.
	 * 
	 * @return <code>true</code> if there are more changes to come, <code>false</code> if
	 * the changes sequence has ended. Not that frame() method will return <code>false</code>
	 * all the time after onFrame() will return <code>false</code> for the first time. After that
	 * the method will not be called again by frame() method unless execute is called again.
	 */
	protected abstract boolean onFrame();
	
	/**
	 * Animation subclasses implement this method to perform initalization before the object is
	 * inserted to the animation queue.
	 *
	 */
	protected abstract void onStart();
	
	/**
	 * Animation sublasses may override this method to tell which queue would they like to
	 * be executed in. The default implementation returns default animation queue.
	 * 
	 * @return the prefered Executor object. This method should not return <code>null</code>.
	 */
	protected Executor getExecutor() {
		return DefaultAnimationQueue.getDefaultAnimationQueue();
	}
	
}
