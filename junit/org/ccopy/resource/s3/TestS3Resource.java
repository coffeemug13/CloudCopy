/**
 * 
 */
package org.ccopy.resource.s3;

import static org.junit.Assert.*;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.Proxy.Type;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccopy.TestSetup;
import org.ccopy.resource.util.LoggingDateFormatter;
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
public class TestS3Resource {
	private static final String TEST_URL_OBJECT = "https://ccopy.s3.amazonaws.com/tmp.txt";
	private static final String TEST_URL_OBJECT_UMLAUT = "https://ccopy.s3.amazonaws.com/tmp/file with+andÜ.txt";
	private static final String TEST_URL_OBJECT_NOT_EXISTS = "https://ccopy.s3.amazonaws.com/tmp-.txt";
	private static final String TEST_URL_NO_S3SERVICE = "http://www.sun.com/";
	private static Logger logger = Logger.getLogger("org.ccopy");

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
	 * Test method for {@link org.ccopy.resource.s3.S3Resource#S3Resource(org.ccopy.resource.s3.S3Resource, java.lang.String)}.
	 */
	@Test
	public void testS3ResourceS3ResourceString() {
		// positive tests
		try {
			URL url = new URL("http://bucketname.amazon.s3.com/");
			S3Resource s3 = new S3Resource(url);
			S3Resource s3Child = new S3Resource(s3, "child");
			System.out.println(s3.toURL());
			System.out.println(s3Child.toURL());
			assertEquals(s3.toURL()+"/child", s3Child.toURL());
		} catch (Exception e) {
			fail("unexpected error");
		}
	}
	/**
	 * Test method for {@link org.ccopy.resource.s3.S3Resource#getStatus()}.
	 */
	@Test
	public void testGetStatus() {
		try {
			S3Resource s3 = new S3Resource(new URL("https://coffeemug13.s3.amazonaws.com/test.txt"));
			s3.getStatus();
			assertTrue(s3.exists());
			assertTrue(s3.getLength() == 49);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("unexpected Exception occured");
		} 
	}
	/**
	 * Test method for {@link org.ccopy.resource.s3.S3Resource#canRead()}.
	 */
	@Test
	public void testCanRead() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.ccopy.resource.s3.S3Resource#canWrite()}.
	 */
	@Test
	public void testCanWrite() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.ccopy.resource.s3.S3Resource#exists()}.
	 */
	@Test
	public void testExists() {
		S3Resource s3 = new S3Resource(TEST_URL_OBJECT);
		assertTrue(s3.exists());
		S3Resource s3 = new S3Resource(TEST_URL_OBJECT_NOT_EXISTS);
		assertFalse(s3.exists());
	}

	/**
	 * Test method for {@link org.ccopy.resource.s3.S3Resource#isDirectory()}.
	 */
	@Test
	public void testIsDirectory() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.ccopy.resource.s3.S3Resource#toFile()}.
	 */
	@Test
	public void testToFile() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.ccopy.resource.s3.S3Resource#toURI()}.
	 */
	@Test
	public void testToURI() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.ccopy.resource.Resource#addMetadata(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAddMetadata() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.ccopy.resource.Resource#getMetadata()}.
	 */
	@Test
	public void testGetMetadata() {
		fail("Not yet implemented"); // TODO
	}

}
