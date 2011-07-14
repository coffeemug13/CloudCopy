/**
 * 
 */
package org.ccopy.resource;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.logging.Logger;

/**
 * @author coffeemug13
 *
 */
public class ResourceAuthenticator extends Authenticator {
	private static Logger logger = Logger.getLogger("org.ccopy");
	private static String username = null;
	private static String passwd = null;
	
	/**
	 * Construct the Authenticator with username & password
	 * @param username
	 * @param passwd
	 */
	public ResourceAuthenticator (String username, String passwd) {
		// TODO implement setting multiple credentials for different hosts
		ResourceAuthenticator.username = username;
    	ResourceAuthenticator.passwd = passwd;
    	logger.fine("using following Credentials for S3 Authentication: " + username + " & " + passwd);
	}
	
    // This method is called when a password-protected URL is accessed
    public PasswordAuthentication getPasswordAuthentication() {
        // Get information about the request
        //logger.fine("using: " + username + " & " + passwd);
        // Return the information
    	// TODO implement checking for host in case we have multiple accounts
        return new PasswordAuthentication(username, passwd.toCharArray());
    }
}
