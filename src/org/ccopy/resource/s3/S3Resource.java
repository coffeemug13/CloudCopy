package org.ccopy.resource.s3;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ccopy.resource.Resource;
import org.ccopy.resource.ResourceError;
import org.ccopy.resource.ResourceException;
import org.ccopy.resource.util.MimeType;

/**
 * Implements the Asset as simple File Object.
 * 
 * @author coffeemug13
 */
public class S3Resource extends Resource {
	// define some flags of the parent class
	public static final String SEPERATOR = "/";
	public static final boolean SUPPORTS_METADATA = true;
	// TODO implement versioning. At the moment all buckets are treated as non-versioning
	public static boolean SUPPORTS_VERSIONING = false;
	public static final boolean SUPPORTS_SET_LASTMODIFIED = false;
	/**
	 * the S3Object
	 */
	protected S3Object s3Object;
	/**
	 * Contains modified headers or {@code null} if no changes have been made 
	 */
	private Map<String, String> modifiedHeader = null;
	/*
	 * ############################################################ 
	 * Constructors
	 * ############################################################
	 */

	/**
	 * Construct a Resource based on a {@code ResourceLocator}.
	 * 
	 * @param url
	 *        the locator for this resource
	 * @throws MalformedURLException
	 *         when the URL is invalid
	 */
	public S3Resource(URI uri) throws URISyntaxException {
		super(uri);
	}

	/**
	 * Constructor of a new {@code S3Resource} as child of another resource.
	 * 
	 * @param resource
	 * @param child
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 * @throws MalformedURLException
	 *         when the resulting URL is invalid
	 */
	public S3Resource(S3Resource resource, String child) throws URISyntaxException {
		super(resource, child);
	}

	/**
	 * Construct a Resource based on a 'bucket' and 'key' for the S3 object. This is a convenient
	 * method for:
	 * <pre>
	 * URI bucketRoot = new S3Bucket("ccopy").toURI(); 
	 * Resource root = new S3Resource(bucketRoot); 
	 * Resource res = root.getChildResource("test.txt");
	 * </pre>
	 * @param bucket
	 *            the name of the S3 bucket
	 * @param key
	 *            the object key, e.g. "/path/to/my/key.txt" or <code>null</code> for the root
	 *            directory
	 * @throws MalformedURLException
	 * @throws NullPointerException
	 *             when bucket is <code>null</code>
	 */
	public static S3Resource getResource(String bucket, String key) throws URISyntaxException {
		URI uri = new S3Bucket(bucket).getObjectURI(key);
		return new S3Resource(uri);
	}

	/**
	 * 
	 * @param object
	 * @throws MalformedURLException 
	 */
	private S3Resource(S3Object object) throws URISyntaxException {
		super(object.getURL().toURI());
		this.s3Object = object;
//		this.exists = Boolean.TRUE;
//		this.isFile = object.isFile();
//		this.canRead = true;
//		this.canWrite = true;
//		this.lastModified = object.getLastModified();
//		this.contentType = new MimeType(object.getContentType());
//		this.md5Hash = object.getETag();
	}
	@Override
	public URI getChild(String name) throws URISyntaxException {
		return new URI(this.uri.getScheme(),this.uri.getHost(),this.uri.getPath() + "/" + name,null).normalize();
	}

	@Override
	public Resource getChildResource(String name) throws URISyntaxException {
		return new S3Resource(getChild(name));
	}

	@Override
	public URI getParent() throws URISyntaxException {
		this.uri.normalize();
		String path = this.uri.getPath();
		// if path denotes a directory remove trailing "/"
		if (path.endsWith("/"))
			path = path.substring(0,path.length()-1);
		// now cut the last path segment
		if (!path.equals("/"))
			path = path.substring(0,path.lastIndexOf("/"));
		return new URI(this.uri.getScheme(),this.uri.getHost(),path,null);
	}

