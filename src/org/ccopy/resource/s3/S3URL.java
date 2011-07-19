/**
 * 
 */
package org.ccopy.resource.s3;

import java.io.UnsupportedEncodingException;
import java.net.*;

import org.ccopy.resource.ResourceError;

/**
 * This is a package internal helper class to ensure, that the URL for a S3 object is correctly
 * encoded. This is done only once, when creating this class, and then passed along the method
 * calls.
 * 
 * @author coffeemug13
 */
public class S3URL {
	/**
	 * The pathname separator for S3 pathnames
	 */
	protected static final String SEPERATOR = "/";
	/**
	 * indicates whether the URL is of type 'file'. Must be defined when constructing the instance!
	 */
	protected boolean isFile;
	/**
	 * the URL representing this S3URL
	 */
	private URL url;
	/**
	 * the default hostname for S3
	 */
	private static final String s3Host = "s3.amazonaws.com";
	/**
	 * the default protocol for request.
	 */
	private static final String s3Protocol = "https";
	/**
	 * The path element for a S3 REST request MUST be encoded in UTF-8. This is a requirement from
	 * S3 API
	 */
	private static final String S3_ENCODING = "UTF-8";

	/**
	 * Constructs a S3URL from a String. The string will be converted to an URL with the URL parts
	 * (protocol, host, path) by omitting all other parts. The [path] segment is expected to be NOT
	 * octet decoded because it will be reformatted (e.g. must start with "/") and URL encoded by
	 * this method, the other parts are encoded by passing through URI.
	 * 
	 * @see #S3URL(URL)
	 * @param url
	 * @throws MalformedURLException
	 *         when the string is not a well formed S3 URL
	 */
	public S3URL(String url) throws MalformedURLException {
		this(new URL(url));
	}

	/**
	 * Constructs a S3URL from a URL with the URL parts (protocol, host, path) by omitting all other
	 * parts. The [path] segment is expected to be NOT octet decoded because it will be reformatted
	 * (e.g. must start with "/") and URL encoded by this method, the other parts are encoded by
	 * passing through URI.
	 * 
	 * @see URLEncoder
	 * @param url
	 * @throws MalformedURLException
	 *         when the URL is not a well formed S3 URL
	 */
	public S3URL(URL url) throws MalformedURLException {
		this.url = new URL(url.getProtocol(), url.getHost(), this.encodeAbsoluteURLPath(url
				.getPath()));
	}

	/**
	 * Constructs from a parent S3URL a child S3URL
	 * 
	 * @param parent
	 * @param child
	 * @throws MalformedURLException
	 */
	protected S3URL(S3Resource parent, String child) throws MalformedURLException {
		if ((parent == null) || (child == null))
			throw new NullPointerException("both arguments must not be null");
		if (!parent.isDirectory())
			throw new IllegalArgumentException("parent URL must be an directory");
		// only encode the "child" name but not the pathname of the parent
		// append the octet encode child
		this.url = new URL(parent.toURL().getProtocol(), parent.toURL().getHost(), parent.toURL()
				.getPath() + this.encodeRelativeURLPath(child));
	}

	/**
	 * Returns a new S3URL from bucketname and S3 object key
	 * 
	 * @param bucket
	 *        the bucket name
	 * @param key
	 *        the key of the S3 object (must not be octet encoded)
	 * @return a new S3URL instance
	 * @throws MalformedURLException
	 */
	public static S3URL fromPath(String bucket, String key) throws MalformedURLException {
		return new S3URL(new URL(s3Protocol, bucket + "." + s3Host, key));
	}

	/**
	 * Return the S3URL as a simple URL with the path element encoded after the S3 requirements
	 * 
	 * @see #S3URL(URL)
	 * @return the URL with path octet-encoded
	 */
	public URL toURL() {
		return url;
	}

