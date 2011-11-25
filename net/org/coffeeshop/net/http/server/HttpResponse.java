package org.coffeeshop.net.http.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import org.coffeeshop.net.html.SimpleHtmlDocument;
import org.coffeeshop.net.html.Snippets;

/**
 * Use this class to generate the webserver response and to set cookies
 * 
 */
public class HttpResponse {

	private class WatchedOutputStream extends OutputStream {

		@Override
		public void write(int b) throws IOException {
			
			if (!headersSend)
				sendHeaders();
			
			out.write(b);
		}
		
	}

	/** Status-Code 200: OK. */
	public final static int HTTP_OK = 200;
	/** Status-Code 301: Moved Permanently */
	public final static int HTTP_MOVED_PERMANENTLY = 301;
	/** Status-Code 400: Bad Request. */
	public final static int HTTP_BAD_REQUEST = 400;
	/** Status-Code 404: Not Found. */
	public final static int HTTP_NOT_FOUND = 404;
	/** Status-Code 405: Method Not Allowed. */
	public final static int HTTP_BAD_METHOD = 405;
	/** Status-Code 411: Length Required. */
	public final static int HTTP_LENGTH_REQUIRED = 411;
	/** Status-Code 500: Internal Server Error. */
	public final static int HTTP_INTERNAL_ERROR = 500;

	/**
	 * This method converts an error number to a name. When an invalid number is
	 * assigned this method always returns "Internal Server Errror"!
	 * 
	 * @param writer
	 *            DataOutputStream
	 * @param value
	 *            int
	 * @return int
	 * @since V1.03
	 */
	public static String getHttpResultText(int code) {
		switch (code) {
		case HTTP_OK:
			return "OK";
		case HTTP_BAD_REQUEST:
			return "Bad Request";
		case HTTP_NOT_FOUND:
			return "Not Found";
		case HTTP_BAD_METHOD:
			return "Method Not Allowed";
		case HTTP_LENGTH_REQUIRED:
			return "Length Required";
		default:
		case HTTP_INTERNAL_ERROR:
			return "Internal Server Error";
		}
	}

	/**
	 * this method sends an error message to client and returns the error code
	 * This need to be done not this way
	 * 
	 * @todo this method need to dissapear an be replaced by 'custom' error
	 *       handlers, specified in the HttpServerSettings
	 * 
	 * This method is also temporary, we need to build in Custom error docs in
	 * the near future
	 * 
	 * @param response
	 *            the http response
	 * @param httpResult
	 *            the resultcode
	 * @throws IOException
	 */
	public static void sendErrorMessage(HttpResponse response, int httpResult)
			throws IOException {
		response.setHttpResult(httpResult);
		String msg = getHttpResultText(httpResult);
		
		SimpleHtmlDocument document = new SimpleHtmlDocument(msg);
		
		document.append(Snippets.htmlTitle(msg));
		document.append("<hr />");
		document.append(Snippets.htmlParagraph("<em>" + response.getServerInformation().getName() + "</em>"));
		document.writeToStream(response.getOutputStream());
		
	}
	
	private OutputStream out;

	private String resultHeader;

	private int resultHeaderCode;

	private boolean headersSend = false;

	private HttpServerInformation serverInfo;
	
	
	/**
	 * the http-headers we need to send. The key contains the headername
	 * (incluseive ':'). The value is the header value
	 */
	private HashMap<String, String> headers = new HashMap<String, String>();

	/**
	 * the http cookies we need to send. Cookies can result in multiple HTTP
	 * headers with the same key. (The cannot be placed in the header hasmap)
	 * key=cookieid, value=cookievalue
	 */
	private HashMap<String, Cookie> cookies = new HashMap<String, Cookie>();

	/**
	 * The server thread for accessing the server settings
	 * 
	 */
	private HttpServerThread serverThread;

	/**
	 * Should the connection be kept alive
	 * 
	 */
	private boolean keepAlive = true;

	private static SimpleDateFormat dateFormatterGMT;

