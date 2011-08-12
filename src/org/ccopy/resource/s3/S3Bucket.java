/**
 * 
 */
package org.ccopy.resource.s3;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore.Builder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccopy.resource.Resource;
import org.ccopy.resource.util.StringUtil;
import org.ccopy.util.HttpMethod;
import org.ccopy.util.InputStreamLogger;

/**
 * @author mholakovsky
 *
 */
public class S3Bucket {
	private static final Logger logger = Logger.getLogger("org.ccopy");
	private static final int DEFAULT_MAX_KEYS = 1000;
	private String bucket;
	private URI uri;
	private String SERVICE_HOST = "s3.amazonaws.com";
	private String SERVICE_SCHEMA = "https";
	/**
	 * Constructor of a S3Bucket
	 * @param bucket
	 * @throws IllegalArgumentException
	 */
	public S3Bucket(String bucket) {
		this.bucket = bucket;
		this.uri = buildURI(bucket);
	}
	
	public S3Bucket (URI uri) {
		this.uri = uri;
	}
	private URI buildURI(String bucket) {
		try {
			return new URI(SERVICE_SCHEMA,bucket + "." + SERVICE_HOST,"/",null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("parameter is not a valid bucket",e);
		}
	}
	/**
	 * This implementation of the GET operation returns some or all (up to 1000)
	 * of the objects in a bucket. You can use the request parameters as
	 * selection criteria to return a subset of the objects in a bucket.
	 * 
	 * To use this implementation of the operation, you must have READ access to
	 * the bucket.
	 * 
	 * @param bucketUri
	 *            the bucketname
	 * @param prefix
	 *            Limits the response to keys that begin with the specified
	 *            prefix. You can use prefixes to separate a bucket into
	 *            different groupings of keys. (You can think of using prefix to
	 *            make groups in the same way you'd use a folder in a file
	 *            system.)
	 * @param marker
	 *            Specifies the key to start with when listing objects in a
	 *            bucket. Amazon S3 lists objects in alphabetical order.
	 * @param maxKeys
	 *            Sets the maximum number of keys returned in the response body.
	 *            The response might contain fewer keys but will never contain
	 *            more. If there are additional keys that satisfy the search
	 *            criteria but were not returned because max-keys was exceeded,
	 *            the response contains <IsTruncated>true</IsTruncated>. To
	 *            return the additional keys, see marker.
	 * @param delimiter
	 *            A delimiter is a character you use to group keys. All keys
	 *            that contain the same string between the prefix, if specified,
	 *            and the first occurrence of the delimiter after the prefix are
	 *            grouped under a single result element, CommonPrefixes. If you
	 *            don't specify the prefix parameter, then the substring starts
	 *            at the beginning of the key. The keys that are grouped under
	 *            CommonPrefixes result element are not returned elsewhere in
	 *            the response.
	 * @return an array of S3Objects or empty array if nothing found
	 * @throws MalformedURLException 
	 * @throws NullPointerException when bucket name is <code>null</code>
	 */
	public static List<S3Object> listObjects(URI bucketUri, String prefix, String marker, int maxKeys, String delimiter) throws IOException {
		long start = 0L;
		// log the entry of this method
		if (logger.isLoggable(Level.FINE)) {
			start = System.currentTimeMillis();
			logger.fine(null);
		}
		// check the arguments
		if (null == bucketUri)
			throw new NullPointerException("Bucket URI must not be null");
		/**
		 * Prepare the request
		 */
		StringBuffer buf = new StringBuffer();
		if ((null != prefix) && (prefix.startsWith("/"))) {
			prefix = prefix.substring(1);
			if ((!prefix.isEmpty()))
				buf.append("prefix=" + prefix);
		}
		if (null != marker)
			buf.append(((buf.length() > 0) ? "&" : "") + "marker=" + marker);
		if (null != delimiter)
			buf.append(((buf.length() > 0) ? "&" : "") + "delimiter=" + delimiter);
		if (maxKeys > 0)
			buf.append(((buf.length() > 0) ? "&" : "") + "max-keys=" + String.valueOf(maxKeys));
		try {
			if (buf.length() > 0)
				bucketUri = new URI(bucketUri.getScheme(), bucketUri.getHost(), "/",
						buf.toString(), null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("can't construct the listObjects request URI", e);
		}
		S3Request req = new S3Request(bucketUri);
		// init some vars, so you can grab them in exception or finally clause
		req.setHttpMethod(HttpMethod.GET);
		HttpURLConnection con = null;
		InputStream in = null;
		/**
		 * Process the request
		 */
		try {
			// open the connection
			con = req.getConnection();
			// open InputStream to read the response from the PUT action
			if (logger.isLoggable(Level.FINEST))
				in = new InputStreamLogger(con.getInputStream());
			else
				in = con.getInputStream();
			// log the last written line to the logger
			if (logger.isLoggable(Level.FINEST)) {
				logger.finest("S3 response headers:\n"
						+ StringUtil.mapToString(con.getHeaderFields()));
			}
			// read the InputStream in chunks and write
			S3BucketListObjectsRequestParser parser = new S3BucketListObjectsRequestParser(in);

			// Workaround!!, because in case of a HTTP PUT you MUST check
			// con.responseCode AFTER the upload otherwise you would implicit
			// close the connection BEFORE you upload the content which ends in
			// an HTTP error 400 - EntityTooSmall
			if ((con.getResponseCode()) >= 300)
				throw new S3Exception(con.getResponseCode(),
						con.getResponseMessage(), StringUtil.streamToString(con
								.getErrorStream()));
			// log success
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully listed objects for '"
						+ bucketUri + ((null!= prefix)?prefix:"") + "' in '" + String.valueOf(System.currentTimeMillis()-start)+ "'ms");
			// finish the method
			return parser.list;
		} catch (IOException e) {
			if (null != con) {
				// if open connection found, it's likely a http error code from S3
				throw new S3Exception(con.getResponseCode(), con.getResponseMessage(),
						StringUtil.streamToString(con.getErrorStream()));
			} else
				// otherwise pass the original exception
				throw e;
		} finally {
			// be sure to always close the Input/Outputstreams
			if (null != in)
				in.close();
		}
	}
	/**
	 * @see #listObjects(String, String, String, int, String)
	 * @param bucket
	 * @param prefix
	 * @param marker
	 * @return
	 * @throws IOException
	 */
	public static List<S3Object> listObjects(URI bucketUri, String prefix, String marker) throws IOException {
		return S3Bucket.listObjects(bucketUri, prefix, marker, S3Bucket.DEFAULT_MAX_KEYS, "/");
	}
	public List<S3Object> listObjects(String prefix, String marker) throws IOException {
		return S3Bucket.listObjects(this.uri, prefix, marker, S3Bucket.DEFAULT_MAX_KEYS, "/");
	}
	public URI toURI() {
		return this.uri;
	}
	public URI getRoot(String string) {
		return buildURI(string);
	}
	/**
	 * 
	 * @param key a valid object key; otherwise <code>null</code>
	 * @return the URI for the object in this bucket or <code>null</code>
	 * @throws URISyntaxException 
	 */
	public URI getObjectURI(String key) throws URISyntaxException {
		if (!key.startsWith("/")) key = "/" + key;
		// the root is not a valid object key
		if (key.length()==1) return null;
		return new URI(this.uri.getScheme(),this.uri.getHost(),key,null);
	}
}