	/**
	 * Return the String representation of the S3URL.
	 * 
	 * @return url as string with the path element decoded from octet to UTF-8
	 */
	@Override
	public String toString() {
		try {
			return URLDecoder.decode(url.toString(), S3_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new ResourceError(ResourceError.ERROR_JVM, "can't decode the S3 URL because '"
					+ S3_ENCODING + "' is not supported", e);
		}
	}

	/**
	 * Return the path of this resource.
	 * 
	 * @see URL#getPath()
	 * @return the string representing the URL path of this resource (including octet encoding)
	 */
	public String getPath() {
		return url.getPath();
	}

	/**
	 * Reformat the S3URL to locate a directory resource, i.e. contains an octet encoded trailing
	 * "/"
	 */
	protected void encodeAsDirectory() {
		try {
			if (this.isFile) {
				// add a trailing, octet encoded "/" to the path and rebuild the URL
				this.url = new URL(url.getProtocol(), url.getHost(), this.url.getPath() + "%2F");
			}
		} catch (MalformedURLException e) {
			throw new ResourceError(ResourceError.ERROR,
					"uups, this should never happens! Just added '%2F' to the URL path", e);
		}
	}

	/**
	 * Reformat the S3URL to locate a file resource, i.e. doesn't contain an octet encoded trailing
	 * "/"
	 */
	protected void encodeAsFile() {
		try {
			if (!this.isFile) {
				// remove the trailing, octet encoded "/" from the path and rebuild the URL
				this.url = new URL(url.getProtocol(), url.getHost(), this.url.getPath().substring(
						0, this.url.getPath().length() - 3));
			} // else do nothing
		} catch (MalformedURLException e) {
			throw new ResourceError(ResourceError.ERROR,
					"uups, this should never happens! Just added '%2F' to the URL path", e);
		}
	}

	/**
	 * URLEncode the path segment from an URL and ensure that it is absolute (i.e. starts with an
	 * {@value #SEPERATOR}) and doesn't contain two following path separators "//". This method also
	 * init's the class variable {@link #isFile} depending on the ending of the path. Trailing
	 * {@value #SEPERATOR} indicates a directory
	 * <p>
	 * The path argument <em>MUST NOT</em> be octet encoded!
	 * 
	 * @param path
	 *        the path to URL encode
	 * @return the cleaned and encoded path
	 * @throws MalformedURLException
	 *         when the path is not well formed
	 */
	private String encodeAbsoluteURLPath(String path) throws MalformedURLException {
		// when empty return an absolute path
		if (0 == path.length()) return S3URL.SEPERATOR;
		// ensure the pathname is absolute and start with a "/"
		if (!path.startsWith(S3URL.SEPERATOR))
			path = S3URL.SEPERATOR + path;
		// check whether the URL denotes a directory, i.e. contains trailing "/"
		if (!path.endsWith(S3URL.SEPERATOR))
			this.isFile = true;
		// the path must have in between two "/" at least one char, because the directory needs a
		// name
		if (path.contains("//"))
			throw new MalformedURLException(
					"the URL has an invalid path segement, because it contains two immediate following '/'");
		try {
			// now octet encode the path
			return URLEncoder.encode(path, S3_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new ResourceError(ResourceError.ERROR_JVM, "can't encode the S3 URL because '"
					+ S3_ENCODING + "' is not supported", e);
		}
	}

	/**
	 * URLEncode the path segment from an URL and ensure that it is relative (i.e. doesn't start
	 * with an {@value #SEPERATOR}) and doesn't contain two following path separators "//". This
	 * method also init's the class variable {@link #isFile} depending on the ending of the path.
	 * Trailing {@value #SEPERATOR} indicates a directory
	 * <p>
	 * The path argument <em>MUST NOT</em> be octet encoded!
	 * 
	 * @param path
	 *        the path to URL encode
	 * @return the cleaned and encoded path
	 * @throws MalformedURLException
	 *         when the path is not well formed
	 */
	private String encodeRelativeURLPath(String path) throws MalformedURLException {
		// ensure the pathname is relative and doesn't start with a "/"
		if (path.startsWith(S3URL.SEPERATOR))
			path = path.substring(1, path.length());
		// check whether the URL denotes a directory, i.e. contains trailing "/"
		if (!path.endsWith(S3URL.SEPERATOR))
			this.isFile = true;
		// the path must have in between two "/" at least one char, because the directory needs a
		// name
		if (path.contains("//"))
			throw new MalformedURLException(
					"the URL has an invalid path segement, because it contains two immediate following '/'");
		try {
			// now octet encode the path
			return URLEncoder.encode(path, S3_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new ResourceError(ResourceError.ERROR_JVM, "can't encode the S3 URL because '"
					+ S3_ENCODING + "' is not supported", e);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return url.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return url.equals(obj);
	}

	public String toExternalForm() {
		return url.toExternalForm();
	}
}