	@Override
	public Resource getParentResource() throws URISyntaxException {
		return new S3Resource(getParent());
	}
	/**
	 * Reset the resource to an "new" status. The {@link #s3URL} is not reset, because it defines a S3Resource
	 */
	@Override
	protected void reset() {
		super.reset();
		this.s3Object = null;
		this.modifiedHeader = null;
	}
	/*
	 * ############################################################ 
	 * operations which alter the physical resource and persist changes
	 * ############################################################
	 */
	@Override
	public boolean delete() throws ResourceException, IOException {
		try {
			S3Response response = S3Object.deleteObject(this.uri);
			if (response.getReturnCode() != HttpURLConnection.HTTP_NO_CONTENT)
				throw new ResourceException("something went wrong when deleting the file: "
						+ response.toString());
			/*
			 * At this moment we know the URI of the object and that it is deleted (isFile=FALSE). Therefore the resource
			 * has to be reset, to behave like a new constructed one. 
			 * But under versioning it still exists but with new versionID and delete marker 
			 */
			this.s3Object = new S3Object(response);
			this.modifiedHeader = null;
			return true;
		} catch (S3Exception e) {
			if (e.getErrorCode() == HttpURLConnection.HTTP_NOT_FOUND) {
				logger.info("file not found" + e.toString());
				return false;
			} else
				throw new ResourceException("error while deleting", e);
		}
	}

	@Override
	public S3Resource createNewFileResource() throws ResourceException, IOException {
		if (null != this.s3Object)
			if (!this.s3Object.isDeleted())
				throw new IllegalStateException(
						"Can't create new file resource because it already exists");
		// extract the header attributes and contentType
		Map<String, String> header = this.finalizeHeader();
		MimeType contentType = new MimeType(header.remove(S3Headers.CONTENT_TYPE));
		// put the S3 object
		try {
			S3Response response = S3Object.putObject(encodeAsFile(this.uri), header, contentType,
					0, null);
			// init this resource instance
			this.s3Object = new S3Object(response);
			this.uri = this.s3Object.uri;
			this.modifiedHeader = null;
		} catch (S3Exception e) {
			if (e.getErrorCode() == HttpURLConnection.HTTP_NOT_FOUND)
				throw new FileNotFoundException("file not found" + e.toString());
			else
				throw new ResourceException("error while getting resource", e);
		}
		return this;
	}

	@Override
	public S3Resource createDirectoryResource() throws ResourceException, IOException {
		if (null != this.s3Object)
			if (!this.s3Object.isDeleted())
				throw new IllegalStateException(
						"Can't create new directory resource because it already exists");
		// extract the header attributes and contentType
		Map<String, String> header = this.finalizeHeader();
		// put the S3 object
		try {
			S3Response response = S3Object.putObject(encodeAsDirectory(this.uri), header, null, 0,
					null);
			// init this resource instance
			this.s3Object = new S3Object(response);
			this.uri = this.s3Object.uri;
			this.modifiedHeader = null;
		} catch (S3Exception e) {
			if (e.getErrorCode() == HttpURLConnection.HTTP_NOT_FOUND)
				throw new FileNotFoundException("file not found" + e.toString());
			else
				throw new ResourceException("error while creating resource", e);
		}
		return this;
	}
	
	@Override
	public S3Resource write(long length, InputStream in) throws ResourceException, IOException {
		// TODO V2 better errorhandling
		if ((null != s3Object) && (isDirectory()))
			throw new IllegalStateException(
					"This resource is already defined as directory. Can't write content to directory");
		// extract the header attributes and contentType
		Map<String, String> header = this.finalizeHeader();
		String ct = header.remove(S3Headers.CONTENT_TYPE);
		MimeType contentType = (null!=ct)? new MimeType(ct):null;
		this.uri = encodeAsFile(this.uri);
		// put the S3 object
		try {
			S3Response response = S3Object.putObject(encodeAsFile(this.uri), header, contentType,
					length, in);
			S3Response delete = null;
			// delete old S3Object if URL has changed
			if ((null != this.s3Object) && (!this.s3Object.uri.equals(this.uri)))
				delete = S3Object.deleteObject(this.s3Object.uri);
			// init this resource instance
			if (null == s3Object)
				this.s3Object = new S3Object(response);
			else
				this.s3Object.updateByResponse(response);
			this.uri = this.s3Object.uri;
			this.modifiedHeader = null;
		} catch (S3Exception e) {
			if (e.getErrorCode() == HttpURLConnection.HTTP_NOT_FOUND)
				throw new FileNotFoundException("file not found" + e.toString());
			else
				throw new ResourceException("error while writing resource", e);
		}
		return this;
	}
	
