/**
 * 
 */
package org.ccopy.resource.s3;

/**
 * @author coffeemug13
 *
 *
    public static final String ETAG = "ETag";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String SERVER = "Server";
    
 */
//public enum S3Headers {
//	/*
//     * Standard HTTP Headers
//     */
//	CACHE_CONTROL = "Cache-Control";
//	CONTENT_ENCODING = "Content-Encoding";
//	CONTENT_DISPOSITION = "Content-Disposition";
//	CONTENT_LENGTH = "Content-Length";
//	CONTENT_MD5 = "Content-MD5";
//	CONTENT_TYPE = "Content-Type";
//	DATE = "Date";
//	ETAG = "ETag";
//	LAST_MODIFIED = "Last-Modified";
//	SERVER = "Server";
//	/*
//     * Amazon S3 HTTP Headers
//     */
//	AMAZON_PREFIX = "x-amz-";
//	X_AMZ_REQUEST_ID = "x-amz-request-id";
//	X_AMZ_VERSION_ID = "x-amz-version-id";
//	/**
//	 * Prefix for custom meta attributes
//	 */
//	X_AMZ_META = "x-amz-meta-";
//	UNKNOWN = "";
//	;
//    
//    String header;
//    static public Proxy proxy = null;
//    
//    private S3Headers(String method) {
//    	this.header=method;
//    }
//    @Override
//	public String toString() {return header;}
//    /**
//     * Return korrekt S3Headers konverted from String
//     * @param key
//     * @return the korrekt S3Header
//     */
//	public static S3Headers fromString(String key) {
//		for (S3Headers v : S3Headers.values()) {
//			if (v.toString().equals(key)) return v;
//		}
//		return S3Headers.UNKNOWN;
//	}
//}

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
	public static final String X_AMZ_METADATA_DIRECTIVE = "x-amz-metadata-directive";
	
}
