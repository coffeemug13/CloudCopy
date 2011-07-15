/**
 * 
 */
package org.ccopy.resource.s3;

import java.util.logging.Logger;

/**
 * @author mholakovsky
 *
 */
public class TestS3InitURLs {
	/**
	 * TEST_* strings are always available
	 */
	protected static final String TEST_URL_S3 = "https://ccopy.s3.amazonaws.com/";
	protected static final String TEST_URL_S3_UNCLEAN = "https://ccopy.s3.amazonaws.com";
	protected static final String TEST_URL_FILE = "https://ccopy.s3.amazonaws.com/test.txt";
	protected static final String TEST_URL_FILE_UNCLEAN = "https://user@ccopy.s3.amazonaws.com/test.txt?with#fragment";
	protected static final String TEST_URL_FILE_FILENAME = "test.txt";
	protected static final String TEST_URL_FILE_MALFORMED = "https://ccopy.s3.amazonaws.com/dir//test.txt";
	protected static final String TEST_URL_FILE_WITH_UMLAUTE = "https://ccopy.s3.amazonaws.com/test/Mit Ümlaut+Sonderzeichen.txt";
	protected static final String TEST_URL_FILE_WITH_UMLAUTE_ENCODED = "https://ccopy.s3.amazonaws.com%2Ftest%2FMit+%C3%9Cmlaut%2BSonderzeichen.txt";
	protected static final String TEST_URL_DIR = "https://ccopy.s3.amazonaws.com/test/";
	/**
	 * TMP_* strings are for creating, changing and deleting S3 objects
	 */
	private static final String   TMP_ROOT = "https://ccopy.s3.amazonaws.com/tmp";
	protected static final String TMP_URL = TMP_ROOT + ".txt";
	protected static final String TMP_URL_WITH_UMLAUT = TMP_ROOT + "/file with+andÜ.txt";
	/**
	 * generic logger
	 */
	protected static Logger logger = Logger.getLogger("org.ccopy");

}
