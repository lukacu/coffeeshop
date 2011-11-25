package org.coffeeshop.net.html;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


/**
 * The Class Snippets.
 */
public class Snippets {

	/**
	 * Xml header.
	 * 
	 * @return the string
	 */
	public static String xmlHeader() {
		
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";
		
	}

	/**
	 * Html style include.
	 * 
	 * @param link
	 *            the link
	 * 
	 * @return the string
	 */
	public static String htmlStyleInclude(String link) {
		
		return String.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\" />", link);
		
	}

	/**
	 * Html script include.
	 * 
	 * @param link
	 *            the link
	 * 
	 * @return the string
	 */
	public static String htmlScriptInclude(String link) {
		
		return String.format("<script src=\"%s\" type=\"text/javascript\"></script>", link);
		
	}
	
	/**
	 * Html header.
	 * 
	 * @param title
	 *            the title
	 * 
	 * @return the string
	 */
	public static String htmlHeader(String title) {
		
		return String.format("<title>%s</title>", title);
		
	}
	
	/**
	 * Html document title.
	 * 
	 * @param title
	 *            the title
	 * 
	 * @return the string
	 */
	public static String htmlDocumentTitle(String title) {
		
		return String.format("<title>%s</title>", title);
		
	}

	/**
	 * Html paragraph.
	 * 
	 * @param text
	 *            the text
	 * 
	 * @return the string
	 */
	public static String htmlParagraph(String text) {
		
		return String.format("<p>%s</p>", text);
		
	}
	
	/**
	 * Html title.
	 * 
	 * @param title
	 *            the title
	 * 
	 * @return the string
	 */
	public static String htmlTitle(String title) {
		
		return String.format("<h1>%s</h1>", title);
		
	}

	/**
	 * Html subtitle.
	 * 
	 * @param title
	 *            the title
	 * 
	 * @return the string
	 */
	public static String htmlSubtitle(String title) {
		
		return String.format("<h2>%s</h2>", title);
		
	}

	/**
	 * Html image.
	 * 
	 * @param source
	 *            the source
	 * @param alt
	 *            the alt
	 * @param id
	 *            the id
	 * 
	 * @return the string
	 */
	public static String htmlImage(String source, String alt, String id) {
		
		if (id == null)
			return String.format("<img src=\"%s\" alt=\"%s\" />", source, alt);
		
		return String.format("<img src=\"%s\" alt=\"%s\" id=\"%s\" />", source, alt, id);
		
	}
	
	/**
	 * Html image.
	 * 
	 * @param source
	 *            the source
	 * @param alt
	 *            the alt
	 * 
	 * @return the string
	 */
	public static String htmlImage(String source, String alt) {
		
		return htmlImage(source, alt, null);
		
	}
	
	/**
	 * Html meta.
	 * 
	 * @param name
	 *            the name
	 * @param content
	 *            the content
	 * 
	 * @return the string
	 */
	public static String htmlMeta(String name, String content) {
		
		return String.format("<meta name=\"%s\" content=\"%s\" />", name, content);
		
	}

	/**
	 * Html link list.
	 * 
	 * @param elements
	 *            the elements
	 * 
	 * @return the string
	 */
	public static String htmlLinkList(HashMap<String, String> elements) {
		return htmlLinkList(elements);
	}
	
	
	public static String htmlLinkList(HashMap<String, String> elements, boolean ordered) {
		
		StringBuilder b = new StringBuilder();
		b.append(ordered ? "<ol>\n" : "<ul>\n");
		
		List<String> keys = new Vector<String>(elements.keySet());
		if (ordered)
			Collections.sort(keys);
		
		for (String key : keys) {
			b.append("<li>");
			b.append(htmlLink(key, elements.get(key)));
			b.append("</li>\n");
		}
		
		b.append(ordered ? "</ol>\n" : "</ul>\n");
		
		return b.toString();
		
	}
	
	/**
	 * Html list.
	 * 
	 * @param elements
	 *            the elements
	 * 
	 * @return the string
	 */
	public static String htmlList(Collection<String> elements) {
		
		StringBuilder b = new StringBuilder();
		b.append("<ul>\n");
		
		for (String e : elements) {
			b.append("<li>");
			b.append(e);
			b.append("</li>\n");
		}
		
		b.append("</ul>\n");
		
		return b.toString();
		
	}
	
	/**
	 * Html link.
	 * 
	 * @param title
	 *            the title
	 * @param url
	 *            the url
	 * 
	 * @return the string
	 */
	public static String htmlLink(String title, String url) {
		return String.format("<a href=\"%s\">%s</a>", url, title);
	}
	
	/**
	 * Html simple table.
	 * 
	 * @param header
	 *            the header
	 * @param cells
	 *            the cells
	 * 
	 * @return the string
	 */
	public static String htmlSimpleTable(String[] header, String[][] cells) {
		
		StringBuilder b = new StringBuilder();
		b.append("<table>\n<tr>\n");
		
		for (int i = 0; i < header.length; i++) {
			b.append("<th>" + header[i] + "</th>\n");
		}
		
		b.append("</tr>\n");
		
		for (int j = 0; j < cells.length; j++) {
			b.append("<tr>\n");
			for (int i = 0; i < header.length; i++) {
				b.append("<td>" + cells[j][i] + "</td>\n");
			}
			b.append("</tr>\n");
		}
		
		b.append("</table>\n");
		
		return b.toString();
		
	}
	
}
