/**
 * 
 */
package org.ccopy.resource.s3;

import static org.junit.Assert.*;

import java.io.OutputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccopy.TestSetup;
import org.ccopy.resource.util.LoggingDateFormatter;
import org.ccopy.resource.util.MimeType;
import org.ccopy.resource.Resource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author coffeemug13
 */
public class TestS3Resource extends TestS3InitURLs{

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
	 * This test simulates typical behavior when crawling directories.
	 * {@link org.ccopy.resource.s3.S3Resource#S3Resource(org.ccopy.resource.s3.S3Resource, java.lang.String)}
	 */
	@Test
	public void testS3Resource_Crawling() {
		try {
			/**
			 * first create the root resource to start the crawling
			 */
			// construct the root for a bucket
			Resource root = new S3Resource("ccopy", null);
			// check that it exists and connect implicitly
			if (!root.exists() && root.isDirectory())
				throw new Exception("wrong root resource");
			// from here every resource "knows" already that it exists
			// now get the child's resources
			Resource[] childs = root.listResources();
			// do some checks on the resource, this doesn't trigger requests to the service because
			// status of
			// the resource is already known (fetched from the service by listResources())
			if (childs[1].exists() && childs[1].isDirectory())
				System.out.println("everything is fine");
			/**
			 * now add a new child file resource and write something
			 */
			// in this case I know already, that this resource doesn't exist
			Resource child = root.getChildResource("newChild.txt").addMetadata("key", "value")
					.setContentType(MimeType.fromFileName("newChild.txt"))
					.setLastModificationTime(System.currentTimeMillis() + 1000);
			OutputStream out = child.getOutputStream();
			// write something to the file resource and close afterwards
			out.close();
			/**
			 * now add a new child file resource
			 */
			Resource child2 = root.getChildResource("newChild2").addMetadata("key", "value")
					.createDirectoryResource();
			// now continue crawling down and get the child's of one child
			Resource[] childs2 = childs[1].listResources();
		} catch (Exception e) {
			fail("unexpected exception: " + e.toString());
		}
	}
	/**
	 * Tests simple manipulation of a S3Resource
	 * {@link org.ccopy.resource.s3.S3Resource#S3Resource(org.ccopy.resource.s3.S3Resource, java.lang.String)}
	 */
	@Test
	public void testS3Resource_Manipulation() {
		try {
			// first create the resource you believe it should exist
			Resource r = new S3Resource("ccopy", "/test.txt");
			// check existence and populate implicit with metadata
			if (!r.exists())
				throw new Exception();
			// now do something with the resource
			String type = null;
			if (r.isFile())
				type = r.getContentType().toString();
			// now change the resource and persist changes
			r.setLastModificationTime(System.currentTimeMillis())
			 .addMetadata("key", "value")
			 .persistChanges();
			// cool
		} catch (Exception e) {
			e.printStackTrace();
			fail("unexpected exception: " + e.toString());
		}
	}

}
