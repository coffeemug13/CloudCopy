/**
 * 
 */
package org.ccopy.resource.util;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

/**
 * This class represents a valid mimetype. You can create a MimeType only throw
 * factory methods which ensure that the mimetype string is valid.
 * 
 * @author coffeemug13
 * 
 */
public class MimeType {
	public static final String DEFAULT = "application/octet-stream";
	/**
	 * The internal String representation for this MimeType
	 */
	private String mimeType;
	/**
	 * The FileNameMap which holds the mapping between some extensions and their
	 * corresponding mimetype
	 */
	private static final FileNameMap mimeTypeMap = URLConnection.getFileNameMap();
	/**
	 * the predefined Pattern to match a mimetype
	 */
	private static Pattern mimeTypePattern = Pattern
			.compile("^[a-zA-Z0-9\\.\\(\\)\\+\\-]+/[a-zA-Z0-9\\.\\(\\)\\+\\-]+$");

	/**
	 * Private Constructor of MimeType
	 */
	public MimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Get a MimeType for a filename based on
	 * {@link java.net.FileNameMap#getContentTypeFor(String)}. This is the
	 * preferred method to generate a MimeType.
	 * 
	 * @param fileName
	 *            to find the MimeType for.
	 * @return a valid MimeType, with the default value {@value #DEFAULT} when
	 *         extension is unkown
	 * @throws NullPointerException
	 *             if argument is {@code null}
	 */
	public static MimeType fromFileName(String fileName) {
		// the method FileNameMap.getcontentTypeFor throws a
		// NullPointerException if argument is null. Therefore we delegate that
		// check to the function.
		String mt = mimeTypeMap.getContentTypeFor(fileName);
		return (null != mt) ? new MimeType(mt) : getDefault();
	}

	/**
	 * Validates a given mimeType. The mimeType will be checked according to the
	 * pattern from RFC 2046 and 2045 Pattern := type "/" subtype Allowed
	 * Character: a-zA-Z0-9-+.()
	 * 
	 * @see http://tools.ietf.org/html/rfc2046
	 * @see http://tools.ietf.org/html/rfc2045
	 * @param mimeType
	 *            a mimetype of pattern, e.g. "text/plain".
	 * @return a valid mimetype
	 * @throws NullPointerException
	 *             if argument is {@code null}
	 * @throws DataFormatException
	 *             if mimeType doesn't comply to RFC Standards
	 */
	public static String validate(String mimeType) throws DataFormatException {
		if (null == mimeType)
			throw new NullPointerException("The mimeType MUST not be null");
		Matcher m = mimeTypePattern.matcher(mimeType);
		if (m.matches())
			return mimeType;
		else
			throw new DataFormatException("mimetype format not correct:" + mimeType);
	}

	/**
	 * Get a default MimeType with {@link MimeType
	 * @return
	 */
	public static MimeType getDefault() {
		return new MimeType(MimeType.DEFAULT);
	}

	/**
	 * Get the mimetype as String
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return mimeType;
	}
}
