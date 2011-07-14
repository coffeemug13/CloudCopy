/**
 * 
 */
package org.ccopy.util;

import java.net.Proxy;

/**
 * Enumeration of valid HTTP methods.
 */
public enum HttpMethod {
    GET("GET"),
    PUT("PUT"),
    HEAD("HEAD"),
    DELETE("DELETE");
    
    String method;
    static public Proxy proxy = null;
    HttpMethod(String method) {this.method=method;}
    @Override
	public String toString() {return method;}
}
