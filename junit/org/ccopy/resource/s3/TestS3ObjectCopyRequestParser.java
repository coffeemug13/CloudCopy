package org.ccopy.resource.s3;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.ccopy.resource.util.StringUtil;
import org.junit.Test;

public class TestS3ObjectCopyRequestParser extends TestS3InitURLs {

	@Test
	public void testS3ObjectCopyRequestParser() {
		try {
			S3Resource obj = new S3Resource(new URI(TEST_URL_FILE));
			InputStream in = new FileInputStream(new File(this.getClass().getResource("objectCopyResponse.xml").toURI()));
			S3ObjectCopyRequestParser parse = new S3ObjectCopyRequestParser(in);
			// check that both values are correct set
			assertEquals(1311173485000L, parse.lastModified);
			assertEquals("d33dfa987962515b9efa63489bdcf8e0",parse.eTag);
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		}
	}

}
