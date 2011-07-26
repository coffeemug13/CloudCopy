package org.ccopy.resource.util;

import static org.junit.Assert.*;

import java.net.FileNameMap;
import java.net.URLConnection;

import org.ccopy.TestSetup;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestMimeType {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestSetup.initialSetup();
	}
	@Test
	public void testValidate() {
		String s;
		try {
			String d = MimeType.DEFAULT;
			s = "text/plain";
			assertEquals(s, MimeType.validate(s));
			s = "unkown/mimetype";
			assertEquals(s, MimeType.validate(s));
			s = "Unkown/mimetype";
			assertEquals(s, MimeType.validate(s));
		} catch (Exception e) {
			fail("unexcepcted exception:\n" + e);
		}

	}

	@Test
	public void testFromFileName() {
		String s;
		s = "unkown.extension";
		assertEquals(MimeType.DEFAULT, MimeType.fromFileName(s).toString());
		s = "has_no_extension";
		assertEquals(MimeType.DEFAULT, MimeType.fromFileName(s).toString());
		s = "image.gif";
		assertEquals("image/gif", MimeType.fromFileName(s).toString());
		s = "image.xml";
		assertEquals("application/xml", MimeType.fromFileName(s).toString());
		// only last '.' defines the extension
		s = "name.double.gif";
		assertEquals("image/gif", MimeType.fromFileName(s).toString());
	}
	
	@Test
	public void testGetDefault() {
		assertEquals(MimeType.DEFAULT, MimeType.getDefault().toString());
	}

}
