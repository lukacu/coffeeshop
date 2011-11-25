package org.coffeeshop.net.http.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.coffeeshop.string.StringUtils;

/**
 * This class represents an http request An url is defined the following way (in
 * this request point of view): location?getString#anchorName <br/> You can use
 * the request to read the HTTP Header information and obtain the GET, POST and
 * COOKIE variables.
 * 
 * @author Rick Blommers
 */

public class HttpRequest {

	public final static int HTTP_METHOD_GET = 0, HTTP_METHOD_POST = 1,
			HTTP_METHOD_HEAD = 2;

	/**
	 * this arrayslist contains a http header with the following items key:
	 * header-key value: the header value. The header keys are translated to
	 * lowercase can the spaces are trimmed
	 */
	private HashMap<String, String> httpHeaders = new HashMap<String, String>();

	/**
	 * this is the complete request (the first line) send to the server
	 */
	private String rawRequest;

	// parsed data ////////////////////

	/**
	 * this is the request send to the server the complete unparsed URL for
	 * example: /locationname?value=x&tmp=12
	 */
	private String rawurl;

	/** the requested method. HTTP_METHOD_* constants */
	private int method;

	/** the requested location (with the Anchor and GET values trimmed of) */
	private String location;

	/**
	 * the request anchorName (not very interesting for the webserver)
	 * url#anchorname
	 */
	private String anchorName;

	private String rawGetString;

	private String rawPostString;

	private HashMap<String, Object> get = new HashMap<String, Object>();

	private HashMap<String, Object> post = new HashMap<String, Object>();

	private HashMap<String, HttpFileUpload> postFiles = new HashMap<String, HttpFileUpload>();

	private String ipaddress;

	private byte[] byteipaddress;

	private HashMap<String, Object> cookies = new HashMap<String, Object>();

	private HttpServerThread httpServerThread;

	private boolean keepAlive = false;
	
	private HttpServerInformation serverInfo;
	
	/**
	 * The constructor with the serverthread, so we can read the webserver
	 * settings
	 * 
	 * @since V1.01
	 */
	public HttpRequest(HttpServerThread httpServerThread) {
		this.httpServerThread = httpServerThread;
		
		this.serverInfo = httpServerThread.getServer();
	}

	public HttpServerInformation getServerInformation() {
		return serverInfo;
	}
	
	/**
	 * this method sets the location of the given request. You shouldn't use
	 * this method. It's used internally by the server alias mechanism
	 * 
	 * @param l
	 *            the location string
	 */
	public void setLocation(String l) {
		location = l;
	}

	/**
	 * this method returns the location of the given request. This means the
	 * complete path name with the get values trimmed of, without the domainname
	 * and the anchorname (url#anchorname).
	 * 
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * this method returns the ip-number as a string
	 * 
	 * @return the ipnumber of the client as a string
	 */
	public String getIP() {
		return ipaddress;
	}

	/**
	 * this method returns the ip-number as a long
	 * 
	 * @return the ipnumber of the client requesting this page
	 */
	public long getIP4() {
		if (byteipaddress.length >= 4)
			return byteipaddress[3] << 24 + byteipaddress[2] << 16 + byteipaddress[1] << 8 + byteipaddress[0];
		return 0;
	}

	/**
	 * this method returns the value of a GET variable. WARNING: if the variable
	 * is used multiple times in the form the LAST
	 * 
	 * @param id
	 *            the variable name (case sensitive !!)
	 * @param def
	 *            the value to return if the given GET parameter doesn't exist.
	 * @return the value of the given GET variable or def if not found.
	 */
	public String getGet(String id, String def) {
		if (!get.containsKey(id))
			return def;
		Object o = get.get(id);
		if (o instanceof ArrayList) {
			ArrayList arr = (ArrayList) o;
			return (String) arr.get(arr.size() - 1);
		}
		return (String) o;
	}

