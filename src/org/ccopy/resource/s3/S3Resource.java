package org.ccopy.resource.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.ccopy.resource.Resource;
import org.ccopy.resource.Resource;
import org.ccopy.resource.ResourceException;
import org.ccopy.resource.ResourceLocator;
import org.ccopy.util.HttpMethod;

/**
 * Implements the Asset as simple File Object.
 * 
 * 
 * @author mholakovsky
 * 
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
	 * Construct an Resource based on a {@code ResourceLocator}.
	 * 
	 * @param url
	 *            - the locator for this resource
	 */
	public S3Resource(S3RL url) {
		super(url);
	}

	// /**
	// * Creates a new <code>S3Resource</code> instance from a parent abstract
	// * pathname and a child pathname string.
	// *
	// * <p>
	// * If <code>parent</code> is <code>null</code> then the new
	// * <code>File</code> instance is created as if by invoking the
	// * single-argument <code>File</code> constructor on the given
	// * <code>child</code> pathname string.
	// *
	// * <p>
	// * Otherwise the <code>parent</code> abstract pathname is taken to denote
	// a
	// * directory, and the <code>child</code> pathname string is taken to
	// denote
	// * either a directory or a file. If the <code>child</code> pathname string
	// * is absolute then it is converted into a relative pathname in a
	// * system-dependent way. If <code>parent</code> is the empty abstract
	// * pathname then the new <code>File</code> instance is created by
	// converting
	// * <code>child</code> into an abstract pathname and resolving the result
	// * against a system-dependent default directory. Otherwise each pathname
	// * string is converted into an abstract pathname and the child abstract
	// * pathname is resolved against the parent.
	// *
	// * @param parent
	// * @param child
	// * @throws MalformedURLException
	// * @throws ResourceException
	// */
	// public S3Resource(S3Resource parent, String child) throws
	// MalformedURLException {
	// super(parent, child);
	// }

	/**
	 * Validate the URL to be a S3 URL
	 * 
	 * @param url
	 * @return
	 */
	private boolean validateUrl(URL url) {
		// TODO check the Url to locate a S3 request, e.q. check hostname and
		// you have a
		// bucket as subdomain
		return true;
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

	public boolean canRead() throws IOException, SecurityException, ResourceException {
		// TODO check whether to catch SecurityException because in that case
		// you can't read it??!
		if (exists == null)
			getStatus();
		return canRead;
	}

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
	public boolean delete() throws IOException {
		// TODO implement delete in S3 the file
		return false;
	}

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
	public InputStream getInputStream() throws IOException {
		// if (inStream == null) inStream = new FileInputStream(file);
		return null;
	}

	public OutputStream getOutputStream() throws IOException {
		// if (inStream == null) outStream = new FileOutputStream(file);
		return null;
	}

	/**
	 * Return true only if the object key ends with a "/" and exists.
	 * 
	 * @throws ResourceException
	 * @throws IOException
	 * @throws SecurityException
	 */
	public boolean isDirectory() throws SecurityException, IOException, ResourceException {
		if (exists == null)
			getStatus();
		return isDirectory;
	}

	public long lastModified() throws SecurityException, IOException, ResourceException {
		if (exists == null)
			getStatus();
		// return new Daattributes.get(EnumResourceAttributes.LAST_MODIFIED);;
		return 0;
	}

	public long length() throws SecurityException, IOException, ResourceException {
		if (exists == null)
			getStatus();
		return 0;
	}

	public String[] list() {
		return null;
	}

	/**
	 * Returns an array of abstract pathnames denoting the resources in the
	 * directory denoted by this abstract pathname.
	 * 
	 * <p>
	 * If this abstract pathname does not denote a directory, then this method
	 * returns <code>null</code>. Otherwise an array of <code>Resource</code>
	 * objects is returned, one for each resource or directory in the directory.
	 * Pathnames denoting the directory itself and the directory's parent
	 * directory are not included in the result. Each resulting abstract
	 * pathname is constructed from this abstract pathname using the
	 * <code>Resource(Resource,String)}</code> constructor. Therefore if this
	 * pathname is absolute then each resulting pathname is absolute; if this
	 * pathname is relative then each resulting pathname will be relative to the
	 * same directory.
	 * 
	 * <p>
	 * There is no guarantee that the name strings in the resulting array will
	 * appear in any specific order; they are not, in particular, guaranteed to
	 * appear in alphabetical order.
	 * 
	 * @throws ResourceException
	 * @throws IOException
	 * @throws SecurityException
	 */
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
	 * Creates a S3 Resource with 0 Byte content if it doesn't exist. This
	 * method triggers an immediate request to S3.
	 */
	public void mkdir() {
		// TODO implement

	}

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
}
