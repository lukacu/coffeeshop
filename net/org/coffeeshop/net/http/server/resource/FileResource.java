package org.coffeeshop.net.http.server.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import org.coffeeshop.io.Streams;
import org.coffeeshop.net.http.server.HttpRequest;
import org.coffeeshop.net.http.server.HttpResponse;
import org.coffeeshop.net.http.server.MimeTypes;

public class FileResource extends Resource {

	private File file;
	
	public FileResource(String name, Folder parent, File file) {
		super(name, parent);

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
