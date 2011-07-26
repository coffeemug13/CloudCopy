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
 * 
 * The classes are used in the following order
 * <S3Resource> ====> <S3Object, or S3Bucket> =====> <S3Request>
 *
 * One big difference between {@link org.ccopy.resource.Resource} and the S3Object is, that S3 operations return 
 * only a valid response when the operation can be performed, e.g. the S3 object exists when deleting. Otherwise 
 * it will throw an {@link org.ccopy.resource.s3.S3Exception}. The parent {@code Resource} must map this behavior to 
 * to a behavior similar to {@code File}.
 * 
 * Performance optimization or caching to reduce the number of requests to S3 is not part of this package! 
 * This has to be done by Resource
 * TODO this has to be checked later, whether it is true 
 * 
 * @since 0.1
 */
package org.ccopy.resource.s3;