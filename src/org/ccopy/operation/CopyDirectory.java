/**
 *
 */

package org.ccopy.operation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;

import javax.imageio.IIOException;

import org.ccopy.resource.Resource;

/**
 * @author mholakovsky
 * 
 */
public class CopyDirectory {
	private Resource sourceFile = null, targetResource = null;
	// private int maxLevel = 100;
	private int countMaxLevel = 0;
	private int countDir = 0;
	private int countResouce = 0;
	private int countException = 0;

	public CopyDirectory(Resource source, Resource target) throws FileNotFoundException {
		// if (source.exists() && source.canRead()&&source.isDirectory()) {
		sourceFile = source;
		targetResource = target;
		// } else
		// throw new FileNotFoundException();
	}

	public void start() throws Exception {
		copy(sourceFile, targetResource, 0);
	}

	public void copy(Resource sourceDirectory, Resource targetDirectory, int level)
			throws Exception {
		// if (level > maxLevel)
		// return; // limit the crawler to a certain depth
		if (level >= countMaxLevel)
			countMaxLevel++; // count the deepest directory level
		System.out.println(sourceDirectory + File.separator);
		// System.out.println(targetDirectory + File.separator);
		Resource[] list = sourceDirectory.listResources(); // get the files of the resource directory
		if (!targetDirectory.isDirectory()) {
			if (!targetDirectory.isFile()) {
				targetDirectory.mkdir();
			} else
				throw new IIOException("Can't create target directory: "
						+ targetDirectory);
		} // else everything is fine
		if (list != null) {
			for (Resource sourceResource : list) {
				// create the target Resource for later use
				Resource targetResource = new FileResource(targetDirectory,sourceResource.getName());
				if (sourceResource.isDirectory()) {
					/**
					 * Copy the Directory
					 */
					countDir++;
					copy(sourceResource, targetResource, level + 1);
				} else {
					/**
					 * Copy the Resource
					 */
					if (!targetResource.isFile()) {
						countResouce++;
						// new
						System.out.print(sourceResource + " - copy");
						FileInputStream in = new FileInputStream(sourceResource);
						FileOutputStream out = new FileOutputStream(
								targetResource);
						byte[] c = new byte[65536]; // with increasing value speed goes up 
						int read = 0;
						// Read (and print) till end of file.
						while ((read = in.read(c)) != -1) {
							out.write(c, 0, read);
							System.out.print(".");
						}
						System.out.println();
					}
				}
			}
		}
	}

	/**
	 * @return the countAsset
	 */
	public int getCountAsset() {
		return countResouce;
	}

	/**
	 * @return the countDir
	 */
	public int getCountDir() {
		return countDir;
	}

	/**
	 * @return the countException
	 */
	public int getCountException() {
		return countException;
	}

	/**
	 * @return the countLevel
	 */
	public int getCountMaxLevel() {
		return countMaxLevel;
	}
}
