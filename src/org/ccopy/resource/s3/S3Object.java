package org.ccopy.resource.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	/**
	 * The size of the byte array, which holds the chunks from the InputStream
	 */
	private static final int STREAM_BYTE_BUFFER = 100;
	private static Logger logger = Logger.getLogger("org.ccopy");
	public static final String DELIMITER = "/";
	/**
	 * the default hostname for S3
	 */
	private static final String s3Host = "s3.amazonaws.com";
	/**
	 * the default protocol for request.
	 */
	private static final String s3Protocol = "https";
	/**
	 * the url representing this object in S3
	 */
	protected URI uri;
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
	 * Constructor for S3Object. 
	 * 
	 * @param uri
	 *            - An absolute, hierarchical URI with a scheme equal to "http" or "https", a
	 *            non-empty authority and path component, and undefined query, and fragment
	 *            components. The authority must not contain a user-info.
	 * @throws NullPointerException
	 *             - If uri is {@link NullPointerException}
	 */
	private S3Object(URI uri) {
		if (null == uri)
			throw new NullPointerException("argument must not be null");
		logger.fine(null);
		this.uri = uri;
//		this.isFile = (uri.getPath().endsWith("/"))?false:true;
	}
	protected S3Object(S3Response response) {
		S3Object obj = new S3Object(response.getUri());
		obj.eTag = response.getETag();
		obj.lastModified = response.getLastModified();
		// TODO implement versioning
	}
	protected void updateByResponse(S3Response response) {
		// make array modifiable if not already
		if (this.responseHeader.getClass().getName().contains("Unmodifiable")) {
			this.responseHeader = new HashMap<String, List<String>>(this.responseHeader);
		}
		this.responseHeader.putAll(response.responseHeader);
		boolean b;
//		if (!uri.equals(response.getUri())) uri = response.getUri();
//		if (null != response.getETag()) eTag = response.getETag();
//		if (lastModified != response.getLastModified()) lastModified = response.getLastModified();
//		if (!uri.equals(response.getUri())) uri = response.getUri();
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
	 * @return the requested S3 object with all meta information
	 * @throws S3Exception
	 *             to handle S3 errors
	 * @throws IOException
	 *             in case of general connection problems
	 */
	static public S3Object getObject(URI uri, String versionId) throws IOException {
		long start = 0L;
		// log the entry of this method
		if (logger.isLoggable(Level.FINE)) {
			start = System.currentTimeMillis();
			logger.fine(null);
		}
		// check arguments
		if (null == uri)
			throw new NullPointerException("parameter uri must not be null");
		/**
		 * Prepare the request
		 */
		S3Request req;
		// add query with versionID if provided
		try {
			if (null != versionId)
				uri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), "versionID="
						+ versionId, null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("can't construct a new URI including a versionID", e);
		}
		// create the request
		req = new S3Request(uri);
		// init some vars, so you can grab them in exception or finally clause
		req.setHttpMethod(HttpMethod.GET);
		HttpURLConnection con = null;
		/**
		 * Process the request
		 */
		con = req.getConnection();
		S3Object obj = new S3Object(req.toURI());
		// Set the attributes of the object
//		obj.setResponseHeaders(con.getHeaderFields());
		obj.responseHeader = con.getHeaderFields();
		// pass the open connection
		obj.con = con;
		// log something
		if (logger.isLoggable(Level.FINEST))
			logger.finest("S3 response headers:\n" + StringUtil.mapToString(obj.responseHeader));
		if (logger.isLoggable(Level.FINE))
			logger.fine("Successfully read metadata and opened InputStream for S3 object to '"
					+ uri.toString() + "' in '"
					+ String.valueOf(System.currentTimeMillis() - start) + "'ms");
		// finish the method and return
		return obj;
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
	static public S3Object getHeadObject(URI uri, String versionId) throws IOException {
		long start = 0L;
		// log the entry of this method
		if (logger.isLoggable(Level.FINE)) {
			start = System.currentTimeMillis();
			logger.fine(null);
		}
		// check arguments
		if (null == uri)
			throw new NullPointerException("parameter uri must not be null");
		/**
		 * Prepare the request
		 */
		S3Request req;
		// add query with versionID if provided
		try {
			if (null != versionId)
				uri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), "versionID="
						+ versionId, null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("can't construct a new URI including a versionID", e);
		}
		// create the request
		req = new S3Request(uri);
		req.setHttpMethod(HttpMethod.HEAD);
		// init some vars, so you can grab them in exception or finally clause
		HttpURLConnection con = null;
		/**
		 * Process the request
		 */
		con = req.getConnection();
		S3Object obj = new S3Object(req.toURI());
		// Set the attributes of the object
//		obj.setResponseHeaders(con.getHeaderFields());
		obj.responseHeader = con.getHeaderFields();
		// log some infos
		if (logger.isLoggable(Level.FINEST))
			logger.finest("S3 response headers:\n" + StringUtil.mapToString(obj.responseHeader));
		else if (logger.isLoggable(Level.FINE))
			logger.fine("Successfully read metadata from S3 object to '" + uri.toString()
					+ "' in '" + String.valueOf(System.currentTimeMillis() - start) + "'ms");
		// finish the method and return
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
	static public S3Response putObject(URI uri, Map<String, String> meta, MimeType contentType,
			long contentLength, InputStream in) throws IOException {
		long start = 0L;
		// log the entry of this method
		if (logger.isLoggable(Level.FINE)) {
			start = System.currentTimeMillis();
			logger.fine(null);
		}
		// perform some checks
		if ((null == uri) || ((null == in) && contentLength>0))
			throw new NullPointerException("parameter 'uri' and 'in' may not be null");
		if (uri.getPath().getBytes().length > 1024)
			throw new IllegalArgumentException(
					"The path of the URL (= the S3 key) exceeds 1024 Bytes");
		/**
		 * Prepare the request
		 */
		S3Request req;
		req = new S3Request(uri);
		// this is a PUT request
		req.setHttpMethod(HttpMethod.PUT);
		// you can't set the "Content-Length" attribute via addRequestHeader
		req.setFixedLengthStreamingMode(contentLength);
		// content type is mandatory for this request!
		if (null != contentType)
			req.setContentType(contentType.toString());
		// now set some header attributes if available
		if (null != meta) {
			for (Entry<String, String> entry : meta.entrySet()) {
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
			byte[] c = new byte[STREAM_BYTE_BUFFER]; // with increasing value speed goes up
			int read, lastRead = 0;
			int readCounter = 0; // count the Bytes which are processed
			if (contentLength > 0) {
				// Read (and print) till end of file.
				while ((read = in.read(c)) != -1) {
					out.write(c, 0, read);
					readCounter += read;
					if (read != -1)
						lastRead = read;
				}
			}
			// Workaround!!, because in case of a HTTP PUT you MUST check
			// con.responseCode AFTER the upload otherwise you would implicit
			// close the connection BEFORE you upload the content which ends in
			// an HTTP error 400 - EntityTooSmall
			// response = new S3Response(, con.getHeaderFields());
			if ((con.getResponseCode()) >= 300) {
				throw new S3Exception(con.getResponseCode(), con.getResponseMessage(),
						StringUtil.streamToString(con.getErrorStream()));
			} else
				response = new S3Response(req.toURI(),con.getResponseCode(), con.getHeaderFields());
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
						+ uri.toString() + "' in '" + String.valueOf(System.currentTimeMillis()-start)+"'ms");
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
	 * @param sourceUri
	 * @param targetUri
	 * @param meta
	 * @param contentType
	 * @return
	 */
	public static S3Response copyObject(URI sourceUri, URI targetUri, Map<String, String> meta,
			MimeType contentType) throws IOException {
		long start = 0L;
		// log the entry of this method
		if (logger.isLoggable(Level.FINE)) {
			start = System.currentTimeMillis();
			logger.fine(null);
		}
		// perform some checks
		if ((null == sourceUri) || (null == targetUri))
			throw new NullPointerException("parameter sourceUri and targetUri must not be null");
		/**
		 * Prepare the request
		 */
		S3Request req;
		req = new S3Request(targetUri);
		// this is a PUT request
		req.setHttpMethod(HttpMethod.PUT);
		req.setContentType(contentType.toString());
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
		// and encode special character of URI
		req.addRequestHeader(S3Headers.X_AMZ_COPY_SOURCE,
				S3Request.getCanonicalizedResource(new URL(sourceUri.toASCIIString())));
		// if the URLs are equal, than set according to S3 API the header
		// "x-amz-metadata-directive" with "REPLACE" otherwise you get an
		// error from S3. See the S3 API documentation
		if (sourceUri.equals(targetUri) || (null!= meta))
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
			response = new S3Response(req.toURI(),con.getResponseCode(),con.getHeaderFields());
			response.setLastModified(parser.lastModified);
			response.setETag(parser.eTag);
			response.updateByList(meta);
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
						+ sourceUri.toString() + "' to '" + targetUri.toString() + "' in '" + String.valueOf(System.currentTimeMillis()-start)+ "'ms\nwith LastModified:'" + parser.lastModified + "' and ETag:'" + parser.eTag + "'");
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
	 * Deletes an S3 object from the bucket. In case the bucket is under versioning, this operation
	 * will create a new S3 object with new versionId, <code>x-amz-delete-marker: true</code> and
	 * zero content.
	 * 
	 * @see <a
	 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/API/RESTObjectDELETE.html">Amazon
	 *      Simple Storage Service - API Reference</a>
	 * @param uri
	 * @return S3Response information with the new versionId of the deleted object in case the
	 *         bucket is under versioning
	 * @throws S3Exception
	 *             to handle S3 errors
	 * @throws IOException
	 *             in case of general connection problems
	 */
	static public S3Response deleteObject(URI uri) throws IOException, S3Exception {
		return deleteObjectVersion(uri, null);
	}

	/**
	 * Deletes an S3 object from the bucket. In case the bucket is under versioning, this operation
	 * will create a new S3 object with new versionId, <code>x-amz-delete-marker: true</code> and
	 * zero content.
	 * 
	 * @see <a
	 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/API/RESTObjectDELETE.html">Amazon
	 *      Simple Storage Service - API Reference</a>
	 * @param uri
	 * @param versionId
	 *            the version of the resource you want to delete or <code>null</code> for the latest
	 *            version
	 * @return S3Response information with the new versionId of the deleted object in case the
	 *         bucket is under versioning
	 * @throws S3Exception
	 *             to handle S3 errors
	 * @throws IOException
	 *             in case of general connection problems
	 */
	static public S3Response deleteObjectVersion(URI uri, String versionId) throws IOException,
			S3Exception {
		long start = 0L;
		// log the entry of this method
		if (logger.isLoggable(Level.FINE)) {
			start = System.currentTimeMillis();
			logger.fine(null);
		}
		// check arguments
		if (null == uri)
			throw new NullPointerException("parameter uri must not be null");
		/**
		 * Prepare the request
		 */
		S3Request req;
		// add query with versionID if provided
		try {
			if (null != versionId)
				uri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), "?versionID="
						+ versionId, null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("can't construct a new URI including a versionID", e);
		}
		// create request
		req = new S3Request(uri);
		req.setHttpMethod(HttpMethod.DELETE);
		// init some vars, so you can grab them in the finally clause
		HttpURLConnection con = null;
		S3Response response = null;
		/**
		 * Process the request
		 */
		con = req.getConnection();
		// check response code
		if ((con.getResponseCode()) >= 300) {
			throw new S3Exception(con.getResponseCode(), con.getResponseMessage(),
					StringUtil.streamToString(con.getErrorStream()));
		} else
			response = new S3Response(req.toURI(),con.getResponseCode(), con.getHeaderFields());
		// log some infos
		if (logger.isLoggable(Level.FINEST)) 
			logger.finest("S3 response headers:\n" + StringUtil.mapToString(con.getHeaderFields()));
		if (logger.isLoggable(Level.FINE)) 
			logger.fine("Successfully deleted S3 object '" + uri.toString() + "' with version '"
					+ versionId + "' in '" + String.valueOf(System.currentTimeMillis()-start)+"'ms");
		// finish the method
		return response;
	}

	/**
	 * Tests whether the object can be read under this URI.
	 * 
	 * @return <code>true</code> if the object exists AND can be read; <code>false</code> otherwise
	 */
	public boolean canRead() {
		return (null != responseHeader) ? true : false;
	}

	/**
	 * Tests whether the object can be written under this URI.
	 * 
	 * @return <code>true</code> if the object exists AND can be written; <code>false</code> otherwise
	 */
	public boolean canWrite() {
		// TODO implement the function. I think we need to get the ACLs here
		return (null != responseHeader) ? true : false;
	}

	/**
	 * Tests whether the object exists under this URI.
	 * 
	 * @return <code>true</code> if the object exists, <code>false</code> if the resource
	 *         doesn't exist or <code>null</code> if it's unknown whether the
	 *         object exists, e.g. because of IO connection problems
	 */
	public boolean exists() {
		return (!this.isDeleted()) ? true : false;
	}
	/**
	 * Tests whether the object has been deleted. If a bucket 
	 * @return
	 */
	public boolean isDeleted() {
		return ((null != responseHeader) && 
				(responseHeader.containsKey(S3Headers.X_AMZ_DELETE_MARKER.toString()))) ? true : false;
	}
	/**
	 * Returns the content length of the object.
	 * 
	 * @return length of the object if it exists; otherwise <code>0L</code> 
	 */
	public long getContentLength() {
		if (null != responseHeader) {
				return Long.parseLong(this.responseHeader.get(S3Headers.CONTENT_LENGTH).get(0));
		} else return 0L;
	}

	/**
	 * Returns the content type of this object. This is an optional attribute
	 * and must be set manually when you put the object
	 * 
	 * @return the content type if the object exist; otherwise <code>null</code>
	 *         if the file does not exist or the content type is not set
	 */
//	public String getContentType() {
//		if (null != responseHeader) {
//			List<String> list = responseHeader.get(S3Headers.CONTENT_TYPE);
//			return (null!=list)?list.get(0):null;
//		} else
//			return null;
//	}

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
		if (null != responseHeader) {
			String etag = responseHeader.get(S3Headers.ETAG).get(0);
			return etag.substring(1,etag.length()-1);
		} else
			return null;
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
//	private void setResponseHeaders(Map<String, List<String>> responseHeader) {
//		// now set the response header
//		this.responseHeader = responseHeader;
//		if (null != responseHeader) {
//			// extract last modification time
//			this.lastModified = (responseHeader.containsKey(S3Headers.LAST_MODIFIED)) ? Integer
//					.parseInt(responseHeader.get(S3Headers.LAST_MODIFIED).get(0)) : 0L;
//			// extract the content size
//			this.size = (responseHeader.containsKey(S3Headers.CONTENT_LENGTH)) ? Integer
//					.parseInt(responseHeader.get(S3Headers.CONTENT_LENGTH).get(0)) : 0L;
//			// extract the ETag
//			if (responseHeader.containsKey(S3Headers.ETAG)) {
//				this.eTag = responseHeader.get(S3Headers.ETAG).get(0);
//				this.eTag = this.eTag.substring(1, this.eTag.length() - 1);
//			}
//		} 
//	}
//	public boolean isFile() {
//		return this.isFile;
//	}
	
//	public boolean isDirectory() {
//		return !this.isFile();
//	}

	public URL getURL() {
		try {
			return this.uri.toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new Error("error converting URI to URL");
		}
	}
	/**
	 * Return the containing bucket
	 * @return
	 */
	public S3Bucket getBucket() {
		try {
			return new S3Bucket(new URI(this.uri.getScheme(),this.uri.getHost(),"/",null));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new Error("error constructing bucket URI for this object");
		}
	}

	
	private void makeModifiable()  {
		// make array modifiable if not already
		if (this.responseHeader.getClass().getName().contains("Unmodifiable")) {
			this.responseHeader = new HashMap<String, List<String>>(this.responseHeader);
		}
	}
	public String get(String key) {
		return StringUtil.join(this.responseHeader.get(key));
	}
	public boolean containsKey(String key) {
		return this.responseHeader.containsKey(S3Headers.X_AMZ_META.toString() + key);
	}
	/**
	 * Return the content type of the S3 Object
	 * @return the content type or <code>null</code>
	 */
	public String getContentType() {
		return responseHeader.get(S3Headers.CONTENT_TYPE).get(0);
	}

	public static S3Object fromPath(String bucket, String key) throws MalformedURLException {
		if (null == key) 
			key = DELIMITER;
		if (!key.startsWith(DELIMITER))
			key = DELIMITER + key;
		try {
//			URI u = new URI(s3Protocol, bucket + "." + s3Host, key,null);
//			return new S3Object(u);
			return new S3Object(new URI(s3Protocol, bucket + "." + s3Host, key,null));
		} catch (URISyntaxException e) {
			throw new MalformedURLException(e.toString());
		}
	}
}
