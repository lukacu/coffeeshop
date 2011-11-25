package org.coffeeshop.net.http.server.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import org.coffeeshop.io.Streams;
import org.coffeeshop.net.http.server.HttpRequest;
import org.coffeeshop.net.http.server.HttpResponse;
import org.coffeeshop.net.http.server.MimeTypes;

public class DirectoryProxyFolder extends ProxyFolder {

	private File directory;
	
	private class FileResource extends Resource {

		private File file;
		
		protected FileResource(String name, File file) {
			super(name);

			this.file = file;
			
		}

		@Override
		public void execute(HttpRequest request, HttpResponse response)
				throws Exception {
		
			OutputStream out = response.getOutputStream();
			
			FileInputStream in = new FileInputStream(file);
			
			response.setHeader("content-type:", MimeTypes.getMimeType(file.getAbsolutePath()));
			
			Streams.copyStream(in, out);
			
			in.close();

		}

	}
	
	public DirectoryProxyFolder(String name, Folder parent, ProxyFilter filter, File directory) {
		super(name, parent, filter);

		if (directory == null || !directory.isDirectory())
			throw new IllegalArgumentException("Not a directory");
		
		this.directory = directory;
	}

	@Override
	protected Resource getProxy(String s) {

		//s.replace("..", "");		
		
		File f = new File(directory, s);

		if (!f.exists())
			return null;
		
		return new FileResource(s, f);
		
	}

}
