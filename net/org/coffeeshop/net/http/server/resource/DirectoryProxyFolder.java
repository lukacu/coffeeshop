package org.coffeeshop.net.http.server.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.coffeeshop.io.Streams;
import org.coffeeshop.net.html.SimpleHtmlDocument;
import org.coffeeshop.net.html.Snippets;
import org.coffeeshop.net.http.server.HttpRequest;
import org.coffeeshop.net.http.server.HttpResponse;
import org.coffeeshop.net.http.server.MimeTypes;

public class DirectoryProxyFolder extends ProxyFolder {

	private File directory;
	
	public DirectoryProxyFolder(String name, Folder parent, File directory) {
		super(name, parent);

		if (directory == null || !directory.isDirectory())
			throw new IllegalArgumentException("Not a directory");
		
		this.directory = directory;
	}

	@Override
	protected void execute(HttpRequest request, HttpResponse response,
			String virtualPath) throws Exception {
		
		File file = new File(directory, virtualPath.replace(SEPARATOR, File.separator));
		
		if (!file.exists()) {
			response.setHttpResult(404);
			return;
		}

		if (file.isDirectory()) {
			String prefix = getFullName() + virtualPath;
			
			HashMap<String, String> listing = new HashMap<String, String>();
			
			if (getParent() != null) {
				listing.put("..", "..");
			}
			
			for (File child : file.listFiles()) {
				
				listing.put(child.getName(), prefix + SEPARATOR + child.getName());
				
			}
			
			SimpleHtmlDocument document = new SimpleHtmlDocument("Listing for " + request.getLocation());
			
			document.append(Snippets.htmlTitle("Listing for " + request.getLocation()));
			document.append(Snippets.htmlLinkList(listing, true));
			document.append("<hr />");
			document.append(Snippets.htmlParagraph("<em>" + request.getServerInformation().getName() + "</em>"));
			document.writeToStream(response.getOutputStream());
			
			return;
		}

		if (file.isFile() && file.canRead()) {
			OutputStream out = response.getOutputStream();
			
			FileInputStream in = new FileInputStream(file);
			
			response.setHeader("content-type:", MimeTypes.getMimeType(file.getAbsolutePath()));
			
			Streams.copyStream(in, out);
			
			in.close();

			return;
		}
		
		
	}

}
