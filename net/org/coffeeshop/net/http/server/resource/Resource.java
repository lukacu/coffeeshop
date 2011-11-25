package org.coffeeshop.net.http.server.resource;

import org.coffeeshop.net.http.server.HttpRequest;
import org.coffeeshop.net.http.server.HttpResponse;


public abstract class Resource {

	public static final String SEPARATOR = "/";
	
	private String name;
	
	private Folder parent;
	
	protected Resource(String name) {
		this.name = name;
	}
	
	public Resource(String name, Folder parent) {
		this.name = name;
		this.parent = parent;
		
		if (parent != null)
			parent.add(this);
	}
	
	public String getName() {
		return name;
	}
	
	public String getFullName() {
		if (parent == null)
			return name;
		return parent.getFullName() + SEPARATOR + name;
	}
	
	public Folder getParent() {
		return parent;
	}
	
	public abstract void execute(HttpRequest request, HttpResponse response) throws Exception;
	
}
