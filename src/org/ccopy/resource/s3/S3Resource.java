package org.ccopy.resource.s3;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.ccopy.resource.Resource;
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
	public static final boolean SUPPORTS_SET_LASTMODIFIED = false;
	/**
	 * The region of the S3 bucket for this resource
	 */
	protected String bucketRegion = null;
	/**
	 * The name of the bucket
	 */
	protected String bucketName = null;
	/**
	 * the proper encoded S3 URL of this resource
	 */
	protected S3URL s3URL;
	protected S3Object s3Object;
	private String versionId;

	/**
	 * Construct a Resource based on a {@code ResourceLocator}.
	 * 
	 * @param url
	 *        the locator for this resource
	 * @throws MalformedURLException
	 *         when the URL is invalid
	 */
	public S3Resource(URL url) throws MalformedURLException {
		super(url);
		this.s3URL = new S3URL(url);
		this.reset();
	}

	/**
	 * Constructor of a new {@code S3Resource} as child of another resource.
	 * 
	 * @param s3
	 * @param child
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 * @throws MalformedURLException
	 *         when the resulting URL is invalid
	 */
	public S3Resource(S3Resource s3, String child) throws MalformedURLException {
		super(s3, child);
		this.s3URL = new S3URL(s3, child);
		this.reset();
	}

	/**
	 * Construct a Resource based on a 'bucket' and 'key' for the S3 object.
	 * 
	 * @param bucket
	 *        the name of the S3 bucket
	 * @param key
	 *        the object key, e.g. "/path/to/my/key.txt" or <code>null</code> for the root directory
	 * @throws MalformedURLException
	 * @throws NullPointerException
	 *         when bucket is <code>null</code>
	 */
	public S3Resource(String bucket, String key) throws MalformedURLException {
		this.reset();
		this.s3URL = S3URL.fromPath(bucket, key);
	}

	/**
	 * Get the status of the S3 resource from S3(exists, isDirectory, isReadable, etc.
	 * <p>
	 * ATTENTION: this initiates a HEAD request to S3
	 * 
	 * @throws ResourceException
	 * @throws IOException
	 */
	protected void getStatus() throws SecurityException, IOException, ResourceException {
		this.reset();
		try {
			this.s3Object = S3Object.getHeadObject(this.s3URL, null);
			this.isDefined = true;
			this.exists = this.s3Object.exists();
			this.isFile = this.s3URL.isFile;
			this.contentType = new MimeType(this.s3Object.getContentType());
		} catch (S3Exception e) {
			if (e.getErrorCode() == HttpURLConnection.HTTP_NOT_FOUND) {
				// object not found is not an exception for getStatus but an valid information
				this.isDefined = false;
				this.exists = false;
			} else
				// everything else in en exception
				throw new ResourceException(e);
		}
	}

	/**
	 * Reset the resource to an "new" status. The {@link #s3URL} is not reset, because it defines a S3Resource
	 */
	protected void reset() {
		super.reset();
		this.s3Object = null;
//		this.s3URL = null;
	}

	@Override
	public boolean canRead() {
		return canRead;
	}

	@Override
	public boolean canWrite() {
		return canWrite;
	}

	/**
	 * Delete the S3 Resource
	 */
	@Override
	public boolean delete() throws ResourceException, IOException {
		S3Response res;
		try {
			res = S3Object.deleteObject(s3URL);
		} catch (S3Exception e) {
			if (e.getErrorCode()==HttpURLConnection.HTTP_NOT_FOUND)
				throw new FileNotFoundException("file not found" + e.toString());
			else 
				throw new ResourceException("error while deleting",e);
		}
		if (res.getReturnCode() != HttpURLConnection.HTTP_NO_CONTENT)
			throw new ResourceException("something went wrong when deleting the file: " + res.toString());
		this.reset();
		this.exists = Boolean.FALSE; // we are sure now, that it don't exist
		this.versionId = versionId;
		return false;
	}

	@Override
	public boolean exists() throws ResourceException, SecurityException, IOException {
		if (exists == null)
			getStatus();
		return exists;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.astor.asset.Asset#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException, ResourceException {
		this.s3URL.encodeAsFile();
		try {
			this.s3Object = S3Object.getObject(this.s3URL, this.versionId);
		} catch (S3Exception e) {
			if (e.getErrorCode()==HttpURLConnection.HTTP_NOT_FOUND)
				throw new FileNotFoundException("file not found" + e.toString());
			else 
				throw new ResourceException("error while getting resource",e);
		}
		this.exists = true;
		this.isModified = false;
		return s3Object.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException, ResourceException {
		this.s3URL.encodeAsFile();
		// TODO implement
		this.isModified = false;
		return s3Object.getOutputStream();
	}

	@Override
	public long lastModified() {
		return this.lastModified;
	}

	@Override
	public long length() {
		return this.size;
	}

	@Override
	public Resource[] listResources() throws ResourceException {
		if (!this.isDirectory())
			throw new IllegalStateException("a file resource can't have child resources");
		// TODO fetch the sub resources for this S3 resource by creating correct request
		return new Resource[0];
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getChild(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource getChildResource(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource getParentResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public S3Resource createFileResource() throws ResourceException {
		this.isModified = false;
		this.s3URL.encodeAsFile();
		// TODO implement
		return null;

	}

	@Override
	public S3Resource createDirectoryResource() throws ResourceException {
		this.isModified = true;
		this.s3URL.encodeAsDirectory();
		// TODO implement
		return null;

	}

	@Override
	public S3Resource persistChanges() throws ResourceException, IOException {
		// check if already created
		if (!isDefined)
			throw new IllegalStateException(
					"you must create the file or check whether it exists before you can persist changes");
//		try {
			// make a copy of the object to itself
			S3Response response = S3Object.copyObject(this.s3URL, this.s3URL, this.attributes, contentType);
			// reset modification flag
			this.isModified = false;
//		} catch (IO e) {
//			
//			else 
//				throw new ResourceException("error while copying the resource to itself: ",e);
//		}
		return null;
	}

	@Override
	public S3Resource renameTo(URL dest) throws ResourceException {
		if (null != dest)
			throw new NullPointerException("argument 'dest' must not be null");
		// if the resource has been created, check that the URL is of the same type as the resource
		// when renaming
		if (this.isDefined) {
			if (((this.isFile) && s3URL.isFile) || ((!this.isFile) && (!s3URL.isFile))) {
				this.isModified = true;
			} else
				throw new IllegalStateException(
						"you must not change the type of the resource after creation");
		} else {
			// do nothing, because you may change the type of a resource which is not yet created
		}
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public URL toURL() {
		return s3URL.toURL();
	}

	protected void setETag(String eTag) {
		this.md5Hash = eTag;
	}
}
