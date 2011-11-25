package org.coffeeshop.net.http.server.resource;

import org.coffeeshop.net.http.server.HttpRequest;
import org.coffeeshop.net.http.server.HttpResponse;

public class ProxyResource extends Resource {

	private Proxy proxy;
	
	public ProxyResource(String name, Proxy proxy) {
		super(name);

		this.proxy = proxy;
	}

	public ProxyResource(String name, Folder parent, Proxy proxy) {
		super(name, parent);

		this.proxy = proxy;
	}
	
	@Override
	public void execute(HttpRequest request, HttpResponse response)
			throws Exception {
		
		proxy.execute(request, response);
		
	}

}
