package org.ccopy.resource;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ccopy.resource.s3.S3Resource;



/**
 * This class provides a skeletal implementation of the Asset interface, to
 * minimize the effort required to implement this interface.
 * 
 * @author mholakovsky
 * 
 */
public abstract class AbstractResource implements Resource {
	protected URL url;
	protected HashMap<String, String> attributes = null;

	/**
	 * Constructor of AbstractResource with URL object
	 * 
	 * @param url
	 */
	protected AbstractResource(URL url) {
		this.url = url;
	}
	/**
	 * Constructor of AbstractResource as child of another resource
	 * TODO parent MUST be a directory (=trailing "/") otherwise Exception
	 * @param res
	 * @param child
	 * @throws MalformedURLException 
	 */
	protected AbstractResource(Resource parent, String child) throws MalformedURLException {
		if (child == null) throw new NullPointerException("name of child must not be null");
		if (parent == null) url = new URL(child);
		if (((AbstractResource)parent).url.getPath().endsWith("/")) {
			url = new URL(parent.toURL(),child);
		} else throw new MalformedURLException("parent resource must be an directory");
	}
	public URL toURL() {
		return url;
	}
	public void addMetadata(String key, String val) {
		if (key != null)
			attributes.put(key, val);
	}

	/**
	 * Return an unmodifiable Map of the metadata of the resource
	 * 
	 * @return
	 */
	public Map<String, String> getMetadata() {
		return Collections.unmodifiableMap(attributes);
	}
}
