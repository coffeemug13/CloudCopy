/**
 * 
 */
package org.ccopy.resource;

import java.net.URL;

/**
 * The {@code ResourceLocator} represents a specialized Uniform Resource Locator
 * {@link URL}, a pointer to a "resource" to <b>specific Services</b> in the
 * World Wide Web. The pattern of the {@code SRL} follows the implementation of
 * {@link URL} which is based on <a
 * href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>.
 * <p>
 * The syntax of the ResourceLocator:<br>
 * {@code [scheme:][//[user-info@]host[:port]][/path][?query][#fragment]} <br>
 * Example: <br>
 * {@code [https:][//[][bucketname.s3.amazon.com][][/my_file.txt][][]}
 * <p>
 * It is up to the implementing Class to suppress specific parts of the syntax
 * or implement constraints on the path to comply with the API of certain
 * services, e.g. to locate a resource in S3 you don't need the [query] or
 * [fragment] part.
 * <p>
 * One important difference to the {@code Resource} is, that {@code ResourceURL}
 * doesn't have an understanding whether a resource is a directory or file and
 * what a "filename" is. It only "locates" one Resource! The {@code Resource}
 * interprets this resource locator
 * 
 * @author mholakovsky
 * 
 */
public interface ResourceLocator {
	/**
	 * Gets the path part of this <code>ResourceURL</code>.
	 * 
	 * @return
	 */
	public String getPath();

	/**
	 * Constructs a URL that represent this Resource. Don't mistake that as
	 * working URL to make a request.
	 * 
	 * @return
	 */
	public URL toURL();

	/**
	 * Compares this ResourceLocator with an other object.
	 * 
	 * @param obj
	 *            - the object to compare with
	 * @return {@code true} if obj is {@code !null} and is a
	 *         {@code ResourceLocator} or {@code URL} object which has the same
	 *         hash code
	 */
	public boolean equals(Object obj);

	/**
	 * Computes the hash code for this resource based on the internal URL
	 * representation
	 * 
	 * @return the hash code
	 */
	public int hashCode();

	/**
	 * Constructs a string representation of this <code>ResourceLocator</code>.
	 * 
	 * @return a string representing this resource URL, e.g.
	 *         "https://bucket.s3.amazon.com/textfile.txt"
	 */
	public String toExternalForm();
}
