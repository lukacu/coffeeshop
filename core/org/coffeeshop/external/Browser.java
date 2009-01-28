/* THIS FILE IS A MEMBER OF THE COFFEESHOP LIBRARY
 * 
 * License:
 * 
 * Coffeeshop is a conglomerate of handy general purpose Java classes.  
 * 
 * Copyright (C) 2006-2008 Luka Cehovin
 * This library is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation; either version 2.1 of 
 *  the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 *  GNU Lesser General Public License for more details. 
 *  
 *  http://www.opensource.org/licenses/lgpl-license.php
 *  
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the 
 *  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA 
 * 
 */

package org.coffeeshop.external;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.StringTokenizer;
import java.util.Vector;

import org.coffeeshop.external.OperatingSystem.*;

/**
 * This static class provides methods for invoking external browser commands
 * (souch as opening urls)
 * 
 * <b>Note:</b> Currently supported operating systems are:
 * <ul>
 * <li>Posix family</li>
 * <li>Windows (rundll32)</li>
 * <li>OS X (via Apple java extension classes)</li>
 * </ul>
 * 
 * This class is a member of CoffeeShop Core package.
 * 
 * @author Luka Cehovin
 * @author <a href="http://ostermiller.org/utils/Browser.html">Stephen
 *         Ostermiller</a>
 * @since CoffeeShop Core 1.0
 */
public class Browser {

	/**
	 * stores browsers found on computer (loaded dynamically)
	 */
	private static String[] execBrowsers = null;