	/**
	 * this method returns the value of a GET variable at the given index. You
	 * can
	 * 
	 * @param id
	 *            the variable name (case sensitive !!)
	 * @param index
	 *            the index of the given get variable
	 * @param def
	 *            the value to return if the given GET parameter doesn't exist.
	 * @return the value of the given GET variable or def if not found.
	 * @since V1.03
	 */
	public String getGet(String id, int index, String def) {
		if (!get.containsKey(id))
			return def;
		Object o = get.get(id);
		if (o instanceof ArrayList) {
			ArrayList arr = (ArrayList) o;
			if (index >= arr.size())
				return def;
			return (String) arr.get(index);
		}
		return (String) o;
	}

	/**
	 * Sets a get variable. WARNING: if the given get variable was an array the
	 * complete array is replaced by this variable.
	 * 
	 * @since V1.03
	 */
	public void setGet(String id, String value) {
		get.put(id, value);
	}

	/**
	 * Returns the number of get values
	 * 
	 * @param id
	 *            the name of the get variable
	 * @return the number of times the given get variable is used.
	 * @since v1.03
	 */
	@SuppressWarnings("unchecked")
	public int getGetCount(String id) {
		if (!get.containsKey(id))
			return 0;
		Object o = get.get(id);
		if (o instanceof ArrayList)
			return ((ArrayList<String>) o).size();
		return 1;
	}

	/**
	 * this method returns a the value of a POST variable
	 * 
	 * @param id
	 *            the variable name (case sensitive !!)
	 * @param def
	 *            the value to return if the given POST parameter doesn't exist.
	 * @return the value of the given POST variable or def if not found.
	 */
	public String getPost(String id, String def) {
		if (!post.containsKey(id))
			return def;
		Object o = post.get(id);
		if (o instanceof ArrayList) {
			ArrayList arr = (ArrayList) o;
			return (String) arr.get(arr.size() - 1);
		}
		return (String) o;
	}

	/**
	 * this method returns the value of a POST variable at the given index. You
	 * can
	 * 
	 * @param id
	 *            the variable name (case sensitive !!)
	 * @param index
	 *            the index of the given get variable
	 * @param def
	 *            the value to return if the given POST parameter doesn't exist.
	 * @return the value of the given POST variable or def if not found.
	 * @since V1.03
	 */
	public String getPost(String id, int index, String def) {
		if (!post.containsKey(id))
			return def;
		Object o = post.get(id);
		if (o instanceof ArrayList) {
			ArrayList arr = (ArrayList) o;
			if (index >= arr.size())
				return def;
			return (String) arr.get(index);
		}
		return (String) o;
	}

	/**
	 * Sets a post variable. WARNING: if the given post variable was an array
	 * the complete array is replaced by this variable.
	 * 
	 * @since V1.03
	 */
	public void setPost(String id, String value) {
		post.put(id, value);
	}

	/**
	 * Returns the number of get values
	 * 
	 * @param id
	 *            the name of the get variable
	 * @return the number of times the given get variable is used.
	 * @since v1.03
	 */
	public int getPostCount(String id) {
		if (!post.containsKey(id))
			return 0;
		Object o = post.get(id);
		if (o instanceof ArrayList)
			return ((ArrayList) o).size();
		return 1;
	}

	/**
	 * this method returns a HttpFileUpload object
	 * 
	 * @param id
	 *            the filename id
	 * @return The HttpFileUpload object or null
	 * @since V1.01
	 */
	public HttpFileUpload getPostFile(String id) {
		return (HttpFileUpload) postFiles.get(id);
	}

	/**
	 * this method returns the value of a COOCKIE variable NOTE: for setting a
	 * cookie variable see
	 * {@link com.blommersit.httpd.HttpResponse#setCookie(String, String) HttpResponse.setCookie(String, String}<br/>
	 * NOTE: for session variables see
	 * {@link com.blommersit.httpd.HttpServerThread#getHttpSession() HttpServerThread.getHttpSession()}<br/>
	 * 
	 * @param id
	 *            the variable name (case sensitive!!)
	 * @param def
	 *            the value to return if the given COOKIE doesn't exist.
	 * @return the value of the COOKIE variable or def if not found
	 */
	public String getCookie(String id, String def) {
		if (!cookies.containsKey(id))
			return def;
		Object o = cookies.get(id);
		if (o instanceof ArrayList) {
			ArrayList arr = (ArrayList) o;
			return (String) arr.get(arr.size() - 1);
		}
		return (String) o;
	}

