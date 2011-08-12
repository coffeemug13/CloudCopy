package org.ccopy.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccopy.resource.util.MimeType;

/**
 * This class represents a generic Resource. The design of this class is derived from the
 * {@link File} class but differs in the way write operations are done.
 * <p>
 * Resources are hierarchically organized, like a file system. Therefore a resource can be of type
 * 'file' or 'directory'. A file resource can have a content and content type but no child
 * resources. In contrast a directory resource can't have content or content type but may have child
 * resources, either file or directory resources. But both types may e.g. contain metadata.
 * <p>
 * Instantiating a resource by using the constructors of this class <em>DOES NOT</em> define the
 * type of this resource. Read more at {@link #isDefined}. You have to call the methods
 * {@link #exists()} or {@link #getInputStream()} to connect the resource with the background
 * service, i.e. to check whether the resource exists and to populate the resource with metadata
 * from the background service.
 * <p>
 * Only the following methods of this class guarantee, that modification to properties are finally
 * persisted:
 * <ul>
 * <li>{@link Resource#createNewFileResource()}
 * <li>{@link Resource#createDirResource()}
 * <li>{@link Resource#persistChanges()}
 * <li>{@link Resource#delete()}
 * <li>writing to {@link #getOutputStream()}
 * </ul>
 * It can't be guaranteed that this methods are atomic operations because they depend on the API of
 * the background service, where this resource is located. So it could yield to several physical
 * operations depending on the resource implementation.
 * <p>
 * When a resource content get's written with a {@link ResourceOutputStream} the properties of the
 * resource are also persisted, so you don't need to call the above methods afterwards. In case the
 * resource doesn't exist it will be created implicitly. Nevertheless it is possible, that an
 * implementation of {@code Resource} like {@link FileResource} decides to perform changes to
 * properties immediately.
 * 
 * @author coffeemug13
 */
public abstract class Resource {
	/**
	 * The service-dependent default name-separator character. This field is initialized to the
	 * typical value
	 */
	public static String SEPERATOR;
	/**
	 * Tests whether this Resource supports reading and writing of custom metadata.
	 */
	protected static boolean SUPPORTS_METADATA;
	/**
	 * Tests whether this Resource supports versioning
	 */
	// TODO not yet implemented
	protected static boolean SUPPORTS_VERSIONING;
	/**
	 * Tests whether this Resource supports to set the lastModified timestamp manually.
	 */
	protected static boolean SUPPORTS_SET_LASTMODIFIED;
	/**
	 * The metadata of this resource
	 */
//	protected HashMap<String, String> attributes = null;
	/**
	 * Identifies this resource.
	 */
	protected URI uri;
	/**
	 * indicates whether the resource exists (<code>true</code>) oder not (<code>false</code>).
	 * <code>null</code> means it is unknown.
	 */
//	protected Boolean exists = null;
	/**
	 * indicates that the resource has been altered after constructions
	 */
//	protected boolean isModified;
	/**
	 * indicates whether the resource is of type 'file'. This flag is only meaningful in combination
	 * of {@link #exists}.
	 * <p>
	 * The type of a resource is defined when:
	 * <ul>
	 * <li>you created this resource by {@link #listResources()}
	 * <li>by calling {@link #createNewFileResource()} or {@link #createDirectoryResource()}
	 * <li>reading bytes from {@link #getInputStream()}
	 * <li>or successfully written and closed the {@link #getOutputStream()}
	 * <li>set the {@link #setContentType(MimeType)}
	 * </ul>
	 * Calling the constructor of this class does not define the type of the resource!
	 * <p>
	 * There is a subtle difference between {@link #exists} and {@code #isFile}: if a resource
	 * exists it is also defined but if a resource is defined it doesn't need to exist yet.
	 */
//	protected Boolean isFile;
//	protected boolean isRoot;
//	protected boolean canRead;
//	protected boolean canWrite;
	/**
	 * Date and time the object was last modified or "0L" when undefined
	 * 
	 * @see System#currentTimeMillis()
	 */
//	protected long lastModified;
	/**
	 * The content type of this resource
	 */
//	protected MimeType contentType;
	/**
	 * The length of this resource
	 */
//	protected long length;
	/**
	 * The MD5 hash of this resource. Only a file resource has a md5 Hash.
	 */
//	protected String md5Hash;
	/**
	 * The logger for the class
	 */
	protected static Logger logger = Logger.getLogger("org.ccopy");

