package org.ccopy.resource.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.ccopy.resource.Resource;
import org.ccopy.resource.ResourceException;
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
	/**
	 * the proper encoded S3 URL of this resource
	 */
	protected S3URL s3URL;
	/**
	 * The pathname separator for S3 pathnames
	 */
	public static final String SEPERATOR = "/";

	/**
	 * Construct an Resource based on a {@code ResourceLocator}.
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
	public S3Resource(S3Resource s3, String child) throws MalformedURLException, ResourceException {
		super(s3, child);
			this.s3URL = new S3URL(s3,child);
	}

	/**
	 * Get the status of the S3 resource (exists, isDirectory, isReadable, etc.
	 * 
	 * @throws ResourceException
	 * @throws IOException
	 * @throws InvalidKeyException
	 */
	protected void getStatus() throws SecurityException, IOException, ResourceException {
		// TODO implement
	}

	@Override
	public boolean canRead() throws ResourceException {
		// TODO check whether to catch SecurityException because in that case
		// you can't read it??!
		if (exists == null)
			getStatus();
		return canRead;
	}

	@Override
	public boolean canWrite() throws SecurityException, IOException, ResourceException {
		// TODO check whether to catch SecurityException because in that case
		// you can't read it??!
		if (exists == null)
			getStatus();
		return canWrite;
	}

	/**
	 * Delete the S3 Resource
	 */
	@Override
	public boolean delete() throws IOException {
		// TODO implement delete in S3 the file
		return false;
	}

	@Override
	public boolean exists() throws SecurityException, IOException, ResourceException {
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
	public InputStream getInputStream() throws IOException {
		// if (inStream == null) inStream = new FileInputStream(file);
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		// if (inStream == null) outStream = new FileOutputStream(file);
		return null;
	}

	@Override
	public long lastModified() throws SecurityException, IOException, ResourceException {
		if (exists == null)
			getStatus();
		// return new Daattributes.get(EnumResourceAttributes.LAST_MODIFIED);;
		return 0;
	}

	@Override
	public long length() throws SecurityException, IOException, ResourceException {
		if (exists == null)
			getStatus();
		return 0;
	}

	public String[] list() {
		return null;
	}

	/**
	 * Returns an array of abstract pathnames denoting the resources in the directory denoted by
	 * this abstract pathname.
	 * <p>
	 * If this abstract pathname does not denote a directory, then this method returns
	 * <code>null</code>. Otherwise an array of <code>Resource</code> objects is returned, one for
	 * each resource or directory in the directory. Pathnames denoting the directory itself and the
	 * directory's parent directory are not included in the result. Each resulting abstract pathname
	 * is constructed from this abstract pathname using the <code>Resource(Resource,String)}</code>
	 * constructor. Therefore if this pathname is absolute then each resulting pathname is absolute;
	 * if this pathname is relative then each resulting pathname will be relative to the same
	 * directory.
	 * <p>
	 * There is no guarantee that the name strings in the resulting array will appear in any
	 * specific order; they are not, in particular, guaranteed to appear in alphabetical order.
	 * 
	 * @throws ResourceException
	 * @throws IOException
	 * @throws SecurityException
	 */
	@Override
	public Resource[] listResources() throws SecurityException, IOException, ResourceException {
		if (!this.isDirectory())
			return null;
		// TODO fetch the sub resources for this S3 resource by creating correct
		// Request URL
		Resource[] assetList = new Resource[0]; // allocate a array to fit all
												// found resources
		// for(int count=0;count<fileList.length;count++) {
		// //assetList[count]= new
		// S3Resource(this,"relative name of subresource");
		// }
		return assetList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ccopy.resource.Resource#renameTo(java.net.URI)
	 */
	// @Override
	public boolean renameTo(URI dest) throws SecurityException, IOException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Creates a S3 Resource with 0 Byte content if it doesn't exist. This method triggers an
	 * immediate request to S3.
	 */
	public void mkdir() {
		// TODO implement

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFile() throws SecurityException, IOException, ResourceException {
		if (exists == null)
			getStatus();
		return !isDirectory;
	}

	@Override
	public boolean renameTo(ResourceLocator dest) throws SecurityException, IOException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * S3 supports custom metadata
	 * 
	 * @return true
	 */
	@Override
	public boolean supportsMetadata() {
		return true;
	}

	@Override
	public String getChild(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource getChildResource(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource getParentResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createFileResource() throws ResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void createDirectoryResource() throws ResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void persistChanges() throws ResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean renameTo(URL dest) throws ResourceException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setLastModificationTime(long last) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public URL toURL() {
		return s3URL.toURL();
	}
}
