/* THIS FILE IS A MEMBER OF THE COFFEESHOP LIBRARY
 * 
 * License:
 * 
 * Coffeeshop is a conglomerate of handy general purpose Java classes.  
 * 
 * Copyright (C) 2006-2008 Luka Cehovin
 * This library is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation; either version 2.1 of 
 *  the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 *  GNU Lesser General Public License for more details. 
 *  
 *  http://www.opensource.org/licenses/lgpl-license.php
 *  
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the 
 *  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA 
 * 
 */

package org.coffeeshop.log;

import java.io.PrintStream;

/**
 * Static class that can be used by an application to display various log
 * messages that can be filtered so it is not really necessary to remove the
 * logging commands when releasing the application.
 * 
 * @author Luka Cehovin
 * @since CoffeeShop Core 1.0
 */
public class Logger {

	/**
	 * Default message channel.
	 */
	public static final int DEFAULT = 1;

	/**
	 * Errors can be reported through this channel.
	 */
	public static final int ERROR = 2;

	/**
	 * Warnings can be reported through this channel.
	 */
	public static final int WARNING = 4;

	/**
	 * Coffeeshop toolbox internal debug channel.
	 * <br><b>Note:</b> this channel is used by Coffeeshop toolbox
	 * classes. Do not use it for anything else.
	 */
	public static final int COFFEESHOP = 8;
	
	/**
	 * Internal application debug messages channel 1.
	 */
	public static final int APPLICATION_INTERNAL_1 = 256;

	/**
	 * Internal application debug messages channel 2.
	 */
	public static final int APPLICATION_INTERNAL_2 = 512;

	/**
	 * Internal application debug messages channel 3.
	 */
	public static final int APPLICATION_INTERNAL_3 = 1024;

	/**
	 * Internal application debug messages channel 4.
	 */
	public static final int APPLICATION_INTERNAL_4 = 2048;

	/**
	 * Internal application debug messages channel 5.
	 */
	public static final int APPLICATION_INTERNAL_5 = 4096;

	/**
	 * Internal application debug messages channel 6.
	 */
	public static final int APPLICATION_INTERNAL_6 = 8192;

	/**
	 * Internal application debug messages channel 7.
	 */
	public static final int APPLICATION_INTERNAL_7 = 16384;

	/**
	 * Internal application debug messages channel 8.
	 */
	public static final int APPLICATION_INTERNAL_8 = 32768;

	private PrintStream output = System.err;

	private int mask = 0;

	private LogFormat formatter = null;
	
	/**
	 * Constructs new logger with a default formatter.
	 * 
	 * @see DefaultFormat
	 */
	public Logger() {
		this(new DefaultFormat());
	}
	
	/**
	 * Construct new logger object with a formatter.
	 * 
	 * @param formatter
	 */
	public Logger(LogFormat formatter) {
		if (formatter == null)
			throw new IllegalArgumentException("Null pointer");
		
		this.formatter = formatter;
	}
	
	/**
	 * Changes the log format interface
	 * 
	 * @param formatter a new log format interface
	 */
	public void setLogFormat(LogFormat formatter) {
		if (formatter != null)
			this.formatter = formatter;
	}
	
	/**
	 * Reports a message through a specific channel. In case the channel is
	 * disabled it does nothing.
	 * 
	 * @param channel
	 *            channel to use
	 * @param message
	 *            message to report
	 */
	public synchronized void report(int channel, String message) {
		if (isChannelEnabled(channel))
			output.println(formatter.formatReport(channel, message));
	}

	/**
	 * Reports a message through a specific channel. In case the channel is
	 * disabled it does nothing. Object is transformed into String using its
	 * toString() method
	 * 
	 * @param channel
	 *            channel to use
	 * @param object
	 *            message to report
	 */
	public synchronized void report(int channel, Object object) {
		if (isChannelEnabled(channel))
			output.println(formatter.formatReport(channel, object.toString()));
	}
	