	/*
	 * ############################################################ 
	 * Constructors
	 * ############################################################
	 */
//	/**
//	 * Default Constructor of {@code Resource}. The implementing class must take care, that the url
//	 * get's defined
//	 */
//	protected Resource() {
//		logger.fine(null);
//	}

	/**
	 * Constructor of {@code Resource} based on a {@link URI}. It will 
	 * 
	 * @param url
	 *        - the locator for this resource
	 * @throws NullPointerException
	 *         when argument is {@code null}
	 * @throws URISyntaxException
	 *         when the URI is not absolute
	 */
	protected Resource(URI uri) throws URISyntaxException {
		if (null == uri)
			throw new NullPointerException("Argument must not be null");
		if (logger.isLoggable(Level.FINE))
			logger.fine("Resource creating for '" + uri.toString() + "'");
		// ensure the URI is absolute
		if ((null == uri.getPath()) || uri.getPath().isEmpty())
			uri = new URI(uri.getScheme(),uri.getHost(),SEPERATOR,null);
		// set URI and reset the properties to their inital value 
		this.uri = uri;
		reset();
	}
	
	/**
	 * Constructor of {@code Resource} as child of another resource.
	 * 
	 * @param parent
	 *        a {@code Resource} with "{@code Resource#isDirectory()==true}"
	 * @param child
	 *        the filename of the child as string
	 * @throws NullPointerException
	 *         when arguments are {@code null}
	 * @throws IllegalArgumentException
	 *         when the parent resource is not a directory resource
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	protected Resource(Resource parent, String child) throws URISyntaxException {
		if ((child == null) || (parent == null))
			throw new NullPointerException("both arguments must not be null");
		if (isFile())
			throw new IllegalArgumentException("parent resource must be an directory");
		if (logger.isLoggable(Level.FINE))
			logger.fine("Resource creating for '" + parent.toString() + "' + '" + child + "'");
		// set URI and reset the properties to their inital value
		this.uri = parent.uri.resolve(child);
		reset();
	}

	/**
	 * Reset the properties of the resource to their initial values
	 */
	protected void reset() {
		logger.fine("reseting the resource parameters");
//		this.exists = null; // null means is undefined, false means it doesn't exist
//		this.isFile = null;
//		this.isModified = false;
//		this.canRead = false;
//		this.canWrite = false;
//		this.lastModified = 0L;
//		this.contentType = null;
//		this.length = 0L;
//		this.md5Hash = null;
	}

	/*
	 * ############################################################ operations which alter the
	 * physical resource and persist changes
	 * ############################################################
	 */
	/**
	 * Creates a new, empty file resource according to the specification of the service
	 * implementation. This mean typically, that the path of the resource doesn't end with a "/"
	 * <p>
	 * This is a guaranteed persistent operation. All prior modifications of this resource
	 * properties are also persisted.
	 * 
	 * @throws ResourceException
	 *             when the service behind the resource can't process the request, e.g. bad request
	 * @throws IOException
	 *             in case of general I/O problems
	 */
	public abstract Resource createNewFileResource() throws ResourceException, IOException;

	/**
	 * Creates a directory resource according to the specification of the service implementation.
	 * This mean typically, that the path of the resource must end with a "/"
	 * <p>
	 * This is a guaranteed persistent operation. All prior modifications of this resource
	 * properties are also persisted.
	 * 
	 * @throws ResourceException
	 *             when the service behind the resource can't process the request, e.g. bad request
	 * @throws IllegalArgumentException
	 *             when you try to create a root directory
	 * @throws IOException
	 *             in case of general I/O problems
	 */
	public abstract Resource createDirectoryResource() throws ResourceException, IOException;

	/**
	 * Writes an InputStream to a file resource. In case the resource doesn't exist yet it will be
	 * created on the fly. The {@link #uri} of this resource could be changed to comply to the
	 * service specification, e.g. no trailing "/". 
	 * <p>
	 * This is a guaranteed persistent operation. All prior modifications of this resource
	 * properties are also persisted.
	 * 
	 * @param length
	 *            of the InputStream
	 * @param in
	 *            the InputStream to write to the file resource
	 * @return the updated resource
	 * @throws IOException
	 *             in case of general I/O problems
	 * @throws IllegalStateException
	 *             when the resource is already defined as directory resource
	 */
	public abstract Resource write(long length, InputStream in) throws IOException;

