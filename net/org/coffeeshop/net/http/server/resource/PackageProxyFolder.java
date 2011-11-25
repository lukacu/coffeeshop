package org.coffeeshop.net.http.server.resource;

import java.io.InputStream;
import java.io.OutputStream;

import org.coffeeshop.io.Streams;
import org.coffeeshop.net.http.server.HttpRequest;
import org.coffeeshop.net.http.server.HttpResponse;

public class PackageProxyFolder extends ProxyFolder {

	private Class<?> origin;
	
	public PackageProxyFolder(String name, Folder parent, ProxyFilter filter, Class<?> origin) {
		super(name, parent, filter);

		this.origin = origin;
		
	}

	@Override
	protected Resource getProxy(String s) {

		final InputStream in = origin.getResourceAsStream(s);
		
		if (in == null)
			return null;
		
		return new Resource(s) {
			
			@Override
			public void execute(HttpRequest request, HttpResponse response)
					throws Exception {
				
				OutputStream out = response.getOutputStream();
				
				Streams.copyStream(in, out);
				
				in.close();
				
			}
		};
		
	}
	
}
