/**
 * 
 */
package org.ccopy.resource;

import java.io.OutputStream;

/**
 * @author mholakovsky
 *
 */
public abstract class ResourceOutputStream extends OutputStream{
	protected Resource res;
	
	protected ResourceOutputStream(Resource res) {
		this.res = res;
	}

}
