package org.coffeeshop.net.http.server.resource;

import java.io.InputStream;
import java.io.OutputStream;

import org.coffeeshop.io.Streams;
import org.coffeeshop.net.http.server.HttpRequest;
import org.coffeeshop.net.http.server.HttpResponse;

public class PackageProxyFolder extends ProxyFolder {

	private Class<?> origin;
	
	public PackageProxyFolder(String name, Folder parent, Class<?> origin) {
		super(name, parent);

		this.origin = origin;
		
	}

	@Override
	protected void execute(HttpRequest request, HttpResponse response,
			String virtualPath) throws Exception {
		final InputStream in = origin.getResourceAsStream(virtualPath);
		
		if (in == null) {
			response.setHttpResult(404);
			return;
		}
		
		OutputStream out = response.getOutputStream();
		
		Streams.copyStream(in, out);
		
		in.close();
		
	}
		
}
