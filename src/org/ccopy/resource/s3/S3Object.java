package org.ccopy.resource.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccopy.resource.ResourceError;
import org.ccopy.resource.util.MimeType;
import org.ccopy.resource.util.StringUtil;
import org.ccopy.util.HttpMethod;
import org.ccopy.util.InputStreamLogger;

/**
 * The S3Object provides methods to access and manipulate objects in S3 buckets.
 * 
 * @author coffeemug13
 */
public class S3Object {
	private static Logger logger = Logger.getLogger("org.ccopy");
	static public String delimiter = "/";
	/**
	 * the url representing this object in S3
	 */
	protected S3URL url;
	// protected HashMap<String, String> meta = null;
	/**
	 * contains the response headers from amazon, in case we made a GET or HEAD to the object
	 */
	protected Map<String, List<String>> responseHeader = null;
	/**
	 * the InputStream of the object if it was requested
	 */
	protected HttpURLConnection con = null;
	/**
	 * Date and time the object was last modified.
	 */
	protected long lastModified;
	/**
	 * The MD5 hash of the object. The ETag only reflects changes to the contents of an object, not
	 * its metadata.
	 */
	protected String eTag;
	/**
	 * Size in bytes of the object.
	 */
	protected long size;

	/**
	 * Constructor for S3Object
	 */
	protected S3Object(S3URL url) {
		logger.fine(null);
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
	static public S3Object getObject(S3URL url, String versionId) throws IOException {
		long start = 0L;
		// log the entry of this method
		if (logger.isLoggable(Level.FINE)) {
			start = System.currentTimeMillis();
			logger.fine(null);
		}
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
			obj.setResponseHeaders(con.getHeaderFields());
			obj.con = con;
			if (logger.isLoggable(Level.FINEST)) {
				StringBuffer buf = new StringBuffer();
				buf.append("S3 response headers:\n" + StringUtil.mapToString(obj.responseHeader));
				logger.finest(buf.toString());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Successfully read metadata and opened InputStream for S3 object to '"
						+ url.toString() + "' in '" + String.valueOf(System.currentTimeMillis()-start)+"'ms");
			}
			return obj;
		} finally {
			// DON'T close the 'con' in this case, because you loose connection
			// to read the InputStream later
		}
	}

	/**
	 * @see <a
	 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/API/RESTObjectGETacl.html">Amazon
	 *      Simple Storage Service - API Reference</a>
	 * @param url
	 * @param versionId
	 * @return
	 * @throws S3Exception
	 *         to handle S3 errors
	 * @throws IOException
	 *         in case of general connection problems
	 */
	static public String getObjectAcl(URL url, String versionId) throws IOException {
		// log the entry of this method
		logger.fine(null);
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
	 * The HEAD operation retrieves metadata from an object without returning the object itself.
	 * This operation is useful if you're only interested in an object's metadata. To use HEAD, you
	 * must have READ access to the object.
	 * 
	 * @see <a
	 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/API/RESTObjectHEAD.html">Amazon
	 *      Simple Storage Service - API Reference</a>
	 * @param url
	 * @param versionId
	 * @return
	 * @throws S3Exception
	 *         to handle S3 errors
	 * @throws IOException
	 *         in case of general connection problems
	 */
	static public S3Object getHeadObject(S3URL url, String versionId) throws IOException {
		long start = 0L;
		// log the entry of this method
		if (logger.isLoggable(Level.FINE)) {
			start = System.currentTimeMillis();
			logger.fine(null);
		}
		// check arguments
		if (null == url)
			throw new NullPointerException("url must not be null");
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
		obj.setResponseHeaders(con.getHeaderFields());
		// log some infos
		if (logger.isLoggable(Level.FINEST)) {
			StringBuffer buf = new StringBuffer();
			buf.append("S3 response headers:\n" + StringUtil.mapToString(obj.responseHeader));
			logger.finest(buf.toString());
		} else if (logger.isLoggable(Level.FINE)) {
			logger.fine("Successfully read metadata from S3 object to '" + url.toString() + "' in '" + String.valueOf(System.currentTimeMillis()-start)+"'ms");
		}
		// finish the method
		return obj;
	}

	/**
	 * This implementation of the PUT operation adds an object to a bucket. You must have WRITE
	 * permissions on a bucket to add an object to it. Amazon S3 never adds partial objects; if you
	 * receive a success response, Amazon S3 added the entire object to the bucket.
	 * 
	 * @see <a
	 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/API/RESTObjectPUT.html">Amazon
	 *      Simple Storage Service - API Reference</a>
	 * @param the
	 *        URL for the S3 object. The key length is max. 1024 Byte
	 * @param additional
	 *        metadata for the S3 object
	 * @param the
	 *        InputStream to be written to S3
	 * @return null or the VersionId of the S3 Object
	 * @throws S3Exception
	 *         to handle S3 errors
	 * @throws IOException
	 *         in case of general connection problems
	 */
	static public S3Response putObject(S3URL url, Map<String, String> meta2, MimeType contentType,
			int contentLength, InputStream in) throws IOException {
		long start = 0L;
		// log the entry of this method
		if (logger.isLoggable(Level.FINE)) {
			start = System.currentTimeMillis();
			logger.fine(null);
		}
		// perform some checks
		if (null == in)
			throw new NullPointerException("InputStream may not be null");
		if (null == url)
			throw new NullPointerException("URL may not be null");
		if (url.getPath().getBytes().length > 1024)
			throw new IllegalArgumentException(
					"The path of the URL (= the S3 key) exceeds 1024 Bytes");
		/**
		 * Prepare the request
		 */
		S3Request req = new S3Request(url);
		// this is a PUT request
		req.setHttpMethod(HttpMethod.PUT);
		// you can't set the "Content-Length" attribute via addRequestHeader
		req.setFixedLengthStreamingMode(contentLength);
		// content type is mandatory for this request!
		if (null != contentType)
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
		S3Response response = null;
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
			// response = new S3Response(, con.getHeaderFields());
			int res;
			if ((res = con.getResponseCode()) >= 300) {
				throw new S3Exception(con.getResponseCode(), con.getResponseMessage(),
						StringUtil.streamToString(con.getErrorStream()));
			} else
				response = new S3Response(con.getResponseCode(), con.getHeaderFields());
			// log the last written line to the logger
			if (logger.isLoggable(Level.FINEST)) {
				StringBuffer buf = new StringBuffer();
				buf.append("S3 response headers:\n" + StringUtil.mapToString(con.getHeaderFields()));
				buf.append(readCounter + " Bytes processed. Last line was <"
						+ new String(c, 0, lastRead) + ">");
				logger.finest(buf.toString());
			}
			if (logger.isLoggable(Level.FINE))
				logger.fine("Successfully written '" + readCounter + "' Bytes to '"
						+ url.toString() + "' in '" + String.valueOf(System.currentTimeMillis()-start)+"'ms");
			// finish the method
			return response;
		} finally {
			// be sure to always close the Input/Outputstreams
			if (null != in)
				in.close();
			if (null != out)
				out.close();
		}
	}

	/**
	 * This implementation of the PUT operation creates a copy of an object that
	 * is already stored in Amazon S3. A PUT copy operation is the same as
	 * performing a GET and then a PUT. Adding the request header,
	 * x-amz-copy-source, makes the PUT operation copy the source object into
	 * the destination bucket. When copying an object, you can preserve most of
	 * the metadata (default) or specify new metadata. However, the ACL is not
	 * preserved and is set to private for the user making the request. To
	 * override the default ACL setting, use the x-amz-acl header to specify a
	 * new ACL when generating a copy request. For more information, see Amazon
	 * S3 ACLs.
	 * 
	 * All copy requests must be authenticated and cannot contain a message
	 * body. Additionally, you must have READ access to the source object and
	 * WRITE access to the destination bucket. For more information, see REST
	 * Authentication.
	 * 
	 * @see <a
	 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/API/RESTObjectCOPY.html">Amazon
	 *      Simple Storage Service - API Reference</a>
	 * @param fromUrl
	 * @param toUrl
	 * @param meta
	 * @param contentType
	 * @return
	 */
	public static S3Response copyObject(S3URL fromUrl, S3URL toUrl, Map<String, String> meta,
			MimeType contentType) throws IOException {
		long start = 0L;
		// log the entry of this method
		if (logger.isLoggable(Level.FINE)) {
			start = System.currentTimeMillis();
			logger.fine(null);
		}
		// perform some checks
		if ((null == fromUrl) || (null == toUrl))
			throw new NullPointerException("URL may be not be null");
		// check whether to make a "REPLACE" call, see S3 documentation
		/**
		 * Prepare the request
		 */
		S3Request req = new S3Request(toUrl);
		// this is a PUT request
		req.setHttpMethod(HttpMethod.PUT);
		// you can't set the "Content-Length" attribute via addRequestHeader
		// we don't stream content, just headers
		req.setFixedLengthStreamingMode(0); 
		// now set some header attributes if available
		if (null != meta) {
			for (Entry<String, String> entry : meta.entrySet()) {
				req.addRequestHeader(entry.getKey(), entry.getValue());
			}
		}
		// set a special directive according to S3 API
		req.addRequestHeader(S3Headers.X_AMZ_COPY_SOURCE,
				S3Request.getCanonicalizedResource(fromUrl.toURL()));
		// if the URLs are equal, than set according to S3 API the header
		// "x-amz-metadata-directive" with "REPLACE" otherwise you get an
		// error from S3. See the S3 API documentation
		if (fromUrl.equals(toUrl) || (null!= meta))
			req.addRequestHeader(S3Headers.X_AMZ_METADATA_DIRECTIVE, "REPLACE");
		else
			req.addRequestHeader(S3Headers.X_AMZ_METADATA_DIRECTIVE, "COPY");
		// init some vars, so you can grab them in exception or finally clause
		HttpURLConnection con = null;
		InputStream in = null;
		S3Response response = null;
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
			S3ObjectCopyRequestParser parser = new S3ObjectCopyRequestParser(in);
			// create the response object
			response = new S3Response(con.getResponseCode(),
					con.getHeaderFields());
			response.lastModified = parser.lastModified;
			response.eTag = parser.eTag;
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
				logger.fine("Successfully copied/modified object from '"
						+ fromUrl.toString() + "' to '" + toUrl.toString() + "' in '" + String.valueOf(System.currentTimeMillis()-start)+ "'ms\nwith LastModified:'" + parser.lastModified + "' and ETag:'" + parser.eTag + "'");
			// finish the method
			return response;
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
	 * This is a convinient method for {@code deleteObjectVersion(url,null)}.
	 * 
	 * @see org.ccopy.resource.s3.S3Object#deleteObjectVersion(URL, String)
	 * @param url
	 *        the <code>URL</code> to the S3 object
	 * @return the new versionId in case the bucket versioning is enabled; <code>null</code>
	 *         otherwise.
	 * @throws S3Exception
	 *         to handle S3 errors
	 * @throws IOException
	 *         in case of general connection problems
	 */
	static public S3Response deleteObject(S3URL url) throws IOException, S3Exception {
		return deleteObjectVersion(url, null);
	}

	/**
	 * Deletes an S3 object from the bucket. In case the bucket is under versioning, this operation
	 * will create a new S3 object with new versionId, <code>x-amz-delete-marker: true</code> and
	 * zero content.
	 * 
	 * @see <a
	 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/API/RESTObjectDELETE.html">Amazon
	 *      Simple Storage Service - API Reference</a>
	 * @param url
	 * @param versionId
	 * @return the new versionId of the deleted object or {@code null} in case the bucket is not
	 *         under versioning
	 * @throws S3Exception
	 *         to handle S3 errors
	 * @throws IOException
	 *         in case of general connection problems
	 */
	static public S3Response deleteObjectVersion(S3URL url, String versionId) throws IOException,
			S3Exception {
		long start = 0L;
		// log the entry of this method
		if (logger.isLoggable(Level.FINE)) {
			start = System.currentTimeMillis();
			logger.fine(null);
		}
		/**
		 * Prepare the request
		 */
		S3Request req = new S3Request(url);
		req.setHttpMethod(HttpMethod.DELETE);
		// init some vars, so you can grab them in the finally clause
		HttpURLConnection con = null;
		S3Response response = null;
		/**
		 * Process the request
		 */
		con = req.getConnection();
		// check response code
		int res;
		if ((res = con.getResponseCode()) >= 300) {
			throw new S3Exception(con.getResponseCode(), con.getResponseMessage(),
					StringUtil.streamToString(con.getErrorStream()));
		} else
			response = new S3Response(con.getResponseCode(), con.getHeaderFields());
		// log some infos
		if (logger.isLoggable(Level.FINEST)) {
			StringBuffer buf = new StringBuffer();
			buf.append("S3 response headers:\n" + StringUtil.mapToString(con.getHeaderFields()));
			logger.finest(buf.toString());
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Successfully deleted S3 object '" + url.toString() + "' with version '"
					+ versionId + "' in '" + String.valueOf(System.currentTimeMillis()-start)+"'ms");
		}
		// finish the method
		return response;
	}

	/**
	 * Tests whether the object can be read under this URL.
	 * 
	 * @return <code>true</code> if the object exists AND can be read; <code>false</code> otherwise
	 */
	public boolean canRead() {
		return (null != responseHeader) ? true : false;
	}

	/**
	 * Tests whether the object can be written under this URL.
	 * 
	 * @return <code>true</code> if the object exists AND can be written; <code>false</code> otherwise
	 */
	public boolean canWrite() {
		// TODO implement the function. I think we need to get the ACLs here
		return (null != responseHeader) ? true : false;
	}

	/**
	 * Tests whether the object exists under this URL.
	 * 
	 * @return <code>true</code> if the object exists, <code>false</code> if the resource
	 *         doesn't exist or <code>null</code> if it's unknown whether the
	 *         object exists, e.g. because of IO connection problems
	 */
	public boolean exists() {
		return (null != responseHeader) ? true : false;
	}

	/**
	 * Returns the content length of the object.
	 * 
	 * @return length of the object if it exists; otherwise <code>0L</code> 
	 */
	public long getContentLength() {
		return this.size;
	}

	/**
	 * Returns the content type of this object. This is an optional attribute
	 * and must be set manually when you put the object
	 * 
	 * @return the content type if the object exist; otherwise <code>null</code>
	 *         if the file does not exist or the content type is not set
	 */
	public String getContentType() {
		if (null != responseHeader) {
			List<String> list = responseHeader.get(S3Headers.CONTENT_TYPE);
			return (null!=list)?list.get(0):null;
		} else
			return null;
	}

	/**
	 * Returns the content encoding of this object. This is an optional attribute and must be set
	 * manually when you put the object
	 * 
	 * @return  the content encoding if the object exist and is set; otherwise <code>null</code>
	 *         if the file does not exist or the content encoding is not set
	 */
	public String getContentEncoding() {
		if (null != responseHeader) {
			List<String> list = responseHeader.get(S3Headers.CONTENT_ENCODING);
			return (null!=list)?list.get(0):null;
		} else
			return null;
	}

	/**
	 * Returns the value of the last-modified header field. The result is the number of milliseconds
	 * since January 1, 1970 GMT.
	 * 
	 * @return  A <code>long</code> value representing the time this resource was
     *          last modified, measured in milliseconds since the epoch
     *          (00:00:00 GMT, January 1, 1970), or <code>0L</code> if the
     *          file does not exist or if an I/O error occur
	 */
	public long getLastModified() {
		return this.lastModified;
	}

	/**
	 * Returns the versionId of this object, e.g.
	 * "3HL4kqtJlcpXrof3vjVBH40Nrjfkd"
	 * 
	 * @return the versionID if the object exist and is set; otherwise
	 *         <code>null</code> if the file does not exist or the versionID is
	 *         not set
	 */
	public String getVersionId() {
		if (null != responseHeader) {
			return (responseHeader.containsKey(S3Headers.X_AMZ_VERSION_ID)) ? responseHeader.get(
					S3Headers.X_AMZ_VERSION_ID).get(0) : null;
		} else
			return null;
	}

	/**
	 * Returns the ETag of this object, e.g. "fba9dede5f27731c9771645a39863328"
	 * 
	 * @return the ETag if the object exist; otherwise <code>null</code> if the
	 *         file does not exist
	 */
	public String getETag() {
		return this.eTag;
	}

	/**
	 * Return the InputStream if the S3Object was generated by method {@code getObject}
	 * 
	 * @return the InputStream
	 * @throws IOException
	 *         in case of general connection problems
	 */
	public InputStream getInputStream() throws IOException {
		if (null == con) throw new IllegalStateException("no connection yet established. This instance was not created by method getObject()");
		return con.getInputStream();
	}

	/**
	 * Return the OutputStream if the S3Object was generated by method {@code getObject}
	 * 
	 * @return the OutputStream
	 * @throws IOException
	 *         in case of general connection problems
	 */
	public OutputStream getOutputStream() throws IOException {
		if (null == con) throw new IllegalStateException("no connection yet established. This instance was not created by method putObject()");
		return con.getOutputStream();
	}

	/**
	 * Set the response Headers for this object and extract some import variables like lastModified
	 * 
	 * @param responseHeader
	 */
	protected void setResponseHeaders(Map<String, List<String>> responseHeader) {
		// now set the response header
		this.responseHeader = responseHeader;
		if (null != responseHeader) {
			// extract last modification time
			this.lastModified = (responseHeader.containsKey(S3Headers.CONTENT_LENGTH)) ? Integer
					.parseInt(responseHeader.get(S3Headers.CONTENT_LENGTH).get(0)) : -1L;
			// extract the content size
			this.size = (responseHeader.containsKey(S3Headers.CONTENT_LENGTH)) ? Integer
					.parseInt(responseHeader.get(S3Headers.CONTENT_LENGTH).get(0)) : -1L;
			// extract the ETag
			if (responseHeader.containsKey(S3Headers.ETAG)) {
				this.eTag = responseHeader.get(S3Headers.ETAG).get(0);
				this.eTag = this.eTag.substring(1, this.eTag.length() - 1);
			}
		} 
	}
	public boolean isFile() {
		return url.isFile;
	}
	
	public boolean isDirectory() {
		return !url.isFile;
	}

	public URL getURL() {
		return this.url.toURL();
	}

}
