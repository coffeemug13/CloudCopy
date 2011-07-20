/**
 * 
 */
package org.ccopy.resource.s3;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class indicates a error from the Amazon Simple Storage Service. You can retrieve the
 * errorCode and detailed error message if available
 * 
 * @author coffeemug13
 */
public class S3Exception extends IOException {
	/**
	 * 301	PermanentRedirect - The bucket you are attempting to access must be addressed using the specified endpoint. Please send all future requests to this endpoint.
	 */
	/**
	 * 307	TemporaryRedirect - You are being redirected to a new location
	 */
	/**
	 * 400	Bad Request, e.g. BadDigest, EntityTooSmall, IncompleteBody etc.
	 */
	/**
	 * 403	Access Denied e.g. because of account problem, wrong credentials or request signature doesn't match
	 */
	/**
	 * 404	NotFound, e.g. the bucket, an object, a specific version or a given multipart upload
	 */
	/**
	 * 405	The specified method is not allowed against this resource.
	 */
	/**
	 * 409	Conflict, e.g. you delete a non-empty bucket, you create a bucket you already own
	 */
	/**
	 * 411	You must provide the Content-Length HTTP header.
	 */
	/**
	 * 412	At least one of the preconditions you specified did not hold.
	 */
	/**
	 * 416	The requested range cannot be satisfied.
	 */
	/**
	 * 500	We encountered an internal error. Please try again.
	 */
	/**
	 * 501	A header you provided implies functionality that is not implemented.
	 */
	/**
	 * 503	Please reduce your request rate.
	 */


	/**
	 * Constructs a new exception with <code>null</code> as its detail message.
	 */
	private static final long serialVersionUID = 3597526141149304571L;
	private int errorCode;

	/**
	 * Constructs a new exception with the errorCode and errorMessage from the S3 response and
	 * optional the content from the error response stream, which details the reason for failing.
	 * 
	 * @param errorCode
	 *        the HTTP error codes and MUST NOT be null!
	 * @param errorMessage
	 * @param detailedErrorMessage
	 *        the detailed error message from S3
	 */
	public S3Exception(int errorCode, String errorMessage, String detailedErrorMessage) {
		super(errorCode + " - " + parseReason(errorCode) + "; " + detailedErrorMessage);
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

	private static String parseReason(int code) {
		switch (code) {
		case 301:
			return "PermanentRedirect - The bucket you are attempting to access must be addressed using the specified endpoint. Please send all future requests to this endpoint.";
		case 307:
			return "TemporaryRedirect - You are being redirected to a new location";
		case 400:
			return "Bad Request, e.g. BadDigest, EntityTooSmall, IncompleteBody etc.";
		case 403:
			return "Access Denied e.g. because of account problem, wrong credentials or request signature doesn't match";
		case 404:
			return "NotFound, e.g. the bucket, an object, a specific version or a given multipart upload";
		case 405:
			return "The specified method is not allowed against this resource.";
		case 409:
			return "Conflict, e.g. you delete a non-empty bucket, you create a bucket you already own";
		case 411:
			return "You must provide the Content-Length HTTP header.";
		case 412:
			return "At least one of the preconditions you specified did not hold.";
		case 416:
			return "The requested range cannot be satisfied.";
		case 500:
			return "We encountered an internal error. Please try again.";
		case 501:
			return "A header you provided implies functionality that is not implemented.";
		case 503:
			return "Please reduce your request rate.";
		default:
			return "unkown reason";
		}
	}

}
