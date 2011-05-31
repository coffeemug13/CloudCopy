/**
 * 
 */
package org.ccopy.resource;

/**
 * @author mholakovsky
 *
 */
public class ResourceNotFoundException extends ResourceException {

	/**
     * Constructs a <code>FileNotFoundException</code> with
     * <code>null</code> as its error detail message.
     */
    public ResourceNotFoundException() {
	super();
    }

    /**
     * Constructs a <code>FileNotFoundException</code> with the
     * specified detail message. The string <code>s</code> can be
     * retrieved later by the
     * <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     *
     * @param   s   the detail message.
     */
    public ResourceNotFoundException(String s) {
	super(s);
    }

}