	/**
	 * All modifications to the resource properties are persisted. This is a guaranteed persistent
	 * operation.
	 * 
	 * @throws ResourceException
	 *             when the service behind the resource can't process the request, e.g. bad request
	 * @throws IOException
	 *             in case of general I/O problems
	 * @throws IllegalStateException
	 *             when the type of the resource is not defined; see also {@link #isDefined}
	 */
	public abstract Resource persistChanges() throws ResourceException, IOException;

	/**
	 * Deletes the Resource denoted by this abstract pathname. If this pathname denotes a directory
	 * (trailing slash) then it must be empty in order to be deleted.
	 * <p>
	 * This is a guaranteed persistent operation. All prior modifications of this resource
	 * properties are discarded!
	 * 
	 * @return <code>true</code> if and only if the file or directory is successfully deleted;
	 *         <code>false</code> otherwise
	 * @throws ResourceException
	 *             when the service behind the resource can't process the request, e.g. bad request
	 * @throws IOException
	 *             in case of general I/O problems
	 */
	public abstract boolean delete() throws ResourceException, IOException;

	/*
	 * ############################################################ 
	 * setting properties of the resource 
	 * ############################################################
	 */
	/**
	 * Set the metadata to this Resource. This method replaces the existing metadata of this
	 * resource. In case this Resource doesn't {@link #supportsMetadata()} this method call is
	 * ignored. Therefore check {@code #supportsMetadata()} if you care about the metadata!
	 * <p>
	 * This method is not guaranteed to be persistent. You <em>MUST</em> call one of the persistent
	 * methods or write with an {@code ResourceOutputStream} to the resource to ensure that this
	 * change get's persisted.
	 * 
	 * @param map
	 *            the List of metadata to replace the existing; <code>null</code> purges all
	 *            metadata
	 * @throws ResourceException
	 *             when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract void setMetadata(Map<String, String> map) throws ResourceException; // {
//		this.isModified = true;
//		if (map != null)
//			attributes = (HashMap<String, String>) map;
//		else
//			attributes = null;
//	}

	/**
	 * Add metadata to this Resource. In case this Resource doesn't {@link #supportsMetadata()} this
	 * method call is ignored. Therefore check {@code #supportsMetadata()} if you care about the
	 * metadata!
	 * <p>
	 * This method is not guaranteed to be persistent. You <em>MUST</em> call one of the persistent
	 * methods or write with an {@code ResourceOutputStream} to the resource to ensure that this
	 * change get's persisted.
	 * 
	 * @param key
	 *            the key of the metadata
	 * @param val
	 *            the values of the metadata
	 * @throws NullPointerException
	 *             when key is {@code null}
	 * @throws ResourceException
	 *             when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract Resource addMetadata(String key, String value) throws ResourceException; // {
//		if (key != null) {
//			this.isModified = true;
//			if (attributes == null)
//				attributes = new HashMap<String, String>();
//			attributes.put(key, value);
//			return this;
//		} else
//			throw new NullPointerException("Key MUST NOT be null");
//	}

	/**
	 * Rename the resource. Differently to the {@code File#renameTo(File)} this resource points
	 * after this method call to to renamed resource!
	 * <p>
	 * This method is not guaranteed to be persistent. You <em>MUST</em> call one of the persistent
	 * methods or write with an {@code ResourceOutputStream} to the resource to ensure that this
	 * change get's persisted.
	 * 
	 * @param dest
	 *        - the new locator of the resource.
	 * @return {@code Resource} if the new URL is valid; {@code null} otherwise
	 * @throws NullPointerException
	 *         when argument is <code>null</code>
	 * @throws IllegalStateException
	 *         when changing the type of the resource (file or directory) after it has been created
	 */
	public abstract Resource renameTo(URI dest) throws ResourceException;

	/**
	 * Sets the last-modified time of the resource. In case this Resource doesn't
	 * {@link #supportsVersioning()} this method call is ignored. Therefore check
	 * {@code #supportsVersioning()} if you care about the timestamp!
	 * <p>
	 * This method is not guaranteed to be persistent. You <em>MUST</em> call one of the persistent
	 * methods or write with an {@code ResourceOutputStream} to the resource to ensure that this
	 * change get's persisted.
	 * 
	 * @see #SUPPORTS_SET_LASTMODIFIED
	 * @see System#currentTimeMillis()
	 * @param timestamp
	 *            in milliseconds or "0L" when you want to unset
	 * @return this resource with new "last modified" timestamp if the resource supports this
	 * @throws IllegalArgumentException
	 *             when argument {@code < 0L}
	 */
	public abstract Resource setLastModificationTime(long timestamp); //{
//		if (!SUPPORTS_SET_LASTMODIFIED)
//			throw new IllegalStateException(
//					"setting lastModified is not supported for this resource");
//		if (timestamp < 0L)
//			throw new IllegalArgumentException("argument must be greater or equal then -1");
//		this.isModified = true;
//		this.lastModified = timestamp;
//		return this;
//
//	}

