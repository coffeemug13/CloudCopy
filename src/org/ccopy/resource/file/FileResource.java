/**
 *
 */
package org.ccopy.resource.file;

import java.io.File; 
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Assume;

/**
 * Implements the Asset as simple File Object.
 *
 * @author coffeemug13
 *
 */
public class FileResource extends AbstractResource implements Resource{
	/**
	 * The File object representing the asset
	 */
	protected File file;
	protected FileInputStream inStream;
	protected FileOutputStream outStream;
//	/**
//	 * Construct an asset base on a File object.
//	 * @param URI
//	 * @throws IOException
//	 */
//	protected FileResource(File file) {
//		super(file.toURI());
//		this.file = file;
//	}
	/**
	 * Construct an asset base on a Uri object.
	 * @param uri
	 * @throws IOException
	 */
	protected FileResource(URI url) {
		//TODO check whether it's a FileResource
		super(url);
		file = new File(url);
	}
	public FileResource(Resource targetDirectory, String name) throws URISyntaxException {
		//TODO check whether it's a FileResource
		super(new URI (targetDirectory.getName()));
		file = new File (new File(targetDirectory.getName()),name);
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.astor.asset.Asset#canRead()
	 */
	public boolean canRead() {
		return file.canRead();
	}
	public boolean canWrite() {
		return file.canWrite();
	}
	/**
	 * {@inheritDoc}
	 *
	 * Don't think about using the references to the filestreams inStream & outStream to
	 * automatically close them.
	 */
	public boolean delete() throws IOException {
//		if (inStream!=null) {
			inStream.close();
//			if (outStream != null) outStream.close();
//			System.out.println("\n# WARNING: automatic closing all open streams for this file");
//		}
		return file.delete();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FileResource))
			return false;
		FileResource other = (FileResource) obj;
		File otherFile = other.toFile();
		if (!file.equals(otherFile))
			return false;
		return true;
	}

	public boolean exists() {
		return file.exists();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.astor.asset.Asset#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		if (inStream == null) inStream = new FileInputStream(file);
		return inStream;
	}

	public OutputStream getOutputStream() throws IOException {
		if (inStream == null) outStream = new FileOutputStream(file);
		return outStream;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return file.hashCode();
	}

	public boolean isDirectory() {
		return file.isDirectory();
	}

	public boolean isFile() {
		return file.isFile();
	}

	public long lastModified() {
		return file.lastModified();
	}

	public long length() {
		return file.length();
	}

	public String[] list() {
		return file.list();
	}

	public Resource[] listResources() {
		File[] fileList = file.listFiles();
		Resource[] assetList = new Resource[fileList.length];
		for(int count=0;count<fileList.length;count++) {
			assetList[count]= new FileResource(fileList[count]);
		}
		return assetList;
	}

	public boolean renameTo(URI dest) {
		return file.renameTo(new File(dest));
	}
	public File toFile(){
		return file;
	}
	public URI toURI() {
		return file.toURI();
	}
	@Override
	public void mkdir() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}