	@Override
	public S3Resource persistChanges() throws ResourceException, IOException {
		// TODO V2 better errorhandling
		if (null == this.s3Object)
			throw new IllegalStateException("The type and status of the resource is undefined");
		if (isDirectory(this.s3Object.uri))
			throw new IllegalStateException(
					"This resource is already defined as directory. Can't write content to directory");
		// extract the header attributes and contentType
		Map <String,String> header = this.finalizeHeader();
		String ct = header.remove(S3Headers.CONTENT_TYPE);
		MimeType contentType = new MimeType(ct);
		this.uri = encodeAsFile(this.uri);
		// copy the S3 object
		S3Response response = S3Object.copyObject(this.s3Object.uri, this.uri, header, contentType);
		S3Response delete = null;
		// workaround to "remember" user metadata
		// delete old S3Object if URL has changed
		if ((null == this.s3Object ) && (!this.s3Object.uri.equals(this.uri))) 
			delete = S3Object.deleteObject(this.s3Object.uri);
		// init this resource instance
		if (null == s3Object)
			this.s3Object = new S3Object(response);
		else 
			this.s3Object.updateByResponse(response);
		this.uri = this.s3Object.uri;
		this.modifiedHeader = null;
		return this;
	}
	/*
	 * ############################################################ 
	 * setting properties of the resource 
	 * ############################################################
	 */
	@Override
	public S3Resource renameTo(URI dest) throws ResourceException {
		if (null != dest)
			throw new NullPointerException("argument 'dest' must not be null");
		// if the resource has been created, check that the URL is of the same type as the resource
		// when renaming
		// TODO thing wether we check isFile or not!!!!!!!!!!!!!!!!!
		// TODO V2 check relative change
		if ((null == s3Object) ||                                      // resource not yet definied
			(isFile(this.s3Object.uri) && isFile(dest)) ||       // change a file
			(isDirectory(this.s3Object.uri) && isDirectory(dest))) {
				this.uri = dest;
				if (null != this.modifiedHeader) 
					initModifiedHeader();
		} else
			throw new IllegalStateException(
					"you must not change the type of the resource after creation");
		return null;
	}
	
	@Override
	public void setMetadata(Map<String, String> map) throws ResourceException {
		// TODO V2 check max allowed headers
		this.modifiedHeader = map;
		// set prefix for the custom metadata
		if (null != modifiedHeader) 
			for (Entry<String,String> entry : modifiedHeader.entrySet()) {
				entry.setValue(S3Headers.X_AMZ_META + entry.getValue());
			}
}
	@Override
	public Resource addMetadata(String key, String value) throws ResourceException {
		if (key != null) {
			if (null == this.modifiedHeader)
				initModifiedHeader();
			this.modifiedHeader.put(S3Headers.X_AMZ_META + key, value);
			return this;
		} else
			throw new NullPointerException("Key MUST NOT be null");
	}
	@Override
	public Resource setLastModificationTime(long timestamp) {
			logger.warning("setting lastModified is not supported for this resource");
			return this;
	}
	@Override
	public Resource setContentType(MimeType mimeType) {
		// you can only set a content type to a file
		if ((null != this.s3Object) && isDirectory(this.s3Object.uri)) {
			throw new IllegalStateException("Cant't set content type for directory resource");
		} 
		if (null == this.modifiedHeader)
			initModifiedHeader();
		this.modifiedHeader.put(S3Headers.CONTENT_TYPE.toString(), mimeType.toString());
		return this;
	}
	/*
	 * ############################################################ 
	 * retrieve infos about the resource
	 * ############################################################
	 */
	@Override
	public boolean isFile() {
		return (null != this.s3Object)? (isFile(this.s3Object.uri)) : false;
	}
	
	@Override
	public boolean isDirectory() {
		return (null != this.s3Object)? (isDirectory(this.s3Object.uri)) : false;
	}
	
	@Override
	public boolean exists() throws IOException, ResourceException {
		if (null == s3Object)
			getStatus();
		return (null != s3Object)?s3Object.exists():false;
	}

	@Override
	public MimeType getContentType() {
		if ((null != modifiedHeader) && modifiedHeader.containsKey(S3Headers.CONTENT_TYPE))
				return new MimeType(modifiedHeader.get(S3Headers.CONTENT_TYPE));
		if (null != s3Object)
			return new MimeType(s3Object.getContentType());
		else return null;
	}
	
	@Override
	public boolean containsKey(String key) {
		if (null != key) {
			if ((null != this.modifiedHeader)
					&& this.modifiedHeader.containsKey(S3Headers.CONTENT_TYPE.toString()))
				return true;
			else if ((null != s3Object) && s3Object.containsKey(S3Headers.CONTENT_TYPE.toString()))
				return true;
			else
				return false;
		} else
			throw new NullPointerException("Argument 'key' must not be null");
	}
	
