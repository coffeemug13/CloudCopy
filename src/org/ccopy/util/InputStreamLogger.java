
package org.ccopy.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.logging.Logger;


/**
 * @author mholakovsky
 *
 */
public class InputStreamLogger extends InputStream {

	private static Logger logger = Logger.getLogger("org.ccopy");
	private InputStream in;
	private StringBuffer buf = new StringBuffer();

	
	public InputStreamLogger(InputStream in) {
		this.in = in;
	}
	@Override
	public int read() throws IOException {
		int i = in.read();
		if (i>=0)
			buf.append((char)i);
		else 
			logger.finest(buf.toString());
		return i;
	}
	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] b) throws IOException {
		int i = in.read(b);
		if (i>=0)
			buf.append(new String(b));
		else 
			logger.finest(buf.toString());
		return i;
	}
	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int i = in.read(b, off, len);
		if (i>=0)
			buf.append(new String(b,off,len));
		else 
			logger.finest(buf.toString());
		return i;
	}
	/* (non-Javadoc)
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		return in.skip(n);
	}
	/* (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return in.available();
	}
	/* (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		in.close();
	}
	/* (non-Javadoc)
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public synchronized void mark(int readlimit) {
		in.mark(readlimit);
	}
	/* (non-Javadoc)
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		in.reset();
	}
	/* (non-Javadoc)
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

}
