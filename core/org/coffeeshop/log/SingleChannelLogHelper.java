package org.coffeeshop.log;

public class SingleChannelLogHelper implements LogHelper {

	private AbstractLogger logger = null;
	
	private int channel = 0;

	public AbstractLogger getLogger() {
		return logger;
	}

	public void setLogger(AbstractLogger logger) {
		this.logger = logger;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}
	
	/**
	 * Reports a throwable object through the error channel (if it is enabled).
	 * 
	 * @param throwable
	 *            throwable object
	 */
	public synchronized void report(Throwable throwable) {
		if (logger != null)
			logger.report(throwable);
	}

	/**
	 * Reports a message through the default channel (if it is enabled).
	 * 
	 * @param message
	 *            message string
	 */
	public synchronized void report(String message) {
		if (logger != null)
			logger.report(channel, message);
	}
	
	/**
	 * Reports a message through a default channel. In case the channel is
	 * disabled it does nothing. Message is assembled from format and objects
	 * using {@link String#format(String, Object[])} method
	 * 
	 * @param format
	 *            message to report
	 * @param objects 
	 * 			objects to use
	 * 
	 * @see String#format(String, Object[])
	 */
	public synchronized void report(String format,
			Object... objects) {
		if (logger != null)
			logger.report(channel, format, objects);
	}
	
	
	public synchronized void report(Object object) {
		if (logger != null)
			logger.report(channel, object);
	}
}
