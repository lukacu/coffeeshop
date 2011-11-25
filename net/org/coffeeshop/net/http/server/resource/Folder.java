package org.coffeeshop.net.http.server.resource;

import java.util.HashMap;
import java.util.Hashtable;

import org.coffeeshop.net.html.SimpleHtmlDocument;
import org.coffeeshop.net.html.Snippets;
import org.coffeeshop.net.http.server.HttpRequest;
import org.coffeeshop.net.http.server.HttpResponse;

public class Folder extends Resource {

	private Hashtable<String, Resource> children = new Hashtable<String, Resource>();
	
	private String defaultChild = "index";
	
	public String getDefaultChild() {
		return defaultChild;
	}

	public void setDefaultChild(String defaultChild) {
		this.defaultChild = defaultChild;
	}

	public Folder(String name, Folder parent) {
		super(name, parent);
		
	}

	protected void add(Resource o) {
		if (o != null) {
			synchronized (children) {
				Resource p = children.get(o.getName());
				if (p != null && p != o)
					throw new IllegalArgumentException("Object with that name already exists");
				if (p != o) {
					children.put(o.getName(), o);
				}
			}
		}
	}
	
	public Resource get(String s) {
		
		int i = s.indexOf(SEPARATOR);

		if (i == -1) {
			if (s.length() == 0)
				return this;
			
			return children.get(s);
		} else {
			
			String name = s.substring(0, i);
			
			Resource r = children.get(name);
			
			if (r == null)
				return null;
			
			if (!Folder.class.isInstance(r))
				return null;
			
			return ((Folder) r).get(s.substring(i+1));
	
		}
			
	}
	
	@Override
	public void execute(HttpRequest request, HttpResponse response) throws Exception {
		
		if (defaultChild != null && children.containsKey(defaultChild)) {
			
			Resource res = children.get(defaultChild);
			
			res.execute(request, response);
			
			return;
			
		}
		
		HashMap<String, String> listing = new HashMap<String, String>();
		
		if (getParent() != null) {
			listing.put("..", getParent().getFullName());
		}
		
		for (Resource r : children.values()) {
			
			listing.put(r.getName(), r.getFullName());
			
		}
		
		SimpleHtmlDocument document = new SimpleHtmlDocument("Listing for " + request.getLocation());
		
		document.append(Snippets.htmlTitle("Listing for " + request.getLocation()));
		document.append(Snippets.htmlLinkList(listing, true));
		document.append("<hr />");
		document.append(Snippets.htmlParagraph("<em>" + request.getServerInformation().getName() + "</em>"));
		document.writeToStream(response.getOutputStream());
		
	}

}
