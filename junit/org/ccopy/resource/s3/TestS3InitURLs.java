/**
 * 
 */
package org.ccopy.resource.s3;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * @author mholakovsky
 *
 */
public class TestS3InitURLs {
	/**
	 * TEST_* strings are always available
	 */
	protected static final String SCHEMA = "https";
	protected static final String HOST = "ccopy.s3.amazonaws.com";
	protected static final String URL_S3 = SCHEMA + "://" + HOST;
	protected static final String TMP_ROOT = "/tmp";
	protected static final String TEST_URL_FILE = "https://ccopy.s3.amazonaws.com/test.txt";
	protected static final String TEST_URL_FILE_UNCLEAN = "https://user@ccopy.s3.amazonaws.com/test.txt?with#fragment";
	protected static final String FILENAME = TMP_ROOT + ".txt";
	protected static final String FILENAME_MALFORMED = "/./dir//test.txt";
	protected static final String FILENAME_WITH_UMLAUTE = "/Mit Ümlaut+Sonderzeichen.txt";
	protected static final String FILENAME_WITH_UMLAUTE_ENCODED = "/Mit%20Ümlaut+Sonderzeichen.txt";
	protected static final String FILENAME_WITH_UMLAUTE_ENCODED2 = "/Mit%20%C3%9Cmlaut+Sonderzeichen.txt";
	protected static final String TEST_URL_DIR = "https://ccopy.s3.amazonaws.com/test/";
	protected static final String TMP_URL_WITH_UMLAUT = TMP_ROOT + "/file with+andÜ.txt";
	/**
	 * generic logger
	 */
	protected static Logger logger = Logger.getLogger("org.ccopy");
	public static URI TMP_URI_FILE;
	public static URI TMP_URI_FILE_WITH_PATH_AND_UMLAUT;
	public static URI TMP_URI_FILE_NOT_FOUND;
	
	{
		try {
			TMP_URI_FILE = new URI(SCHEMA,HOST,FILENAME,null);
			TMP_URI_FILE_WITH_PATH_AND_UMLAUT = new URI(SCHEMA,HOST,TMP_ROOT+FILENAME_WITH_UMLAUTE,null);
			TMP_URI_FILE_NOT_FOUND = new URI(SCHEMA,HOST,TMP_ROOT+"/this_does_not_exist.txt",null);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