	/**
	 * Set the content type for this resource. In case this resource is already of type file or undefined, the
	 * type of the resource will be set to {@link #isFile(true)}.
	 * In case this Resource doesn't {@link #supportsMetadata()} this method call is
	 * ignored. Therefore check {@code #supportsMetadata()} if you care about the metadata!
	 * 
	 * @param mimeType
	 * @return
	 * @throw IllegalStateException when this is a directory resource
	 */
	public abstract Resource setContentType(MimeType mimeType); // {
//		this.isModified = true;
//		if ((null != this.isFile) && this.isFile.equals(Boolean.FALSE)) {
//			throw new IllegalStateException("Cant't set content type for directory resource");
//		} else {
//			this.isFile = Boolean.TRUE;
//		}
//		this.contentType = mimeType;
//		return this;
//	}

	/*
	 * ############################################################ 
	 * retrieve infos about the resource
	 * ############################################################
	 */
	/**
	 * Returns the content type of the resource.
	 * 
	 * @return the content type if known otherwise <code>null</code>
	 */
	public abstract MimeType getContentType(); // {
//		return this.contentType;
//	}

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
	public abstract String get(String key); // {
//		if (null != key) {
//			if (null != attributes)
//				return attributes.get(key);
//			else
//				return null;
//		} else
//			throw new NullPointerException("Argument 'key' must not be null");
//	}

	/**
	 * Returns true if this resource contains a mapping for the specified metadata key.
	 * 
	 * @param key
	 *        the key whose presence is to be tested
	 * @return true if this map contains a mapping for the specified key
	 */
	public abstract boolean containsKey(String key); // {
//		if (null != attributes)
//			return attributes.containsKey(key);
//		else
//			return false;
//	}

	/**
	 * Return the {@link URL} representing this Resource.
	 * 
	 * @return a URL object representing this Resource, which is URLEncoded
	 */
	public URI toURI(){
		return this.uri;
	}

