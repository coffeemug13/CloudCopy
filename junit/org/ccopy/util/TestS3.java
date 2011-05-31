package org.ccopy.util;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.ccopy.s3.S3Request;
import org.junit.Before;
import org.junit.Test;
import org.omg.PortableInterceptor.SUCCESSFUL;

/**
 * 
 */

/**
 * @author mholakovsky
 *
 */
public class TestS3 {
	S3 req;
	
	@Before
	public void setup() {
		req = new S3();
	}

	/**
	 * Test method for {@link org.ccopy.S3.S3Request#sign()}.
	 */
	@Test
	public void testSign() {
		
		try {
			String sign = req.sign("uV3F3YluFJax1cknvbcGwgjvx4QpvB+leU8dUj2o", "GET\n\n\nTue, 27 Mar 2007 19:36:42 +0000\n/johnsmith/photos/puppy.jpg");
			assertEquals(sign, "xXjDGYUmKxnwqr5KXNPGldn5LbA=");
			sign = req.sign("uV3F3YluFJax1cknvbcGwgjvx4QpvB+leU8dUj2o", "PUT\n\nimage/jpeg\nTue, 27 Mar 2007 21:15:45 +0000\n/johnsmith/photos/puppy.jpg");
			assertEquals(sign, "hcicpDDvL9SsO6AkvxqmIWkmOuQ=");
			sign = req.sign("uV3F3YluFJax1cknvbcGwgjvx4QpvB+leU8dUj2o", "PUT\n4gJE4saaMU4BqNR0kLY+lw==\napplication/x-download\nTue, 27 Mar 2007 21:06:08 +0000\nx-amz-acl:public-read\nx-amz-meta-checksumalgorithm:crc32\nx-amz-meta-filechecksum:0x02661779\nx-amz-meta-reviewedby:joe@johnsmith.net,jane@johnsmith.net\n/static.johnsmith.net/db-backup.dat.gz");
			assertEquals(sign, "C0FlOtU8Ylb9KDTpZqYkZPX91iI=");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
