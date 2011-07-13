/**
 * 
 */
package org.ccopy.util;

/**
 * Signals that an HTTP error code of some sort has been returned. 
 * This class is the general class of exceptions produced by failed or
 * interrupted HTTP operations.
 * 
 * @author mholakovsky
 *
 */
public class HttpException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2716361682080872723L;
	
	public HttpException(String errorCode, String errorMessage, String detailedMessage) {
		super(errorCode + " - " + errorMessage + "; " + detailedMessage);
	}

}
