/**
 * 
 */
package org.ccopy.resource.s3;

import java.net.Proxy;

/**
 * @author mholakovsky
 *
 *
    public static final String ETAG = "ETag";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String SERVER = "Server";
    
 */
public enum S3Headers {
	/*
     * Standard HTTP Headers
     */
	CACHE_CONTROL ("Cache-Control"),
	CONTENT_ENCODING ("Content-Encoding"),
	CONTENT_DISPOSITION ("Content-Disposition"),
	CONTENT_LENGTH ("Content-Length"),
	CONTENT_MD5 ("Content-MD5"),
	CONTENT_TYPE ("Content-Type"),
	DATE ("Date"),
	ETAG ("ETag"),
	LAST_MODIFIED ("Last-Modified"),
	SERVER ("Server"),
	/*
     * Amazon S3 HTTP Headers
     */
	AMAZON_PREFIX ("x-amz-"),
	REQUEST_ID ("x-amz-request-id"),
	UNKNOWN (""),
	;
    
    String header;
    static public Proxy proxy = null;
    S3Headers(String method) {this.header=method;}
    public String toString() {return header;}
	public static S3Headers fromString(String key) {
		try {
			return S3Headers.valueOf(key.toUpperCase().replace("-", "_"));
		} catch (Exception e) {
			return S3Headers.UNKNOWN;
		}
	}
}
