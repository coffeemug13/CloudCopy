/**
 *
 */

package org.ccopy.operation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.IIOException;

import org.ccopy.resource.Resource;
import org.ccopy.resource.file.FileResource;

/**
 * @author coffeemug13
 * 
 */
public class CopyDirectory {
	/**
	 * The size of the byte array, which holds the chunks from the InputStream
	 */
	private Resource sourceFile = null, targetResource = null;
	private int countMaxLevel = 0;
	private int countDir = 0;
	private int countResource = 0;
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
		List<Resource> list = sourceDirectory.listResources(); // get the files of the resource directory
		if (!targetDirectory.isDirectory()) {
			if (!targetDirectory.isFile()) {
				targetDirectory = targetDirectory.createDirectoryResource();
			} else
				throw new IIOException("Can't create target directory: "
						+ targetDirectory);
		} // else everything is fine
		if (list != null) {
			for (Resource source : list) {
				// create the target Resource for later use
				Resource target = targetDirectory.getChildResource(source.getName());
				if (source.isDirectory()) {
					/**
					 * Copy the Directory
					 */
					countDir++;
					copy(source, target, level + 1);
				} else {
					/**
					 * Copy the Resource
					 */
					if (!target.isFile()) {
						countResource++;
						System.out.print(source + " - copy");
						InputStream in = source.getInputStream();
						// if target supports metadata, write some
						if (target.supportsMetadata()) {
							target.setContentType(source.getContentType());
							target.setMetadata(source.getMetadata());							
						}
						// extract the last modified timestamp from source
						long lm;
						final String key = "last-modified";
						if (source.supportsSetLastModified())
							lm = source.lastModified();
						else if (source.containsKey(key))
							lm = Long.valueOf(source.get(key));
						else
							lm = source.lastModified();
						// if target doesn't support to set last modified
						// store original timestamp in metadata
						if (target.supportsSetLastModified())
							target.setLastModificationTime(lm);
						else if (target.supportsMetadata())
							target.addMetadata(key, String.valueOf(lm));
						// now write the metadata 
						target.write(source.length(), in);
					}
				}
			}
		}
	}

	/**
	 * @return the countAsset
	 */
	public int getCountAsset() {
		return countResource;
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
