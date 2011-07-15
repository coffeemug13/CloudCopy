/**
 * 
 */
package org.ccopy.resource.s3;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.namespace.QName;
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
public class S3ObjectCopyRequestParser extends DefaultHandler {
	private static final String LAST_MODIFIED = "LastModified";
	private static final String ETAG = "ETag";
	private static final String COPY_OBJECT_RESULT = "CopyObjectResult";
	protected S3Resource object;
	private String tmpValue;
	private boolean rootElementFound = false;
	static protected SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
			Locale.ENGLISH);

	/**
	 * Constructs the Copy Request Parser
	 */
	protected S3ObjectCopyRequestParser(InputStream in, S3Resource object) {
		super();
		this.object = object;
		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(in, this);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		tmpValue = new String(ch, start, length);
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
				object.lastModified = df.parse(this.tmpValue).getTime();
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
			object.setETag(this.tmpValue.substring(1, this.tmpValue.length() - 1));
		}
	}

}
