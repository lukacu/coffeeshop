package org.coffeeshop.net.http.server;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.coffeeshop.string.StringUtils;

public class HttpUtils {

	/**
	 * this method safely parses a long
	 * 
	 * @param value
	 *            the value to parse
	 * @param radix
	 *            the radix (use 10 for decimal, 16 for hexadecimal)
	 * @param def
	 *            the default value to return on a parse exception
	 * @return the parsed string ( or def if a parse exception occured)
	 */
	public static long parseLong(String value, int radix, long def) {
		try {
			return Long.parseLong(value, radix);
		} catch (Exception e) {
		}
		return def;
	}

	/**
	 * this method safely parses an integer
	 * 
	 * @param value
	 *            the string value to parse
	 * @param radix
	 *            the radix (use 10 for decimal, 16 for hexadecimal)
	 * @param def
	 *            the default value to return on a parse exception
	 * @return the parsed string ( or def if a parse exception occured)
	 */
	public static int parseInt(String value, int radix, int def) {
		try {
			return Integer.parseInt(value.trim(), radix);
		} catch (Exception e) {
		}
		return def;
	}



	// //////////////////////////////////////////////////////////////////////////////
	// parsing help
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * this method adds a variable to a HashMap. If the key already exists the
	 * current value is converted to an arraylist.
	 * 
	 * @param id
	 *            the variable name
	 * @param value
	 *            the value
	 * @since V1.03
	 */
	@SuppressWarnings("unchecked")
	public static void addToHashMap(HashMap<String, Object> map, String id, String value) {
		if (map.containsKey(id)) {
			Object o = map.get(id);
			if (o instanceof ArrayList) {
				((ArrayList<String>) o).add(value);
			} else {
				ArrayList<Object> arr = new ArrayList<Object>();
				arr.add(o);
				arr.add(value);
				map.put(id, arr);
			}
		} else {
			map.put(id, value);
		}
	}

	/**
	 * this method parses a raw put or get string an puts it in a hashmap
	 * 
	 * @param options
	 *            the put/get string. example: variable=x&variable=y
	 * @param output
	 *            the Hashmap to fill with the given variables. Every value of
	 *            the options are being URLDecoded.
	 */
	public static void parseString(String options, HashMap<String, Object> output) {
		parseString(options, output, false);
	}

	/**
	 * this method parses a raw put or get string an puts it in a hashmap
	 * 
	 * @param options
	 *            the put/get string. example: variable=x&variable=y
	 * @param output
	 *            the Hashmap to fill with the given variables. Every value of
	 *            the options are being URLDecoded.
	 * @param makeArrayList
	 *            set this method to true, to make an arraylist if a method is
	 *            used multiple times
	 * @since V1.03
	 */
	public static void parseString(String options, HashMap<String, Object> output,
			boolean makeArrayList) {
		parseString(options, output, makeArrayList, "&");
	}

	/**
	 * this method parses a raw put or get string an puts it in a hashmap
	 * 
	 * @param options
	 *            the put/get string. example: variable=x&variable=y
	 * @param output
	 *            the Hashmap to fill with the given variables. Every value of
	 *            the options are being URLDecoded.
	 * @param makeArrayList
	 *            set this method to true, to make an arraylist if a method is
	 *            used multiple times
	 * @param optionSeperator
	 *            added an option seperator so this method can also be used to
	 *            parse the cookies
	 * @since V1.03
	 */
	public static void parseString(String options, HashMap<String, Object> output,
			boolean makeArrayList, String optionSeperator) {
		String items[] = StringUtils.explode(optionSeperator, options);
		if (items != null && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				int equalPos = items[i].indexOf('=');
				if (equalPos != 0) {
					// parse the item in a key and a value
					String key = "", value = "";
					if (equalPos > 0) {
						key = items[i].substring(0, equalPos).trim();
						try {
							value = java.net.URLDecoder.decode(items[i]
									.substring(equalPos + 1), "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

					} else
						key = items[i];

					// add te item in an arraylist
					if (makeArrayList) {
						addToHashMap(output, key, value);
					}
					// add the value
					else {
						output.put(key, value);
					}
				}
			}
		}
	}

}