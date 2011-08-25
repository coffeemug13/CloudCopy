/**
 * Bucket in the cloud
 * Synchronisation tool for file into buckets in the cloud like
 * Amazon S3.
 * 
 * @author coffeemug13
 */
package org.ccopy;

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
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
import org.ccopy.resource.Resource;
import org.ccopy.resource.ResourceAuthenticator;
import org.ccopy.resource.file.FileResource;
import org.ccopy.resource.s3.S3Resource;
import org.ccopy.resource.util.LoggingDateFormatter;
import org.ccopy.util.HttpMethod;

/**
 * This is the main programm for cloudcopy
 * 
 * The workflow is as follow: - check and extract the cmd arguments - decide
 * whether to handle a single file or a file tree - case: single file - get the
 * checksum of the file - stream the file to the new destination - case: file
 * tree - process first all directories beginning in the root
 * 
 * @author coffeemug13
 * 
 */
public class CCopy {
	/**
	 * @param args
	 * @throws URISyntaxException 
	 */
	public static void main(String[] args) throws URISyntaxException {
		long startTime = System.currentTimeMillis();
		System.out.println("Starting cloudcopy...");
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
		 * Enable Logging
		 */
//		// set the Log Format and Level
//		Logger logger = Logger.getLogger("org.ccopy");
//		ConsoleHandler ch = new ConsoleHandler();
//		ch.setLevel(Level.FINEST);
//		ch.setFormatter(new LoggingDateFormatter());
//		// add to logger
//		logger.addHandler(ch);
//		logger.setLevel(Level.FINEST);
		/**
		 * Local Variables
		 */
		Resource sourceUrl = null;
		Resource targetUrl = null;
		/**
		 * CMD handling - process the cmd line arguments and assign the proper
		 * variables
		 */
		if (args.length == 0) {
			System.err
					.println("Usage for CloudBucket:\njava cbucket [action] -l=<url> -r=<url>");
			System.exit(1);
		}
		int argsPosition = 0;
		if (args.length > 0) {
			for (String s : args) {
				switch (argsPosition) {
				case 0: // this are the options
					// do nothing at the moment
					argsPosition++;
					break;
				case 1: // source URL
					sourceUrl = new FileResource(new URI(s));
					argsPosition++;
					break;
				case 2: // target URL
//					targetUrl = new FileResource(new URI(s));
					targetUrl = new S3Resource(new URI(s));
					argsPosition++;
					break;
				default:
					System.err.println("wrong arguments");
					System.exit(1);
				}
			}
		}
		System.out.println("source: " + sourceUrl);
		System.out.println("target: " + targetUrl);
		System.out.println("---");
		/**
		 * Action handling
		 */
		try {
			if (sourceUrl != null) {
				if (sourceUrl.exists()) {
					CopyDirectory Crawler = new CopyDirectory(sourceUrl, targetUrl);
					Crawler.start();
					/**
					 * Print the Statistik
					 */
					System.out.println("---\nStatistik:\n");
					System.out.println("Max. Level:"
							+ Crawler.getCountMaxLevel());
					System.out.println("Count objects:"
							+ Crawler.getCountAsset());
					System.out.println("Count dirs:" + Crawler.getCountDir());
					long endTime = System.currentTimeMillis();
					System.out.println("Duration: " + (endTime-startTime) + "ms");
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

}
