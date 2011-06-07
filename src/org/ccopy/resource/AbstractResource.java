package org.ccopy.resource;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.ccopy.resource.s3.S3Resource;



/**
 * This class provides a skeletal implementation of the Asset interface, to
 * minimize the effort required to implement this interface.
 * 
 * @author mholakovsky
 * 
 */
public abstract class AbstractResource implements Resource {
	/**
	 * The resource locator
	 */
	protected URL url;
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
	 */
	protected AbstractResource(URL url) throws NullPointerException {
		this.url = url;
	}
	/**
	 * Constructor of AbstractResource as child of another resource
	 * TODO parent MUST be a directory (=trailing "/") otherwise Exception
	 * @param res
	 * @param child
	 * @throws MalformedURLException 
	 */
	protected AbstractResource(Resource parent, String child) throws MalformedURLException, NullPointerException {
		if (child == null) throw new NullPointerException("name of child must not be null");
		if (parent == null) url = new URL(child);
		if (((AbstractResource)parent).url.getPath().endsWith("/")) {
			url = new URL(parent.toURL(),child);
		} else throw new MalformedURLException("parent resource must be an directory");
	}
	/**
	 * Get the URL of the resource
	 */
	public URL toURL() {
		return url;
	}
	/**
	 * Add metadata to the resource
	 */
	public void addMetadata(String key, String val) throws NullPointerException{
		if (key != null)
			attributes.put(key, val);
		else throw new NullPointerException("Key MUST NOT be null");
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
}
