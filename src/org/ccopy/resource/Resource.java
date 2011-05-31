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
 * @author mholakovsky
 *
 */
public interface Resource {
	public boolean canRead();
	public boolean canWrite();
	public boolean delete() throws SecurityException, IOException;
	public boolean exists();
	public InputStream getInputStream() throws IOException;
	public OutputStream getOutputStream() throws IOException;
	/**
	 * Returns true only if the resource is a directory and exists
	 * @return
	 */
	public boolean isDirectory();
	public boolean isFile();
	public long lastModified();
	public long length();
	public String[] list();
	public Resource[] listResources();
	public boolean renameTo(URI dest);
	public URL toURL();
	public void addMetadata(String key, String val);
	public void mkdir();
	public String getName();
}
