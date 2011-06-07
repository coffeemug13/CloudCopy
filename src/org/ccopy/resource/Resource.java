/**
 *
 */
package org.ccopy.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

/**
 * The root interface in the <i>Asset hierarchy</i>. A asset represents the
 * digital form of textual or media content.
 * 
 * @author mholakovsky
 * 
 */
public interface Resource {
	public boolean canRead() throws SecurityException, IOException, ResourceException;

	public boolean canWrite() throws SecurityException, IOException, ResourceException;

	public boolean delete() throws SecurityException, IOException;

	public boolean exists() throws SecurityException, IOException, ResourceException;

	public InputStream getInputStream() throws SecurityException, IOException,
			ResourceNotFoundException;

	public OutputStream getOutputStream() throws SecurityException,
			IOException, ResourceNotFoundException;

	/**
	 * Returns true only if the resource is a directory and exists
	 * 
	 * @return
	 * @throws ResourceException 
	 */
	public boolean isDirectory() throws SecurityException, IOException,
			ResourceNotFoundException, ResourceException;

	public boolean isFile() throws SecurityException, IOException,
			ResourceNotFoundException, ResourceException;

	public long lastModified() throws SecurityException, IOException,
			ResourceNotFoundException, ResourceException;

	public long length() throws SecurityException, IOException,
			ResourceNotFoundException, ResourceException;

	public String[] list() throws SecurityException, IOException,
			ResourceNotFoundException;

	public Resource[] listResources() throws SecurityException, IOException,
			ResourceNotFoundException, ResourceException;

	public boolean renameTo(URI dest) throws SecurityException, IOException,
			ResourceNotFoundException, NullPointerException;

	public URL toURL();

	public void addMetadata(String key, String val) throws NullPointerException;

	public void mkdir() throws SecurityException, IOException,
			ResourceNotFoundException;

	public String getName();
}
