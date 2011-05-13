/**
 *
 */
package org.ccopy.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

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
	public boolean isDirectory();
	public boolean isFile();
	public long lastModified();
	public long length();
	public String[] list();
	public Resource[] listResources();
	public boolean renameTo(URI dest);
	public URI toURI();
}
