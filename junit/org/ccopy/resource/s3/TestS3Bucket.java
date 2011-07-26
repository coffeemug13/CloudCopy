/**
 * 
 */
package org.ccopy.resource.s3;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccopy.TestSetup;
import org.ccopy.resource.util.LoggingDateFormatter;
import org.ccopy.resource.util.StringUtil;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author mholakovsky
 *
 */
public class TestS3Bucket {
	/**
	 * String with Umlaute for testing purpose
	 */
	private static final String TEST_STRING = "1234567890\nabcdefghijklmnoprstuvwxyz\n‰ˆ¸ƒ÷‹~@Äﬂ"; 
	private static String TEST_STRING_MD5;
	private static int TEST_STRING_LENGTH;
	/**
	 * The logger
	 */
	private static Logger logger = Logger.getLogger("org.ccopy");
	{
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(TEST_STRING.getBytes(Charset.forName("UTF8")));
			final byte[] resultByte = messageDigest.digest();
			TEST_STRING_MD5 = new String(StringUtil.bytToHexString(resultByte));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		try {
			TEST_STRING_LENGTH = TEST_STRING.getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}

	}
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestSetup.initialSetup();
	}

	/**
	 * Test method for {@link org.ccopy.resource.s3.S3Bucket#listObjects(java.lang.String, java.lang.String, java.lang.String, int, java.lang.String)}.
	 */
	@Test
	public void testListObjects() {
		try {
			List<S3Object> list = S3Bucket.listObjects("ccopy", null, null, 1000, "/");
			// I expect 3 object, i.e. one file and two directories
			assertEquals(3, list.size());
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		}
	}

}
