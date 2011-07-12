package org.ccopy.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.ccopy.resource.s3.S3RL;

/**
 * This class provides a skeletal implementation of the Asset interface, to
 * minimize the effort required to implement this interface.
 * 
 * @author mholakovsky
 * 
 */
public abstract class Resource {
	/**
	 * The service-dependent default name-separator character. This field is
	 * initialized to the typical value
	 */
	public static String seperator = "/";
	/**
	 * The resource locator of this resource
	 */
	protected ResourceLocator url;
	/**
	 * The metadata
	 */
	protected HashMap<String, String> attributes = null;
	/**
	 * The following attributes are flags
	 */
	protected Boolean exists = null;
	protected boolean isDirectory;
	protected boolean canRead;
	protected boolean canWrite;
	protected String md5Hash = null;
	private int length;
	protected long lastModified;
	/**
	 * The logger for the class
	 */
	private static Logger logger = Logger.getLogger("org.ccopy");

	/**
	 * Constructor of AbstractResource with URL object
	 * 
	 * @param url
	 *            - the locator for this resource
	 * @throws NullPointerException
	 *             when argument is {@code null}
	 */
	protected Resource(ResourceLocator url) {
		if (null == url)
			throw new NullPointerException("Argument must not be null");
		this.url = url;
	}

	/**
	 * Constructor of AbstractResource as child of another resource TODO parent
	 * MUST be a directory (=trailing "/") otherwise Exception
	 * 
	 * @param res
	 * @param child
	 * @throws ResourceException
	 * @throws IOException
	 * @throws ResourceNotFoundException
	 * @throws SecurityException
	 */
	protected Resource(Resource parent, String child) throws SecurityException, IOException,
			ResourceException {
		if ((child == null) || (parent == null))
			throw new NullPointerException("both arguments must not be null");
		if (parent.isDirectory()) {
			url = new S3RL(parent.toURL() + child);
		} else
			throw new MalformedURLException("parent resource must be an directory");
	}

	/**
	 * Add metadata to this Resource
	 * 
	 * @param key
	 * @param val
	 * @throws NullPointerException
	 */
	public void addMetadata(String key, String val) throws NullPointerException {
		if (key != null)
			attributes.put(key, val);
		else
			throw new NullPointerException("Key MUST NOT be null");
	}
	/**
	 * Rename the resource.
	 * 
	 * @param dest
	 *            - the locator of the resource.
	 * @return {@code true} if and only if the renaming succeeded; {@code false}
	 *         otherwise
	 * @throws SecurityException
	 * @throws IOException
	 */
	public boolean renameTo(ResourceLocator dest) throws SecurityException, IOException {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * Deletes the Resource denoted by this abstract pathname. If this pathname
	 * denotes a directory (trailing slash) then it must be empty in order to be
	 * deleted.
	 * 
	 * @return
	 * @throws SecurityException
	 * @throws IOException
	 */
	public boolean delete() throws SecurityException, IOException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Return the URL representation of this Resource.
	 * 
	 * @return a URL object representing this Resource, which is URLEncoded
	 */
	public URL toURL() {
		return url.toURL();
	}

	/**
	 * Return an unmodifiable Map of the metadata of the resource
	 * 
	 * @return
	 */
	public Map<String, String> getMetadata() {
		return Collections.unmodifiableMap(attributes);
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Returns the pathname of this Resource into a pathname string.
	 * 
	 * @return
	 */
	public String getPath() {
		// TODO unclear whether it should be decoded first?
		return url.getPath();
	}

	public abstract String getChild(String name);

	public abstract Resource getChildResource(String name);

	public abstract String getParent();

	public abstract Resource getParentResource();

	public InputStream getInputStream() throws SecurityException, IOException, ResourceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public OutputStream getOutputStream() throws SecurityException, IOException, ResourceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public long lastModified() throws SecurityException, IOException, ResourceException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long length() throws SecurityException, IOException, ResourceException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String[] list() throws SecurityException, IOException {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * Creates a directory named by this Resource. This is an atomic operation.
	 * 
	 * @throws SecurityException
	 * @throws IOException
	 * @throws ResourceNotFoundException
	 */
	public abstract void mkdir() throws SecurityException, IOException, ResourceException;

	/**
	 * Returns the name of the file or directory denoted by this Resource. This
	 * is just the last name in the pathname's name sequence, separated by the
	 * service specific separator.
	 * 
	 * @return The name of the file or directory denoted by this Resource
	 */
	public abstract String getName();

	/*********
	 * perform checks on the resource
	 *********/
	/**
	 * Tests whether the Resource can be read
	 * 
	 * @return <code>true</code> if you can read the resource, otherwise
	 *         <code>false</code>
	 * @throws SecurityException
	 * @throws IOException
	 * @throws ResourceException
	 */
	public boolean canRead() throws SecurityException, IOException, ResourceException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Tests wheter the Resource can be written
	 * 
	 * @return <code>true</code> if you can write the resource, otherwise
	 *         <code>false</code>
	 * @throws SecurityException
	 * @throws IOException
	 * @throws ResourceException
	 */
	public boolean canWrite() throws SecurityException, IOException, ResourceException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean exists() throws SecurityException, IOException, ResourceException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Returns true only if the resource is a directory and exists
	 * 
	 * @return
	 * @throws ResourceException
	 */
	public boolean isDirectory() throws SecurityException, IOException, ResourceException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFile() throws SecurityException, IOException, ResourceException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Tests whether this Resource supports reading and writing of custom
	 * metadata. A {@code FileResource} doesn't support custom metadata but a
	 * {@code S3Resource}
	 * 
	 * @return
	 */
	public abstract boolean supportsMetadata();

	/********
	 * overwrite some Object methods
	 ********/
	/**
	 * Compares this Resource with an other object.
	 * 
	 * @param obj
	 * @return {@code true} if obj is {@code !null} and is a Resource object
	 *         which has the same hash code
	 */
	@Override
	public boolean equals(Object obj) {
		return url.equals(obj);
	}

	/*
	 * Returns a string representation of this object, e.q.<br> {@code
	 * S3Resource@https://bucket.s3.amazon.com/file.txt}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getName() + "@" + url.toString();
	}

	/**
	 * Computes a hash code for this Resource based on the ResourceLocator
	 * 
	 * @return the unique hash code
	 */
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
}
