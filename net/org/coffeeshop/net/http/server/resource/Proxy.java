package org.coffeeshop.net.http.server.resource;

import org.coffeeshop.net.http.server.HttpRequest;
import org.coffeeshop.net.http.server.HttpResponse;

public interface Proxy {

	public void execute(HttpRequest request, HttpResponse response) throws Exception;
	
}
