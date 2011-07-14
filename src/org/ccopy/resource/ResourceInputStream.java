/**
 * 
 */
package org.ccopy.resource;

import java.io.InputStream;

/**
 * @author mholakovsky
 *
 */
public abstract class ResourceInputStream extends InputStream{
	protected Resource res;
	
	protected ResourceInputStream(Resource res) {
		this.res = res;
	}

}
