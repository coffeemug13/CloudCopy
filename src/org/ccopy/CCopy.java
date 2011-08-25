/**
 * Bucket in the cloud Synchronisation tool for file into buckets in the cloud like Amazon S3.
 * 
 * @author coffeemug13
 */
package org.ccopy;

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.Proxy;

import javax.sound.midi.SysexMessage;

import org.ccopy.operation.CopyDirectory;
import org.ccopy.operation.ResourceOperation;
import org.ccopy.resource.Resource;
import org.ccopy.resource.ResourceAuthenticator;
import org.ccopy.resource.file.FileResource;
import org.ccopy.resource.s3.S3Resource;
import org.ccopy.resource.util.LoggingDateFormatter;
import org.ccopy.util.HttpMethod;

/**
 * This is the main programm for cloudcopy. 
 * The workflow is as follow: 
 * - check and extract the cmd arguments 
 * - decide whether to handle a single file or a file tree 
 * - case: single file 
 * - get the checksum of the file 
 * - stream the file to the new destination 
 * - case: file tree - process first all directories beginning in the root
 * 
 * @author coffeemug13
 */
public class CCopy {
	static String manPage = "Usage for CloudBucket:\n" + "ccopy [action] [options] <source url> <target url>\n\n"
			+ "Action:\n"
			+ "copy - copy source to target. Existing files in target get overwritten\n\n"
			+ "Options:\n"
			+ "-v - verbose output\n\n"
			+ "Example URLs:"
			+ "Filesystem: 'C:\\folder\\my_file.txt' or 'file:///folder/my_file.txt'\n"
			+ "Amazon S3: 'https://<bucketname>.s3.amazonaws.com/folder/my_file.txt'\n";

	/**
	 * @param args
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) throws URISyntaxException {
		long startTime = System.currentTimeMillis();
		/**
		 * Setup the default Authenticator for http requests
		 */
		URL main = CCopy.class.getResource("pwd.crypt");
		try {
			Authenticator.setDefault(new ResourceAuthenticator(new File(main.toURI())));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/**
		 * Setup the proxy if needed
		 */
		SocketAddress addr = new InetSocketAddress("proxy.sozvers.at", 8080);
		HttpMethod.proxy = new Proxy(Type.HTTP, addr);
		/**
		 * Local Variables
		 */
		Resource sourceUrl = null;
		Resource targetUrl = null;
		ResourceOperation operation = null;
		/**
		 * CMD handling - process the cmd line arguments and assign the proper variables
		 */
		if (args.length == 0) {
			System.err.println(manPage);
			System.exit(1);
		}
		try {
			int argsPosition = 0;
			if (args.length > 0) {
				for (String arg : args) {
					switch (argsPosition) {
					case 0: // Define the mode of operation 
						operation = new CopyDirectory();
						argsPosition++;
						break;
					case 1: // Enable Logging
						if (arg.equals("-v")) {
						 // set the Log Format and Level
						 Logger logger = Logger.getLogger("org.ccopy");
						 ConsoleHandler ch = new ConsoleHandler();
						 ch.setLevel(Level.FINEST);
						 ch.setFormatter(new LoggingDateFormatter());
						 // add to logger
						 logger.addHandler(ch);
						 logger.setLevel(Level.FINEST);
						}
						 argsPosition++;
						 break;
					case 2: // set the source URL
						sourceUrl = fromURL(arg);
						argsPosition++;
						break;
					case 3: // set the target URL
						targetUrl = fromURL(arg);
						argsPosition++;
						break;
					default:
						System.err.println("wrong arguments");
						System.out.println(manPage);
						System.exit(1);
					}
				}
			}
		} catch (URISyntaxException e) {
			System.err.println("One of the URL you provided was not a valid or supported URL");
			System.out.println(manPage);
			System.exit(1);
		} catch (IllegalArgumentException e) {
			System.err.println("One of the URL you provided was not a valid or supported URL");
			System.out.println(manPage);
			System.exit(1);
		}
		System.out.println("Starting cloudcopy:");
		System.out.println("source: " + sourceUrl);
		System.out.println("target: " + targetUrl);
		System.out.println("---");
		/**
		 * Action handling
		 */
		try {
			if (sourceUrl != null) {
				if (sourceUrl.exists()) {
					operation.start(sourceUrl, targetUrl);
					/**
					 * Print the Statistic
					 */
					System.out.println("---\nStatistik:\n");
					System.out.println("Number of Directories detected:" + operation.getCountDir());					
					System.out.println("Number of Files detected:" + operation.getCountObject());
					System.out.println("Number of Files copied:" + operation.getCountObjectTransfered());
					System.out.println("Max. Level:" + operation.getCountMaxLevel());
					long endTime = System.currentTimeMillis();
					System.out.println("Duration: " + (endTime - startTime) + "ms");
				} else {
					System.out.println(sourceUrl + ": doesn't exist");
				}
			} else {
				System.out.println("no localRoot defined");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Parse the URL string and return a valid Resource
	 * 
	 * @param url
	 * @return
	 * @throws URISyntaxException
	 */
	private static Resource fromURL(String url) throws URISyntaxException {
		URI uri = new URI(url);
		if (null != uri.getScheme())
			if (uri.getScheme().equals("file")) {
				return new FileResource(uri);
			} else if (uri.getScheme().startsWith("http"))
				if (uri.getHost().contains("s3.amazonaws.com"))
					return new S3Resource(uri);
			// TODO V3 return HttpResource
		throw new IllegalArgumentException("The URL '" + url.toLowerCase() + "' is not supported");
	}

}