	/**
	 * this method returns the value of a COOKIE variable at the given index.
	 * You can
	 * 
	 * @param id
	 *            the variable name (case sensitive !!)
	 * @param index
	 *            the index of the given get variable
	 * @param def
	 *            the value to return if the given COOKIE parameter doesn't
	 *            exist.
	 * @return the value of the given COOKIE variable or def if not found.
	 * @since V1.03
	 */
	public String getCookie(String id, int index, String def) {
		if (!cookies.containsKey(id))
			return def;
		Object o = cookies.get(id);
		if (o instanceof ArrayList) {
			ArrayList arr = (ArrayList) o;
			if (index >= arr.size())
				return def;
			return (String) arr.get(index);
		}
		return (String) o;
	}

	/**
	 * Returns the number of COOKIE values
	 * 
	 * @param id
	 *            the name of the COOKIE variable
	 * @return the number of times COOKIE given get variable is used.
	 * @since v1.03
	 */
	public int getCookieCount(String id) {
		if (!cookies.containsKey(id))
			return 0;
		Object o = cookies.get(id);
		if (o instanceof ArrayList)
			return ((ArrayList) o).size();
		return 1;
	}

	/**
	 * this method retrieves a given header
	 * 
	 * @param key
	 *            the name (case insensitive) of the header. (Inclusive the ':')
	 * @param def
	 *            the default value to return if the header doesn't exist
	 * @return the header value or def if the header doesn't exist.
	 */
	public String getHeader(String key, String def) {
		key = key.toLowerCase();
		if (!httpHeaders.containsKey(key))
			return def;
		return (String) httpHeaders.get(key);
	}

	/**
	 * this method returns the first line of the HTTP request. This usually is a
	 * GET or POST value
	 * 
	 * @return the raw http request
	 */
	public String getRawHttpRequest() {
		return rawRequest;
	}

	/**
	 * this method returns the raw URL. This is the url containing all GET
	 * variables and anchornames.
	 * 
	 * @return the raw URL
	 */
	public String getRawURL() {
		return rawurl;
	}

	/**
	 * This method returns the http request method. At the moment this only can
	 * be GET or POST.
	 * 
	 * @return the http request method. HTTP_METHOD_GET or HTTP_METHOD POST
	 */
	public int getHttpMethod() {
		return method;
	}

	/**
	 * this method returns the anchorname of the HTTP request. I can't imagine
	 * this would be very interesting for a webserver, but hey you never know.
	 * example of anchored url:<br/> /home/value.html#test<br/> here the
	 * anchor is 'test'.
	 * 
	 * @return the anchorname
	 */
	public String getAnchorName() {
		return anchorName;
	}

	/**
	 * this method returns the raw unparsed get string
	 * 
	 * @return the unpased get string. In the form getvar1=value1&getvar2=value2
	 */
	public String getRawGetString() {
		return rawGetString;
	}

	/**
	 * this method returns the raw unparsed post string
	 * 
	 * @return the unpased post string. In the form
	 *         postvar1=value1&postvar2=value2
	 */
	public String getRawPostString() {
		return rawPostString;
	}

	// //////////////////////////////////////////////////////////////////////////////
	// initialises the http request
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * this method parses the rawurl
	 * 
	 * @param rawurl
	 * @return the HTTP result code HttpUtils.HTTP_OK on success
	 */
	private int parseUrl(String rawurl) {
		// remove anchor tag
		int hekje = rawurl.indexOf('#');
		location = rawurl;
		if (hekje >= 0) {
			anchorName = location.substring(hekje + 1);
			location = location.substring(0, hekje);
		} else
			anchorName = "";
		// get 'GET' part
		int quest = location.indexOf('?');
		if (quest >= 0) {
			rawGetString = location.substring(quest + 1);
			location = location.substring(0, quest);
			HttpUtils.parseString(rawGetString, get, true);
		} else
			rawGetString = "";
		return HttpResponse.HTTP_OK;
	}

