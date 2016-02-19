package org.coffeeshop.net.http.server.resource;

public abstract class VirtualResource extends Resource {

	public VirtualResource(String name, Folder parent) {
		super(name, parent);
		
	}

	protected String relativePath(String path) {
		
		String parent = getFullName();
		
		if (path.startsWith(parent)) {
			return path.substring(parent.length());
		}
		
		return null;
	}
	
}
