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
	 * Coffeeshop toolbox internal debug channel. <br>
	 * <b>Note:</b> this channel is used by Coffeeshop toolbox classes. Do not
	 * use it for anything else.
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

	private PrintStream[] outputs;

	private int[] masks;

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

		this.masks = new int[] { 0 };
		this.outputs = new PrintStream[] { System.err };
	}

	/**
	 * Changes the log format interface
	 * 
	 * @param formatter
	 *            a new log format interface
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
			print(formatter.formatReport(channel, message), channel);
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
			print(formatter.formatReport(channel, object.toString()), channel);
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
	public synchronized void report(int channel, String format,
			Object... objects) {
		if (isChannelEnabled(channel))
			print((formatter.formatReport(channel, String.format(format,
					objects))), channel);
	}

	/**
	 * Reports a throwable object through the error channel (if it is enabled).
	 * 
	 * @param throwable
	 *            throwable object
	 */
	public synchronized void report(Throwable throwable) {
		if (isChannelEnabled(ERROR))
			print(formatter.formatThrowable(ERROR, throwable), ERROR);
	}

	/**
	 * Reports a throwable object through the error channel (if it is enabled).
	 * 
	 * @param throwable
	 *            throwable object
	 */
	public synchronized void report(int channel, Throwable throwable) {
		if (isChannelEnabled(channel))
			print(formatter.formatThrowable(channel, throwable), channel);
	}

	/**
	 * Sets the output stream.
	 * 
	 * @param out
	 *            the output stream
	 * @deprecated use {@link Logger#addOutputStream(PrintStream, int)} from now
	 *             on
	 */
	public void setOutputStream(PrintStream out) {
		if (out == null)
			throw new IllegalArgumentException(
					"Disable output with Logger.disableAllChannels()");

		addOutputStream(out, 0);
	}
	/**
	 * Adds a stream to logger with all channels disabled
	 * 
	 * @param stream the stream
	 */
	public synchronized void addOutputStream(PrintStream stream) {
		addOutputStream(stream, 0);
	}

	/**
	 * Adds a stream to logger
	 * 
	 * @param stream the stream
	 * @param channels channels mask
	 */
	public synchronized void addOutputStream(PrintStream stream, int channels) {

		if (stream == null)
			return;

		boolean found = false;
		for (int i = 0; i < outputs.length; i++) {

			if (outputs[i].equals(stream))
				found = true;
		}

		if (found)
			return;

		PrintStream[] tmpoutputs = new PrintStream[outputs.length + 1];
		int[] tmpmasks = new int[masks.length + 1];

		System.arraycopy(outputs, 0, tmpoutputs, 0, outputs.length);
		System.arraycopy(masks, 0, tmpmasks, 0, masks.length);

		tmpoutputs[outputs.length] = stream;
		tmpmasks[masks.length] = channels;

		outputs = tmpoutputs;
		masks = tmpmasks;

	}

	/**
	 * Removes the stream that was so far subscribed to display logger events 
	 * 
	 * @param stream stream to remove
	 */
	public synchronized void removeOutputStream(PrintStream stream) {

		if (stream == null)
			return;

		int index = -1;
		for (int i = 0; i < outputs.length; i++) {

			if (outputs[i].equals(stream))
				index = i;
		}

		if (index == -1)
			return;

		PrintStream[] tmpoutputs = new PrintStream[outputs.length - 1];
		int[] tmpmasks = new int[masks.length - 1];

		if (index != 0) {
			System.arraycopy(outputs, 0, tmpoutputs, 0, index);
			System.arraycopy(masks, 0, tmpmasks, 0, index);
		}

		if (index != outputs.length - 1) {
			System.arraycopy(outputs, index+1, tmpoutputs, index, outputs.length - index - 1);
			System.arraycopy(masks, index+1, tmpmasks, index, masks.length - index - 1);
		}

		outputs = tmpoutputs;
		masks = tmpmasks;

	}

	/**
	 * Enables a specific channel
	 * 
	 * @param channel
	 *            channel to enable
	 */
	public synchronized void enableChannel(int channel) {
		for (int i = 0; i < outputs.length; i++) {
			masks[i] |= channel;
		}
	}

	/**
	 * Disables a specific channel
	 * 
	 * @param channel
	 *            channel to disable
	 */
	public synchronized void disableChannel(int channel) {
		for (int i = 0; i < outputs.length; i++) {

			masks[i] &= ~channel;

		}
	}

	/**
	 * Disables output through all channels.
	 * 
	 */
	public synchronized void disableAllChannels() {
		for (int i = 0; i < outputs.length; i++) {
			masks[i] = 0;
		}
	}

	/**
	 * Enables output through all channels.
	 * 
	 */
	public synchronized void enableAllChannels() {
		for (int i = 0; i < outputs.length; i++) {
			masks[i] = 0xFFFFFFFF;
		}
	}

	/**
	 * Checks weather the channel is enabled.
	 * 
	 * @param channel
	 *            channel to check
	 * @return <code>true</code> if the channel is enabled for any output
	 *         stream, <code>false</code> otherwise.
	 */
	public synchronized boolean isChannelEnabled(int channel) {

		for (int i = 0; i < outputs.length; i++) {

			if ((masks[i] & channel) != 0)
				return true;

		}

		return false;
	}

	/**
	 * Enables a specific channel for a stream
	 * 
	 * @param stream
	 *            the stream of interest
	 * @param channel
	 *            channel to enable
	 */
	public synchronized void enableChannel(PrintStream stream, int channel) {
		for (int i = 0; i < outputs.length; i++) {

			if (outputs[i].equals(stream))
				masks[i] |= channel;

		}
	}

	/**
	 * Disables a specific channel for a stream
	 * 
	 * @param stream
	 *            the stream of interest
	 * @param channel
	 *            channel to disable
	 */
	public synchronized void disableChannel(PrintStream stream, int channel) {
		for (int i = 0; i < outputs.length; i++) {

			if (outputs[i].equals(stream))
				masks[i] &= ~channel;

		}
	}

	/**
	 * Disables output through all channels.
	 * 
	 * @param stream
	 *            the stream of interest
	 */
	public synchronized void disableAllChannels(PrintStream stream) {
		for (int i = 0; i < outputs.length; i++) {

			if (outputs[i].equals(stream))
				masks[i] = 0;

		}
	}

	/**
	 * Enables output through all channels.
	 * 
	 * @param stream
	 *            the stream of interest
	 */
	public synchronized void enableAllChannels(PrintStream stream) {

		for (int i = 0; i < outputs.length; i++) {

			if (outputs[i].equals(stream))
				masks[i] = 0xFFFFFFFF;

		}
	}

	/**
	 * Checks weather the channel is enabled.
	 * 
	 * @param stream
	 *            the stream of interest
	 * @param channel
	 *            channel to check
	 * @return <code>true</code> if the channel is enabled for this stream,
	 *         <code>false</code> otherwise.
	 */
	public synchronized boolean isChannelEnabled(PrintStream stream, int channel) {

		for (int i = 0; i < outputs.length; i++) {

			if (outputs[i].equals(stream) && (masks[i] & channel) != 0)
				return true;

		}

		return false;
	}

	/**
	 * Enables output through the specific channels.
	 * 
	 * @param channels
	 *            an array of chars each representing a channel to open.
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

	private synchronized void print(String str, int channel) {

		for (int i = 0; i < outputs.length; i++) {
			if ((masks[i] & channel) != 0)
				outputs[i].println(str);
		}

	}

}