	/**
	 * this method parses the first line of the http request
	 * 
	 * @param line
	 *            the line to parse
	 * @return the HTTP result code HttpUtils.HTTP_OK on success
	 */
	private int parseRequest(String line) {
		// copy the raw request
		rawRequest = line;

		// parse the line into components, check if there are enough components
		String[] items = StringUtils.explode(" ", line);
		if (items.length < 2)
			return HttpResponse.HTTP_BAD_REQUEST;
		items[0] = items[0].toUpperCase();

		// select the correct method
		if (items[0].equals("GET"))
			method = HTTP_METHOD_GET;
		else if (items[0].equals("POST"))
			method = HTTP_METHOD_POST;
		else if (items[0].equals("HEAD"))
			method = HTTP_METHOD_HEAD;
		else
			return HttpResponse.HTTP_BAD_METHOD;

		// the raw url is directly placed at position 1
		rawurl = items[1];

		// parse the get methods
		parseUrl(rawurl);

		// return ok
		return HttpResponse.HTTP_OK;
	}

	/**
	 * This method is used internally by the webserver to initialise a http
	 * request.
	 * 
	 * @param input
	 *            the inputstream to parse.
	 * @param socket
	 *            the socket we are connected to
	 * @param keepAliveRequest
	 *            is it a keepalive request (which means a timeout of the first
	 *            readline isn't a problem)
	 * @return the HTTP result code HttpUtils.HTTP_OK on success returns 0 if
	 *         it's a keepAliveRequest and no request is recieved
	 * @throws IOException
	 *             on a communnications error
	 */
	public int init(InputStream input, Socket socket, boolean keepAliveRequest)
			throws IOException {
		this.ipaddress = socket.getInetAddress().getHostAddress();
		this.byteipaddress = socket.getInetAddress().getAddress();

		BufferedReader reader = new BufferedReader(new InputStreamReader(input));

		// first read and parse the first line
		String line = null;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			if (keepAliveRequest)
				return 0;
			throw e;
		}
		if (line == null && keepAliveRequest)
			return 0;

		int result = parseRequest(line);
		if (result != HttpResponse.HTTP_OK)
			return result;

		// read the header
		line = reader.readLine();
		while (line != null && !line.equals("")) {

			int spacePos = line.indexOf(' ');
			if (spacePos >= 0) {
				String key = line.substring(0, spacePos).toLowerCase();
				httpHeaders.put(key, line.substring(spacePos + 1));
			}
			line = reader.readLine();
		}

