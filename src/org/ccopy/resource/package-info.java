/**
 * This package provides the base classes for handling Resources. The design of this 
 * classes is derived from the way Java works with Files and Streams and should therefore be very handy
 * for developers.
 * <p> 
 * First there is an abstract representation of a {@link Resource}. This is similar to the 
 * class {@link java.io.File}. You can perform certain atomic operations on the {@code Resource}, 
 * e.g. delete and rename, and also retrieve information about the {@code Resource} itself, 
 * e.g. the last modification time or whether it is a directory.
 * <p>
 * The {@link org.ccopy.resource.ResourceLocator} locates a {@code org.ccopy.resource.Resource} and 
 * behaves like the {@link java.net.URL} class. The only difference is that it ensures proper otctet
 * encoding and provides less methods.
 * <p>
 * To read or write a {@code Resource} content you use the {@link ResourceInputStream} and 
 * {@link ResourceOutputStream}. This is similar to the usage of {@link java.io.FileInputStream}
 * or {@link java.io.FileOutputStream}. Additionally there are wrapper to cipher a Stream.
 * <p>
 * Credentials are handled by the {@link org.ccopy.resource.ResourceAuthenticator} which extends the 
 * Java {@link java.net.Authenticator} and follows therefore the Java standard. For this reason there 
 * is no need for a factory or connection client for {@link javax.annotation.Resources}. 
 */
package org.ccopy.resource;

