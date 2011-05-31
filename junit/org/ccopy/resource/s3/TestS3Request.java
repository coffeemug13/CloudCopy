/**
 * 
 */
package org.ccopy.resource.s3;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccopy.resource.DateFormatter;
import org.ccopy.resource.ResourceAuthenticator;
import org.ccopy.util.HttpMethod;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author mholakovsky
 *
 */
public class TestS3Request {
	public static Logger logger = Logger.getLogger("org.ccopy");

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// set the default Authentication
		Authenticator.setDefault(new ResourceAuthenticator(
				"AKIAIGZKXWFKU74XTWAA",
				"q5If10+UBO8Gu4jlD5Lno038Y9TXF06fj98CWn8L"));
		// set the Log Format and Level
		ConsoleHandler ch = new ConsoleHandler();
		ch.setLevel(Level.FINEST);
		ch.setFormatter(new DateFormatter());
		// add to logger
		logger.addHandler(ch);
		logger.setLevel(Level.FINEST);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.ccopy.resource.s3.S3#S3Request()}.
	 */
	@Test
	public void testS3RequestGetObjectUrl() {
		try {
			URL url = new URL ("https://mholakovsky.s3.amazonaws.com/test.txt");
			S3Request req = new S3Request(url);
			req.proxy = null;
			req.setHttpMethod(HttpMethod.GET);
			HttpURLConnection con = req.getConnection();
			InputStream in = null;
			byte[] c = new byte[100]; // with increasing value speed goes up
			int read = 0;
			try {
				in = con.getInputStream();
				// Read (and print) till end of file.
				while ((read = in.read(c)) != -1) {
					// String result = new String(c);
					System.out.print(new String(c, 0, read));
				}
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				InputStream ein = con.getErrorStream();
				while ((read = ein.read(c)) != -1) {
					// String result = new String(c);
					System.out.write(c, 0, read);
					// System.out.println("e----");
				}
			} finally {
				if (in != null) {
					in.close();
				}
				con.disconnect();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			fail(e.toString());
		}
	}
	/**
	 * Test method 
	 */
	@Test
	public void testGetcanonicalizedAmzHeaders() {
		S3Request req = new S3Request(null);
		req.addRequestHeader("ytest", "value1");
		req.addRequestHeader("x-amz-meta-Username", "value1");
		req.addRequestHeader("X-Amz-Meta-ReviewedBy", "alice@s3.com");
		req.addRequestHeader("x-amz-meta-checksumalgorithm ", " crc32");
		req.addRequestHeader("X-Amz-Meta-ReviewedBy", "bob@s3.com");
		//System.out.println(req.getcanonicalizedAmzHeaders());
		assertEquals(req.getcanonicalizedAmzHeaders(), "x-amz-meta-checksumalgorithm:crc32\nx-amz-meta-reviewedby:bob@s3.com,alice@s3.com\nx-amz-meta-username:value1\n");
	}
}
