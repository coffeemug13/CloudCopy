package org.ccopy.resource.s3;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.junit.Test;

public class TestS3ObjectCopyRequestParser extends TestS3InitURLs {

	@Test
	public void testS3ObjectCopyRequestParser() {
		try {
			S3Resource obj = new S3Resource(new URL(TEST_URL_FILE));
			InputStream in = new FileInputStream(new File("objectCopyResponse.xml"));
			S3ObjectCopyRequestParser parse = new S3ObjectCopyRequestParser(in, obj);
			// check that both values are correct set
			assertEquals(obj.lastModified(), 1256765520000L);
			assertEquals(obj.getMD5Hash(),"9b2cf535f27731c974343645a3985328");
		} catch (Exception e) {
			fail("unexpected ecxeption:" + e.toString());
		}
	}

}
