package org.coffeeshop.net.http.server;

import java.io.*;
import java.net.*;

import org.coffeeshop.net.http.server.resource.Resource;

class HttpServerThread extends Thread {

	private static final int MAX_KEEPALIVE_REQUESTS = 3;
	
	private Socket socket;

	private HttpServer server;

	private HttpRequest request = null;

	private HttpResponse response = null;

	private HttpSession session = null;

	/**
	 * The default constructor.
	 * 
	 * @param group
	 *            the thread group this thread belongs to
	 * @param name
	 *            the name of the thread
	 * @param server
	 *            a reference to the server
	 * @param socket
	 *            the socket of the client
	 */
	public HttpServerThread(ThreadGroup group, String name, HttpServer server,
			Socket socket) {
		super(group, name);
		this.server = server;
		this.socket = socket;
	}

	// //////////////////////////////////////////////////////////////////////////////
	// public functions
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * this method returns the HttpSession object
	 * 
	 */
	public HttpSession getHttpSession() {
		return session;
	}

	/**
	 * this method returns the http server object. Required for accessing
	 * settings and other options
	 * 
	 */
	public HttpServerInformation getServer() {
		return this.server;
	}

	/**
	 * Returns the current httprequest. Warning: you can only use this after the
	 * request has been succesfully parsed
	 * 
	 */

	public HttpRequest getHttpRequest() {
		return this.request;
	}

	/**
	 * Returns the current response. Warning: you can only use this after the
	 * request has been succesfully parsed
	 * 
	 */

	public HttpResponse getHttpResponse() {
		return this.response;
	}

	/**
	 * the main run method, that handles the client request This method is
	 * called by the webserver. You never should call it manually.
	 */
	public void run() {
		try {
			try {

				// set the timeout settings
				if (MAX_KEEPALIVE_REQUESTS > 0)
					socket.setSoTimeout(server.getKeepAliveConnectionTimeout() * 1000);

				// retrieve the data input stream
				InputStream input = socket.getInputStream();
				OutputStream output = socket.getOutputStream();

				int requestCounter = 0;

				do {
					requestCounter++;

					// create the request and response
					request = new HttpRequest(this);
					
					// init the request object
					int result = request
							.init(input, socket, requestCounter > 0);
					
					response = new HttpResponse(this, request, output);


					if (result != HttpResponse.HTTP_OK) {
						if (result != 0) {
							HttpResponse.sendErrorMessage(response, result);
						}
						break;
					}

					//session = server.getSessionManager().loadSessionVars(request, response);

					String location = request.getLocation();
					
					if (location.charAt(0) == '/')
						location = location.substring(1);
					
					Resource r = server.getRoot().get(location);

					if (r == null) {
						
						HttpResponse.sendErrorMessage(response, HttpResponse.HTTP_NOT_FOUND);
						break;
						
					} else {
						
						try {
							
							r.execute(request, response);
							
							if (!response.isHeadersSent()) {
								
								if (response.getResultCode() == HttpResponse.HTTP_OK) {
									HttpResponse.sendErrorMessage(response, HttpResponse.HTTP_INTERNAL_ERROR);
								} else {
									HttpResponse.sendErrorMessage(response, response.getResultCode());
								}
								
								break;
								
							}
							
						} catch (Exception e) {
	
							HttpResponse.sendErrorMessage(response, HttpResponse.HTTP_INTERNAL_ERROR);
							
						}
						
						
					}

					output.flush();
					
				} while (response.isKeepAlive()
						&& requestCounter < MAX_KEEPALIVE_REQUESTS);

				// done ?
			} catch (Exception e) {
				e.printStackTrace();
			}
		} finally {
			// always close the socket
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}