/**
 *
 */
package org.ccopy.resource.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ccopy.resource.Resource;
import org.ccopy.resource.ResourceError;
import org.ccopy.resource.ResourceException;
import org.ccopy.resource.s3.S3Headers;
import org.ccopy.resource.util.MimeType;
import org.ccopy.resource.util.StringUtil;

/**
 * Implements the Asset as simple File Object.
 * 
 * @author coffeemug13
 */
public class FileResource extends Resource {
	/**
	 * The size of the byte array, which holds the chunks from the InputStream
	 */
	private static final int STREAM_BYTE_BUFFER = 100;
	/**
	 * The File object representing the asset
	 */
	protected File file;
	protected FileInputStream inStream;
	private String md5;
	private URI renameTo = null;
	private long lastModified = -1L;

	/*
	 * ############################################################ Constructors
	 * ############################################################
	 */
	/**
	 * Construct an asset base on a Uri object.
	 * 
	 * @param uri
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public FileResource(URI uri) throws URISyntaxException {
		super(uri);
		this.file = new File(this.uri);
	}

	public FileResource(Resource resource, String child) throws URISyntaxException {
		super(resource, child);
		this.file = new File(this.uri);
	}
	private FileResource(File file) throws URISyntaxException {
		super(file.toURI());
		this.file = file;
	}

	/*
	 * ############################################################ operations which alter the
	 * physical resource and persist changes
	 * ############################################################
	 */
	/**
	 * {@inheritDoc} Don't think about using the references to the filestreams inStream & outStream
	 * to automatically close them.
	 */
	public boolean delete() throws IOException {
		this.resetModifications();
		return file.delete();
	}

	@Override
	public Resource createNewFileResource() throws IOException {
		file.createNewFile();
		this.applyModifications();
		return this;
	}

	@Override
	public Resource createDirectoryResource() throws ResourceException, IOException {
		file.mkdir();
		this.applyModifications();
		return this;
	}

	@Override
	public Resource write(long length, InputStream in) throws IOException {
		FileOutputStream out = null;
		try {
			if (isDirectory())
				throw new IllegalStateException(
						"This resource is already defined as directory. Can't write content to directory");
			out = new FileOutputStream(file);
			// read the InputStream in chunks and write them to S3 OutputStream
			byte[] c = new byte[STREAM_BYTE_BUFFER]; // with increasing value speed goes up
			int read, lastRead = 0;
			int readCounter = 0; // count the Bytes which are processed
			if (length > 0) {
				// Read (and print) till end of file.
				while ((read = in.read(c)) != -1) {
					out.write(c, 0, read);
					readCounter += read;
					if (read != -1)
						lastRead = read;
				}
			}
			this.applyModifications();
			return this;
		} finally {
			if (null != in)
				in.close();
			if (null != out)
				out.close();
		}
	}
	@Override
	public Resource persistChanges() throws ResourceException, IOException {
		this.applyModifications();
		return this;
	}
	/**
	 * Apply pending modification to the resource and reset the placeholder
	 * values
	 */
	private void applyModifications() {
		if (null != this.renameTo) 
			this.renameTo(this.renameTo);
		if (this.lastModified >= 0L)
			this.setLastModificationTime(this.lastModified);
		this.resetModifications();
	}
	
	private void resetModifications() {
		this.renameTo=null;
		this.lastModified = -1L;
		
	}
	/*
	 * ############################################################ setting properties of the
	 * resource ############################################################
	 */

	@Override
	public void setMetadata(Map<String, String> map) throws ResourceException {
		// is not supported
	}

	@Override
	public Resource addMetadata(String key, String value) throws ResourceException {
		// is not supported
		return this;
	}

	@Override
	public Resource setLastModificationTime(long timestamp) {
		if (file.exists()) 
			file.setLastModified(timestamp);
		else
			this.lastModified = timestamp;
		return this;
	}

	@Override
	public Resource setContentType(MimeType mimeType) {
		// is not supported
		return this;
	}

	@Override
	public Resource renameTo(URI dest) {
		if (file.exists()) 
			file.renameTo(new File(dest));
		else
			this.renameTo = dest;
		return this;
	}

	/*
	 * ############################################################ 
	 * retrieve infos about the resource 
	 * ############################################################
	 */
	@Override
	public boolean supportsMetadata() {
		return false;
	}
	@Override
	public boolean supportsVersioning() {
		return false;
	}

	@Override
	public boolean supportsSetLastModified() {
		return true;
	}
	@Override
	public boolean canRead() {
		return file.canRead();
	}

	@Override
	public boolean canWrite() {
		return file.canWrite();
	}

	@Override
	public boolean exists() {
		return file.exists();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (inStream == null)
			inStream = new FileInputStream(file);
		return inStream;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return new FileOutputStream(file);
	}

	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}

	@Override
	public boolean isFile() {
		return file.isFile();
	}

	@Override
	public long lastModified() {
		return file.lastModified();
	}

	@Override
	public long length() {
		return file.length();
	}
	@Override
	public List<Resource> listResources() throws ResourceException, IOException{
		File[] fileList = file.listFiles();
		List<Resource> resources = new ArrayList<Resource>(fileList.length);
		for (int count = 0; count < fileList.length; count++) {
			try {
				resources.add(new FileResource(fileList[count]));
			} catch (URISyntaxException e) {
				// TODO handle error
				e.printStackTrace();
				throw new ResourceError(e);
			}
		}
		return resources;
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public MimeType getContentType() {
		return MimeType.fromFileName(this.file.getName());
	}

	@Override
	public String get(String key) {
		// is not supported
		return null;
	}

	@Override
	public boolean containsKey(String key) {
		// is not supported
		return false;
	}

	@Override
	public Map<String, String> getMetadata() throws ResourceException {
		// is not supported
		return null;
	}

	@Override
	public URI getChild(String name) throws URISyntaxException {
		return new File(this.file,name).toURI();
	}

	@Override
	public Resource getChildResource(String name) throws URISyntaxException {
		return new FileResource(this, name);
	}

	@Override
	public URI getParent() throws URISyntaxException {
		return file.getParentFile().toURI();
	}

	@Override
	public Resource getParentResource() throws URISyntaxException {
		return new FileResource(file.getParentFile().toURI());
	}

	@Override
	public String getMD5Hash() throws IOException {
		if (null != this.md5)
			return this.md5;
		else {
			FileInputStream in = null;
			try {
				in = new FileInputStream(this.file);
				MessageDigest digest = MessageDigest.getInstance("md5");
				byte[] c = new byte[STREAM_BYTE_BUFFER]; // with increasing value speed goes up
				int read, lastRead = 0;
				// Read (and print) till end of file.
				while ((read = in.read(c)) != -1) {
					digest.update(c, 0, read);
				}
				return StringUtil.bytToHexString(digest.digest());
			} catch (FileNotFoundException e) {
				return null;
			} catch (NoSuchAlgorithmException e) {
				throw new Error(e);
			} finally {
				if (null != in)
					in.close();
			}
		}
	}

	@Override
	public boolean isRoot() throws IOException {
		for (File root : File.listRoots()) {
			if (root.equals(this.file))
				return true;
		}
		return false;
	}
}
