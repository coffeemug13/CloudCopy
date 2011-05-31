/**
 * 
 */
package org.ccopy.resource;

/**
 * @author mholakovsky
 *
 */
public enum ResourceAttributes {
	/**
	 * Identifier for the Resource
	 */
	VERSION_ID,
	/**
	 * MD5 Hash of the resource
	 */
	MD5HASH,
	/**
	 * Creation Date
	 */
	CREATED,
	/**
	 * Modification Date
	 */
	LAST_MODIFIED,
	/**
	 * Size of the resource in Bytes
	 */
	SIZE,
	/**
	 * Mimetype of the resource
	 */
	TYPE,
	IS_READABLE,
	IS_WRITEABLE,
	IS_VISIBLE;

}
