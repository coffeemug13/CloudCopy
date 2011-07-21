/**
 * 
 */
package org.ccopy.resource.s3;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * {@code} 
 * <CopyObjectResult> 
 * 		<LastModified>2009-10-28T22:32:00</LastModified>
 * 		<ETag>"9b2cf535f27731c974343645a3985328"</ETag> 
 * </CopyObjectResult> }
 * 
 * @author mholakovsky
 */
public class S3ObjectCopyRequestParser extends DefaultHandler {
	private static final String LAST_MODIFIED = "LastModified";
	private static final String ETAG = "ETag";
	private static final String COPY_OBJECT_RESULT = "CopyObjectResult";
	protected long lastModified = -1L;
	protected String eTag = null;
	private StringBuffer tmpValue = new StringBuffer(50);
	private boolean rootElementFound = false;
	static protected SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
			Locale.ENGLISH);
	private static Logger logger = Logger.getLogger("org.ccopy");
	private StringBuffer logBuf;

	/**
	 * Constructs the Copy Request Parser
	 * 
	 * @throws S3Exception
	 *             when the InputStream doesn't contain lastModified and ETag
	 * @throws IOException
	 * 			when parsing the InputStream by SAXParser is not possible
	 */
	protected S3ObjectCopyRequestParser(InputStream in) throws IOException{
		super();
		logger.fine(null);
		if (logger.isLoggable(Level.FINEST)) 
			this.logBuf = new StringBuffer();
		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(in, this);
		} catch (Throwable t) {
			if (logger.isLoggable(Level.FINEST) && (logBuf.length() > 0))
				logger.finest("parsed the following parameters from response:"
						+ logBuf.toString());
			throw new IOException("error while parsing S3 response from PutCopy operation",t);
//			throw new Error(
//					"the response content for the S3 copy request couldn't be parsed",
//					t);
		}
		// check that both variables have been found otherwise throw exception
		if ((-1L == lastModified) || (null == this.eTag))
			throw new S3Exception(
					S3Exception.INTERNAL_ERROR,
					"LastModified or ETag not found in InputStream. Looks like, there was an error while PutCopy an S3 object",
					null);
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		tmpValue.append(ch, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		// flag that you found the root element
		if (qName.equals(S3ObjectCopyRequestParser.COPY_OBJECT_RESULT))
			this.rootElementFound = true;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		// parse the first child element
		if (qName.equals(S3ObjectCopyRequestParser.LAST_MODIFIED)) {
			// check that this is a child element of CopyObjectResult
			if (!this.rootElementFound)
				throw new SAXException("no root element 'CopyObjectResult' found");
			// now parse the time string into a long
			try {
				this.lastModified = df.parse(this.tmpValue.toString()).getTime();
				if (logger.isLoggable(Level.FINEST)){
					logBuf.append(qName + ":"+ this.lastModified + ", ");
				}
			} catch (ParseException e) {
				throw new SAXException("error while converting the last modified timestamp", e);
			}
		}
		// parse the second child element
		if (qName.equals(S3ObjectCopyRequestParser.ETAG)) {
			// check that this is a child element of CopyObjectResult
			if (!this.rootElementFound)
				throw new SAXException("no root element 'CopyObjectResult' found");
			// now set the ETag attribute
			this.eTag = this.tmpValue.substring(1, this.tmpValue.length() - 1);
			if (logger.isLoggable(Level.FINEST)){
				logBuf.append(qName + ":"+ this.eTag + ", ");
			}
		} 
		tmpValue.setLength(0);
	}

}
