/**
 * Bucket in the cloud
 * Synchronisation tool for file into buckets in the cloud like
 * Amazon S3.
 * 
 * @author coffeemug13
 */
package org.ccopy;

import java.io.File;
import java.net.URL;
import java.util.Date;

import javax.sound.midi.SysexMessage;

import org.ccopy.resource.CopyDirectory;
import org.ccopy.resource.FileResource;
import org.ccopy.resource.Resource;

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
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		System.out.println("Starting cloudcopy...");
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
					sourceUrl = new FileResource(new URL(s));
					argsPosition++;
					break;
				case 2: // target URL
					targetUrl = new File(s);
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
