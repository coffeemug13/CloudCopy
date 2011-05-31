/**
 * 
 */
package org.ccopy.resource.s3;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.Proxy.Type;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.ccopy.resource.Base64;
import org.ccopy.resource.ResourceException;
import org.ccopy.util.HttpMethod;

/**
 * @author mholakovsky
 *
 */
public class S3Request {
	private static Logger logger = Logger.getLogger("org.ccopy");
	protected Proxy proxy = null;
	protected URL url;
	private HttpMethod httpVerb = HttpMethod.GET;
	protected String contentMD5 = "";
	protected String contentType = "";
	protected String canonicalizedAmzHeaders = "";
	protected String canonicalizedResource;
	protected TreeMap<String,String> amzHeaders = new TreeMap<String, String>();
	protected List<String> headers = new ArrayList<String>();
	protected HttpURLConnection con = null;
	
	static protected SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
	
	static {
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	/**
	 * Constructor for S3Request
	 * you will need always a URL to start with, which contains
	 * authority (= host + bucketname) + path (=key)
	 * @param url
	 */
	public S3Request(URL url) {
		this.url = url;
	}
	/**
	 * Set the HTTP method of the request
	 * @param method
	 */
	protected void setHttpMethod(HttpMethod method) {
		this.httpVerb = method;
	}
	/**
	 * Set the md5 hash for the object to upload
	 * @param contentMD5 the contentMD5 to set
	 */
	public void setContentMD5(String contentMD5) {
		this.contentMD5 = contentMD5;
	}
	/**
	 * Set the content type of the object to upload
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public void addRequestHeader(String key, String value) {
		key = key.trim();
		value = value.trim();
		// add request header
		headers.add(key + ": " + value);
		// collect amz headers
		if (key.toLowerCase().startsWith("x-amz-")) {
			String oldKey = amzHeaders.put(key.toLowerCase(), value);
			if (oldKey!=null) amzHeaders.put(key.toLowerCase(), value + ","+oldKey);
		}
		
	}
	public Map<String, String> getRequestHeaders() {
		return Collections.unmodifiableMap(amzHeaders);
	}
	public Map<String, List<String>> getResponseHeaders() {
		return (con != null) ? con.getHeaderFields() : null;
	}
	/**
	 * Get the connection for the specified S3 request
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws ResourceException 
	 */
	protected HttpURLConnection getConnection() throws IOException, InvalidKeyException, ResourceException  {
		if (HttpMethod.proxy!=null) {
			con = (HttpURLConnection) url.openConnection(HttpMethod.proxy);
		} else {
			con = (HttpURLConnection) url.openConnection();
		}
		// set the http request method
		con.setRequestMethod(httpVerb.toString());
		// set the correct date for the request and add to request header
		String date = df.format(new Date());
		con.addRequestProperty("Date", date);
		// create the canonicalizedResource
		String host = url.getHost();
		canonicalizedResource = "/" + host.substring(0, host.indexOf(".s3.amazonaws.com")) + url.getPath();
		// create the string to sign
		String stringToSign = httpVerb + "\n" + contentMD5 + "\n" + contentType + "\n" + date + "\n" + getcanonicalizedAmzHeaders() + canonicalizedResource;
		logger.fine("this is the string to sign:\n" + stringToSign);
		// sign this string and add it as Authorization header
		PasswordAuthentication pwd = Authenticator.requestPasswordAuthentication(null, 0, "", "", "");
		String sign = sign(new String(pwd.getPassword()), stringToSign);
		String authorization = "AWS " + pwd.getUserName() + ":" + sign;
		logger.fine("using following S3 Authorization string: " + authorization);
		con.addRequestProperty("Authorization", authorization);
		
		/**
		 * Print the request and response headers for logging
		 */
		if (logger.isLoggable(Level.FINER)) {
			String log = "Request Headers...\n";
			Map<String, List<String>> map = con.getRequestProperties();
			Set<Entry<String, List<String>>> mapSet = map.entrySet();
			Iterator<Entry<String, List<String>>> mapIterator = mapSet
					.iterator();
			while (mapIterator.hasNext()) {
				Map.Entry<String, List<String>> e = (Map.Entry<String, List<String>>) mapIterator
						.next();
				if (e.getKey()!=null) {
					log += "   " + e.getKey() + ": ";
				}
				Iterator<String> val = e.getValue().iterator();
				while (val.hasNext()) {
					log += val.next() + " ";
				}
				log += "\n";
			}
			logger.finer(log);
			log = "Response Headers...\n";
			map = con.getHeaderFields();
			mapSet = map.entrySet();
			mapIterator = mapSet.iterator();
			while (mapIterator.hasNext()) {
				Map.Entry<String, List<String>> e = (Map.Entry<String, List<String>>) mapIterator
						.next();
				if (e.getKey()!=null) {
					log += "   " + e.getKey() + ": ";
				} else log += "   ";
				Iterator<String> val = e.getValue().iterator();
				while (val.hasNext()) {
					log += val.next() + " ";
				}
				log += "\n";
			}
			logger.finer(log);
		} else con.connect();
		// return the connection
		return con;
	}
	/**
	 * @param proxy the proxy to set
	 */
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}
	/**
	 * sign the request
	 * 
	 * @param yourSecretAccessKeyID
	 * @param stringToSign
	 * @return
	 * @throws InvalidKeyException
	 * @throws ResourceException
	 */
	protected String sign(String yourSecretAccessKeyID, String stringToSign ) throws InvalidKeyException, ResourceException {
		String signature = null;
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(yourSecretAccessKeyID.getBytes(), "HmacSHA1"));
			signature = Base64.encodeBytes(mac.doFinal(stringToSign.getBytes("UTF-8")));
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			throw new ResourceException(e);
		}
		return signature;
	}
	/**
	 * Get the relevant request headers and convert them to the
	 * canonicalizedAmzHeaders string for the request
	 * 
	 * To construct the CanonicalizedAmzHeaders part of StringToSign, select all HTTP request headers that start with 'x-amz-' (using a case-insensitive comparison) and use the following process.
	 * 
	 * CanonicalizedAmzHeaders Process
	 * 1	Convert each HTTP header name to lower-case. For example, 'X-Amz-Date' becomes 'x-amz-date'.
	 * 2	Sort the collection of headers lexicographically by header name.
	 * 3	Combine header fields with the same name into one "header-name:comma-separated-value-list" pair as prescribed by RFC 2616, section 4.2, without any white-space between values. For example, the two metadata headers 'x-amz-meta-username: fred' and 'x-amz-meta-username: barney' would be combined into the single header 'x-amz-meta-username: fred,barney'.
	 * 4	"Unfold" long headers that span multiple lines (as allowed by RFC 2616, section 4.2) by replacing the folding white-space (including new-line) by a single space.
	 * 5	Trim any white-space around the colon in the header. For example, the header 'x-amz-meta-username: fred,barney' would become 'x-amz-meta-username:fred,barney'
	 * 6	Finally, append a new-line (U+000A) to each canonicalized header in the resulting list. Construct the CanonicalizedResource element by concatenating all headers in this list into a single string.
	 * 
	 * @return
	 */
	protected String getcanonicalizedAmzHeaders() {
		StringBuffer buf = new StringBuffer();
		for(Entry<String,String> entry : amzHeaders.entrySet()) {
			String key = entry.getKey();
			buf.append(key);
			buf.append(":");
			buf.append(entry.getValue());
			buf.append("\n");
		}
		return buf.toString();
	}
}