	@Override
	public String get(String key) {
		if (null != key) {
			key = S3Headers.X_AMZ_META + key;
			if ((null != this.modifiedHeader)
					&& this.modifiedHeader.containsKey(key))
				return this.modifiedHeader.get(key);
			else if (null != s3Object)
				return s3Object.get(key);
			else
				return null;
		} else
			throw new NullPointerException("Argument 'key' must not be null");
	}
	/**
	 * Return the custom metadata of this resource. Returns also modified attributes
	 * which have been not yet saved.
	 */
	@Override
	public Map<String, String> getMetadata() throws ResourceException {
		// TODO V2 optimize map handling and size
		Map<String, String> tmp = new HashMap<String, String>();
		if (null != this.s3Object)
			for (Entry<String, List<String>> entry : this.s3Object.responseHeader.entrySet())
				if (entry.getKey().startsWith(S3Headers.X_AMZ_META))
					tmp.put(entry.getKey().substring(S3Headers.X_AMZ_META.length()), entry.getValue().toString());
		if (null != this.modifiedHeader)
			for (Entry<String, String> entry : this.modifiedHeader.entrySet())
				if (entry.getKey().startsWith(S3Headers.X_AMZ_META))
					tmp.put(entry.getKey().substring(S3Headers.X_AMZ_META.length()), entry.getValue());
		return tmp;
	}
	
	@Override
	public InputStream getInputStream() throws IOException, ResourceException {
		if ((null != this.s3Object) && (!isFile(this.s3Object.uri)))
			throw new IllegalStateException("Cant getInputStream for directory resource");
		// check the resource status
		this.uri = encodeAsFile(this.uri);
		try {
			// if connection not already available fetch the object
			if ((null==this.s3Object) || (null == this.s3Object.getInputStream())) {
				this.s3Object = S3Object.getObject(uri, (null!=this.s3Object)?this.s3Object.getVersionId():null);
				this.uri = this.s3Object.uri;
				this.modifiedHeader = null;
			}
			return s3Object.getInputStream();
		} catch (S3Exception e) {
			if (e.getErrorCode() == HttpURLConnection.HTTP_NOT_FOUND)
				throw new FileNotFoundException("file not found" + e.toString());
			else
				throw new ResourceException("error while getting resource", e);
		}
	}

	@Override
	public OutputStream getOutputStream() throws IOException, ResourceException {
		if ((null != this.s3Object) && (!isFile(this.s3Object.uri)))
			throw new IllegalStateException("Cant getInputStream for directory resource");
		// check the resource status
		this.uri = encodeAsFile(this.uri);
		try {
			// if connection not already available fetch the object
			if ((null==this.s3Object) || (null == this.s3Object.getOutputStream())) {
				this.s3Object = S3Object.getObject(uri, (null!=this.s3Object)?this.s3Object.getVersionId():null);
				this.uri = this.s3Object.uri;
				this.modifiedHeader = null;
			}
			return s3Object.getOutputStream();
		} catch (S3Exception e) {
			if (e.getErrorCode() == HttpURLConnection.HTTP_NOT_FOUND)
				throw new FileNotFoundException("file not found" + e.toString());
			else
				throw new ResourceException("error while getting resource", e);
		}
	}
	/**
	 * Get the status of the S3 resource from S3(exists, isDirectory, isReadable, etc.
	 * <p>
	 * ATTENTION: this initiates a HEAD request to S3
	 * 
	 * @throws ResourceException
	 * @throws IOException
	 */
	protected void getStatus() throws IOException, ResourceException {
		try {
			this.s3Object = S3Object.getHeadObject(this.uri, null);
			this.modifiedHeader = null;
		} catch (S3Exception e) {
			if (e.getErrorCode() == HttpURLConnection.HTTP_NOT_FOUND) {
				// object not found is not an exception for getStatus but an valid information
				this.s3Object = null;
			} else
				// everything else in en exception
				throw new ResourceException(e);
		}
	}
	@Override
	public boolean canRead() {
		return (null!=this.s3Object)?true:false;
	}

	@Override
	public boolean canWrite() {
		// TODO implement ACL handling. At the moment, every s3 object you can
		return (null!=this.s3Object)?true:false;
	}

	@Override
	public long lastModified() {
		if ((null != modifiedHeader) && modifiedHeader.containsKey(S3Headers.LAST_MODIFIED))
			return Long.parseLong(modifiedHeader.get(S3Headers.LAST_MODIFIED));
	if (null != s3Object)
		return s3Object.getLastModified();
	return 0L;
	}

	@Override
	public long length() {
		if (null != s3Object)
			return s3Object.getContentLength();
		return 0L;
	}

