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

package org.coffeeshop.string;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class can be used to manipulate certain kinds of escape sequences in the
 * text (add or remove them).
 * 
 * Class is a member of CoffeeShop Core package.
 * 
 * @author Luka Cehovin
 * @since CoffeeShop Core 1.0
 */
public class EscapeSequences {

	/**
	 * The Constant AMPERSAND_PATTERN.
	 */
	private static final Pattern AMPERSAND_PATTERN = Pattern
			.compile("&((?:#\\d{0,4})|(?:[a-zA-Z]+));");

	/**
	 * The ampersand map.
	 */
	private static Map<String, Character> ampersandMap = new TreeMap<String, Character>();

	static {
		// thanks to Fabian M. Suchanek http://www.mpi-inf.mpg.de/~suchanek/ for this data
		// that would take some time to write...
		
		Object[] o = new Object[] { "nbsp", (char) 160, "iexcl", (char) 161,
				"cent", (char) 162, "pound", (char) 163, "curren", (char) 164,
				"yen", (char) 165, "brvbar", (char) 166, "sect", (char) 167,
				"uml", (char) 168, "copy", (char) 169, "ordf", (char) 170,
				"laquo", (char) 171, "not", (char) 172, "shy", (char) 173,
				"reg", (char) 174, "macr", (char) 175, "deg", (char) 176,
				"plusmn", (char) 177, "sup2", (char) 178, "sup3", (char) 179,
				"acute", (char) 180, "micro", (char) 181, "para", (char) 182,
				"middot", (char) 183, "cedil", (char) 184, "sup1", (char) 185,
				"ordm", (char) 186, "raquo", (char) 187, "frac14", (char) 188,
				"frac12", (char) 189, "frac34", (char) 190, "iquest",
				(char) 191, "Agrave", (char) 192, "Aacute", (char) 193,
				"Acirc", (char) 194, "Atilde", (char) 195, "Auml", (char) 196,
				"Aring", (char) 197, "AElig", (char) 198, "Ccedil", (char) 199,
				"Egrave", (char) 200, "Eacute", (char) 201, "Ecirc",
				(char) 202, "Euml", (char) 203, "Igrave", (char) 204, "Iacute",
				(char) 205, "Icirc", (char) 206, "Iuml", (char) 207, "ETH",
				(char) 208, "Ntilde", (char) 209, "Ograve", (char) 210,
				"Oacute", (char) 211, "Ocirc", (char) 212, "Otilde",
				(char) 213, "Ouml", (char) 214, "times", (char) 215, "Oslash",
				(char) 216, "Ugrave", (char) 217, "Uacute", (char) 218,
				"Ucirc", (char) 219, "Uuml", (char) 220, "Yacute", (char) 221,
				"THORN", (char) 222, "szlig", (char) 223, "agrave", (char) 224,
				"aacute", (char) 225, "acirc", (char) 226, "atilde",
				(char) 227, "auml", (char) 228, "aring", (char) 229, "aelig",
				(char) 230, "ccedil", (char) 231, "egrave", (char) 232,
				"eacute", (char) 233, "ecirc", (char) 234, "euml", (char) 235,
				"igrave", (char) 236, "iacute", (char) 237, "icirc",
				(char) 238, "iuml", (char) 239, "eth", (char) 240, "ntilde",
				(char) 241, "ograve", (char) 242, "oacute", (char) 243,
				"ocirc", (char) 244, "otilde", (char) 245, "ouml", (char) 246,
				"divide", (char) 247, "oslash", (char) 248, "ugrave",
				(char) 249, "uacute", (char) 250, "ucirc", (char) 251, "uuml",
				(char) 252, "yacute", (char) 253, "thorn", (char) 254, "yuml",
				(char) 255, "fnof", (char) 402, "Alpha", (char) 913, "Beta",
				(char) 914, "Gamma", (char) 915, "Delta", (char) 916,
				"Epsilon", (char) 917, "Zeta", (char) 918, "Eta", (char) 919,
				"Theta", (char) 920, "Iota", (char) 921, "Kappa", (char) 922,
				"Lambda", (char) 923, "Mu", (char) 924, "Nu", (char) 925, "Xi",
				(char) 926, "Omicron", (char) 927, "Pi", (char) 928, "Rho",
				(char) 929, "Sigma", (char) 931, "Tau", (char) 932, "Upsilon",
				(char) 933, "Phi", (char) 934, "Chi", (char) 935, "Psi",
				(char) 936, "Omega", (char) 937, "alpha", (char) 945, "beta",
				(char) 946, "gamma", (char) 947, "delta", (char) 948,
				"epsilon", (char) 949, "zeta", (char) 950, "eta", (char) 951,
				"theta", (char) 952, "iota", (char) 953, "kappa", (char) 954,
				"lambda", (char) 955, "mu", (char) 956, "nu", (char) 957, "xi",
				(char) 958, "omicron", (char) 959, "pi", (char) 960, "rho",
				(char) 961, "sigmaf", (char) 962, "sigma", (char) 963, "tau",
				(char) 964, "upsilon", (char) 965, "phi", (char) 966, "chi",
				(char) 967, "psi", (char) 968, "omega", (char) 969, "thetasym",
				(char) 977, "upsih", (char) 978, "piv", (char) 982, "bull",
				(char) 8226, "hellip", (char) 8230, "prime", (char) 8242,
				"Prime", (char) 8243, "oline", (char) 8254, "frasl",
				(char) 8260, "weierp", (char) 8472, "image", (char) 8465,
				"real", (char) 8476, "trade", (char) 8482, "alefsym",
				(char) 8501, "larr", (char) 8592, "uarr", (char) 8593, "rarr",
				(char) 8594, "darr", (char) 8595, "harr", (char) 8596, "crarr",
				(char) 8629, "lArr", (char) 8656, "uArr", (char) 8657, "rArr",
				(char) 8658, "dArr", (char) 8659, "hArr", (char) 8660,
				"forall", (char) 8704, "part", (char) 8706, "exist",
				(char) 8707, "empty", (char) 8709, "nabla", (char) 8711,
				"isin", (char) 8712, "notin", (char) 8713, "ni", (char) 8715,
				"prod", (char) 8719, "sum", (char) 8721, "minus", (char) 8722,
				"lowast", (char) 8727, "radic", (char) 8730, "prop",
				(char) 8733, "infin", (char) 8734, "ang", (char) 8736, "and",
				(char) 8743, "or", (char) 8744, "cap", (char) 8745, "cup",
				(char) 8746, "int", (char) 8747, "there4", (char) 8756, "sim",
				(char) 8764, "cong", (char) 8773, "asymp", (char) 8776, "ne",
				(char) 8800, "equiv", (char) 8801, "le", (char) 8804, "ge",
				(char) 8805, "sub", (char) 8834, "sup", (char) 8835, "nsub",
				(char) 8836, "sube", (char) 8838, "supe", (char) 8839, "oplus",
				(char) 8853, "otimes", (char) 8855, "perp", (char) 8869,
				"sdot", (char) 8901, "lceil", (char) 8968, "rceil",
				(char) 8969, "lfloor", (char) 8970, "rfloor", (char) 8971,
				"lang", (char) 9001, "rang", (char) 9002, "loz", (char) 9674,
				"spades", (char) 9824, "clubs", (char) 9827, "hearts",
				(char) 9829, "diams", (char) 9830, "quot", (char) 34, "amp",
				(char) 38, "lt", (char) 60, "gt", (char) 62, "OElig",
				(char) 338, "oelig", (char) 339, "Scaron", (char) 352,
				"scaron", (char) 353, "Yuml", (char) 376, "circ", (char) 710,
				"tilde", (char) 732, "ensp", (char) 8194, "emsp", (char) 8195,
				"thinsp", (char) 8201, "zwnj", (char) 8204, "zwj", (char) 8205,
				"lrm", (char) 8206, "rlm", (char) 8207, "ndash", (char) 8211,
				"mdash", (char) 8212, "lsquo", (char) 8216, "rsquo",
				(char) 8217, "sbquo", (char) 8218, "ldquo", (char) 8220,
				"rdquo", (char) 8221, "bdquo", (char) 8222, "dagger",
				(char) 8224, "Dagger", (char) 8225, "permil", (char) 8240,
				"lsaquo", (char) 8249, "rsaquo", (char) 8250, "euro",
				(char) 8364,
				// extra - don't know if it is in the standard
				"apos", '\''
				};

		for (int i = 0; i < o.length; i += 2)
			ampersandMap.put((String) o[i], (Character) o[i + 1]);

	}

	/**
	 * Strip the ampersand sequence (used in HTML to encode all kinds of unusual
	 * symbols).
	 * 
	 * @param s
	 *            string with ampersand sequences
	 * 
	 * @return string without ampersand sequences (converted to normal symbols)
	 */
	public static String stripAmpersandSequence(String s) {

		if (s == null) return null;
		if (s.length() == 0) return s;
		
		Matcher m = AMPERSAND_PATTERN.matcher(s);

		StringBuffer result = new StringBuffer();

		int start = 0;

		while (m.find()) {

			result.append(s.substring(start, m.start()));

			if (m.group(1).startsWith("#")) {

				int c = Integer.parseInt(m.group(1).substring(1));

				result.append((char) c);

			} else {
				Character c = ampersandMap.get(m.group(1));

				if (c != null)
					result.append(c.charValue());
				else
					result.append(m.group());

			}

			start = m.end();
		}

		if (start < s.length())
			result.append(s.substring(start));

		return result.toString();

	}

}
