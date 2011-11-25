package org.coffeeshop.net.html;

import java.io.OutputStream;
import java.io.PrintWriter;

public class SimpleHtmlDocument {

	private String title;
	
	private StringBuffer content = new StringBuffer(), head = new StringBuffer();
	
	public SimpleHtmlDocument(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void head(String s) {
		head.append(s);
	}
	
	public void append(String s) {
		content.append(s);
	}
	
	public String toString() {
		
		StringBuilder b = new StringBuilder();
		b.append(Snippets.xmlHeader());
		b.append("<html><head><title>" + title + "</title>");
		b.append(head);
		b.append("</head><body>");
		b.append(content);
		b.append("</body></html>");
		
		return b.toString();
		
	}
	
	public void writeToStream(OutputStream out) {
		
		PrintWriter writer = new PrintWriter(out);
		
		writer.println(this);
		
		writer.flush();
		
	}
	
}
