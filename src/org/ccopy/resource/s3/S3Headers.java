/**
 * 
 */
package org.ccopy.resource.s3;

/**
 * @author coffeemug13
 *
 */

public class S3Headers {
	/*
	 * Standard HTTP Headers
	 */
	public static final String CACHE_CONTROL = "Cache-Control";
	public static final String CONTENT_ENCODING = "Content-Encoding";
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONTENT_MD5 = "Content-MD5";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String DATE = "Date";
	public static final String ETAG = "ETag";
	public static final String LAST_MODIFIED = "Last-Modified";
	public static final String SERVER = "Server";
	/*
	 * Prefix for custom meta attributes
	 */
	public static final String X_AMZ_META = "x-amz-meta-";
	/*
     * Amazon S3 HTTP Headers
     */
	public static final String AMAZON_PREFIX = "x-amz-";
	public static final String X_AMZ_REQUEST_ID = "x-amz-request-id";
	public static final String X_AMZ_VERSION_ID = "x-amz-version-id";
	public static final String X_AMZ_COPY_SOURCE = "x-amz-copy-source";
	public static final String X_AMZ_COPY_SOURCE_VERSION_ID = "x-amz-copy-source-version-id";
	public static final String X_AMZ_METADATA_DIRECTIVE = "x-amz-metadata-directive";
	public static final String X_AMZ_DELETE_MARKER = "x-amz-delete-marker";
	
	
}
