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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccopy.TestSetup;
import org.ccopy.resource.util.DateFormatter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author mholakovsky
 *
 */
public class TestS3Object {
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
	 * Test method for {@link org.ccopy.resource.s3.S3Object#getObject(java.net.URL, java.lang.String)}.
	 */
	@Test
	public void testGetObject() {
		try {
			S3Object obj = S3Object.getObject(new URL("https://mholakovsky.s3.amazonaws.com/test.txt"), null);
			assertTrue(obj.exists());
			assertTrue(obj.canRead());
			assertTrue(49 == obj.getContentLength());
			assertEquals("c278f05f3f5aeba3dc83fad531c11957", obj.getETag());
			
			InputStream in = null;
			StringBuffer buf = new StringBuffer(49);
			byte[] c = new byte[100]; // with increasing value speed goes up
			int read = 0;
			try {
				in = obj.getInputStream();
				// Read (and print) till end of file.
				while ((read = in.read(c)) != -1) {
					// String result = new String(c);
					//System.out.print(new String(c, 0, read));
					buf.append(new String(c, 0, read));
				}
				in.close();
				logger.finer(buf.toString());
				assertEquals("1234567890\nabcdefghijklmopqrstuvwxyz\n^�������~#", buf.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				InputStream ein = obj.getErrorStream();
				while ((read = ein.read(c)) != -1) {
					// String result = new String(c);
					System.out.write(c, 0, read);
					// System.out.println("e----");
				}
			} finally {
				if (in != null) {
					in.close();
				}
			}
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	/**
	 * Test method for {@link org.ccopy.resource.s3.S3Object#getHeadObject(java.net.URL, java.lang.String)}.
	 */
	@Test
	public void testGetHeadObject() {
		try {
			S3Object obj = S3Object.getHeadObject(new URL("https://mholakovsky.s3.amazonaws.com/test.txt"), null);
			assertTrue(obj.exists());
			assertTrue(obj.canRead());
			assertTrue(49 == obj.getContentLength());
			assertEquals("c278f05f3f5aeba3dc83fad531c11957", obj.getETag());
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	/**
	 * Test method for {@link org.ccopy.resource.s3.S3Object#putObject(java.net.URL, java.util.HashMap, java.io.InputStream)}.
	 */
	@Test
	public void testPutObject() {
		File file = null;
		try {
			file = File.createTempFile("s3testfile", null);
			FileWriter w = new FileWriter(file);
			w.write("1234567890\nabcdefghijklmnoprstuvwxyz\näöüÄÖÜ~@€ß");
			w.close();
		} catch (IOException e) {
			fail("Error while writing the tempfile");
		}
		try {
			String versionId = S3Object.putObject(new URL("https://mholakovsky.s3.amazonaws.com/test2.txt"), null, (int) file.length(), new FileInputStream(file));
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	/**
	 * Test method for {@link org.ccopy.resource.s3.S3Object#deleteObject(java.net.URL, java.lang.String)}.
	 */
	@Test
	public void testDeleteObject() {
		fail("Not yet implemented"); // TODO
	}

}
