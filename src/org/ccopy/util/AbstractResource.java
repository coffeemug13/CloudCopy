package org.ccopy.util;

import java.net.URI;

/**
 * This class provides a skeletal implementation of the Asset interface,
 * to minimize the effort required to implement this interface.
 * @author mholakovsky
 *
 */
public abstract class AbstractResource implements Resource {
	protected URI uri;
	protected AbstractResource(URI uri) {
		this.uri = uri;
	}
}
