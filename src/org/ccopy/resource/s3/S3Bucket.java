/**
 * 
 */
package org.ccopy.resource.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	/**
	 * Constructor of a S3Bucket
	 * @param bucket
	 */
	public S3Bucket(String bucket) {
		this.bucket = bucket;
	}

	/**
	 * This implementation of the GET operation returns some or all (up to 1000)
	 * of the objects in a bucket. You can use the request parameters as
	 * selection criteria to return a subset of the objects in a bucket.
	 * 
	 * To use this implementation of the operation, you must have READ access to
	 * the bucket.
	 * 
	 * @param bucket
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
	public static List<S3Object> listObjects(String bucket, String prefix, String marker, int maxKeys, String delimiter) throws IOException {
		long start = 0L;
		// log the entry of this method
		if (logger.isLoggable(Level.FINE)) {
			start = System.currentTimeMillis();
			logger.fine(null);
		}
		// check the arguments
		if (null == bucket)
			throw new NullPointerException("Bucketname must not be null");
		/**
		 * Prepare the request
		 */
		S3URL url;
		try {
			url = new S3URL("https://" + bucket + ".s3.amazonaws.com/");
			if (null != prefix)
				url.addQuery("prefix", URLEncoder.encode(prefix, "UTF-8"));
			if (null != marker)
				url.addQuery("marker", URLEncoder.encode(marker, "UTF-8"));
			if (null != delimiter)
				url.addQuery("delimiter", URLEncoder.encode(delimiter, "UTF-8"));
			if (maxKeys > 0)
				url.addQuery("max-keys", String.valueOf(maxKeys));
		} catch (UnsupportedEncodingException e) {
			throw new MalformedURLException(StringUtil.exceptionToString(e));
		}
		S3Request req = new S3Request(url);
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
						+ bucket + ((null!= prefix)?prefix:"") + "' in '" + String.valueOf(System.currentTimeMillis()-start)+ "'ms");
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
	public static List<S3Object> listObjects(String bucket, String prefix, String marker) throws IOException {
		return S3Bucket.listObjects(bucket, prefix, marker, S3Bucket.DEFAULT_MAX_KEYS, "/");
	}
}
