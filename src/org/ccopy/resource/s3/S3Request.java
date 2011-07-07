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
import java.security.GeneralSecurityException;
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

import org.ccopy.resource.util.Base64;
import org.ccopy.resource.util.StringUtil;
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
	/**
	 * the default HTTP Method is GET
	 */
	private HttpMethod httpVerb = HttpMethod.GET;
	protected String contentMD5 = "";
	protected String contentType = "";
	protected String canonicalizedAmzHeaders = "";
	protected String canonicalizedResource;
	protected TreeMap<String, String> amzHeaders = new TreeMap<String, String>();
	protected List<String> headers = new ArrayList<String>();
	protected HttpURLConnection con = null;
	private int fixedStreamLength;

	static protected SimpleDateFormat df = new SimpleDateFormat(
			"EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

	static {
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * Constructor for S3Request you will need always a URL to start with, which
	 * contains authority (= host + bucketname) + path (=key)
	 * 
	 * @param url
	 */
	public S3Request(URL url) {
		this.url = url;
	}

	/**
	 * Set the HTTP method of the request
	 * 
	 * @param method
	 */
	protected void setHttpMethod(HttpMethod method) {
		this.httpVerb = method;
	}

	/**
	 * Set the md5 hash for the object to upload
	 * 
	 * @param contentMD5
	 *            the contentMD5 to set
	 */
	public void setContentMD5(String contentMD5) {
		this.contentMD5 = contentMD5;
	}

	/**
	 * Set the content type of the object to upload
	 * 
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addRequestHeader(String key, String value) {
		key = key.trim();
		value = value.trim();
		// add request header
//		headers.add(key + ": " + value);
		// collect amz headers
		if (key.toLowerCase().startsWith("x-amz-")) {
			if (!key.toLowerCase().equals(S3Headers.X_AMZ_META.toString())) {
			String oldKey = amzHeaders.put(key.toLowerCase(), value);
			if (oldKey != null)
				amzHeaders.put(key.toLowerCase(), value + "," + oldKey);
			} else System.err.println("skipping to add user metadata to S3 Request Header because missing unique key after 'x-amz-meta-'");
		} else amzHeaders.put(key, value);

	}

	public Map<String, String> getRequestHeaders() {
		return Collections.unmodifiableMap(amzHeaders);
	}

	public Map<String, List<String>> getResponseHeaders() {
		return (con != null) ? con.getHeaderFields() : null;
	}

	/**
	 * Get the connection for the specified S3 request
	 * 
	 * @return
	 * @throws IOException in case of problems with the connectivity e.g. Proxy 
	 * @throws S3Exception when S3 response with HTTP Statuscode != 200 
	 */
	protected HttpURLConnection getConnection() throws IOException, S3Exception {
		if (HttpMethod.proxy != null) {
			con = (HttpURLConnection) url.openConnection(HttpMethod.proxy);
		} else {
			con = (HttpURLConnection) url.openConnection();
		}
		// set the http request method
		con.setRequestMethod(httpVerb.toString());
		// enable output mode for the connection for put
		if (httpVerb == HttpMethod.PUT) {
			con.setDoOutput(true);
			con.setFixedLengthStreamingMode(fixedStreamLength);
		}
			
		// set the correct date for the request and add to request header
		String date = df.format(new Date());
		con.addRequestProperty("Date", date);
		if ("" != contentType) con.setRequestProperty("Content-Type", contentType);
		// add the manually set header attributes
		for (Entry<String, String> entry : amzHeaders.entrySet()) {
			con.setRequestProperty(entry.getKey(), entry.getValue());
		}
		
		// create the canonicalizedResource
		String host = url.getHost();
		canonicalizedResource = "/"
				// TODO clean this line of code to allow other hosts
				+ host.substring(0, host.indexOf(".s3.amazonaws.com")) 
				+ url.getPath();
		// create the string to sign
		String stringToSign = httpVerb + "\n" + contentMD5 + "\n" + contentType
				+ "\n" + date + "\n" + getcanonicalizedAmzHeaders()
				+ canonicalizedResource;
		// sign this string and add it as Authorization header
		PasswordAuthentication pwd = Authenticator
				.requestPasswordAuthentication(null, 0, "", "", "");
		String sign = sign(new String(pwd.getPassword()), stringToSign);
		String authorization = "AWS " + pwd.getUserName() + ":" + sign;
		con.addRequestProperty("Authorization", authorization);

		/**
		 * Print the request and response headers for logging
		 */
		if (logger.isLoggable(Level.FINEST)) {
			StringBuffer buf = new StringBuffer();
			buf.append("S3 request for '" + url + "'\n");
			buf.append("request headers...\n");
			buf.append("* " + con.getRequestMethod() + "\n");
			buf.append("* Authorization: " + authorization + "\n");
			buf.append(StringUtil.mapToString(con.getRequestProperties()));
			logger.finest(buf.toString());
		}
		/**
		 * Check the HTTP status code of the connection for errors
		 */
		int res;
		if ((httpVerb != HttpMethod.PUT) && (res = con.getResponseCode()) >= 300) {
				throw new S3Exception(con.getResponseCode(), con.getResponseMessage(), StringUtil.streamToString(con.getErrorStream()));
		}
		/**
		 * finish the method
		 */
		return con;
	}

	/**
	 * @param proxy
	 *            the proxy to set
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
	 * @throws GeneralSecurityException
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
			throw new IllegalStateException("Got a problem signing or encoding the stringTosign", e);
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
			if (entry.getKey().startsWith(S3Headers.AMAZON_PREFIX.toString())) {
				String key = entry.getKey();
				buf.append(key);
				buf.append(":");
				buf.append(entry.getValue());
				buf.append("\n");
			}
		}
		return buf.toString();
	}

	public void setFixedLengthStreamingMode(int contentLength) {
		this.fixedStreamLength = contentLength;
		
	}
}
