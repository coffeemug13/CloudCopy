/**
 * 
 */
package org.ccopy.resource.s3;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * {@code} <CopyObjectResult> <LastModified>2009-10-28T22:32:00</LastModified>
 * <ETag>"9b2cf535f27731c974343645a3985328"</ETag> </CopyObjectResult> }
 * 
 * @author mholakovsky
 */
public class S3BucketListObjectsRequestParser extends DefaultHandler {
	/*
	 * expected XML Tags
	 */
	private static final String NAME = "Name";
	private static final String LAST_MODIFIED = "LastModified";
	private static final String ETAG = "ETag";
	private static final String LIST_BUCKET_RESULT = "ListBucketResult";
	private static final String CONTENTS = "Contents";
	private static final String SIZE = "Size";
	private static final String KEY = "Key";
	private static final String COMMON_PREFIX = "CommonPrefixes";
	private static final String PREFIX = "Prefix";
	/*
	 * attributes
	 */
	private String bucket;
	private String prefix;
	protected S3Object s3Obj;
	protected List<S3Object> list = new ArrayList<S3Object>();
	/*
	 * helper vars
	 */
	private StringBuffer tmpValue = new StringBuffer(50);
	private boolean rootElementFound = false;
	private boolean contentElementFound = false;
	private boolean commonPrefixElementFound = false;
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
	 *             when parsing the InputStream by SAXParser is not possible
	 */
	protected S3BucketListObjectsRequestParser(InputStream in) throws IOException {
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
				logger.finest("parsed the following parameters from response:" + logBuf.toString());
			throw new IOException("error while parsing S3 response from list objects operation", t);
		}
		// check that both variables have been found otherwise throw exception
		if (!this.rootElementFound)
			throw new S3Exception(
					S3Exception.INTERNAL_SERVER_ERROR,
					"LastModified or ETag not found in InputStream. Looks like, there was an error while PutCopy an S3 object",
					null);
	}

	/*
	 * (non-Javadoc)
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
		if (qName.equals(S3BucketListObjectsRequestParser.LIST_BUCKET_RESULT))
			this.rootElementFound = true;
		else if (qName.equals(S3BucketListObjectsRequestParser.CONTENTS) && !contentElementFound
				&& rootElementFound) {
			this.contentElementFound = true;
		} else if (qName.equals(S3BucketListObjectsRequestParser.COMMON_PREFIX)
				&& !contentElementFound && rootElementFound) {
			this.commonPrefixElementFound = true;
		}
		tmpValue.setLength(0);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		// check "Content" boundary
		if (qName.equals(S3BucketListObjectsRequestParser.CONTENTS) && contentElementFound) {
			this.contentElementFound = false;
			this.list.add(this.s3Obj);
		} else {
			// check that this is a child element of ListObjectResult & Content
			try {
				if (this.rootElementFound)
					if (this.contentElementFound) {
						// parse the possible child elements
						if (qName.equals(S3BucketListObjectsRequestParser.KEY)) {
							// now set the ETag attribute
							this.s3Obj = new S3Object(S3URL.fromPath(this.bucket,
									this.tmpValue.toString()));
							if (logger.isLoggable(Level.FINEST)) {
								logBuf.append(qName + ":" + this.tmpValue.toString() + ", ");
							}
						} else if (qName.equals(S3BucketListObjectsRequestParser.LAST_MODIFIED)) {
							// now parse the time string into a long
							this.s3Obj.lastModified = df.parse(this.tmpValue.toString()).getTime();
							if (logger.isLoggable(Level.FINEST)) {
								logBuf.append(qName + ":" + this.tmpValue.toString() + ", ");
							}
						} else if (qName.equals(S3BucketListObjectsRequestParser.ETAG)) {
							// now set the ETag attribute
							this.s3Obj.eTag = this.tmpValue
									.substring(1, this.tmpValue.length() - 1);
							if (logger.isLoggable(Level.FINEST)) {
								logBuf.append(qName + ":" + this.tmpValue.toString() + ", ");
							}
						} else if (qName.equals(S3BucketListObjectsRequestParser.SIZE)) {
							// now set the ETag attribute
							this.s3Obj.size = Long.valueOf(this.tmpValue.toString());
							if (logger.isLoggable(Level.FINEST)) {
								logBuf.append(qName + ":" + this.tmpValue.toString() + ", ");
							}
						}
						// else throw new SAXException("no root element 'CopyObjectResult' found");
					} else if (this.commonPrefixElementFound) {
						if (qName.equals(S3BucketListObjectsRequestParser.PREFIX))
							this.list.add(new S3Object(S3URL.fromPath(this.bucket, this.prefix
									+ this.tmpValue.toString())));
					} else if (qName.equals(S3BucketListObjectsRequestParser.NAME)) {
						this.bucket = this.tmpValue.toString();
					} else if (qName.equals(S3BucketListObjectsRequestParser.PREFIX)) {
						this.prefix = this.tmpValue.toString();
					}
				// else throw new SAXException("found unkown xml structure");
			} catch (MalformedURLException e) {
				throw new SAXException("error while creating S3Object from response", e);
			} catch (ParseException e) {
				throw new SAXException("error while converting the last modified timestamp", e);
			}
		}
	}
}
