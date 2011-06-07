/**
 * 
 */
package org.ccopy.resource.s3;

/**
 * @author mholakovsky
 *
 */
public class S3Exception extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3597526141149304571L;

	/**
	 * 
	 */
	public S3Exception() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public S3Exception(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public S3Exception(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public S3Exception(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
