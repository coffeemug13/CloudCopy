package org.ccopy.resource.s3;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.ccopy.TestSetup;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestS3URL extends TestS3InitURLs {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestSetup.initialSetup();
	}

	/**
	 * Test method for {@link org.ccopy.resource.s3.S3URL#S3URL(URL)} .
	 */
	@Test
	public void testS3URLURL() {
		try {
			// create S3 object from URL
			URL url = new URL(TEST_URL_FILE);
			S3URL s3 = new S3URL(url);
			assertEquals(TEST_URL_FILE, s3.toString());
		} catch (Exception e) {
			fail("unexpected exception:\n" + e.toString());
		}
	}

	@Test
	public void testS3URLString() {
		S3URL s3;

		try {
			s3 = new S3URL(TEST_URL_FILE);
			assertEquals(TEST_URL_FILE, s3.toString());
			assertTrue(s3.isFile);
			// testing encoding of Umlaute and spaces
			s3 = new S3URL(TEST_URL_FILE_WITH_UMLAUTE);
			assertEquals(TEST_URL_FILE_WITH_UMLAUTE, s3.toString());
			assertEquals(TEST_URL_FILE_WITH_UMLAUTE_ENCODED, s3.toURL().toString());
			// create S3 and clean the URL, e.g. remove query, fragment
			s3 = new S3URL(TEST_URL_FILE + "?acl");
			assertEquals(TEST_URL_FILE, s3.toString());
			// create S3 and clean the URL, from user info
			s3 = new S3URL(TEST_URL_FILE_UNCLEAN);
			assertEquals(TEST_URL_FILE, s3.toString());
			// create S3 and clean the URL, from user info
			s3 = new S3URL(TEST_URL_S3_UNCLEAN);
			assertEquals(TEST_URL_S3, s3.toString());
			// test dir detection
			s3 = new S3URL(TEST_URL_DIR);
			assertFalse(s3.isFile);
		} catch (Exception e) {
			fail("unexpected exception:\n" + e.toString());
		}
	}

	@Test
	public void testS3URLString_catch_malformed_URL() {
		S3URL s3;
		try {
			try {
				s3 = new S3URL(TEST_URL_FILE_MALFORMED);
				fail("malformed URL not detected");
			} catch (MalformedURLException e) {
				// good, exception was thrown
			}
		} catch (Exception e) {
			fail("unexpected exception:\n" + e.toString());
		}
	}

	@Test
	public void testS3URLS3ResourceString() {
		S3URL s3;

		try {
			s3 = new S3URL(TEST_URL_FILE);
			assertEquals(TEST_URL_FILE, s3.toString());
			assertTrue(s3.isFile);

		} catch (Exception e) {
			fail("unexpected exception:\n" + e.toString());
		}
	}

}
