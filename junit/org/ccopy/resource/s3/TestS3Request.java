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
import java.net.URI;
import java.net.URL;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccopy.TestSetup;
import org.ccopy.resource.util.LoggingDateFormatter;
import org.ccopy.resource.util.StringUtil;
import org.ccopy.resource.ResourceAuthenticator;
import org.ccopy.util.HttpMethod;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author coffeemug13
 *
 */
public class TestS3Request {
	private static final String TEST_URL_OBJECT = "https://ccopy.s3.amazonaws.com/test.txt";
	public static Logger logger = Logger.getLogger("org.ccopy");

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestSetup.initialSetup();
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
			URI url = new URI (TEST_URL_OBJECT);
			S3Request req = new S3Request(url);
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
				e.printStackTrace();
				InputStream ein = con.getErrorStream();
				while ((read = ein.read(c)) != -1) {
					// String result = new String(c);
					System.out.write(c, 0, read);
					// System.out.println("e----");
				}
				fail(StringUtil.exceptionToString(e));
			} finally {
				if (in != null) {
					in.close();
				}
				con.disconnect();
			}
			
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		}
	}
	/**
	 * Test method 
	 */
	@Test
	public void testGetcanonicalizedAmzHeaders() {
		S3Request req;
		try {
			req = new S3Request(new URI(TEST_URL_OBJECT));
		req.addRequestHeader("x-amz-meta-Username", "value1");
		req.addRequestHeader("X-Amz-Meta-ReviewedBy", "alice@s3.com");
		req.addRequestHeader("x-amz-meta-checksumalgorithm ", " crc32");
		req.addRequestHeader("X-Amz-Meta-ReviewedBy", "bob@s3.com");
		//System.out.println(req.getcanonicalizedAmzHeaders());
		assertEquals(req.getcanonicalizedAmzHeaders(), "x-amz-meta-checksumalgorithm:crc32\nx-amz-meta-reviewedby:bob@s3.com,alice@s3.com\nx-amz-meta-username:value1\n");
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		}
	}
	/**
	 * Test method for {@link org.ccopy.S3.S3Request#sign()}.
	 */
	@Test
	public void testSign() {
		S3Request req;
		try {
			req = new S3Request(new URI(TEST_URL_OBJECT));
			String sign = req.sign("uV3F3YluFJax1cknvbcGwgjvx4QpvB+leU8dUj2o", "GET\n\n\nTue, 27 Mar 2007 19:36:42 +0000\n/johnsmith/photos/puppy.jpg");
			assertEquals(sign, "xXjDGYUmKxnwqr5KXNPGldn5LbA=");
			sign = req.sign("uV3F3YluFJax1cknvbcGwgjvx4QpvB+leU8dUj2o", "PUT\n\nimage/jpeg\nTue, 27 Mar 2007 21:15:45 +0000\n/johnsmith/photos/puppy.jpg");
			assertEquals(sign, "hcicpDDvL9SsO6AkvxqmIWkmOuQ=");
			sign = req.sign("uV3F3YluFJax1cknvbcGwgjvx4QpvB+leU8dUj2o", "PUT\n4gJE4saaMU4BqNR0kLY+lw==\napplication/x-download\nTue, 27 Mar 2007 21:06:08 +0000\nx-amz-acl:public-read\nx-amz-meta-checksumalgorithm:crc32\nx-amz-meta-filechecksum:0x02661779\nx-amz-meta-reviewedby:joe@johnsmith.net,jane@johnsmith.net\n/static.johnsmith.net/db-backup.dat.gz");
			assertEquals(sign, "C0FlOtU8Ylb9KDTpZqYkZPX91iI=");
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		} 
	}
}
