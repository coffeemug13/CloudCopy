/**
 *
 */
package org.ccopy.resource.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.ccopy.resource.AbstractResource;
import org.ccopy.resource.Resource;
import org.ccopy.resource.ResourceException;
import org.ccopy.util.HttpMethod;



/**
 * Implements the Asset as simple File Object.
 * 
 *
 * @author mholakovsky
 *
 */
public class S3Resource extends AbstractResource implements Resource{
	/**
	 * The File object representing the asset
	 */
	protected S3 req = null;
	protected InputStream inStream;
	protected OutputStream outStream;
	/**
	 * The region of the S3 bucket for this resource
	 */
	protected String bucketRegion = null;
	/**
	 * The name of the bucket
	 */
	protected String bucketName = null;
	/**
	 * indicates that the status of the S3 resource is known
	 */
	protected boolean gotStatus = false;
	protected boolean isDirectory;
	protected Boolean exists = null;
	protected boolean canRead;
	protected boolean canWrite;
	protected int length;
	private static Logger logger = Logger.getLogger("org.ccopy");


	/**
	 * Construct an asset base on a URL object, e.g. 
	 * <code>http://bucketname.amazon.s3.com/path/to/file</code>
	 * 
	 * @param uri
	 * @throws MalformedURLException 
	 * @throws IOException
	 */
	public S3Resource(URL url) throws ResourceException{
		super(url);
		if (!validateUrl(url)) throw new ResourceException("Url doesn't locate a S3 resource");
	}
	/**
	 * Creates a new <code>S3Resource</code> instance from a parent abstract
     * pathname and a child pathname string.
     *
     * <p> If <code>parent</code> is <code>null</code> then the new
     * <code>File</code> instance is created as if by invoking the
     * single-argument <code>File</code> constructor on the given
     * <code>child</code> pathname string.
     *
     * <p> Otherwise the <code>parent</code> abstract pathname is taken to
     * denote a directory, and the <code>child</code> pathname string is taken
     * to denote either a directory or a file.  If the <code>child</code>
     * pathname string is absolute then it is converted into a relative
     * pathname in a system-dependent way.  If <code>parent</code> is the empty
     * abstract pathname then the new <code>File</code> instance is created by
     * converting <code>child</code> into an abstract pathname and resolving
     * the result against a system-dependent default directory.  Otherwise each
     * pathname string is converted into an abstract pathname and the child
     * abstract pathname is resolved against the parent.
	 * @param parent
	 * @param child
	 * @throws MalformedURLException 
	 * @throws ResourceException
	 */
	public S3Resource (S3Resource parent, String child) throws MalformedURLException {
		super(parent,child);
	}
	/**
	 * Validate the URL to be a S3 URL
	 * @param url
	 * @return
	 */
	private boolean validateUrl(URL url) {
		//TODO check the Url to locate a S3 request, e.q. check hostname and you have a
		//     bucket as subdomain
		return true;
	}
	/**
	 * Get the status of the S3 resource (exists, isDirectory, isReadable, etc.
	 *    
   HTTP/1.1 200 OK 
   x-amz-request-id: BB37634861C5CC22 
   ETag: "c278f05f3f5aeba3dc83fad531c11957" 
   Date: Tue, 31 May 2011 16:50:33 GMT 
   Content-Length: 49 
   x-amz-id-2: bUiJA/EzHpU4L5pWUfnEyKnxlbJpw58pyx7Z7JyYUj/twkBVJ8MNgIWKuNczCUv+ 
   Last-Modified: Tue, 17 May 2011 10:29:47 GMT 
   x-amz-meta-s3fox-modifiedtime: 1305628171726 
   x-amz-meta-s3fox-filesize: 49 
   Accept-Ranges: bytes 
   Content-Type: text/plain 
   Server: AmazonS3 
	 */
	protected void getStatus(){
		String log = "";
		// TODO Make a head request for the object
		S3Request req = new S3Request(url);
		req.setHttpMethod(HttpMethod.HEAD);
		HttpURLConnection con = null;
		try {
			con = req.getConnection();
	//		// now fetch the info from the resource
			Map<String, List<String>> map = con.getHeaderFields();
			if (con.getResponseCode()==con.HTTP_OK) {
				/**
				 * Set the quick attributes
				 */
				exists = true;
				isDirectory = url.getPath().toString().endsWith("/");
				for (Entry<String, List<String>> entry : map.entrySet()) {
					Iterator<String> val = entry.getValue().iterator();
					while (val.hasNext()) {
						switch (EnumS3Headers.fromString(entry.getKey())) {
						case CONTENT_LENGTH:
							length = Integer.parseInt(val.next());
							logger.fine("------Length of file: " + length);
							break;
						case UNKNOWN:
						default:
							logger.fine(entry.getKey() + ": " + val.next());
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (con!=null) con.disconnect();
		}
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.astor.asset.Asset#canRead()
	 */
	public boolean canRead() {
		return true;
	}
	public boolean canWrite() {
		if (exists == null) getStatus();
		return canWrite;
	}
	/**
	 * Delete the S3 Resource
	 */
	public boolean delete() throws IOException {
    	//TODO implement delete in S3 the file
		return true;
	}
	
	public boolean exists() {
		if (exists == null) getStatus();
		return exists;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.astor.asset.Asset#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
//		if (inStream == null) inStream = new FileInputStream(file);
		return null;
	}

	public OutputStream getOutputStream() throws IOException {
//		if (inStream == null) outStream = new FileOutputStream(file);
		return null;
	}

	/**
	 * Return true only if the object key ends with a "/" and exists.
	 */
	public boolean isDirectory() {
		if (exists == null) getStatus();
		return isDirectory;
	}

	public long lastModified() {
//		return new Daattributes.get(EnumResourceAttributes.LAST_MODIFIED);;
		return 0;
	}

	public long length() {
		return 0;
	}

	public String[] list() {
		return null;
	}
	/**
	 * Returns an array of abstract pathnames denoting the resources in the
     * directory denoted by this abstract pathname.
     * 
     * <p> If this abstract pathname does not denote a directory, then this
     * method returns <code>null</code>.  Otherwise an array of
     * <code>Resource</code> objects is returned, one for each resource or directory in
     * the directory.  Pathnames denoting the directory itself and the
     * directory's parent directory are not included in the result.  Each
     * resulting abstract pathname is constructed from this abstract pathname
     * using the <code>Resource(Resource,String)}</code> constructor.  Therefore if this pathname
     * is absolute then each resulting pathname is absolute; if this pathname
     * is relative then each resulting pathname will be relative to the same
     * directory.
     *
     * <p> There is no guarantee that the name strings in the resulting array
     * will appear in any specific order; they are not, in particular,
     * guaranteed to appear in alphabetical order.
	 */
	public Resource[] listResources() {
		if (!this.isDirectory()) return null;
		//TODO fetch the sub resources for this S3 resource by creating correct Request URL
		Resource[] assetList = new Resource[0]; // allocate a array to fit all found resources
//		for(int count=0;count<fileList.length;count++) {
//			//assetList[count]= new S3Resource(this,"relative name of subresource");
//		}
		return assetList;
	}

	public void renameTo(URL dest) {
//		return url.renameTo(new File(dest));
	}

	public void mkdir() {
		// TODO Auto-generated method stub
		
	}
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean renameTo(URI dest) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isFile() {
		// TODO Auto-generated method stub
		return false;
	}
}
