package org.ccopy.resource.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.IllegalSelectorException;
import java.security.InvalidKeyException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.ccopy.resource.Resource;
import org.ccopy.resource.ResourceException;
import org.ccopy.resource.util.MimeType;
import org.ccopy.util.HttpMethod;

/**
 * Implements the Asset as simple File Object.
 * 
 * @author coffeemug13
 */
public class S3Resource extends Resource {
	/**
	 * The region of the S3 bucket for this resource
	 */
	protected String bucketRegion = null;
	/**
	 * The name of the bucket
	 */
	protected String bucketName = null;
	// TODO think about lastModified and how to set attribute over package bounderies
	protected long lastModified;
	/**
	 * the proper encoded S3 URL of this resource
	 */
	protected S3URL s3URL;
	protected S3Object s3Object;
	private long length;
	/**
	 * The pathname separator for S3 pathnames
	 */
	public static final String SEPERATOR = "/";

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
		this.s3URL = S3URL.fromPath(bucket, key);
	}

	/**
	 * Get the status of the S3 resource (exists, isDirectory, isReadable, etc.
	 * 
	 * @throws ResourceException
	 * @throws IOException
	 */
	protected void getStatus() throws SecurityException, IOException, ResourceException {
		try {
			this.s3Object = S3Object.getHeadObject(this.s3URL, null);
			this.isDefined = true;
			this.exists = this.s3Object.exists();
			this.isFile = this.s3URL.isFile;
			this.contentType = new MimeType(this.s3Object.getContentType());
		} catch (S3Exception e) {
			throw new ResourceException(e);
		}
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
	public boolean delete() throws ResourceException {
		// TODO implement delete in S3 the file
		this.isModified = false;
		// TODO implement
		this.isDefined = true;
		this.exists = Boolean.FALSE;
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
		// TODO implement
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException, ResourceException {
		this.isModified = false;
		this.s3URL.encodeAsFile();
		// TODO implement
		return null;
	}

	@Override
	public long lastModified() {
		return this.lastModified;
	}

	@Override
	public long length() {
		return this.length;
	}

	@Override
	public Resource[] listResources() throws ResourceException {
		if (!this.isDirectory())
			throw new IllegalStateException("a file resource can't have child resources");
		// TODO fetch the sub resources for this S3 resource by creating correct
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
	public S3Resource persistChanges() throws ResourceException {
		// check if already created
		if (!isDefined)
			throw new IllegalStateException(
					"you must create the file before you can persist changes");
		// reset modification flag
		this.isModified = false;
		// TODO implement the rest
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
	public S3Resource setLastModificationTime(long last) {
		this.isModified = true;
		this.lastModified = last;
		return this;
	}

	@Override
	public URL toURL() {
		return s3URL.toURL();
	}

	protected void setETag(String eTag) {
		this.md5Hash = eTag;
	}
}
