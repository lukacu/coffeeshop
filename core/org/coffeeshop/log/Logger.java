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
public class Logger extends AbstractLogger {

	private PrintStream[] outputs;

	private int[] masks;

	/**
	 * Construct new logger object with a formatter.
	 * 
	 * @param formatter
	 */
	public Logger() {
		this(null);
	}

	/**
	 * Construct new logger object with a formatter.
	 * 
	 * @param formatter
	 */
	public Logger(LogFormat formatter) {
		super(formatter);
		this.masks = new int[] { 0 };
		this.outputs = new PrintStream[] { };
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

	protected synchronized void print(String str, int channel) {

		for (int i = 0; i < outputs.length; i++) {
			if ((masks[i] & channel) != 0)
				outputs[i].println(str);
		}

	}

}
