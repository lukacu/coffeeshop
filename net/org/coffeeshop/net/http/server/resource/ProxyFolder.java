package org.coffeeshop.net.http.server.resource;

import org.coffeeshop.net.http.server.HttpRequest;
import org.coffeeshop.net.http.server.HttpResponse;

public abstract class ProxyFolder extends VirtualResource {

	public ProxyFolder(String name, Folder parent) {
		super(name, parent);	
	}

	@Override
	public void execute(HttpRequest request, HttpResponse response)
			throws Exception {
		
		String virtualPath = relativePath(request.getLocation());
		
		execute(request, response, virtualPath);
			
	}
	
	protected abstract void execute(HttpRequest request, HttpResponse response, String virtualPath) throws Exception;
	
}
