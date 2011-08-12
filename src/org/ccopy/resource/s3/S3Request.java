/**
 * 
 */
package org.ccopy.resource.s3;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.ccopy.resource.ResourceError;
import org.ccopy.resource.util.Base64;
import org.ccopy.resource.util.StringUtil;
import org.ccopy.util.HttpMethod;

/**
 * @author coffeemug13
 * 
 */
public class S3Request {
	private static final Logger logger = Logger.getLogger("org.ccopy");
	private URL url;
	private URI uri;
	/**
	 * the default HTTP Method is GET
	 */
	private HttpMethod httpVerb = HttpMethod.GET;
	private String contentMD5 = "";
	private String contentType = "";
	private String canonicalizedResource;
	private TreeMap<String, String> amzHeaders = new TreeMap<String, String>();
	private HttpURLConnection con = null;
	private long fixedStreamLength;

	static protected SimpleDateFormat df = new SimpleDateFormat(
			"EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

	static {
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * Constructor for a S3Request.
	 * 
	 * @param uri
	 *            - An absolute, hierarchical URI with a non-empty authority and path component, and
	 *            optional query. Fragment and user info are removed.
	 * @throws NullPointerException
	 *             - If uri is {@link NullPointerException}
	 * @throws IllegalArgumentException
	 *             - If the preconditions on the parameter do not hold
	 */
	protected S3Request(URI uri) {
		logger.fine(null);
		// this.url = url.toURL();
		try {
			// check if absolute URL with non-empty path
			// see the junit tests for URI
			if ((uri.getPath() == null) || uri.getPath().isEmpty())
				throw new IllegalArgumentException("preconditions on the parameter do not hold");
			// remove the part user and fragment if in URI
			if ((uri.getUserInfo() != null) || (uri.getFragment() != null))
				try {
					uri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(),
							uri.getPath(), uri.getQuery(), null);
				} catch (URISyntaxException e) {
					System.out.println("This should never happen");
					e.printStackTrace();
					System.exit(1);
				}
			//  URI.normalize() the path and encode non-ASCII characters
			this.uri = uri.normalize();
			this.url = new URL(this.uri.toASCIIString().replace("+", "%2B"));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(uri.toASCIIString() + "is not a valid url", e);
		}
	}

	/**
	 * Set the HTTP method of the request
	 * 
	 * @param method
	 */
	protected void setHttpMethod(HttpMethod method) {
		if (null == method)
			throw new NullPointerException();
		this.httpVerb = method;
	}

	/**
	 * Set the md5 hash for the object to upload
	 * 
	 * @param contentMD5
	 *            the contentMD5 to set
	 */
	public void setContentMD5(String contentMD5) {
		if (null == contentMD5)
			throw new NullPointerException();
		this.contentMD5 = contentMD5;
	}

	/**
	 * Set the content type of the object to upload
	 * 
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(String contentType) {
		if (null == contentType)
			throw new NullPointerException();
		this.contentType = contentType;
	}

	/**
	 * Set request headers manually. In case a key is already added, this value will be added to the
	 * existing key.
	 * 
	 * @param key
	 *            - the header key; To add custom metadata use the prefix {@link S3Headers#X_AMZ_META}
	 *            for the key
	 * @param value
	 * @throws NullPointerException
	 *             when arguments are <code>null</code>
	 */
	public void addRequestHeader(String key, String value) {
		// the next two line will throw a NullPointerException when arguments are null
		// therefore no explicit check whether argument null
		// TODO V2 check max count for headers
		key = key.trim().toLowerCase();
		value = value.trim();
		String oldKey = amzHeaders.put(key, value);
		if (oldKey != null)
			amzHeaders.put(key, value + "," + oldKey);
	}

	/**
	 * Get the connection for the specified S3 request
	 * 
	 * @return
	 * @throws IOException in case of problems with the connectivity e.g. Proxy 
	 * @throws S3Exception when S3 response with HTTP Statuscode != 200 
	 */
	protected HttpURLConnection getConnection() throws IOException {
		logger.fine(null);
		if (HttpMethod.proxy != null) {
			con = (HttpURLConnection) url.openConnection(HttpMethod.proxy);
		} else {
			con = (HttpURLConnection) url.openConnection();
		}
		// set the http request method
		con.setRequestMethod(httpVerb.toString());
		// TODO resolve the redirect manually and rewrite the URL to avoid getting redirected for every request
		// con.setInstanceFollowRedirects(false);
		// enable output mode for the connection for put
		if (httpVerb == HttpMethod.PUT) {
			con.setDoOutput(true);
			con.setFixedLengthStreamingMode((int) fixedStreamLength);
		}
			
		// set the correct date for the request and add to request header
		String date = df.format(new Date());
		con.addRequestProperty("Date", date);
		if ((null != contentType)&&(!contentType.isEmpty())) 
			con.setRequestProperty("Content-Type", contentType);
		// add the manually set header attributes
		for (Entry<String, String> entry : amzHeaders.entrySet()) {
			con.setRequestProperty(entry.getKey(), entry.getValue());
		}
		con.setRequestProperty("Content-Encoding", "UTF-8");
		// create the canonicalizedResource
		canonicalizedResource = getCanonicalizedResource(url);
		// create the string to sign
		String stringToSign = httpVerb + "\n" + contentMD5 + "\n" + contentType
				+ "\n" + date + "\n" + getcanonicalizedAmzHeaders()
				+ canonicalizedResource;
		// sign this string and add it as Authorization header
		PasswordAuthentication pwd = Authenticator
				.requestPasswordAuthentication("s3.amazonaws.com",null, 0, null, null, null);
		String sign = sign(new String(pwd.getPassword()), stringToSign);
		String authorization = "AWS " + pwd.getUserName() + ":" + sign;
		con.addRequestProperty("Authorization", authorization);
		/**
		 * Print the request headers for logging
		 */
		if (logger.isLoggable(Level.FINEST)) {
			StringBuffer buf = new StringBuffer();
			buf.append("S3 request for '" + url + "'\n");
			buf.append("the following request headers have been set:\n");
			buf.append("* " + con.getRequestMethod() + "\n");
			buf.append("* Authorization: " + authorization + "\n");
			buf.append(StringUtil.mapToString(con.getRequestProperties()));
			logger.finest(buf.toString());
		}
		/**
		 * Check the HTTP status code of the connection for errors
		 */
		if (httpVerb != HttpMethod.PUT) {
			if ((con.getResponseCode()) >= 300)
				// throw an exception for S3 errors
				throw new S3Exception(con.getResponseCode(), con.getResponseMessage(), StringUtil.streamToString(con.getErrorStream()));
		} 
		/**
		 * finish the method
		 */
		return con;
	}

	/**
	 * @param url2
	 * @return
	 */
	protected static String getCanonicalizedResource(URL url) {
		String host = url.getHost();
		return "/"
				// TODO clean this line of code to allow other hosts
				+ host.substring(0, host.indexOf(".s3.amazonaws.com")) 
				+ url.getPath();
	}


	/**
	 * Sign the request.
	 * <p>
	 * The first time you call this method the {@code javax.crypto.Mac} instance
	 * is instantiated (about 200ms). Every method call afterwards takes less
	 * then 1ms to sign the string.
	 * 
	 * @param yourSecretAccessKeyID
	 * @param stringToSign
	 * @return
	 */
	protected String sign(String yourSecretAccessKeyID, String stringToSign) {
		String signature = null;
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(yourSecretAccessKeyID.getBytes(),
					"HmacSHA1"));
			signature = Base64.encodeBytes(mac.doFinal(stringToSign
					.getBytes("UTF-8")));
		} catch (Exception e) {
			// this is a critical error when you can't sign a request, no recover possible
			throw new ResourceError(ResourceError.ERROR_JVM,"Got a problem signing or encoding the stringTosign", e);
		}
		return signature;
	}

	/**
	 * Get the relevant request headers and convert them to the
	 * canonicalizedAmzHeaders string for the request
	 * 
	 * To construct the CanonicalizedAmzHeaders part of StringToSign, select all
	 * HTTP request headers that start with 'x-amz-' (using a case-insensitive
	 * comparison) and use the following process.
	 * 
	 * CanonicalizedAmzHeaders Process 1 Convert each HTTP header name to
	 * lower-case. For example, 'X-Amz-Date' becomes 'x-amz-date'. 2 Sort the
	 * collection of headers lexicographically by header name. 3 Combine header
	 * fields with the same name into one
	 * "header-name:comma-separated-value-list" pair as prescribed by RFC 2616,
	 * section 4.2, without any white-space between values. For example, the two
	 * metadata headers 'x-amz-meta-username: fred' and 'x-amz-meta-username:
	 * barney' would be combined into the single header 'x-amz-meta-username:
	 * fred,barney'. 4 "Unfold" long headers that span multiple lines (as
	 * allowed by RFC 2616, section 4.2) by replacing the folding white-space
	 * (including new-line) by a single space. 5 Trim any white-space around the
	 * colon in the header. For example, the header 'x-amz-meta-username:
	 * fred,barney' would become 'x-amz-meta-username:fred,barney' 6 Finally,
	 * append a new-line (U+000A) to each canonicalized header in the resulting
	 * list. Construct the CanonicalizedResource element by concatenating all
	 * headers in this list into a single string.
	 * 
	 * @return
	 */
	protected String getcanonicalizedAmzHeaders() {
		StringBuffer buf = new StringBuffer();
		for (Entry<String, String> entry : amzHeaders.entrySet()) {
			if (entry.getKey().startsWith(S3Headers.AMAZON_PREFIX)) {
				String key = entry.getKey();
				buf.append(key);
				buf.append(":");
				buf.append(entry.getValue());
				buf.append("\n");
			}
		}
		return buf.toString();
	}

	public void setFixedLengthStreamingMode(long contentLength) {
		this.fixedStreamLength = contentLength;
		
	}
	/**
	 * Return the URI for this request 
	 * @return the URI
	 */
	public URI toURI() {
		return this.uri;
	}
}