	/**
	 * Opens an URL in an external browser.
	 * 
	 * @param url
	 *            location to open (must be a valid URL)
	 * @return generaly <code>true</code> if successful and <code>false</code>
	 *         if not (no browser found or other problems)
	 */
	public static boolean openURL(String url) {
		if (execBrowsers == null)
			execBrowsers = getBrowsers();

		if (execBrowsers.length == 0) {
			if (OperatingSystem.getOperatingSystemType() == OperatingSystemType.OSX) {
				boolean success = false;
				try {
					Class<?> nSWorkspace;
					if (new File(
							"/System/Library/Java/com/apple/cocoa/application/NSWorkspace.class")
							.exists()) {
						// Mac OS X has NSWorkspace, but it is not in the
						// classpath so we add it.
						ClassLoader classLoader = new URLClassLoader(
								new URL[] { new File("/System/Library/Java").toURI().toURL() });
						nSWorkspace = Class.forName(
								"com.apple.cocoa.application.NSWorkspace",
								true, classLoader);
					} else {
						nSWorkspace = Class
								.forName("com.apple.cocoa.application.NSWorkspace");
					}
					Method sharedWorkspace = nSWorkspace.getMethod(
							"sharedWorkspace", new Class[] {});
					Object workspace = sharedWorkspace.invoke(null,
							new Object[] {});
					Method openURL = nSWorkspace.getMethod("openURL",
							new Class[] { Class.forName("java.net.URL") });


					success = ((Boolean) openURL.invoke(workspace,
							new Object[] { new java.net.URL(url) }))
							.booleanValue();
					return success;
				} catch (Exception x) {
				}
				if (!success) {
					try {
						Class<?> mrjFileUtils = Class
								.forName("com.apple.mrj.MRJFileUtils");
						Method openURL = mrjFileUtils
								.getMethod("openURL", new Class[] { Class
										.forName("java.lang.String") });
						openURL.invoke(null, new Object[] { url });
						return true;
					} catch (Exception x) {
						return false;
					}
				}
			} else {
				return false;
			}
		} else {
			// for security, see if the url is valid.
			try {
				new URL(url);
			} catch (MalformedURLException e) {
				return false;
			}
			// escape any weird characters in the url. This is primarily
			// to prevent an attacker from putting in spaces
			// that might fool exec into allowing
			// the attacker to execute arbitrary code.
			StringBuffer sb = new StringBuffer(url.length());
			for (int i = 0; i < url.length(); i++) {
				char c = url.charAt(i);
				if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
						|| (c >= '0' && c <= '9') || c == '.' || c == ':'
						|| c == '&' || c == '@' || c == '/' || c == '?'
						|| c == '%' || c == '+' || c == '=' || c == '#'
						|| c == '-' || c == '\\') {
					// characters that are necessary for URLs and should be safe
					// to pass to exec. Exec uses a default string tokenizer
					// with
					// the default arguments (whitespace) to separate command
					// line
					// arguments, so there should be no problem with anything bu
					// whitespace.
					sb.append(c);
				} else {
					c = (char) (c & 0xFF); // get the lowest 8 bits
					// (URLEncoding)
					if (c < 0x10) {
						sb.append("%0" + Integer.toHexString(c));
					} else {
						sb.append("%" + Integer.toHexString(c));
					}
				}
			}
			String[] messageArray = new String[1];
			messageArray[0] = sb.toString();
			String command = null;
			boolean found = false;
			// try each of the exec commands until something works
			try {
				for (int i = 0; i < execBrowsers.length && !found; i++) {
					try {
						// stick the url into the command
						command = MessageFormat.format(execBrowsers[i],
								(Object[]) messageArray);
						// parse the command line.
						Vector<String> argsVector = new Vector<String>();
						StringTokenizer tokens = new StringTokenizer(command);
						while (tokens.hasMoreTokens()) {
							argsVector.add(tokens.nextToken());
						}
						String[] args = new String[argsVector.size()];
						args = (String[]) argsVector.toArray(args);
						// the windows url protocol handler doesn't work well
						// with file URLs.
						// Correct those problems here before continuing
						// Java File.toURL() gives only one / following file: but
						// we need two.
						// If there are escaped characters in the url, we will
						// have
						// to create an Internet shortcut and open that, as the
						// command
						// line version of the rundll doesn't like them.
						boolean useShortCut = false;
						if (args[0].equals("rundll32")
								&& args[1]
										.equals("url.dll,FileProtocolHandler")) {
							if (args[2].startsWith("file:/")) {
								if (args[2].charAt(6) != '/') {
									args[2] = "file://" + args[2].substring(6);
								}
								if (args[2].charAt(7) != '/') {
									args[2] = "file:///" + args[2].substring(7);
								}
								useShortCut = true;
							} else if (args[2].toLowerCase().endsWith("html")
									|| args[2].toLowerCase().endsWith("htm")) {
								useShortCut = true;
							}
						}
						if (useShortCut) {
							File shortcut = File.createTempFile(
									"OpenInBrowser", ".url");
							shortcut = shortcut.getCanonicalFile();
							shortcut.deleteOnExit();
							PrintWriter out = new PrintWriter(new FileWriter(
									shortcut));
							out.println("[InternetShortcut]");
							out.println("URL=" + args[2]);
							out.close();
							args[2] = shortcut.getCanonicalPath();
						}

						// start the browser
						Process p = Runtime.getRuntime().exec(args);

						// this is a weird case. The browser exited after
						// a couple seconds saying that it successfully
						// displayed the url. Either the browser is lying
						// or the user closed it *really* quickly. Oh well.
						if (p.exitValue() != 0) {
							return true;
						}

					} catch (IOException x) {
						// the command was not a valid command.
						return false;
					}
				}
				if (!found) {
					// we never found a command that didn't terminate with an
					// error.
					return false;
				}
			} catch (IllegalThreadStateException e) {
				return true;
				// the browser is still running. This is a good sign.
				// lets just say that it is displaying the url right now!
			}
		}
		return false;

	}

	/**
	 * Searches for browsers. It uses OS specific methods to find browsers that
	 * exist on the current computer.
	 * 
	 * @return array of available browser commands
	 */
	private static String[] getBrowsers() {
		String[] exec = null;
		if (OperatingSystem.getOperatingSystemType() == OperatingSystemType.WINDOWS) {
			// OS = Windows ... that is easy
			exec = new String[] { "rundll32 url.dll,FileProtocolHandler {0}", };
		} else if (OperatingSystem.getOperatingSystemType() == OperatingSystemType.OSX) {
			// OS = MacOS ... MacOS also provides a command for opening
			// a default browser
			Vector<String> browsers = new Vector<String>();
			try {
				Process p = Runtime.getRuntime().exec("which open");
				if (p.waitFor() == 0) {
					browsers.add("open {0}");
				}
			} catch (IOException e) {
			} catch (InterruptedException e) {
			}
			if (browsers.size() == 0) {
				exec = null;
			} else {
				exec = (String[]) browsers.toArray(new String[0]);
			}
		} else {
			/*
			 * OS = Linux ... etc.
			 * 
			 * Linux does not provide a generic command so it is up to us to
			 * find some browsers.
			 * 
			 * Order of search (note that the first of those browsers is used so
			 * the order is importiant): - Free desktop xdg-open, Mozilla Firefox (firefox,
			 * mozilla-firefox, firebird) - Mozilla (mozilla) - Opera (opera) -
			 * Galeon (galeon) - Konqueror (konqueror) - Netscape (netscape) -
			 * Lynx (lynx, links; must also have xterm)
			 */

			Vector<String> browsers = new Vector<String>();
			try {
				Process p = Runtime.getRuntime().exec("which xdg-open");
				if (p.waitFor() == 0) {
					browsers.add("xdg-open {0}");
				}
			} catch (IOException e) {
			} catch (InterruptedException e) {
			}
			try {
				Process p = Runtime.getRuntime().exec("which firefox");
				if (p.waitFor() == 0) {
					browsers.add("firefox {0}");
				}
			} catch (IOException e) {
			} catch (InterruptedException e) {
			}
			try {
				Process p = Runtime.getRuntime().exec("which mozilla-firefox");
				if (p.waitFor() == 0) {
					browsers.add("mozilla-firefox {0}");
				}
			} catch (IOException e) {
			} catch (InterruptedException e) {
			}
			try {
				Process p = Runtime.getRuntime().exec("which firebird");
				if (p.waitFor() == 0) {
					//browsers.add("firebird -remote openURL({0})");
					browsers.add("firebird {0}");
				}
			} catch (IOException e) {
			} catch (InterruptedException e) {
			}
			try {
				Process p = Runtime.getRuntime().exec("which mozilla");
				if (p.waitFor() == 0) {
					browsers.add("mozilla -remote openURL({0})");
					browsers.add("mozilla {0}");
				}
			} catch (IOException e) {
			} catch (InterruptedException e) {
			}

			try {
				Process p = Runtime.getRuntime().exec("which opera");
				if (p.waitFor() == 0) {
					browsers.add("opera -remote openURL({0})");
					browsers.add("opera {0}");
				}
			} catch (IOException e) {
			} catch (InterruptedException e) {
			}
			try {
				Process p = Runtime.getRuntime().exec("which galeon");
				if (p.waitFor() == 0) {
					browsers.add("galeon {0}");
				}
			} catch (IOException e) {
			} catch (InterruptedException e) {
			}
			try {
				Process p = Runtime.getRuntime().exec("which konqueror");
				if (p.waitFor() == 0) {
					browsers.add("konqueror {0}");
				}
			} catch (IOException e) {
			} catch (InterruptedException e) {
			}
			try {
				Process p = Runtime.getRuntime().exec("which netscape");
				if (p.waitFor() == 0) {
					browsers.add("netscape -remote openURL({0})");
					browsers.add("netscape {0}");
				}
			} catch (IOException e) {
			} catch (InterruptedException e) {
			}
			try {
				Process p = Runtime.getRuntime().exec("which xterm");
				if (p.waitFor() == 0) {
					p = Runtime.getRuntime().exec("which lynx");
					if (p.waitFor() == 0) {
						browsers.add("xterm -e lynx {0}");
					}
				}
			} catch (IOException e) {
			} catch (InterruptedException e) {
			}
			try {
				Process p = Runtime.getRuntime().exec("which xterm");
				if (p.waitFor() == 0) {
					p = Runtime.getRuntime().exec("which links");
					if (p.waitFor() == 0) {
						browsers.add("xterm -e links {0}");
					}
				}
			} catch (IOException e) {
			} catch (InterruptedException e) {
			}
			if (browsers.size() == 0) {
				exec = null;
			} else {
				exec = (String[]) browsers.toArray(new String[0]);
			}
		}
		return exec;
	}

}
