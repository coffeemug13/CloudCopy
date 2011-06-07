/**
 * This package provides the classes to perform Amazon S3 requests, wrapped as resource operations.  
 * 
 * S3 Object operations
 * Operation        Input               			Optional Input              Return
 * GET object       bucket, object					byte_range, versionID		S3Object (Metadata, InputStream)
 * GET acl object	bucket, object					versionID					ACL object??
 * HEAD object		bucket, object					versionID					S3Object (Metadata)
 * PUT object		bucket, object, InputStream+Length	x-amz-meta*				String:"versionID|null"
 * DELETE object	bucket, object					versionID					S3object(x-amz-delete-marker, x-amz-version-id)
 * 
 * S3 Bucket operations
 * GET list objects	bucket							prefix, delimiter, max-keys		List<S3Object>
 *
 * @since 0.1
 */
package org.ccopy.resource.s3;