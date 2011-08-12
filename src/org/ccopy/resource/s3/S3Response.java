/**
 * 
 */
package org.ccopy.resource.s3;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ccopy.resource.util.StringUtil;

/**
 * @author mholakovsky
 */
public class S3Response {
	
	protected Map<String, List<String>> responseHeader;
//	/**
//	 * x-amz-version-id if in header
//	 */
//	private String versionId = null;
//	/**
//	 * last modification of the object
//	 */
//	private long lastModified = -1L;
//	/**
//	 * ETag of the object
//	 */
//	private String eTag = null;
//	/**
//	 * x-amz-copy-source-version-id:
//	 */
//	private String xAmzCopySourceVersionId = null;
//	/**
//	 * x-amz-id-2
//	 */
//	private String xAmzId2 = null;
//	/**
//	 * x-amz-delete-marker
//	 */
//	private boolean xAmzDeleteMarker = false;
//	/**
//	 * x-amz-request-id
//	 */
//	private String xAmzRequestId = null;
	private int returnCode = 0;
	private URI uri;


	/**
	 * Constructs the class
	 * @param uri 
	 * 
	 * @param responseHeader must not be null
	 */
	public S3Response(URI uri, int returnCode, Map<String, List<String>> responseHeader) {
		if ((null==uri)||(0==returnCode)||(null==responseHeader))
			throw new NullPointerException();
		this.uri = uri;
		this.returnCode = returnCode;
		this.responseHeader = responseHeader;
	}
	/**
	 * You may manually set this attribute and overwrite the original response Header
	 * @return the eTag
	 */
	public String getETag() {
		return StringUtil.join(responseHeader.get(S3Headers.ETAG.toString()));
	}

	/**
	 * You may manually set this attribute
	 * @return the lastModified
	 */
	public long getLastModified() {
		if (responseHeader.containsKey(S3Headers.LAST_MODIFIED))
			return Long.parseLong(StringUtil.join(responseHeader.get(S3Headers.LAST_MODIFIED)));
		else 
			return 0L;
	}

	/**
	 * @return the returnCode
	 */
	public int getReturnCode() {
		return returnCode;
	}

	/**
	 * @return the uri
	 */
	public URI getUri() {
		return uri;
	}
	/**
	 * @return the versionId
	 */
	public String getVersionId() {
			return StringUtil.join(responseHeader.get(S3Headers.X_AMZ_VERSION_ID.toString()));
	}

	/**
	 * @return the xAmzCopySourceVersionId
	 */
	public String getXAmzCopySourceVersionId() {
		return StringUtil.join(responseHeader.get(S3Headers.X_AMZ_COPY_SOURCE_VERSION_ID.toString()));
	}
	/**
	 * Every S3 Response containts this attribute
	 * @return the xAmzId2
	 */
	public String getXAmzId2() {
		return StringUtil.join(responseHeader.get("x-amz-id-2"));
	}
	/**
	 * Every S3 Response containts this attribute
	 * @return the xAmzRequestId
	 */
	public String getXAmzRequestId() {
		return StringUtil.join(responseHeader.get(S3Headers.X_AMZ_REQUEST_ID.toString()));
	}

	/**
	 * @return the xAmzDeleteMarker
	 */
	public boolean hasXAmzDeleteMarker() {
		return responseHeader.containsKey(S3Headers.X_AMZ_DELETE_MARKER);
	}
	private void makeModifiable()  {
		// make array modifiable if not already
		if (this.responseHeader.getClass().getName().contains("Unmodifiable")) {
			this.responseHeader = new HashMap<String, List<String>>(this.responseHeader);
		}
	}
	public void setETag(String tag) {
		this.makeModifiable();
		responseHeader.put(S3Headers.ETAG.toString(), Arrays.asList(tag));
	}
	public void setLastModified(long timestamp){
		this.makeModifiable();
		responseHeader.put(S3Headers.LAST_MODIFIED.toString(), Arrays.asList(String.valueOf(timestamp)));
	}
	public String toString() {
		return getClass().getName() + "@" + returnCode;
	}
	public void updateByList(Map<String, String> meta) {
		if ((null != meta) && (meta.size()>0)) {
			makeModifiable();
			for(Entry<String, String> entry : meta.entrySet()) {
				if (null!=entry.getKey()){
					if (!this.responseHeader.containsKey(entry.getKey())) {
						this.responseHeader.put(entry.getKey(), Arrays.asList(entry.getValue()));
					}
				}
			}
		}
			
		
	}
}
