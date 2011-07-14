/**
 * 
 */
package org.ccopy.resource.s3;

/**
 * This class indicates a error from the Amazon Simple Storage Service. You can
 * retrieve the errorCode and detailed error message if available
 * 
 * @author coffeemug13
 * 
 */
public class S3Exception extends Exception {

	/**
	 * Constructs a new exception with <code>null</code> as its detail message.
	 */
	private static final long serialVersionUID = 3597526141149304571L;
	private int errorCode;

	/**
	 * Constructs a new exception with the errorCode and errorMessage from the
	 * S3 response and optional the content from the error response stream,
	 * which details the reason for failing. 
	 * 
	 * @param errorCode the HTTP error codes and MUST NOT be null!
	 * @param errorMessage 
	 * @param detailedErrorMessage the detailed error message from S3
	 */
	public S3Exception(int errorCode, String errorMessage, String detailedErrorMessage) {
		super(errorCode + " - " + errorMessage + "; " + detailedErrorMessage);
		this.errorCode = errorCode;
	}
	/**
	 * Return the errorCode for this Exception
	 * 
	 * @return
	 */
	public int getErrorCode() {
		return errorCode;
	}

}
