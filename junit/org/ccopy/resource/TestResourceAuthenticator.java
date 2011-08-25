package org.ccopy.resource;


import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.PasswordAuthentication;

import org.ccopy.resource.util.StringUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestResourceAuthenticator {
	File file = null;

	@Before
	public void setUpBeforeClass() throws Exception {
		this.file = File.createTempFile("cipher", "txt");
	}
	
	@After
	public void closeAfterClass() {
		
	}
	
	@Test
	public void testWritePassword() {
		try {
		ResourceAuthenticator auth = new ResourceAuthenticator(file);
		System.out.println(file);
		// These are fake keys, so don't waste your time :-)
		String key = "ADSVSR23452D3DFHK9BA";
		String value = "fgdrdjiio5689045v890890834mvdfmdl435088L";
		auth.addCredentials(key, value);
		auth.save();
		auth.load();
		assertEquals(key, auth.username);
		assertEquals(value, auth.passwd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(StringUtil.exceptionToString(e));
		}
		
	}
	
}
