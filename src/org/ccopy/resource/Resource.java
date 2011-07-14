package org.ccopy.resource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a generic Resource. The design of this class is derived from the
 * {@link File} class but differs in the way write operations are done.
 * <p>
 * A resource has to kind of representations: as file or directory. A file resource can have a
 * content but no childs resources. In contrast a directory resource can't have content but may have
 * child resources, either file or directory resources.
 * <p>
 * Only the following methods of this class guarantee, that modification to properties are finally
 * persisted:
 * <p>
 * <ul>
 * <li>{@code Resource#createFileResource()}
 * <li>{@code Resource#createDirResource()}
 * <li>{@code Resource#persistChanges()}
 * <li>{@code Resource#delete()}
 * </ul>
 * It can't be guaranteed that this methods are atomic operations because they depend on the API of
 * the background service, where this resource is located. So it could yield to several physical
 * operations depending on the resource implementation.
 * <p>
 * When a resource content get's written with a {@code ResourceOutputStream} the properties of the
 * resource are also persisted, so you don't need to call the above methods afterwards. In case the
 * resource doesn't exist it will be created implicitly. Nevertheless it is possible, that an
 * implementation of {@code Resource} like {@code FileResource} decides to perform changes to
 * properties immediately.
 * <p>
 * Every other method which set's properties of the resource like
 * {@code Resource#renameTo(ResourceLocator)} <em>MAY</em> perform immediate actions on the resource
 * but needs a {@code Resource#create()} or {@code Resource#update()} to get persistent (= written
 * to the service). Therefore you <em>MUST</em> call one of the guaranteed persistent methods to
 * ensure that changes are persisted.
 * 
 * @author coffeemug13
 */
public abstract class Resource {
	/**
	 * The service-dependent default name-separator character. This field is initialized to the
	 * typical value
	 */
	public static String seperator;
	/**
	 * The resource locator of this resource. This property MUST be set when the class is
	 * constructed.
	 */
	protected ResourceLocator url;
	/**
	 * The metadata of this resource
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
	protected long lastModified;
	/**
	 * The logger for the class
	 */
	private static Logger logger = Logger.getLogger("org.ccopy");
	/**
	 * Tests whether this Resource supports reading and writing of custom metadata. A
	 * {@code FileResource} doesn't support custom metadata but a {@code S3Resource}
	 */
	public static boolean SUPPORTS_METADATA;

	/*
	 * ############################################################ 
	 * Constructors
	 * ############################################################
	 */
	/**
	 * Constructor of {@code Resource} based on a {@link ResourceLocator}.
	 * 
	 * @param url
	 *        - the locator for this resource
	 * @throws NullPointerException
	 *         when argument is {@code null}
	 */
	protected Resource(ResourceLocator url) {
		if (null == url)
			throw new NullPointerException("Argument must not be null");
		this.url = url;
		if (logger.isLoggable(Level.FINE)) logger.fine("Resource created for '"+ url.toString() + "'");
	}

	/**
	 * Constructor of {@code Resource} as child of another resource.
	 * 
	 * @param parent
	 *        a {@code Resource} with "{@code Resource#isDirectory()==true}"
	 * @param child
	 *        the filename of the child as string
	 * @throws NullPointerException
	 *         when argument is {@code null}
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	protected Resource(Resource parent, String child) throws SecurityException, IOException,
			ResourceException {
		if ((child == null) || (parent == null))
			throw new NullPointerException("both arguments must not be null");
		if (parent.isDirectory()) {
			url = parent.getChildResource(child).toRL();
			if (logger.isLoggable(Level.FINE)) logger.fine("Resource created for '"+ url.toString() + "'");
		} else
			throw new MalformedURLException("parent resource must be an directory");
	}

	/*
	 * ############################################################ 
	 * operations which alter the physical resource and persist changes
	 * ############################################################
	 */
	/**
	 * Creates a file resource. This is a guaranteed persistent operation. All prior modifications
	 * of this resource properties are also persisted.
	 * 
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract void createFileResource() throws ResourceException;

	/**
	 * Creates a directory resource. This is a guaranteed persistent operation. All prior
	 * modifications of this resource properties are also persisted.
	 * 
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract void createDirectoryResource() throws ResourceException;

	/**
	 * Updates the properties of this resource. This is a guaranteed persistent operation. All prior
	 * modifications of this resource properties are also persisted.
	 * 
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract void persistChanges() throws ResourceException;

	/**
	 * Deletes the Resource denoted by this abstract pathname. If this pathname denotes a directory
	 * (trailing slash) then it must be empty in order to be deleted.
	 * <p>
	 * This is a guaranteed persistent operation. All prior modifications of this resource
	 * properties are discarded!
	 * 
	 * @return
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract boolean delete() throws ResourceException;

	/*
	 * ############################################################ 
	 * setting properties of the resource 
	 * ############################################################
	 */
	/**
	 * Set the metadata to this Resource. This method replaces the existing metadata of this
	 * resource.
	 * <p>
	 * This method is not guaranteed to be persistent. You <em>MUST</em> call one of the persistent
	 * methods or write with an {@code ResourceOutputStream} to the resource to ensure that this
	 * change get's persisted.
	 * 
	 * @param map
	 *        the List of metadata to replace the existing; <code>null</code> purges all metadata
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public void setMetadata(Map<String, String> map) throws ResourceException {
		if (map != null)
			attributes = (HashMap<String, String>) map;
		else
			attributes = null;
	}

	/**
	 * Add metadata to this Resource
	 * <p>
	 * This method is not guaranteed to be persistent. You <em>MUST</em> call one of the persistent
	 * methods or write with an {@code ResourceOutputStream} to the resource to ensure that this
	 * change get's persisted.
	 * 
	 * @param key
	 *        the key of the metadata
	 * @param val
	 *        the values of the metadata
	 * @throws NullPointerException
	 *         when key is {@code null}
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public void addMetadata(String key, String value) throws ResourceException {
		if (key != null)
			attributes.put(key, value);
		else
			throw new NullPointerException("Key MUST NOT be null");
	}

	/**
	 * Rename the resource. Differently to the {@code File#renameTo(File)} this resource points
	 * after this method call to to renamed resource!
	 * <p>
	 * This method is not guaranteed to be persistent. You <em>MUST</em> call one of the persistent
	 * methods or write with an {@code ResourceOutputStream} to the resource to ensure that this
	 * change get's persisted.
	 * 
	 * @param dest
	 *        - the locator of the resource.
	 * @return {@code true} if and only if the renaming succeeded; {@code false} otherwise
	 */
	public abstract boolean renameTo(ResourceLocator dest) throws ResourceException;

	/**
	 * Sets the last-modified time of the resource.
	 * <p>
	 * This method is not guaranteed to be persistent. You <em>MUST</em> call one of the persistent
	 * methods or write with an {@code ResourceOutputStream} to the resource to ensure that this
	 * change get's persisted.
	 * 
	 * @param last
	 * @return
	 */
	public abstract boolean setLastModificationTime(long last);

	/*
	 * ############################################################ 
	 * retrieve infos about the resource
	 * ############################################################
	 */
	/**
	 * Returns the value for a metadata key.
	 * <p>
	 * A return value of null does not necessarily indicate that the map contains no mapping for the
	 * key; it's also possible that the map explicitly maps the key to null. The containsKey
	 * operation may be used to distinguish these two cases.
	 * 
	 * @param key
	 *        the key whose associated value is to be returned
	 * @return the value for the key or <code>null</code>
	 * @throws NullPointerException
	 *         when argument is {@code null}
	 */
	public String getMetadataByKey(String key) {
		if (null != key) {
			if (null != attributes)
				return attributes.get(key);
			else
				return null;
		} else
			throw new NullPointerException("Argument 'key' must not be null");
	}

	/**
	 * Returns true if this resource contains a mapping for the specified metadata key.
	 * 
	 * @param key
	 *        the key whose presence is to be tested
	 * @return true if this map contains a mapping for the specified key
	 */
	public boolean containsMetadataKey(String key) {
		if (null != attributes)
			return attributes.containsKey(key);
		else
			return false;
	}

	/**
	 * Return the {@link ResourceLocator} representation of this Resource.
	 * 
	 * @return a ResourceLocator object representing this Resource, which is URLEncoded
	 */
	private ResourceLocator toRL() {
		return url;
	}

	/**
	 * Return an unmodifiable Map of the metadata of the resource
	 * 
	 * @return a map of the resource metadata
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public Map<String, String> getMetadata() throws ResourceException {
		return Collections.unmodifiableMap(attributes);
	}

	/**
	 * Returns the pathname of this Resource as a pathname string, e.g. "/path/to/my/resource.txt"
	 * 
	 * @return the pathname
	 */
	public String getPath() {
		// TODO unclear whether it should be decoded first?
		return url.getPath();
	}

	/**
	 * Returns the resource location of the child resource, or <code>null</code> if no child
	 * resource can be found.
	 * 
	 * @param name
	 * @return
	 * @throws IllegalStateException
	 *         when this resource is not a directory
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract ResourceLocator getChild(String name) throws ResourceException;

	/**
	 * Returns a {@code Resource} representing the child resource, or <code>null</code> if no child
	 * resource can be found.
	 * 
	 * @param name
	 * @return
	 * @throws IllegalStateException
	 *         when this resource is not a directory
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract Resource getChildResource(String name) throws ResourceException;

	/**
	 * Returns the resource location of this abstract resource parent, or <code>null</code> if this
	 * pathname does not name a parent resource.
	 * 
	 * @return the resource location of the parent resource named by this abstract pathname, or
	 *         <code>null</code> if this pathname does not name a parent
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract ResourceLocator getParent() throws ResourceException;

	/**
	 * Returns a {@code Resource} representing the resource parent, or <code>null</code> if this
	 * pathname does not name a parent resource.
	 * 
	 * @return the resource of the parent resource named by this abstract pathname, or
	 *         <code>null</code> if this pathname does not name a parent
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract Resource getParentResource() throws ResourceException;

	/**
	 * Returns the time that the resource denoted by this abstract pathname was last modified.
	 * 
	 * @return A <code>long</code> value representing the time the file was last modified, measured
	 *         in milliseconds since the epoch (00:00:00 GMT, January 1, 1970), or <code>0L</code>
	 *         if the file does not exist or if an I/O error occurs
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract long lastModified() throws ResourceException;

	/**
	 * Returns the length of the file denoted by this abstract pathname.
	 * 
	 * @return The length, in bytes, of the file resource denoted by this abstract pathname, or
	 *         <code>0L</code> if the file resource does not exist or is a directory.
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract long length() throws ResourceException;

	/**
	 * Returns an array of resources which are childs of this resource.
	 * <p>
	 * If this abstract pathname does not denote a directory, then this method returns
	 * <code>null</code>. Otherwise an array of <code>Resource</code> objects is returned, one for
	 * each file or directory in the directory.
	 * <p>
	 * There is no guarantee that the name strings in the resulting array will appear in any
	 * specific order; they are not, in particular, guaranteed to appear in alphabetical order.
	 * 
	 * @return An array of of resources which are childs of this resource. The array will be empty
	 *         if there are no childs . Returns <code>null</code> if this abstract pathname does not
	 *         denote a directory
	 * @return an array of child resources
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract Resource[] listResources() throws ResourceException;

	/**
	 * Returns the name of the file or directory denoted by this Resource. This is just the last
	 * name in the pathname's name sequence, separated by the service specific separator. In case of
	 * a directory, the trailing separator will be removed.
	 * 
	 * @return The name of the file or directory denoted by this Resource
	 */
	public abstract String getName();

	/**
	 * Returns the output stream for this resource to write the file resource resource
	 * 
	 * @return ResourceOutPutStream
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract ResourceOutputStream getOutputStream() throws ResourceException;

	/**
	 * Returns the input stream for this resource to read the file resource content
	 * 
	 * @return ResourceOutPutStream
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract ResourceInputStream getInputStream() throws ResourceException;

	/*
	 * ############################################################ 
	 * perform checks on the resource
	 * ############################################################
	 */
	/**
	 * Tests whether the application can read the resource denoted by this abstract resource
	 * location.
	 * 
	 * @return <code>true</code> if and only if the resource exists <em>and</em> can be read by the
	 *         application; <code>false</code> otherwise
	 */
	public abstract boolean canRead() throws ResourceException;

	/**
	 * Tests whether the application can modify the resource denoted by this abstract resource
	 * location.
	 * 
	 * @return <code>true</code> if and only if the resource exists <em>and</em> the application is
	 *         allowed to write to the file; <code>false</code> otherwise.
	 * @throws ResourceException
	 *         when the request can't be performed, e.g. because of missing rights
	 */
	public abstract boolean canWrite() throws ResourceException;

	/**
	 * Tests whether the file or directory denoted by this abstract resource location really exists.
	 * 
	 * @return <code>true</code> if and only if the file or directory denoted by this abstract
	 *         pathname exists; <code>false</code> otherwise
	 * @throws ResourceException
	 *         when the request can't be performed, e.g. because of missing rights
	 */
	public abstract boolean exists() throws ResourceException;

	/**
	 * Tests whether this is a directory resource.
	 * 
	 * @return <code>true</code> if and only if the resource exists <em>and</em> is a directory;
	 *         <code>false</code> otherwise
	 * @throws ResourceException
	 *         when the request can't be performed, e.g. because of missing rights
	 */
	public abstract boolean isDirectory() throws ResourceException;

	/**
	 * Tests whether the resource denoted by this abstract resource location is a normal file. A
	 * file resource is not a directory resource.
	 * 
	 * @return <code>true</code> if and only if the file resource exists <em>and</em> is not a
	 *         directory resource; <code>false</code> otherwise
	 * @throws ResourceException
	 *         when the request can't be performed, e.g. because of missing rights
	 */
	public boolean isFile() throws ResourceException {
		return !isDirectory();
	}

	/*
	 * ############################################################ 
	 * overwrite some Object methods
	 * ############################################################
	 */
	/**
	 * Compares this Resource with an other object.
	 * 
	 * @param obj
	 * @return {@code true} if obj is {@code !null} and is a Resource object which has the same hash
	 *         code
	 */
	@Override
	public boolean equals(Object obj) {
		return url.equals(obj);
	}

	/**
	 * Returns a string representation of this object, e.q.<br>
	 * {@code S3Resource@https://bucket.s3.amazon.com/file.txt}
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
		return url.hashCode();
	}
}