	static {
		dateFormatterGMT = new SimpleDateFormat("d MMM yyyy HH:mm:ss 'GMT'");
		dateFormatterGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * the default constructor. This method initailises the default response.
	 * The default headers send are:
	 * 
	 * <pre>
	 * HTTP/1.1 200 OK
	 * date: current data
	 * server: blommers-it webserver
	 * connection: close
	 * content-type: text/html; charset=iso=8859-1
	 * cache-control: no-store, no-cache, must-revalidate, post-check=0, pre-check=0
	 * pragma: no-cache
	 * </pre>
	 * 
	 * @param thread
	 *            the server thread
	 * @param writer
	 *            the outputstream
	 */
	public HttpResponse(HttpServerThread thread, HttpRequest request, OutputStream out) {
		this.out = out;
		resultHeader = "HTTP/1.1 200 OK";
		resultHeaderCode = 200;
		headers.put("date:", dateFormatterGMT.format(new Date()));
		headers.put("server:", thread.getServer().getName());
		headers.put("connection:", "close");
		headers.put("content-type:", "text/html; charset=iso-8859-1");
		headers
				.put("cache-control:",
						"no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
		headers.put("pragma:", "no-cache");
		this.serverThread = thread;
		
		this.keepAlive = request.isKeepAlive();
		
		this.serverInfo = thread.getServer();
	}

	/**
	 * This method sets the header a little smarter. Jou just have to supply the
	 * error number to generate the header.<br />
	 * Can only be called before the headers are sent!<br />
	 * This method modifies the data set with "setHeaderBegin"!
	 */
	public void setHttpResult(int code) {
		String message = "HTTP/1.1 " + code + " ";
		switch (code) {
		case HTTP_OK:
			message += "OK";
			break;
		case HTTP_BAD_REQUEST:
			message += "Bad Request";
			break;
		case HTTP_NOT_FOUND:
			message += "Not Found";
			break;
		case HTTP_BAD_METHOD:
			message += "Method Not Allowed";
			break;
		case HTTP_LENGTH_REQUIRED:
			message += "Length Required";
			break;
		default:
		case HTTP_INTERNAL_ERROR:
			message += "Internal Server Error";
			break;
		}
		this.resultHeader = message;
		this.resultHeaderCode = code;
	}

	public HttpServerInformation getServerInformation() {
		return serverInfo;
	}
	
	
	/**
	 * this method returns the result code from the begin header
	 * 
	 */
	public int getResultCode() {
		return resultHeaderCode;
	}

	/**
	 * this method sets a header
	 * 
	 * @param key
	 *            the name of the header (case insensitive). You should you the
	 *            ':' in the keyname
	 * @param value
	 *            the value of the header
	 */
	public void setHeader(String key, String value) {
		key = key.toLowerCase();
		if (headersSend)
			throw new java.lang.UnsupportedOperationException(
					"Headers already sent");
		headers.put(key, value);
	}

	/**
	 * this method removes a header
	 * 
	 * @param key
	 *            the headername (not case sensitive)
	 */
	public void deleteHeader(String key) {
		key = key.toLowerCase();
		if (headersSend)
			throw new java.lang.UnsupportedOperationException(
					"Headers already sent");
		headers.remove(key);
	}

	/**
	 * this method retrieves a header
	 * 
	 * @param key
	 *            the headername of the header to retrieve (case insensitive)
	 * @param def
	 *            the default value to return if the header doesn't exist
	 * @return the header value
	 */
	public String getHeader(String key, String def) {
		key = key.toLowerCase();
		if (!headers.containsKey(key))
			return def;
		return (String) headers.get(key);
	}

	/**
	 * this method sets a cookie. Deleting a cookie can be done by settings the
	 * value to "" and the expires date in the history. (NOT 0 because 0 means
	 * session expiration, so use 1)
	 * 
	 * @param name
	 *            the name of the cookie (case sensitive)
	 * @param value
	 *            the value of the cookie
	 * @param expires
	 *            when does the cookie expire ?? ms. since the Unix-epoch
	 * @param domain
	 *            the domain name of the cookie
	 * @param path
	 *            the pathname of the cookie
	 * @param secure
	 *            a secure cookie ?? Note yet supported by the webserver
	 */
	public void setCookie(String name, String value, long expires,
			String domain, String path, boolean secure) {
		// get or create the cookie
		Cookie cookie = null;
		if (cookies.containsKey(name))
			cookie = (Cookie) cookies.get(name);
		else {
			cookie = new Cookie();
			cookies.put(name, cookie);
		}

		// set the values
		cookie.value = value;
		cookie.expires = expires;
		cookie.domain = domain;
		cookie.path = path;
		cookie.secure = secure;
	}

	/**
	 * this method deletes a cookie. NOTE: this doesn't delete cookie set with a
	 * different path or domain
	 * 
	 * @param name
	 *            the cookie to delete
	 */
	public void deleteCookie(String name) {
		setCookie(name, "", 1, "", "", false);
	}

	/**
	 * This method sets a cookie. (A simplified method of the extended method).
	 * 
	 * @param name
	 *            the name of the cookie
	 * @param value
	 *            the value of the cookie
	 */
	public void setCookie(String name, String value) {
		setCookie(name, value, 0, "", "", false);
	}

	public OutputStream getOutputStream() {
		return new WatchedOutputStream();
	}
	
	public boolean isKeepAlive() {
		return keepAlive;
	}
	
	public boolean isHeadersSent() {
		return headersSend;
	}
	
	/**
	 * this method sends the headers. only if required.
	 */
	private void sendHeaders() throws IOException {
		// send the headers
		if (!headersSend) {

			DataOutputStream writer = new DataOutputStream(out);
			
			// if there is not content length we cannot keep the connection open
			if (this.resultHeaderCode == HTTP_OK && !headers.containsKey("content-length:"))
				keepAlive = false;
			
			headers.put("keep-alive:", "timeout="
					+ this.serverThread.getServer().getKeepAliveConnectionTimeout() + ",max="
					+ this.serverThread.getServer().getMaxKeepAliveRequestPerConnection());
			if (keepAlive) {
				headers.put("connection:", "keep-alive");
			} else {
				headers.put("connection:", "close");
			}

			// now write the headers
			headersSend = true;
			writer.writeBytes(resultHeader);
			writer.writeBytes("\r\n");

			Iterator<String> it = this.headers.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				writer.writeBytes(key);
				writer.writeBytes((String) headers.get(key));
				writer.writeBytes("\r\n");
			}

			// send all cookies
			it = this.cookies.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				writer.writeBytes("set-cookie: ");
				writer.writeBytes(key);
				writer.write('=');
				Cookie cookie = (Cookie) cookies.get(key);
				writer.writeBytes(java.net.URLEncoder.encode(cookie.value, "UTF-8"));
				if (cookie.expires != 0) {
					writer.writeBytes("; expires=");

					Date d = new Date(cookie.expires);
					SimpleDateFormat sf = new SimpleDateFormat(
							"EEE, dd MMM yyyy HH:mm:ss Z");
					writer.writeBytes(sf.format(d));
				}
				if (!cookie.path.equals("")) {
					writer.writeBytes("; path=");
					writer.writeBytes(cookie.path);
				}
				if (!cookie.domain.equals("")) {
					writer.writeBytes("; domain=");
					writer.writeBytes(cookie.domain);
				}
				if (cookie.secure) {
					writer.writeBytes("; secure");
				}
				writer.writeBytes("\r\n");
			}

			writer.writeBytes("\r\n"); // empty line means END OF HEADERS
						
			writer.flush();
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	// private class that represents a cookie
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * private class for storing cookie information
	 */
	private class Cookie {
		/** cookie value */
		public String value = "";
		/** expires value, 0=no expiration given -&gt; means session cookie */
		public long expires = 0;
		/** the domainname, "" default */
		public String domain = "";
		/** the path, default is none (most safe='/') */
		public String path = "";
		/** secure only flag (only transmitted over https) ? Default is false */
		public boolean secure = false;
	}

}
