package org.ccopy.resource;

import static org.junit.Assert.*;

import org.ccopy.resource.util.StringUtil;
import org.junit.BeforeClass;
import org.junit.Test;

abstract class ResourceTest_InitValues {
	private Resource res = null;
	
	protected ResourceTest_InitValues(Resource res) {
		if (null == res) throw new NullPointerException();
		this.res = res;
	}

	@Test
	public void testGetContentType() {
		assertNull(res.getContentType());
	}

	@Test
	public void testGet() {
		assertNull(res.get(null));
		assertNull(res.get("key"));
	}

	@Test
	public void testContainsKey() {
		assertNull(res.containsKey("key"));
	}

	@Test
	public void testToURI() {
		assertNotNull(res.toURI());
	}

	@Test
	public void testGetMetadata() {
		try {
			assertNull(res.getMetadata());
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		}
	}

	@Test
	public void testGetPath() {
		assertNotNull(res.getPath());
	}

	@Test
	public void testLastModified() {
		try {
			assertEquals(0L, res.lastModified());
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		}
	}

	@Test
	public void testLength() {
		try {
			assertEquals(0L, res.length());
		} catch (Exception e) {
			fail(StringUtil.exceptionToString(e));
		}
	}

	@Test
	public void testGetMD5Hash() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetName() {
		fail("Not yet implemented");
	}

}