	/**
	 * Return an unmodifiable Map of the metadata of the resource
	 * 
	 * @return a map of the resource metadata or <code>null</code> if not defined
	 * @throws ResourceException
	 *             when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract Map<String, String> getMetadata() throws ResourceException; // {
//		if (null != attributes)
//			return Collections.unmodifiableMap(attributes);
//		else
//			return null;
//	}

	/**
	 * Returns the pathname of this Resource as a pathname string, e.g. "/path/to/my/resource.txt". 
	 * This is a shortcut to {@code resource.toURI().toPath()} 
	 * 
	 * @return the pathname
	 */
	public String getPath() {
		return this.uri.getPath();
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
	public abstract URI getChild(String name) throws URISyntaxException;

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
	public abstract Resource getChildResource(String name) throws URISyntaxException;

	/**
	 * Returns the resource location of this abstract resource parent, or <code>null</code> if this
	 * pathname does not name a parent resource.
	 * 
	 * @return the resource location of the parent resource named by this abstract pathname, or
	 *         <code>null</code> if this pathname does not name a parent
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 * @throws URISyntaxException 
	 */
	public abstract URI getParent() throws URISyntaxException;

	/**
	 * Returns a {@code Resource} representing the resource parent, or <code>null</code> if this
	 * pathname does not name a parent resource.
	 * 
	 * @return the resource of the parent resource named by this abstract pathname, or
	 *         <code>null</code> if this pathname does not name a parent
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 */
	public abstract Resource getParentResource() throws URISyntaxException;

	/**
	 * Returns the time that the resource denoted by this abstract pathname was last modified.
	 * 
	 * @return A <code>long</code> value representing the time the file was last modified, measured
	 *         in milliseconds since the epoch (00:00:00 GMT, January 1, 1970), or <code>0L</code>
	 *         if the file does not exist or if an I/O error occurs
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 * @see System#currentTimeMillis()
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
	 * Returns the MD5 hash of the file if known
	 * 
	 * @return
	 */
	public abstract String getMD5Hash();

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
	 * @throws IOException 
	 */
	public abstract List<Resource> listResources() throws ResourceException, IOException;

	/**
	 * Returns the name of the file or directory denoted by this Resource. This is just the last
	 * name in the pathname's name sequence, separated by the service specific separator. In case of
	 * a directory, the trailing separator will be removed.
	 * 
	 * @return The name of the file or directory denoted by this Resource
	 */
	public abstract String getName();

	/**
	 * Returns the output stream for this resource to write the file resource resource. This method
	 * defines the type of the resource to be a file if not yet set.
	 * 
	 * @return ResourceOutPutStream
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 * @throws IOException
	 * @throws IllegalStateException
	 *         when you try to get the OutputStream from a directory
	 */
	public abstract OutputStream getOutputStream() throws ResourceException, IOException;

	/**
	 * Returns the input stream for this resource to read the file resource content. This method
	 * defines the type of the resource to be a file if not yet set.
	 * 
	 * @return ResourceOutPutStream
	 * @throws ResourceException
	 *         when the service behind the resource can't process the request, e.g. bad request
	 * @throws IOException
	 */
	public abstract InputStream getInputStream() throws ResourceException, IOException;

	/*
	 * ############################################################ 
	 * perform checks on the resource
	 * ############################################################
	 */
	/**
	 * Tests whether this Resource supports versioning
	 * @return
	 */
	public boolean supportsVersioning() {
		return SUPPORTS_VERSIONING;
	}
	/**
	 * Tests whether this Resource supports reading and writing of custom metadata.
	 * @return
	 */
	public boolean supportsMetadata() {
		return SUPPORTS_METADATA;
	}
	/**
	 * Tests whether this Resource supports to set the lastModified timestamp manually.
	 * @return
	 */
	public boolean supportsSetLastModified() {
		return SUPPORTS_SET_LASTMODIFIED;
	}
	/**
	 * Tests whether the application can read the resource denoted by this abstract resource
	 * location.
	 * 
	 * @return <code>true</code> if and only if the resource exists <em>and</em> can be read by the
	 *         application; <code>false</code> otherwise
	 */
	public abstract boolean canRead();

	/**
	 * Tests whether the application can modify the resource denoted by this abstract resource
	 * location.
	 * 
	 * @return <code>true</code> if and only if the resource exists <em>and</em> the application is
	 *         allowed to write to the file; <code>false</code> otherwise.
	 */
	public abstract boolean canWrite();

	/**
	 * Tests whether the file or directory denoted by this abstract resource location really exists.
	 * This method will trigger requests to the background service if the existence of the resource
	 * is not yet defined.
	 * 
	 * @return <code>true</code> if and only if the file or directory denoted by this abstract
	 *         pathname exists; <code>false</code> otherwise
	 * @throws ResourceException
	 *             when the request can't be performed, e.g. because of missing rights
	 * @throws IOException
	 * @throws SecurityException
	 */
	public abstract boolean exists() throws IOException;

	/**
	 * Tests whether this is a directory resource.
	 * 
	 * @return <code>true</code> if the resource {@link #exists()} <em>and</em> is a directory
	 *         resource; <code>false</code> otherwise
	 */
	public abstract boolean isDirectory(); // {
//		// isDirectory is the negation of isFile
//		return (null != this.isFile)? (!this.isFile) : false;
//	}

	/**
	 * Tests whether the file denoted by this abstract pathname is a normal file. A file is normal
	 * if it is not a directory and, in addition, satisfies other system-dependent criteria. Any
	 * resource created with {@link #createNewFileResource()} is guaranteed a normal file.
	 * 
	 * @return <code>true</code> if the resource {@link #exists()} <em>and</em> is a file resource;
	 *         <code>false</code> otherwise
	 */
	public abstract boolean isFile(); // {
//		return (null != this.isFile)? this.isFile : false;
//	}

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
		return this.uri.equals(obj);
	}

	/**
	 * Returns a string representation of this object, e.q.<br>
	 * {@code S3Resource@https://bucket.s3.amazon.com/file.txt}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getName() + "@" + this.uri.toString();
	}

	/**
	 * Computes a hash code for this Resource based on the URL
	 * 
	 * @return the unique hash code
	 */
	@Override
	public int hashCode() {
		return this.uri.hashCode();
	}

	public abstract boolean isRoot() throws IOException;

}
