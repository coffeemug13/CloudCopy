/**
 * 
 */
package org.ccopy.resource.s3;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccopy.TestSetup;
import org.ccopy.resource.util.LoggingDateFormatter;
import org.ccopy.resource.util.MimeType;
import org.ccopy.resource.util.StringUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author coffeemug13
 * 
 */
public class TestS3Object extends TestS3InitURLs {
	
	/**
	 * String with Umlaute for testing purpose
	 */
	private static final String TEST_STRING = "1234567890\nabcdefghijklmnoprstuvwxyz\näöüÄÖÜ~@€ß";
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
		TEST_STRING_LENGTH = TEST_STRING.getBytes().length;

	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestSetup.initialSetup();
		// set the Log Format and Level
		ConsoleHandler ch = new ConsoleHandler();
		ch.setLevel(Level.FINEST);
		ch.setFormatter(new LoggingDateFormatter());
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
	 * Test method for
	 * {@link org.ccopy.resource.s3.S3Object#putObject(java.net.URL, java.util.HashMap, java.io.InputStream)}
	 * .
	 */
	@Test
	public void testPutObject() {
		File file = null;
		try {
			file = File.createTempFile("s3testfile", null);
			FileWriter w = new FileWriter(file);
			w.write(TEST_STRING);
			w.close();
		} catch (IOException e) {
			fail("Error while writing the tempfile");
		}
		try {
			Map<String, String> meta = new HashMap<String, String>();
			meta.put(S3Headers.X_AMZ_META.toString() + "custom", "attribute");
			String versionId = S3Object.putObject(new S3URL(TMP_URL), meta, MimeType.fromFileName(TEST_URL_FILE_FILENAME),
					TEST_STRING_LENGTH, new FileInputStream(file));
			versionId = S3Object.putObject(new S3URL(TMP_URL_WITH_UMLAUT), meta, MimeType.fromFileName(TEST_URL_FILE_FILENAME),
					TEST_STRING_LENGTH, new FileInputStream(file));
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	/**
	 * Test method for
	 * {@link org.ccopy.resource.s3.S3Object#putObject(java.net.URL, java.util.HashMap, java.io.InputStream)}
	 * .
	 */
	@Test
	public void testPutObject_ArgumentCheck() {
		File file = null;
		try {
			file = File.createTempFile("s3testfile", null);
			FileWriter w = new FileWriter(file);
			w.write(TEST_STRING);
			w.close();
		} catch (IOException e) {
			fail("Error while writing the tempfile");
		}
		try {
			// put a S3 object with a key >1024 Byte
			try {
				String s = "ThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongKeyIMeanReallyLongKeyThisIsAVeryLongK";
				String versionId = S3Object.putObject(new S3URL(TEST_URL_S3 + s + "1"), null, MimeType.fromFileName(TEST_URL_FILE_FILENAME),
						TEST_STRING_LENGTH, new FileInputStream(file));
				fail("URL with to long key not catched");
			} catch (IllegalArgumentException e) { /* that is the expected Exception	*/ }
			
			// put a S3 object but don't provide a InputStream
			try {
				String versionId = S3Object.putObject(new S3URL(TMP_URL), null, MimeType.fromFileName(TEST_URL_FILE_FILENAME),
						TEST_STRING_LENGTH, null);
				fail("missing InputStream not catched");
			} catch (NullPointerException e) { /* that is the expected Exception	*/ }
			
			// put a S3 object but don't provide a URL
			try {
				String versionId = S3Object.putObject(null, null, MimeType.fromFileName(TEST_URL_FILE_FILENAME),
						TEST_STRING_LENGTH, new FileInputStream(file));
				fail("missing InputStream not catched");
			} catch (NullPointerException e) { /* that is the expected Exception	*/ }
			
		} catch (Exception e) {
			fail("unexpected Exception: " + e.toString());
		}
	}

	/**
	 * Test method for
	 * {@link org.ccopy.resource.s3.S3Object#getHeadObject(java.net.URL, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetHeadObject() {
		try {
			S3Object obj = S3Object.getHeadObject(new S3URL(TMP_URL), null);
			assertTrue(obj.exists());
			assertTrue(obj.canRead());
			assertTrue("Content-Length", TEST_STRING_LENGTH == obj.getContentLength());
			assertEquals(TEST_STRING_MD5, obj.getETag());
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	/**
	 * Test method for
	 * {@link org.ccopy.resource.s3.S3Object#getHeadObject(java.net.URL, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetHeadObject_Extended() {
		// test a complete wrong URL
		try {
			// S3Object obj = S3Object.getHeadObject(new
			// URL("http://www.heise.de/dd"),
			S3Object obj = S3Object.getHeadObject(new S3URL(TMP_URL), null);
		} catch (FileNotFoundException e) {
			// that error is correct, continue testing
		} catch (Exception e) {
			fail("Unexpected Exception occured: " + e.toString());
		}
		// test FileNotFound
		try {
			S3Object obj = S3Object.getHeadObject(new S3URL(TMP_URL), null);
		} catch (FileNotFoundException e) {
			// that error is correct, continue testing
		} catch (Exception e) {
			fail("Unexpected Exception occured: " + e.toString());
		}
	}

	/**
	 * Test method for
	 * {@link org.ccopy.resource.s3.S3Object#getObject(java.net.URL, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetObject() {
		try {
			// connect to the S3 object
			S3Object obj = S3Object.getObject(new S3URL(TMP_URL), null);
			// now check the metadata
			assertTrue(obj.exists());
			assertTrue(obj.canRead());
			assertTrue(TEST_STRING_LENGTH == obj.getContentLength());
			assertEquals(TEST_STRING_MD5, obj.getETag());
			// then compare the content of the object
			assertEquals(TEST_STRING, StringUtil.streamToString(obj.getInputStream()));
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	/**
	 * Test method for
	 * {@link org.ccopy.resource.s3.S3Object#deleteObject(java.net.URL, java.lang.String)}
	 * .
	 */
	@Test
	public void testDeleteObject() {
		try {
			String versionId = S3Object.deleteObject(new S3URL(TMP_URL));
			versionId = S3Object.deleteObject(new S3URL(TMP_URL_WITH_UMLAUT));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Exception occured" + e.toString());
		}
	}
}
