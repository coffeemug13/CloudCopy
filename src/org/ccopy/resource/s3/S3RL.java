/**
 * 
 */
package org.ccopy.resource.s3;

import java.io.UnsupportedEncodingException;
import java.net.*;

import org.ccopy.resource.ResourceLocator;

/**
 * @author mholakovsky
 * 
 */
public class S3RL implements ResourceLocator {
	/**
	 * the valid URL for the S3 object
	 */
	private URL url;
	/**
	 * the default hostname for S3
	 */
	private static final String s3Host = "s3.amazon.com";
	private static final String s3Protocol = "https";
	/**
	 * The path element for a S3 REST request MUST be encoded in US-ASCII. This
	 * is a requirement from S3 API
	 */
	private static final String S3_ENCODING = "UTF-8";

	/**
	 * Constructs a S3URL from a URL with the URL parts (protocol, host,
	 * path) by omitting all other parts. The [path] get's URLEncoded.
	 * 
	 * @param url
	 * @throws URISyntaxException
	 *             in case of malformed URI
	 * @throws MalformedURLException
	 */
	public S3RL(URL url) throws MalformedURLException {
		try {
			this.url = new URL(url.getProtocol(), url.getHost(), URLEncoder
					.encode(url.getPath(), S3_ENCODING));
//			        .encode(url.getPath(), S3_ENCODING).replace("%2F", "/"));
		} catch (UnsupportedEncodingException e) {
			// unrecoverable error = you can't build a correct S3 URL!!!!
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Constructs a S3URL from a String.
	 * 
	 * @see #S3URL(URL)
	 * @param url
	 * @throws MalformedURLException
	 */
	public S3RL(String url) throws MalformedURLException {
		this(new URL(url));
	}
	public static S3RL fromPath(String bucket, String path) throws MalformedURLException {
		return new S3RL(new URL(s3Protocol,bucket + "." + s3Host, path));
	}
	/**
	 * Return the S3URL as a simple URL with the path element encoded after the
	 * S3 requirements
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
	public String toString() {
		try {
			return URLDecoder.decode(url.toString(), S3_ENCODING);
			//return URLDecoder.decode(url.toString().replace("/", "%2F"), S3_ENCODING);
		} catch (UnsupportedEncodingException e) {
			// This should never happen, because the S3URL has already used the
			// "US-ASCII" enocding
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	/**
	 * Return the path of this resource.
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

	@Override
	public String toExternalForm() {
		return url.toExternalForm();
	}
}
