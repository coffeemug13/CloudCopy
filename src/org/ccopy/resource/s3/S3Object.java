/**
 * 
 */
package org.ccopy.resource.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccopy.resource.ResourceException;
import org.ccopy.util.HttpMethod;

/**
 * @author mholakovsky
 * 
 */
public class S3Object {
	private static Logger logger = Logger.getLogger("org.ccopy");
	static public String delimiter = "/";
	protected URL url;
	protected HashMap<String, String> meta = null;
	protected Map<String, List<String>> responseHeader = null;
	private long lastModified = 0;
	// protected String versionId;
	// protected boolean exists;
	// protected boolean isDirectory;
	// protected boolean canRead;
	// protected int contentLength;
	// protected long lastModified;
	// protected String eTag;
	// protected String contentType;
	private InputStream inResponse = null;
	private InputStream inError = null;

	/**
	 * Constructor for S3Object
	 */
	protected S3Object(URL url) {
		this.url = url;
	}

	/**
	 * Get the Object from S3
	 * 
	 * @param url
	 * @param versionId
	 * @return
	 */
	static public S3Object getObject(URL url, String versionId)
			throws IOException, Exception {
		S3Request req = new S3Request(url);
		req.setHttpMethod(HttpMethod.GET);
		HttpURLConnection con = null;
		try {
			con = req.getConnection();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				S3Object obj = new S3Object(url);
				/**
				 * Set the attributes of the object
				 */
				obj.responseHeader = con.getHeaderFields();
				obj.lastModified = con.getLastModified();
				if (con.getResponseCode() < 400) {
					obj.inResponse = con.getInputStream();
				} else {
					obj.inError = con.getErrorStream();
				}
				return obj;
			} else {
				throw new IOException(con.getResponseCode() + ": "
						+ con.getResponseMessage());
			}
		} finally {
			// if (con!=null) con.disconnect();
		}
	}

	static public String getObjectAcl(URL url, String versionId) {
		// TODO finanalize implementation
		S3Request req = new S3Request(url);
		req.setHttpMethod(HttpMethod.GET);
		HttpURLConnection con = null;
		InputStream in = null;
		byte[] c = new byte[100]; // with increasing value speed goes up
		try {
			con = req.getConnection();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				int read = 0;
				// Read (and print) till end of file.
				in = con.getInputStream();
				if (null != con) {
					StringBuffer buf = new StringBuffer();
					buf.append("### response message: ###\n");
					while ((read = in.read(c)) != -1) {
						buf.append(new String(c, 0, read));
					}
					System.err.println(buf.toString() + "\n----------");
				}
			}
		} catch (Exception e) {

		}
		return versionId;
	}

	/**
	 * The HEAD operation retrieves metadata from an object without returning
	 * the object itself. This operation is useful if you're only interested in
	 * an object's metadata. To use HEAD, you must have READ access to the
	 * object.
	 * 
	 * @param url
	 * @param versionId
	 * @return
	 * @throws IOException
	 *             , Exception
	 * @throws ResourceException
	 */
	static public S3Object getHeadObject(URL url, String versionId)
			throws IOException, Exception {
		// TODO add the "?acl" to the request!!!!
		S3Request req = new S3Request(url);
		req.setHttpMethod(HttpMethod.GET);
		HttpURLConnection con = null;
		InputStream in = null;
		try {
			con = req.getConnection();
			in = con.getInputStream();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				S3Object obj = new S3Object(url);
				/**
				 * Set the attributes of the object
				 */
				obj.responseHeader = con.getHeaderFields();
				obj.lastModified = con.getLastModified();
				return obj;
			} else {
				throw new IOException(con.getResponseCode() + ": "
						+ con.getResponseMessage());
			}
		} finally {
			if (con != null)
				con.disconnect();
		}
	}

	/**
	 * This implementation of the PUT operation adds an object to a bucket. You
	 * must have WRITE permissions on a bucket to add an object to it. Amazon S3
	 * never adds partial objects; if you receive a success response, Amazon S3
	 * added the entire object to the bucket.
	 * 
	 * @param url
	 * @param metadata
	 * @param in
	 * @return
	 */
	static public String putObject(URL url, HashMap<String, String> metadata,
			int contentLength, InputStream in) throws IOException, Exception {
		/**
		 * Prepare the request
		 */
		S3Request req = new S3Request(url);
		// this is a PUT request
		req.setHttpMethod(HttpMethod.PUT);
		// you can't set the "Content-Length" attribute via addRequestHeader
		req.setFixedLengthStreamingMode(contentLength);
		// content type is mandatory for this request!
		req.setContentType("text/plain");
		// now set some header attributes if available
		if (null != metadata) {
			for (Entry<String, String> entry : metadata.entrySet()) {
				req.addRequestHeader(entry.getKey(), entry.getValue());
			}
		}
		/**
		 * Now process the request
		 */
		HttpURLConnection con = null;
		OutputStream out = null;
		try {
			con = req.getConnection();
			out = con.getOutputStream();
			byte[] c = new byte[100]; // with increasing value speed goes up
			int read = 0;
			int readCounter = 0;
			// Read (and print) till end of file.
			while ((read = in.read(c)) != -1) {
				out.write(c, 0, read);
				readCounter += read;
			}
			if (readCounter != contentLength)
				throw new S3Exception("Proposed content length '"
						+ contentLength + "' doesn't correspond to '"
						+ readCounter + "' Byte read from the InputStream!");
			// log the last written line to the logger
			if (logger.isLoggable(Level.FINER))
				logger.finer(readCounter
						+ " Bytes processed. Last line was ..." + new String(c));
			// extract from the response header ETag and versionid
			String versionId = null;
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				read = 0;
				// Read (and print) till end of file.
				in = con.getErrorStream();
				if (null != con) {
					StringBuffer buf = new StringBuffer();
					buf.append("### Error response message: ###\n");
					while ((read = in.read(c)) != -1) {
						buf.append(new String(c, 0, read));
					}
					logger.fine(buf.toString() + "\n----------");
				}
			} else {
				versionId = con.getHeaderField(S3Headers.X_VERSION_ID
						.toString());
				String eTag = con.getHeaderField(S3Headers.ETAG.toString());
			}
			// now close the connections
			in.close();
			out.close();
			// finish
			return versionId;
		} catch (Exception e) {
			throw new Exception(con.getResponseCode() + ": "
					+ con.getResponseMessage());
		} finally {
			// be sure to always close the Input/Outputstreams
			if (null != in)
				in.close();
			if (null != out)
				out.close();
		}
	}

	/**
	 * The DELETE operation removes the null version (if there is one) of an
	 * object and inserts a delete marker, which becomes the latest version of
	 * the object. If there isn't a null version, Amazon S3 does not remove any
	 * objects.
	 * 
	 * @param url
	 * @param versionID
	 * @return S3Object with resulting versionId and/or delete marker
	 */
	static public S3Object deleteObject(URL url, String versionID) {
		return null;
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
			return Integer.parseInt(responseHeader.get(
					S3Headers.CONTENT_LENGTH.toString()).get(0));
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
		// TODO implement
		return "text/plain";
	}

	/**
	 * Returns the content encoding of this object. This is an optional
	 * attribute and must be set manually when you put the object
	 * 
	 * @return the content encoding or null if not known
	 */
	public String getContentEncoding() {
		if (null != responseHeader) {
			return responseHeader.get(S3Headers.CONTENT_ENCODING.toString())
					.get(0);
		} else
			return String.valueOf("utf-8");
	}

	/**
	 * Returns the value of the last-modified header field. The result is the
	 * number of milliseconds since January 1, 1970 GMT.
	 * 
	 * @return the date the resource referenced by this URLConnection was last
	 *         modified, or 0 if not known.
	 */
	public long getLastModified() {
		// if (null!=responseHeader) {
		// return
		// Integer.parseInt(responseHeader.get(S3Headers.LAST_MODIFIED).get(0));
		// } else return -1;
		return lastModified;
	}

	/**
	 * Returns the versionId of this object, e.g.
	 * "3HL4kqtJlcpXrof3vjVBH40Nrjfkd"
	 * 
	 * @return the versionId or null if not known
	 */
	public String getVersionId() {
		if (null != responseHeader) {
			return responseHeader.get(S3Headers.X_VERSION_ID).get(0);
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
			// trim the '"' from start and end of string. ETag is always 32Bytes
			// long
			return responseHeader.get(S3Headers.ETAG.toString()).get(0)
					.substring(1, 33);
		} else
			return null;
	}

	/**
	 * Returns the InputStream of this object.
	 * 
	 * @return InputStream or null if not known
	 */
	public InputStream getInputStream() {
		return inResponse;
	}

	public InputStream getErrorStream() {
		// TODO Auto-generated method stub
		return inError;
	}

}
