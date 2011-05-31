/**
 * 
 */
package org.ccopy.resource.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.Proxy.Type;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.ccopy.resource.Base64;
import org.ccopy.resource.ResourceAuthenticator;
import org.ccopy.resource.ResourceException;
import org.ccopy.util.HttpMethod;

/**
 * @author mholakovsky
 *
 */
public class S3 {
	static protected Proxy proxy = null;
	
	protected String region = null;
	protected String bucket = null;
	protected HttpMethod httpVerb = HttpMethod.GET;
	protected String date = null;
	
	static {
		Authenticator.setDefault(new ResourceAuthenticator(
				"AKIAIGZKXWFKU74XTWAA",
				"q5If10+UBO8Gu4jlD5Lno038Y9TXF06fj98CWn8L"));
	}

	public S3() {
		// TODO Auto-generated constructor stub
	}
	
	public void setProxy() {
		SocketAddress addr = new InetSocketAddress("proxy.sozvers.at", 8080);
		proxy = new Proxy(Type.HTTP, addr);
	}
	
	/**
	 * Put an object on S3
	 * 
	 * @param req
	 * @throws ResourceException 
	 * @throws IOException 
	 * @throws InvalidKeyException 
	 */
	static public void getObject(S3Request req) throws InvalidKeyException, IOException, ResourceException {
		if (proxy!=null) req.proxy = proxy;
		HttpURLConnection con = req.getConnection();
		//TODO parse the returning response headers, create a S3 object
	}
	/**
	 * Put an object on S3
	 * 
	 * @param req
	 */
	static public S3Object putObject(S3Request req, InputStream in) {
		SocketAddress addr = new InetSocketAddress("proxy.sozvers.at", 8080);
		Proxy proxy = new Proxy(Type.HTTP, addr);
		try {
			HttpURLConnection con = (HttpURLConnection) req.url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO create the string2Sign req.getCanonicalUrl();
		// TODO sign the request
		// add Date and Authorization to the request
		return null;
	}
	public S3Resource[] listObjects(String bucket, String prefix) {
		return listObjects(bucket, prefix, "/", 1000);
	}
	public S3Resource[] listObjects(String bucket, String prefix, String delimiter, int maxKeys) {
		//TODO do bucket operation to list objects, page thorugh the result
		S3Resource[] list = new S3Resource[1]; //TODO change to real numbers
		list[0] = S3Resource.createResource(url);
		return list;
	}
}