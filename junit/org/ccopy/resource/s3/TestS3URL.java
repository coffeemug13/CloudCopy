package org.ccopy.resource.s3;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class TestS3URL {
	private static final String TEST_URL_OBJECT = "https://ccopy.s3.amazonaws.com/test.txt";
	private static final String TEST_URL_OBJECT_UMLAUTE = "https://ccopy.s3.amazonaws.com/test/Mit Ümlaut+Sonderzeichen.txt";
	/**
	 * The encode String for S3 
	 */
	private static final String TEST_URL_OBJECT_UMLAUTE_ENCODED = "https://ccopy.s3.amazonaws.com%2Ftest%2FMit+%C3%9Cmlaut%2BSonderzeichen.txt";
	private static final String TEST_URL_S3 = "https://ccopy.s3.amazonaws.com/";

	/**
	 * Test method for {@link org.ccopy.resource.s3.S3URL#S3URL(URL)} .
	 */
	@Test
	public void testS3URLURL() {
		try {
			// create S3 object from URL
			URL url = new URL(TEST_URL_OBJECT);
			S3URL s3 = new S3URL(url);
			assertEquals(TEST_URL_OBJECT, s3.toString());
		} catch (Exception e) {
			fail("unexpected exception:\n" + e.toString());
		}
	}

	@Test
	public void testS3URLString() {
		S3URL s3;

		try {
			s3 = new S3URL(TEST_URL_OBJECT);
			assertEquals(TEST_URL_OBJECT, s3.toString());
			// testing encoding of Umlaute and spaces
			s3 = new S3URL(TEST_URL_OBJECT_UMLAUTE);
			assertEquals(TEST_URL_OBJECT_UMLAUTE, s3.toString());
			assertEquals(TEST_URL_OBJECT_UMLAUTE_ENCODED,s3.toURL().toString());
			// create S3 and clean the URL, e.g. remove query, fragment
			s3 = new S3URL(TEST_URL_OBJECT + "?acl");
			assertEquals(TEST_URL_OBJECT, s3.toString());
			// create S3 and clean the URL, from user info
			s3 = new S3URL("https://user@ccopy.s3.amazonaws.com/test.txt");
			assertEquals(TEST_URL_OBJECT, s3.toString());
		} catch (Exception e) {
			fail("unexpected exception:\n" + e.toString());
		}
	}

}
