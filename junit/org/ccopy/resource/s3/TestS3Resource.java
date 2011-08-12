/**
 * 
 */
package org.ccopy.resource.s3;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccopy.TestSetup;
import org.ccopy.resource.util.LoggingDateFormatter;
import org.ccopy.resource.util.MimeType;
import org.ccopy.resource.util.StringUtil;
import org.ccopy.resource.Resource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author coffeemug13
 */
public class TestS3Resource extends TestS3InitURLs {

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
	 * This test simulates typical behavior when crawling directories.
	 * {@link org.ccopy.resource.s3.S3Resource#S3Resource(org.ccopy.resource.s3.S3Resource, java.lang.String)}
	 */
	@Test
	public void testS3Resource_Crawling() {
		try {
			URI bucketRoot = new S3Bucket("ccopy").toURI();
			// construct the root for a bucket
			Resource root = new S3Resource(bucketRoot);
			// check that it exists and connect implicitly
			if (!root.isRoot())
				throw new Exception("wrong root resource");
			// from here every resource "knows" already that it exists
			// start crawling
			crawlDown(root);
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		}	
	}

	/**
	 * Tests simple manipulation of a S3Resource
	 * {@link org.ccopy.resource.s3.S3Resource#S3Resource(org.ccopy.resource.s3.S3Resource, java.lang.String)}
	 */
	@Test
	public void testS3Resource_Modification() {
		try {
			URI bucketRoot = new S3Bucket("ccopy").toURI();
			// first create the resource you believe it should exist
			Resource root = new S3Resource(bucketRoot);
			Resource res = root.getChildResource("test.txt");
			// check existence and populate implicit with metadata
			if (!res.exists())
				throw new Exception();
			// now do something with the resource
			if (res.isFile())
				assertEquals("text/plain",res.getContentType().toString());
			// now change the resource and persist changes
			if (res.supportsSetLastModified())
				res.setLastModificationTime(System.currentTimeMillis());
			String timeStamp = String.valueOf(System.currentTimeMillis());
			res.addMetadata("key", timeStamp)
			   .persistChanges();
			assertEquals(timeStamp, res.get("key"));
			// cool
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		}
	}
	@Test
	public void testS3Resource_Modification2() {
		try {
			// first create the resource you believe it should exist
			Resource res = S3Resource.getResource("ccopy","test.txt");
			// check existence and populate implicit with metadata
			if (!res.exists())
				throw new Exception();
			// now do something with the resource
			if (res.isFile())
				assertEquals("text/plain",res.getContentType().toString());
			// now change the resource and persist changes
			if (res.supportsSetLastModified())
				res.setLastModificationTime(System.currentTimeMillis());
			String timeStamp = String.valueOf(System.currentTimeMillis());
			res.addMetadata("key", timeStamp)
			   .persistChanges();
			assertEquals(timeStamp, res.get("key"));
			// cool
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		}
	}
	@Test
	public void testS3Resource_Extended() {
		try {
			// construct the root for a bucket
			Resource root = S3Resource.getResource("ccopy","tmp/");
			/*
			 * now add a new child file resource and write something
			 */
			// prepare some content
			String timeStamp = String.valueOf(System.currentTimeMillis());
			String content = "That is some content with timestamp: " + timeStamp;
			long length = content.getBytes(Charset.forName("UTF8")).length;
			InputStream in = StringUtil.stringToStream(content, "UTF-8");
			// write content to resource and create resource on the fly with the metadata
			Resource child = root.getChildResource("newChild.txt")
			                     .addMetadata("key", timeStamp)
//			                     .setContentType(MimeType.fromFileName("newChild.txt"))
			                     .setLastModificationTime(System.currentTimeMillis() + 1000)
			                     .write(length, in);
			in.close();
			/*
			 * now add a second new empty child file resource
			 */
			Resource child2 = root.getChildResource("newChild2")
								  .addMetadata("key", "value")
								  .setContentType(MimeType.fromFileName("newChild.txt"))
								  .createNewFileResource();
			/*
			 * now add a new child directory resource
			 */
			Resource child3 = root.getChildResource("newChild3")
								  .addMetadata("key", "value")
								  .createDirectoryResource();
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		}
	}
	@Test
	public void testS3Resource_CrawlAndDelete() {
		try {
			Resource root = S3Resource.getResource("ccopy","tmp/");
			// from here every resource "knows" already that it exists
			// start crawling
			crawlDownAndDelete(root);
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		}
		
	}
	public void crawlDown(Resource root) {
		try {
			// now get the child's resources
			Collection<Resource> childs = root.listResources();
			// do some checks on the resource, this doesn't trigger requests to the service because
			// status of the resource is already known (fetched from the service by listResources())
			for (Resource child : childs) {
				if (child.isDirectory()) {
					System.out.println("----dir: " + child.getName());
					crawlDown(child);
				}
				else
					System.out.println("----file: " + child.getName());
			}
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		}
	}

	private void crawlDownAndDelete(Resource root) {
		try {
			// now get the child's resources
			Collection<Resource> childs = root.listResources();
			// do some checks on the resource, this doesn't trigger requests to the service because
			// status of the resource is already known (fetched from the service by listResources())
			for (Resource child : childs) {
				if (child.isDirectory()) {
					System.out.println("----dir: " + child.getName());
					crawlDown(child);
				} else
					System.out.println("----file: " + child.getName());
				child.delete();
			}
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		}
	}
}