	@Override
	public List<Resource> listResources() throws ResourceException, IOException {
		// check that this resource is a directory
		if (this.isFile())
			throw new IllegalStateException("a file resource can't have child resources");
		// fetch the list of objects from containing bucket
		List<S3Object> list = S3Bucket.listObjects(this.uri, this.getPath(), null);
		List<Resource> res = new ArrayList<Resource>();
		// convert to S3Resources
		for (S3Object s3Obj : list) {
			try {
				if (!this.uri.equals(s3Obj.uri))
					res.add(new S3Resource(s3Obj));
			} catch (URISyntaxException e) {
				// TODO handle error
				e.printStackTrace();
				throw new ResourceError(e);
			}
		}
		// and return
		return res;
	}

	@Override
	public String getName() {
		String path = this.uri.getPath();
		if (path.endsWith("/")) 
			path.substring(0,path.length()-1);
		if (path.lastIndexOf("/")>=0)
			return path.substring(path.lastIndexOf("/")+1);
		else 
			return "";
	}

	

	@Override
	public URI toURI() {
		return this.uri;
	}

	protected void setETag(String eTag) {
		if (null == this.modifiedHeader)
			initModifiedHeader();
		this.modifiedHeader.put(S3Headers.ETAG, eTag);
	}


	@Override
	public String getMD5Hash() {
		if (null != s3Object)
			return s3Object.getETag();
		return null;
	}

	@Override
	public boolean isRoot() throws IOException {
		if (exists())
			return (uri.getPath().isEmpty() || uri.getPath().equals(SEPERATOR)) ? true : false;
		else
			return false;
	}
	private void initModifiedHeader() {
		this.modifiedHeader = new HashMap<String,String>();
	}
	
	private Map<String, String> finalizeHeader() {
		// working Map is modifiedHeader, therfore init if null
		if (null == this.modifiedHeader)
			initModifiedHeader();
		if (null != this.s3Object)
			if (null != this.s3Object.responseHeader)
				for (Entry<String, List<String>> entry : this.s3Object.responseHeader.entrySet()) {
					if ((null != entry.getKey()) &&
						((entry.getKey().startsWith(S3Headers.X_AMZ_META) && // either it is a custom metadata 
						 (!this.modifiedHeader.containsKey(entry.getKey()))) ||         // and not already modified
						 entry.getKey().equals(S3Headers.CONTENT_TYPE)				   // or it is a conent Type
					   )) 
						this.modifiedHeader.put(entry.getKey(), entry.getValue().get(0)); // then add to new header
				}
		return this.modifiedHeader;
	}
	/**
	 * Reformat the S3URL to locate a directory resource, i.e. contains an octet encoded trailing
	 * "/"
	 */
	private static URI encodeAsDirectory(URI uri) {
		if (null == uri)
			throw new NullPointerException();
		// add a trailing, octet encoded "/" to the path and rebuild the URL
		try {
			if (null!=uri.getPath()){
					if (uri.getPath().endsWith(SEPERATOR))
						return uri;
					else
						return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath() + SEPERATOR, null);
			}else 
				return new URI(uri.getScheme(), uri.getAuthority(), SEPERATOR, null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("can't encode a s3 object with directory path", e);
		}
	}

	/**
	 * Reformat the S3URL to locate a file resource, i.e. doesn't contain an octet encoded trailing
	 * "/"
	 */
	private static URI encodeAsFile(URI uri) {
		if (null == uri)
			throw new NullPointerException();
		// add a trailing, octet encoded "/" to the path and rebuild the URL
		try {
			if (null!=uri.getPath()){
					if (!uri.getPath().endsWith(SEPERATOR))
						return uri;
					else
						return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath().substring(
								0, uri.getPath().length() - 1), null);
			}else 
				throw new IllegalArgumentException("can't encode a s3 object with empty file path");
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("can't encode a s3 object with file path", e);
		}
	}
	/**
	 * Tests the given URI whether denotes a file path (doesn't end with "/") or not
	 * @param uri
	 * @return
	 */
	private static boolean isFile(URI uri) {
		return !uri.getPath().endsWith(SEPERATOR);
	}
	/**
	 * Tests the given URI whether denotes a directory path (ends with "/") or not
	 * @param uri
	 * @return
	 */
	private static boolean isDirectory(URI uri) {
		return !S3Resource.isFile(uri);
	}
	
	@Override
	public boolean supportsMetadata() {
		return true;
	}

	@Override
	public boolean supportsVersioning() {
		return false;
	}

	@Override
	public boolean supportsSetLastModified() {
		return false;
	}
}
