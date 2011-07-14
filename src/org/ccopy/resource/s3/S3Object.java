package org.ccopy.resource.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccopy.resource.util.MimeType;
import org.ccopy.resource.util.StringUtil;
import org.ccopy.util.HttpMethod;

/**
 * The S3Object provides methods to access and manipulate objects in S3 buckets.
 * 
 * @author coffeemug13
 */
public class S3Object {
	private static Logger logger = Logger.getLogger("org.ccopy");
	static public String delimiter = "/";
	protected S3URL url;
	protected HashMap<String, String> meta = null;
	protected Map<String, List<String>> responseHeader = null;
	/**
	 * the InputStream of the object if it was requested
	 */
	protected HttpURLConnection con = null;

	/**
	 * Constructor for S3Object
	 */
	protected S3Object(S3URL url) {
		this.url = url;
	}

	/**
	 * Get the Object and http connection from S3.
	 * 
	 * @see <a
	 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/API/RESTObjectGET.html">Amazon
	 *      Simple Storage Service - API Reference</a>
	 * @param url
	 *            the <code>URL</code> of the S3 object
	 * @param versionId
	 *            the versionId or null
	 * @return the requested S3 object
	 * @throws S3Exception
	 *             to handle S3 errors
	 * @throws IOException
	 *             in case of general connection problems
	 */
	static public S3Object getObject(S3URL url, String versionId) throws IOException, S3Exception {
		/**
		 * Prepare the request
		 */
		S3Request req = new S3Request(url);
		// init some vars, so you can grab them in exception or finally clause
		req.setHttpMethod(HttpMethod.GET);
		HttpURLConnection con = null;
		/**
		 * Process the request
		 */
		try {
			con = req.getConnection();
			S3Object obj = new S3Object(url);
			// Set the attributes of the object
			obj.responseHeader = con.getHeaderFields();
			obj.con = con;
			if (logger.isLoggable(Level.FINEST)) {
				StringBuffer buf = new StringBuffer();
				buf.append("S3 response headers:\n" + StringUtil.mapToString(obj.responseHeader));
				logger.finest(buf.toString());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Successfully read metadata and opened InputStream for S3 object to '"
						+ url.toString() + "'");
			}
			return obj;
		} finally {
			// DON'T close the 'con' in this case, because you loose connection
			// to read the InputStream later
		}
	}

	/**
	 * 
	 * @see <a
	 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/API/RESTObjectGETacl.html">Amazon
	 *      Simple Storage Service - API Reference</a>
	 * @param url
	 * @param versionId
	 * @return
	 * @throws S3Exception
	 *             to handle S3 errors
	 * @throws IOException
	 *             in case of general connection problems
	 */
	static public String getObjectAcl(URL url, String versionId) throws IOException, S3Exception {
		// TODO finalize implementation
		// S3Request req = new S3Request(url);
		// req.setHttpMethod(HttpMethod.GET);
		// HttpURLConnection con = null;
		// InputStream in = null;
		// byte[] c = new byte[100]; // with increasing value speed goes up
		// try {
		// con = req.getConnection();
		// if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
		// int read = 0;
		// // Read (and print) till end of file.
		// in = con.getInputStream();
		// if (null != con)
		// System.err.println("### response message: ###\n" +
		// StringUtil.streamToString(in)
		// + "\n----------");
		// }
		// } finally {
		// if (in != null)
		// in.close();
		// }
		// return versionId;
		return null;
	}

	/**
	 * The HEAD operation retrieves metadata from an object without returning
	 * the object itself. This operation is useful if you're only interested in
	 * an object's metadata. To use HEAD, you must have READ access to the
	 * object.
	 * 
	 * @see <a
	 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/API/RESTObjectHEAD.html">Amazon
	 *      Simple Storage Service - API Reference</a>
	 * @param url
	 * @param versionId
	 * @return
	 * @throws S3Exception
	 *             to handle S3 errors
	 * @throws IOException
	 *             in case of general connection problems
	 */
	static public S3Object getHeadObject(S3URL url, String versionId) throws IOException, S3Exception {
		/**
		 * Prepare the request
		 */
		S3Request req = new S3Request(url);
		req.setHttpMethod(HttpMethod.HEAD);
		// init some vars, so you can grab them in exception or finally clause
		HttpURLConnection con = null;
		/**
		 * Process the request
		 */
		con = req.getConnection();
		S3Object obj = new S3Object(url);
		// Set the attributes of the object
		obj.responseHeader = con.getHeaderFields();
		// log some infos
		if (logger.isLoggable(Level.FINEST)) {
			StringBuffer buf = new StringBuffer();
			buf.append("S3 response headers:\n" + StringUtil.mapToString(obj.responseHeader));
			logger.finest(buf.toString());
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Successfully read metadata from S3 object to '" + url.toString() + "'");
		}
		// finish the method
		return obj;
	}

	/**
	 * This implementation of the PUT operation adds an object to a bucket. You
	 * must have WRITE permissions on a bucket to add an object to it. Amazon S3
	 * never adds partial objects; if you receive a success response, Amazon S3
	 * added the entire object to the bucket.
	 * 
	 * @see <a
	 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/API/RESTObjectPUT.html">Amazon
	 *      Simple Storage Service - API Reference</a>
	 * @param the
	 *            URL for the S3 object. The key length is max. 1024 Byte
	 * @param additional
	 *            metadata for the S3 object
	 * @param the
	 *            InputStream to be written to S3
	 * @return null or the VersionId of the S3 Object
	 * @throws S3Exception
	 *             to handle S3 errors
	 * @throws IOException
	 *             in case of general connection problems
	 */
	static public String putObject(S3URL url, Map<String, String> meta2, MimeType contentType, int contentLength,
			InputStream in) throws IOException, S3Exception {
		// perform some checks
		if (null == in) throw new NullPointerException("InputStream may be null");
		if (null == url) throw new NullPointerException("URL may be null");
		if (url.getPath().getBytes().length > 1024) throw new IllegalArgumentException("The path of the URL (= the S3 key) exceeds 1024 Bytes");
		/**
		 * Prepare the request
		 */
		S3Request req = new S3Request(url);
		// this is a PUT request
		req.setHttpMethod(HttpMethod.PUT);
		// you can't set the "Content-Length" attribute via addRequestHeader
		req.setFixedLengthStreamingMode(contentLength);
		// content type is mandatory for this request!
		if (null!=contentType)
			req.setContentType(contentType.toString());
		else
			req.setContentType(MimeType.DEFAULT);
		// now set some header attributes if available
		if (null != meta2) {
			for (Entry<String, String> entry : meta2.entrySet()) {
				req.addRequestHeader(entry.getKey(), entry.getValue());
			}
		}
		// init some vars, so you can grab them in exception or finally clause
		HttpURLConnection con = null;
		OutputStream out = null;
		/**
		 * Process the request
		 */

		try {
			con = req.getConnection();
			out = con.getOutputStream();
			// read the InputStream in chunks and write them to S3 OutputStream
			byte[] c = new byte[100]; // with increasing value speed goes up
			int read, lastRead = 0;
			int readCounter = 0; // count the Bytes which are processed
			// Read (and print) till end of file.
			while ((read = in.read(c)) != -1) {
				out.write(c, 0, read);
				readCounter += read;
				if (read != -1)
					lastRead = read;
			}
			// Workaround!!, because in case of a HTTP PUT you MUST check
			// con.responseCode AFTER the upload otherwise you would implicit
			// close the connection BEFORE you upload the content which ends in
			// an HTTP error 400 - EntityTooSmall
			int res;
			if ((res = con.getResponseCode()) >= 300) {
				throw new S3Exception(con.getResponseCode(), con.getResponseMessage(),
						StringUtil.streamToString(con.getErrorStream()));
			}
			// extract from the response header the versionId
			String versionId = null;
			versionId = con.getHeaderField(S3Headers.X_VERSION_ID.toString());
			// log the last written line to the logger
			if (logger.isLoggable(Level.FINEST)) {
				StringBuffer buf = new StringBuffer();
				buf.append("S3 response headers:\n" + StringUtil.mapToString(con.getHeaderFields()));
				buf.append(readCounter + " Bytes processed. Last line was <" + new String(c, 0, lastRead)
						+ ">");
				logger.finest(buf.toString());
			}
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully written '" + readCounter + "' Bytes to '" + url.toString() + "'");
			// finish the method
			return versionId;
		} finally {
			// be sure to always close the Input/Outputstreams
			if (null != in)
				in.close();
			if (null != out)
				out.close();
		}
	}

	/**
	 * This is a convinient method for {@code deleteObjectVersion(url,null)}.
	 * 
	 * @see org.ccopy.resource.s3.S3Object#deleteObjectVersion(URL, String)
	 * @param url
	 *            the <code>URL</code> to the S3 object
	 * @return the new versionId in case the bucket versioning is enabled;
	 *         <code>null</code> otherwise.
	 * @throws S3Exception
	 *             to handle S3 errors
	 * @throws IOException
	 *             in case of general connection problems
	 */
	static public String deleteObject(S3URL url) throws IOException, S3Exception {
		return deleteObjectVersion(url, null);
	}

	/**
	 * Deletes an S3 object from the bucket. In case the bucket is under
	 * versioning, this operation will create a new S3 object with new
	 * versionId, <code>x-amz-delete-marker: true</code> and zero content.
	 * 
	 * @see <a
	 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/API/RESTObjectDELETE.html">Amazon
	 *      Simple Storage Service - API Reference</a>
	 * @param url
	 * @param versionId
	 * @return the new versionId of the deleted object or {@code null} in case
	 *         the bucket is not under versioning
	 * @throws S3Exception
	 *             to handle S3 errors
	 * @throws IOException
	 *             in case of general connection problems
	 */
	static public String deleteObjectVersion(S3URL url, String versionId) throws IOException, S3Exception {
		/**
		 * Prepare the request
		 */
		S3Request req = new S3Request(url);
		req.setHttpMethod(HttpMethod.DELETE);
		// init some vars, so you can grab them in the finally clause
		HttpURLConnection con = null;
		/**
		 * Process the request
		 */
		con = req.getConnection();
		S3Object obj = new S3Object(url);
		// Set the attributes of the object
		obj.responseHeader = con.getHeaderFields();
		// log some infos
		if (logger.isLoggable(Level.FINEST)) {
			StringBuffer buf = new StringBuffer();
			buf.append("S3 response headers:\n" + StringUtil.mapToString(obj.responseHeader));
			logger.finest(buf.toString());
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Successfully deleted S3 object '" + url.toString() + "' with version '" + versionId
					+ "'");
		}
		// finish the method
		return obj.getVersionId();
	}

	/**
	 * Tests whether the object can be read under this URL.
	 * 
	 * @return true if the object exists AND can be read; false otherwise
	 */
	public boolean canRead() {
		return (null != responseHeader) ? true : false;
	}

	/**
	 * Tests whether the object can be written under this URL.
	 * 
	 * @return true if the object exists AND can be written; false otherwise
	 */
	public boolean canWrite() {
		// TODO implement the function. I think we need to get the ACLs here
		return (null != responseHeader) ? true : false;
	}

	/**
	 * Tests whether the object exists under this URL.
	 * 
	 * @return true if the object exists
	 */
	public boolean exists() {
		return (null != responseHeader) ? true : false;
	}

	/**
	 * Returns the content length of the object.
	 * 
	 * @return length or -1 if not known
	 */
	public int getContentLength() {
		if (null != responseHeader) {
			String s = responseHeader.get(S3Headers.CONTENT_LENGTH.toString()).get(0);
			int r = Integer.parseInt(s);
			return r;
		} else
			return -1;
	}

	/**
	 * Returns the content type of this object. This is an optional attribute
	 * and must be set manually when you put the object
	 * 
	 * @return the content type or null if not known
	 */
	public String getContentType() {
		if (null != responseHeader) {
			return responseHeader.get(S3Headers.CONTENT_TYPE.toString()).get(0);
		} else
			return String.valueOf("utf-8");
	}

	/**
	 * Returns the content encoding of this object. This is an optional
	 * attribute and must be set manually when you put the object
	 * 
	 * @return the content encoding or null if not known
	 */
	public String getContentEncoding() {
		if (null != responseHeader) {
			return responseHeader.get(S3Headers.CONTENT_ENCODING.toString()).get(0);
		} else
			return String.valueOf("utf-8");
	}

	/**
	 * Returns the value of the last-modified header field. The result is the
	 * number of milliseconds since January 1, 1970 GMT.
	 * 
	 * @return the date the resource referenced by this URLConnection was last
	 *         modified, or -1 if not known.
	 */
	public long getLastModified() {
		if (null != responseHeader) {
			return Integer.parseInt(responseHeader.get(S3Headers.LAST_MODIFIED).get(0));
		} else
			return -1;
	}

	/**
	 * Returns the versionId of this object, e.g.
	 * "3HL4kqtJlcpXrof3vjVBH40Nrjfkd"
	 * 
	 * @return the versionId or null if not known
	 */
	public String getVersionId() {
		if (null != responseHeader) {
			return (responseHeader.containsKey(S3Headers.X_VERSION_ID)) ? responseHeader.get(
					S3Headers.X_VERSION_ID).get(0) : null;
		} else
			return null;
	}

	/**
	 * Returns the ETag of this object, e.g. "fba9dede5f27731c9771645a39863328"
	 * 
	 * @return the ETag or null if not known
	 */
	public String getETag() {
		if (null != responseHeader) {
			// trim the '"' from start and end of string. ETag is a MD5 Hash and
			// therefore 32Bytes long
			return responseHeader.get(S3Headers.ETAG.toString()).get(0).substring(1, 33);
		} else
			return null;
	}

	/**
	 * Return the InputStream if the S3Object was generated by method
	 * {@code getObject}
	 * 
	 * @return
	 * @throws IOException
	 *             in case of general connection problems
	 */
	public InputStream getInputStream() throws IOException {
		return con.getInputStream();
	}
}
