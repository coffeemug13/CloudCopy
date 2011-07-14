/**
 * 
 */
package org.ccopy.resource;

/**
 * This exception indicates, that the service behind the resource can't process the request. There
 * are various reason why this can happen, e.g. request was not correct or you don't have the right
 * or problems with the connectivity
 * @author coffeemug13
 *
 */
public class ResourceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2324726616584982057L;
	/**
     * Constructs an {@code IOException} with {@code null}
     * as its error detail message.
     */
    public ResourceException() {
	super();
    }

    /**
     * Constructs an {@code IOException} with the specified detail message.
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     */
    public ResourceException(String message) {
	super(message);
    }

    /**
     * Constructs an {@code IOException} with the specified detail message
     * and cause.
     *
     * <p> Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated into this exception's detail
     * message.
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     *
     * @param cause
     *        The cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A null value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     *
     * @since 1.6
     */
    public ResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an {@code IOException} with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of {@code cause}).
     * This constructor is useful for IO exceptions that are little more
     * than wrappers for other throwables.
     *
     * @param cause
     *        The cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A null value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     *
     * @since 1.6
     */
    public ResourceException(Throwable cause) {
        super(cause);
    }

}
