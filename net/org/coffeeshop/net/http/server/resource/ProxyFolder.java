package org.coffeeshop.net.http.server.resource;

public abstract class ProxyFolder extends Folder {

	public static interface ProxyFilter {
		
		public boolean block(String resource);
		
	}
	
	private ProxyFilter filter;
	
	public ProxyFolder(String name, Folder parent, ProxyFilter filter) {
		super(name, parent);	
	}

	@Override
	protected void add(Resource o) {
		throw new RuntimeException("Unable to add resources to a proxy folder.");
	}
	
	@Override
	public Resource get(String s) {
		
		if (filter != null) {
			if (filter.block(s))
				return null;
		}
		
		return getProxy(s);
	}
	
	protected abstract Resource getProxy(String s);
	
}
