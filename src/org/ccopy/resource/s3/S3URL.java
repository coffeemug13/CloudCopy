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

	private URL url;
	/**
	 * the default hostname for S3
	 */
	private static final String s3Host = "s3.amazon.com";
	private static final String s3Protocol = "https";
	/**
	 * The path element for a S3 REST request MUST be encoded in US-ASCII. This is a requirement
	 * from S3 API
	 */
	private static final String S3_ENCODING = "UTF-8";

	/**
	 * Constructs a S3URL from a URL with the URL parts (protocol, host, path) by omitting all other
	 * parts. The [path] get's URLEncoded, the other parts are encoded by passing through URI.
	 * 
	 * @param url
	 * @throws MalformedURLException
	 */
	public S3URL(URL url) throws MalformedURLException {
		try {
			this.url = new URL(url.getProtocol(), url.getHost(), URLEncoder.encode(url.getPath(),
					S3_ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new ResourceError(ResourceError.ERROR_JVM,"can't encode the S3 URL because '"+ S3_ENCODING + "' is not supported", e);
		}
	}

	/**
	 * Constructs a S3URL from a String.
	 * 
	 * @see #S3URL(URL)
	 * @param url
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	public S3URL(String url) throws MalformedURLException, URISyntaxException {
		this(new URL(url));
	}

	/**
	 * Constructs from a parent S3URL a child S3URL
	 * 
	 * @param s3
	 * @param child
	 * @throws MalformedURLException
	 */
	protected S3URL(S3Resource s3, String child) throws MalformedURLException {
		if ((s3 == null) || (child == null))
			throw new NullPointerException("both arguments must not be null");
		try {
			String parent = s3.toURL().getPath();
			if (!parent.endsWith(SEPERATOR))
				throw new IllegalArgumentException("parent URL must be an directory");
			// only encode the "child" name but not the pathname of the parent
			this.url = new URL(s3.toURL().getProtocol(), s3.toURL().getHost(), parent
					+ URLEncoder.encode(child, S3_ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new ResourceError(ResourceError.ERROR_JVM,"can't encode the S3 URL because '"+ S3_ENCODING + "' is not supported", e);
		}
	}

	public static S3URL fromPath(String bucket, String path) throws MalformedURLException,
			URISyntaxException {
		return new S3URL(new URL(s3Protocol, bucket + "." + s3Host, path));
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
			throw new ResourceError(ResourceError.ERROR_JVM,"can't decode the S3 URL because '"+ S3_ENCODING + "' is not supported", e);
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