		// if method is post, we need to get the post values
		if (method == HTTP_METHOD_POST) {

			// File upload code from Herbert Poul
			if (!httpHeaders.containsKey("content-length:")) {
				return HttpResponse.HTTP_LENGTH_REQUIRED;
			}
			int len = HttpUtils.parseInt((String) httpHeaders
					.get("content-length:"), 10, 0);
			boolean handledPost = false;

			if (httpHeaders.containsKey("content-type:")) {
				String test = (String) httpHeaders.get("content-type:");

				if (test.toLowerCase().startsWith("multipart/form-data")) {
					String boundary = "--"
							+ test.replaceAll(
									"^.*boundary=\"?([^\";,]+)\"?.*$", "$1");
					int blength = boundary.length();
					boolean done = false;

					if (blength > 80 || blength < 2) {
						return HttpResponse.HTTP_BAD_REQUEST;
					}

					rawPostString = "";
					if (boundary != null && !boundary.equals("")) {
						line = reader.readLine();
						while (!done && line != null) {
							HashMap<String, String> header = new HashMap<String, String>();
							if (line.equals(boundary))
								line = reader.readLine();
							// Header loop...
							while (!line.equals("")) {
								header.put(line.substring(0, line.indexOf(' '))
										.toLowerCase(), line.substring(line
										.indexOf(' ') + 1));
								line = reader.readLine();
							}

							// Done reading header ... reading body
							String disposition = (String) header
									.get("content-disposition:");
							String name = disposition.replaceAll(
									"^.* name=\"?([^\";]*)\"?.*$", "$1");
							String filename = disposition.replaceAll(
									"^.* filename=\"?([^\";]*)\"?.*$", "$1");

							if (filename != null
									&& filename.equals(disposition))
								filename = null;
							// normal field
							if (filename == null) // || filename.equals(""))
							{
								line = reader.readLine();
								String value = new String("");
								// shouldn't be used more than once.
								while (line != null
										&& !line.startsWith(boundary)) {
									value += line;
									line = reader.readLine();
								}
								if (line != null
										&& line.equals(boundary + "--"))
									done = true;

								if (rawPostString.length() > 0)
									rawPostString += "&";
								try {
									rawPostString += name
											+ "="
											+ java.net.URLEncoder.encode(value,
													"UTF-8");
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
								HttpUtils.addToHashMap(post, name, value);
							}
							// file control
							else {
								int fileResult = HttpFileUpload.RESULT_NOFILE;
								// should we create a temporary file ?
								File tempfile = null;
								FileOutputStream out = null;
								if (!filename.equals("")) {
									tempfile = File.createTempFile("httpd",
											".file");
									out = new FileOutputStream(tempfile);
								}

								// read the data until we much the content
								// boundary
								line = null;
								boolean matched = false;
								char testa[] = new char[blength + 2];
								char boundarya[] = ("\r\n" + boundary)
										.toCharArray();
								for (int i = 0; i < blength + 2; i++) {
									int c = input.read();
									if (c < 0) {
										return HttpResponse.HTTP_BAD_REQUEST;
									}
									testa[i] = (char) c;
								}
								while (!Arrays.equals(testa, boundarya)) {
									// While we don't reached the maximum we can
									// write to the temp file
									if (tempfile != null
											&& tempfile.length() < httpServerThread.getServer()
													.getMaxUploadSize()) {
										out.write(testa[0]);
									}

									for (int i = 1; i < blength + 2; i++) {
										testa[i - 1] = testa[i];
									}
									int c = input.read();
									if (c < 0) {
										return HttpResponse.HTTP_BAD_REQUEST;
									}
									testa[blength + 2 - 1] = (char) c;
								}
								// close tempfile
								if (tempfile != null) {
									out.close();

									// read the data
									if (tempfile != null
											&& tempfile.length() > 0) {
										fileResult = HttpFileUpload.RESULT_OK;
									}
									if (tempfile != null
											&& tempfile.length() > httpServerThread.getServer()
													.getMaxUploadSize()) {
										fileResult = HttpFileUpload.RESULT_SIZE;
									}
								}

								// read the newline and check the end
								int c = input.read();
								if (c != '\r') {
									if (c == '-' && input.read() == '-')
										done = true;
								} else
									c = input.read();
								if (c == -1)
									done = true;
								line = reader.readLine();

								this.postFiles.put(name, new HttpFileUpload(
										filename, tempfile, fileResult));
							}
							// while(line != null && line.equals("")) line =
							// input.readLine();
						}

						handledPost = true;
					} // boundry != null
				} // multi-form part
			} // Headers contains content-type?

			if (!handledPost) {
				line = "";
				for (int i = 0; i < len; i++) {
					int c = input.read();
					if (c < 0)
						break;
					line += (char) c;
				}
				rawPostString = line;
				HttpUtils.parseString(rawPostString, post, true);
			} // handledPost?
		} // POST?

		// go and retrieves the cookies
		if (httpHeaders.containsKey("cookie:")) {
			HttpUtils.parseString((String) httpHeaders.get("cookie:"), cookies,
					true, ";");
		}

		keepAlive = ( getHeader("connection:","").toLowerCase().startsWith("keep-alive") );
		
		// if post, we should retrieve the postdata
		return HttpResponse.HTTP_OK;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}
	
}
