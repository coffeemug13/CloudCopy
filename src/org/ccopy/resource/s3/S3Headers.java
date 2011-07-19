/**
 * 
 */
package org.ccopy.resource.s3;

import java.net.Proxy;

/**
 * @author coffeemug13
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
	X_AMZ_REQUEST_ID ("x-amz-request-id"),
	X_AMZ_VERSION_ID ("x-amz-version-id"),
	/**
	 * Prefix for custom meta attributes
	 */
	X_AMZ_META ("x-amz-meta-"),
	UNKNOWN (""),
	;
    
    String header;
    static public Proxy proxy = null;
    
    private S3Headers(String method) {
    	this.header=method;
    }
    @Override
	public String toString() {return header;}
    /**
     * Return korrekt S3Headers konverted from String
     * @param key
     * @return the korrekt S3Header
     */
	public static S3Headers fromString(String key) {
		for (S3Headers v : S3Headers.values()) {
			if (v.toString().equals(key)) return v;
		}
		return S3Headers.UNKNOWN;
	}
}
