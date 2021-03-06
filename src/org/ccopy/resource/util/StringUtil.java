/**
 * 
 */
package org.ccopy.resource.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Helper class for manipulating streams
 * 
 * @author coffeemug13
 * 
 */
public class StringUtil {
	/**
	 * List for Hex values containing lower case values
	 */
	private static final char[] HEX_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b',
			'c', 'd', 'e', 'f' };

	/**
	 * Read an InputStream recursive and return the full String. The InputStream
	 * will be automatically closed
	 * 
	 * @param in
	 *            the InputStream you wish to convert to a String.
	 * @return the String or {@code null}
	 * @throws IOException
	 *             in case of connectivity problems
	 */
	static public String streamToString(InputStream in) throws IOException {
		if (null == in)
			return null;
		StringBuffer buf = new StringBuffer();
		int read;
		byte[] c = new byte[100]; // with increasing value speed goes up
		while ((read = in.read(c)) != -1) {
			buf.append(new String(c, 0, read,"UTF-8"));
		}
		in.close();
		return buf.toString();
	}
	/**
	 * Read a String an return an InputStream to this String.
	 * @param string
	 * @param charset
	 * @return
	 */
	static public InputStream stringToStream(String string, String charset) {
		if (null == string)
			throw new NullPointerException();
		try {
			if (null != charset)
				return new ByteArrayInputStream(string.getBytes(charset));
			else
				return new ByteArrayInputStream(string.getBytes());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new Error(e);
		}
	}

	/**
	 * Iterate throw a map and return a nice formated String
	 * 
	 * @param map
	 * @return
	 */
	static public String mapToString(Map<String, List<String>> map) {
		if (null == map)
			return null;
		StringBuffer buf = new StringBuffer();
		Set<Entry<String, List<String>>> mapSet = map.entrySet();
		Iterator<Entry<String, List<String>>> mapIterator = mapSet.iterator();
		int i = 0;
		while (mapIterator.hasNext()) {
			// the "if" ensures, that the last char is not a "\n"
			if (i > 0)
				buf.append("\n");
			else
				i = 1;
			// now proceed
			Map.Entry<String, List<String>> e = mapIterator.next();
			if (e.getKey() != null) {
				buf.append("* " + e.getKey() + ": ");
			} else
				buf.append("* ");
			Iterator<String> val = e.getValue().iterator();
			while (val.hasNext()) {
				buf.append(val.next() + " ");
			}
		}
		return buf.toString();
	}

	/**
	 * Convert a Byte Array to a Char Array containing the corresponding Hex
	 * values. Typical usage in MD5 calculation
	 * 
	 * @param in
	 *            the Byte Array to convert
	 * @return
	 */
	static public char[] byteToHex(byte[] in) {
		// we need the double size of the byte array
		int l = in.length;
		char[] out = new char[l << 1]; // double the size
		// iterate through
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = HEX_LOWER[(0xF0 & in[i]) >>> 4];
			out[j++] = HEX_LOWER[0x0F & in[i]];
		}
		return out;
	}

	/**
	 * Convert a Byte Array to a Hex-String. Typical usage in MD5 calculation
	 * 
	 * @param in
	 *            the Byte Array to convert
	 * @return
	 */
	static public String bytToHexString(byte[] in) {
		return new String(byteToHex(in));
	}

	public static String exceptionToString(Exception e) {
		StackTraceElement[] st = e.getStackTrace();
		StringBuffer buf = new StringBuffer();
		buf.append(e.getClass().getName() + " - ");
		if (null!= e.getMessage()) buf.append(e.getMessage());
		buf.append("\n");
		for (int i = 0;(i<st.length)&&(i<6);i++){
			buf.append(st[i].toString());
			buf.append("\n");
		}
		if (null!= e.getCause()) {
			buf.append("\nCause: " + e.getClass().getName() + " - ");
			buf.append( e.getCause().getMessage());
			st = e.getCause().getStackTrace();
			for (int i = 0;(i<st.length)&&(i<4);i++){
				buf.append(st[i].toString());
				buf.append("\n");
			}
		}
		return buf.toString();
	}
	public static String join(List<String> list) {
		if (null == list) return null;
		StringBuffer buf = new StringBuffer();
		boolean firstLine = true;
		for(String s : list) {
			if (!firstLine) buf.append(";");
			buf.append(s);
		}
		return buf.toString();
	}
}
