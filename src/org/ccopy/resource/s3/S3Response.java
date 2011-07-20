/**
 * 
 */
package org.ccopy.resource.s3;

import java.util.List;
import java.util.Map;

/**
 * @author mholakovsky
 */
public class S3Response {
	protected int returnCode = 0;
	/**
	 * x-amz-version-id if in header
	 */
	protected String versionId = null;
	/**
	 * last modification of the object
	 */
	protected long lastModified = -1L;
	/**
	 * ETag of the object
	 */
	protected String eTag = null;
	/**
	 * x-amz-copy-source-version-id:
	 */
	protected String xAmzCopySourceVersionId = null;
	/**
	 * x-amz-id-2
	 */
	protected String xAmzId2 = null;
	/**
	 * x-amz-delete-marker
	 */
	protected boolean xAmzDeleteMarker = false;
	/**
	 * x-amz-request-id
	 */
	protected String xAmzRequestId = null;

	/**
	 * Constructs the class
	 * 
	 * @param map
	 */
	public S3Response(int returnCode, Map<String, List<String>> map) {
		this.returnCode = returnCode;
		if (null != map) {
			this.xAmzRequestId = map.get(S3Headers.X_AMZ_REQUEST_ID.toString()).toString();
			this.xAmzId2 = map.get("x-amz-id-2").toString();
			this.xAmzDeleteMarker = map.containsKey("x-amz-delete-marker") ? true : false;
			this.eTag = (map.containsKey(S3Headers.ETAG.toString())) ? map.get(S3Headers.ETAG.toString()).toString() : null;
		}
	}

	/**
	 * @return the returnCode
	 */
	public int getReturnCode() {
		return returnCode;
	}

	/**
	 * @return the versionId
	 */
	public String getVersionId() {
		return versionId;
	}

	/**
	 * @return the lastModified
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * @return the eTag
	 */
	public String geteTag() {
		return eTag;
	}

	/**
	 * @return the xAmzCopySourceVersionId
	 */
	public String getxAmzCopySourceVersionId() {
		return xAmzCopySourceVersionId;
	}

	/**
	 * @return the xAmzId2
	 */
	public String getxAmzId2() {
		return xAmzId2;
	}

	/**
	 * @return the xAmzDeleteMarker
	 */
	public boolean isxAmzDeleteMarker() {
		return xAmzDeleteMarker;
	}

	/**
	 * @return the xAmzRequestId
	 */
	public String getxAmzRequestId() {
		return xAmzRequestId;
	}
	public String toString() {
		return getClass().getName() + "@" + returnCode;
	}
}
