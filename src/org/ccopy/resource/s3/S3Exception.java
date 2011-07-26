/**
 * 
 */
package org.ccopy.resource.s3;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * This class indicates a error from the Amazon Simple Storage Service. You can retrieve the
 * errorCode and detailed error message if available
 * 
 * @author coffeemug13
 */
public class S3Exception extends IOException {
	/**
	 * 301 PermanentRedirect - The bucket you are attempting to access must be addressed using the
	 * specified endpoint. Please send all future requests to this endpoint.
	 */
	public static final int MOVED_PERMANENTLY = 301;
	/**
	 * 307 TemporaryRedirect - You are being redirected to a new location
	 */
	public static final int MOVED_TEMPORARILY = 307;
	/**
	 * 400 Bad Request, e.g. BadDigest, EntityTooSmall, IncompleteBody etc.
	 */
	public static final int BAD_REQUEST = 400;
	/**
	 * 403 Access Denied e.g. because of account problem, wrong credentials or request signature
	 * doesn't match
	 */
	public static final int FORBIDDEN = 403;
	/**
	 * 404 NotFound, e.g. the bucket, an object, a specific version or a given multipart upload
	 */
	public static final int NOT_FOUND = 404;
	/**
	 * 405 The specified method is not allowed against this resource.
	 */
	public static final int METHOD_NOT_ALLOWED = 405;
	/**
	 * 409 Conflict, e.g. you delete a non-empty bucket, you create a bucket you already own
	 */
	public static final int CONFLICT = 409;
	/**
	 * 411 You must provide the Content-Length HTTP header.
	 */
	public static final int LENGTH_REQUIRED = 411;
	/**
	 * 412 At least one of the preconditions you specified did not hold.
	 */
	public static final int PRECONDITION_FAILED = 412;
	/**
	 * 416 The requested range cannot be satisfied.
	 */
	public static final int REQUESTED_RANGE_NOT_SATISFIED = 416;
	/**
	 * 500 We encountered an internal error. Please try again.
	 */
	public static final int INTERNAL_SERVER_ERROR = 500;
	/**
	 * 501 A header you provided implies functionality that is not implemented.
	 */
	public static final int NOT_IMPLEMENTED = 501;
	/**
	 * 503 Please reduce your request rate.
	 */
	public static final int SERVICE_UNAVAILABLE = 503;

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
	 *            the HTTP error codes and MUST NOT be null!
	 * @param errorMessage
	 * @param detailedErrorMessage
	 *            the detailed error message from S3
	 */
	public S3Exception(int errorCode, String errorMessage, String detailedErrorMessage) {
		super(detailedErrorMessage);
		this.errorCode = errorCode;
		Logger logger = Logger.getLogger("org.ccopy");
		logger.warning(this.getMessage());
	}

	/**
	 * Return the errorCode for this Exception
	 * 
	 * @return
	 */
	public int getErrorCode() {
		return errorCode;
	}

//	/**
//	 * Parse the S3 error code to a meaningful message
//	 * 
//	 * @param code
//	 * @return
//	 */
//	private static String parseReason(int code) {
//		switch (code) {
//		case MOVED_PERMANENTLY:
//			return "PermanentRedirect - The bucket you are attempting to access must be addressed using the specified endpoint. Please send all future requests to this endpoint.";
//		case MOVED_TEMPORARILY:
//			return "TemporaryRedirect - You are being redirected to a new location";
//		case BAD_REQUEST:
//			return "Bad Request, e.g. BadDigest, EntityTooSmall, IncompleteBody etc.";
//		case FORBIDDEN:
//			return "Access Denied e.g. because of account problem, wrong credentials or request signature doesn't match";
//		case NOT_FOUND:
//			try {
//				return S3Exception.class.getField("NOT_FOUND").getAnnotation(ErrorDescription.class).value();
//			} catch (SecurityException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (NoSuchFieldException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}//"NotFound, e.g. the bucket, an object, a specific version or a given multipart upload";
//		case METHOD_NOT_ALLOWED:
//			return "The specified method is not allowed against this resource.";
//		case CONFLICT:
//			return "Conflict, e.g. you delete a non-empty bucket, you create a bucket you already own";
//		case LENGTH_REQUIRED:
//			return "You must provide the Content-Length HTTP header.";
//		case PRECONDITION_FAILED:
//			return "At least one of the preconditions you specified did not hold.";
//		case REQUESTED_RANGE_NOT_SATISFIED:
//			return "The requested range cannot be satisfied.";
//		case INTERNAL_SERVER_ERROR:
//			return "We encountered an internal error. Please try again.";
//		case NOT_IMPLEMENTED:
//			return "A header you provided implies functionality that is not implemented.";
//		case SERVICE_UNAVAILABLE:
//			return "Please reduce your request rate.";
//		default:
//			return "unkown reason";
//		}
//	}

	/**
	 * Returns a short description of this S3Exception. The error code get's parsed to a more
	 * descriptive description
	 */
	@Override
	public String toString() {
		return getClass().getName() + ": '" + errorCode + "; " + getMessage();
	}
}
