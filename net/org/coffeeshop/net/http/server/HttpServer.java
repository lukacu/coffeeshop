package org.coffeeshop.net.http.server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.coffeeshop.net.http.server.resource.Folder;
import org.coffeeshop.settings.DynamicDefaults;
import org.coffeeshop.settings.ReadableSettings;

public class HttpServer implements HttpServerInformation {

	private class HttpServerAcceptorThread extends Thread {

		public void run() {
			try {
				try {
					// create the server socket
					ServerSocket ssocket = new ServerSocket(port, maxqueue);
					ssocket.setSoTimeout(5000); // 5 seconds
					try {
						while (!interrupted()) {
							// read from the socket
							try {
								Socket socket = ssocket.accept();
								handleClient(socket);
							} catch (SocketTimeoutException e) {
								
							}
						}
					} finally {
						ssocket.close();
						ssocket = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} finally {
				// interrupt ALL threads
				threadGroup.interrupt();
			}
			
			acceptor = null;
		}
	}
	
	private int port;

	private int maxqueue;

	private InetAddress address;

	private String threadGroupName;
	
	private ThreadGroup threadGroup;

	private int threadCounter = 0;

	private HttpServerAcceptorThread acceptor = null;
	
	private int keepAliveConnectionTimeout, maxKeepAliveRequestPerConnection;
	
	private int maxUploadSize;
	
	private int sessionTimeout;
	
	private String sessionKeyName;
	
	private boolean sessionAutoStart;
	
	private String name;
	
	private ReadableSettings configuration;
	
	private Folder root = new Folder("", null);
	
	private HttpSessionManager sessionManager;
	
	/**
	 * the default constructor for the webserver
	 * 
	 * @param port
	 *            the port number. Port 80 is standard HTTP, but for an
	 *            application administration webserver you should use another
	 *            port. Preferably a port that can be changed in a settings
	 *            file.
	 * @param bindAddress
	 *            the addres to bind to (use null for localhost) Use this for
	 *            pc's with multiple ip's
	 */
	public HttpServer(int port, InetAddress bindAddress, ReadableSettings configuration) throws IllegalArgumentException {
		super();
		this.port = port;
		
		DynamicDefaults s = new DynamicDefaults(configuration);
		
		this.maxqueue = s.getInt("httpd.connection.maxqueue", 30);
		
		if (bindAddress != null) {
			this.address = bindAddress;
		} else {
			try {
				this.address = InetAddress.getByName(null);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
		
		threadGroupName = address.toString() + ":" + port;
		threadGroup = new ThreadGroup(threadGroupName);
		
		this.configuration = configuration;
		
		this.name = s.getString("httpd.name", "Embedded webserver");
		this.keepAliveConnectionTimeout = s.getInt("httpd.connection.keepalivetimeout", 2000);
		this.maxKeepAliveRequestPerConnection = s.getInt("httpd.connection.keepalivecount", 10);
		this.maxUploadSize = s.getInt("httpd.uploadsize", 1024 * 1024);
		this.sessionTimeout = s.getInt("httpd.session.timeout", 100000);
		this.sessionKeyName = s.getString("httpd.session.name", this.address.toString());
		this.sessionAutoStart = s.getBoolean("httpd.session.auto", false);
		
		sessionManager = new HttpSessionManager(this);
	}

	public void start() {
		
		if (acceptor != null)
			return;
		
		acceptor = new HttpServerAcceptorThread();
		
		acceptor.start();
	}

	public ReadableSettings getConfiguration() {
		return configuration;
	}
	
	/**
	 * this method handles a single HTTP request.
	 * 
	 * @param socket
	 *            the socket that represents the client.
	 */
	private void handleClient(Socket socket) {
		HttpServerThread thread = new HttpServerThread(threadGroup, "thread"
				+ (threadCounter++), this, socket);
		thread.start();
	}

	/* (non-Javadoc)
	 * @see com.vicos.http.HttpServerInformation#getKeepAliveConnectionTimeout()
	 */
	public int getKeepAliveConnectionTimeout() {
		return keepAliveConnectionTimeout;
	}

	/* (non-Javadoc)
	 * @see com.vicos.http.HttpServerInformation#getMaxKeepAliveRequestPerConnection()
	 */
	public int getMaxKeepAliveRequestPerConnection() {
		return maxKeepAliveRequestPerConnection;
	}

	/* (non-Javadoc)
	 * @see com.vicos.http.HttpServerInformation#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.vicos.http.HttpServerInformation#getMaxUploadSize()
	 */
	public int getMaxUploadSize() {
		return maxUploadSize;
	}

	/* (non-Javadoc)
	 * @see com.vicos.http.HttpServerInformation#getSessionTimeout()
	 */
	public int getSessionTimeout() {
		return sessionTimeout;
	}

	/* (non-Javadoc)
	 * @see com.vicos.http.HttpServerInformation#getSessionKeyName()
	 */
	public String getSessionKeyName() {
		return sessionKeyName;
	}

	/* (non-Javadoc)
	 * @see com.vicos.http.HttpServerInformation#isSessionAutoStart()
	 */
	public boolean isSessionAutoStart() {
		return sessionAutoStart;
	}

	public Folder getRoot() {
		return root;
	}

	public HttpSessionManager getSessionManager() {
		return sessionManager;
	}

	public int getPort() {
		return port;
	}
	
}