/**
 * 
 */
package org.ccopy.resource.s3;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.ccopy.resource.util.StringUtil;
import org.junit.Test;

/**
 * @author mholakovsky
 */
public class TestS3BucketListObjectRequestParser {

	/**
	 * Test method for
	 * {@link org.ccopy.resource.s3.S3BucketListObjectsRequestParser#S3BucketListObjectsRequestParser(java.io.InputStream)}
	 * .
	 */
	@Test
	public void testS3ObjectCopyRequestParser() {
		try {
			InputStream in;
			S3BucketListObjectsRequestParser parse;
			in = new FileInputStream(new File(this.getClass()
					.getResource("bucketListObjectsResponse.xml").toURI()));
			parse = new S3BucketListObjectsRequestParser(in);
			for (S3Object s3 : parse.list) {
				System.out.println(s3.uri.toString());
				// check that both values are correct set
				if (s3.uri.toString().equals("https://ccopy.s3.amazonaws.com/test.txt")) {
					assertEquals(1311571146000L, s3.lastModified);
					assertEquals("d33dfa987962515b9efa63489bdcf8e0", s3.eTag);
					assertEquals(56, s3.size);
				} else if (s3.uri.toString().equals(
						"https://ccopy.s3.amazonaws.com/test/file%20with+andÜ.txt")) {
					assertEquals(1310382669000L, s3.lastModified);
					assertEquals("d33dfa987962515b9efa63489bdcf8e0", s3.eTag);
					assertEquals(56, s3.size);
				} else
					fail("wrong parsing");
			}
			in = new FileInputStream(new File(this.getClass()
					.getResource("bucketListObjectsResponse2.xml").toURI()));
			parse = new S3BucketListObjectsRequestParser(in);
			for (S3Object s3 : parse.list) {
				System.out.println(s3.uri.toString());
					// check that both values are correct set
					if (s3.uri.toString().equals("https://ccopy.s3.amazonaws.com/test.txt")) {
						assertEquals(1311571146000L, s3.lastModified);
						assertEquals("d33dfa987962515b9efa63489bdcf8e0", s3.eTag);
						assertEquals(56, s3.size);
					} else if (s3.uri.toString().equals(
							"https://ccopy.s3.amazonaws.com/test/")) {
						assertEquals(0L, s3.lastModified);
						assertEquals(null, s3.eTag);
						assertEquals(0L, s3.size);
					} else
						fail("wrong parsing");
			}
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		}
	}

}