	/**
	 * Reports a message through a specific channel. In case the channel is
	 * disabled it does nothing. Message is assemled from format and objects
	 * using String.format() method
	 * 
	 * @param channel
	 *            channel to use
	 * @param forma
	 *            message to report
	 *            
	 * @see String#format(String, Object[])
	 */
	public synchronized void report(int channel, String format, Object ... objects) {
		if (isChannelEnabled(channel))
			output.println(formatter.formatReport(channel, String.format(format, objects)));
	}
	
	/**
	 * Reports a throwable object through the error channel (if it is enabled).
	 * 
	 * @param throwable
	 *            throwable object
	 */
	public synchronized void report(Throwable throwable) {
		if (isChannelEnabled(ERROR))
			output.println(formatter.formatThrowable(ERROR, throwable));
	}
	
	/**
	 * Sets the output stream.
	 * 
	 * @param out
	 */
	public void setOutputStream(PrintStream out) {
		if (out == null)
			throw new IllegalArgumentException(
					"Disable output with disableAllChannels()");

		output = out;
	}

	/**
	 * Enables a specific channel
	 * 
	 * @param channel
	 *            channel to enable
	 */
	public synchronized void enableChannel(int channel) {
		mask |= channel;
	}

	/**
	 * Disables a specific channel
	 * 
	 * @param channel
	 *            channel to disable
	 */
	public synchronized void disableChannel(int channel) {
		mask &= ~channel;
	}

	/**
	 * Disables output through all channels.
	 * 
	 */
	public synchronized void disableAllChannels() {
		mask = 0;
	}

	/**
	 * Enables output through all channels.
	 * 
	 */
	public synchronized void enableAllChannels() {
		mask = 0xFFFFFFFF;
	}

	/**
	 * Checks weather the channel is enabled.
	 * 
	 * @param channel
	 *            channel to check
	 * @return <code>true</code> if the channel is enabled, <code>false</code>
	 *         otherwise.
	 */
	public boolean isChannelEnabled(int channel) {
		return (mask & channel) != 0;
	}

	/**
	 * Enables output through the specific channels.
	 * 
	 * @param channels an array of chars each representing a channel to open.
	 * 
	 * <ul>
	 * <li><code>d</code> - default channel</li>
	 * <li><code>e</code> - errors channel</li>
	 * <li><code>w</code> - warnings channel</li>
	 * <li><code>1-8</code> - application internal channels no. 1 to 8</li>
	 * </ul>
	 * 
	 */
	public synchronized void enableChannels(char[] channels) {

		if (channels == null)
			return;

		for (int i = 0; i < channels.length; i++) {
			switch (Character.toLowerCase(channels[i])) {
			case 'd': {
				enableChannel(Logger.DEFAULT);
				break;
			}
			case 'e': {
				enableChannel(Logger.ERROR);
				break;
			}
			case 'w': {
				enableChannel(Logger.WARNING);
				break;
			}
			case 'c': {
				enableChannel(Logger.COFFEESHOP);
				break;
			}
			case '1': {
				enableChannel(Logger.APPLICATION_INTERNAL_1);
				break;
			}
			case '2': {
				enableChannel(Logger.APPLICATION_INTERNAL_2);
				break;
			}
			case '3': {
				enableChannel(Logger.APPLICATION_INTERNAL_3);
				break;
			}
			case '4': {
				enableChannel(Logger.APPLICATION_INTERNAL_4);
				break;
			}
			case '5': {
				enableChannel(Logger.APPLICATION_INTERNAL_5);
				break;
			}
			case '6': {
				enableChannel(Logger.APPLICATION_INTERNAL_6);
				break;
			}
			case '7': {
				enableChannel(Logger.APPLICATION_INTERNAL_7);
				break;
			}
			case '8': {
				enableChannel(Logger.APPLICATION_INTERNAL_8);
				break;
			}
			}
		}
	}
}
