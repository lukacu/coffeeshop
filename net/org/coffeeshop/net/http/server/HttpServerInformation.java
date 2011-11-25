package org.coffeeshop.net.http.server;

public interface HttpServerInformation {

	public int getKeepAliveConnectionTimeout();

	public int getMaxKeepAliveRequestPerConnection();

	public String getName();

	public int getPort();
	
	public int getMaxUploadSize();

	public int getSessionTimeout();

	public String getSessionKeyName();

	public boolean isSessionAutoStart();

}