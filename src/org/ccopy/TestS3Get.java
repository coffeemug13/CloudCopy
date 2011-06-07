package org.ccopy;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.Proxy.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccopy.resource.util.DateFormatter;
import org.ccopy.resource.ResourceAuthenticator;
import org.ccopy.resource.s3.S3;
import org.ccopy.s3.S3Request;

public class TestS3Get {
	private static Logger logger = Logger.getLogger("org.ccopy");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			/**
			 * Setup the Logger
			 */
			ConsoleHandler ch = new ConsoleHandler();
			ch.setLevel(Level.FINEST);
			ch.setFormatter(new DateFormatter());
			logger.addHandler(ch);
			logger.setLevel(Level.FINEST);
			/**
			 * Create custom Authenticator Username = accessKeyId Password =
			 * secretAccessKey
			 */
			Authenticator.setDefault(new ResourceAuthenticator(
					"AKIAIGZKXWFKU74XTWAA",
					"q5If10+UBO8Gu4jlD5Lno038Y9TXF06fj98CWn8L"));
			/**
			 * Open AmazonS3 Connection
			 */
			// AKIAIGZKXWFKU74XTWAA - q5If10+UBO8Gu4jlD5Lno038Y9TXF06fj98CWn8L
			S3 req = new S3();
			// URL("http://mholakovsky.s3.amazonaws.com/public/impressum.html");
			URL obj = new URL("https://mholakovsky.s3.amazonaws.com/test.txt");
			SocketAddress addr = new InetSocketAddress("proxy.sozvers.at", 8080);
			Proxy proxy = new Proxy(Type.HTTP, addr);
			HttpURLConnection con = (HttpURLConnection) obj
					.openConnection();
			/**
			 * Create the canonical, signed URL for the resource
			 */
			SimpleDateFormat df = new SimpleDateFormat(
					"EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			String expire = df.format(new Date());
			logger.finer("using following Expire date: " + expire);
			String stringTosign = "GET\n\n\n" + expire
					+ "\n/mholakovsky/test.txt";
			logger.fine("this is the string to sign:\n" + stringTosign);
			PasswordAuthentication pwd = Authenticator
					.requestPasswordAuthentication(null, 0, "", "", "");
			String sign = req.sign(new String(pwd.getPassword()), stringTosign);
			con.addRequestProperty("Date", expire);
			con.addRequestProperty("Authorization", "AWS AKIAIGZKXWFKU74XTWAA:"
					+ sign);

			/**
			 * Print the request and response headers for logging
			 */
			if (logger.isLoggable(Level.FINER)) {
				String log = "Request Headers...\n";
				Map<String, List<String>> map = con.getRequestProperties();
				Set<Entry<String, List<String>>> mapSet = map.entrySet();
				Iterator<Entry<String, List<String>>> mapIterator = mapSet
						.iterator();
				while (mapIterator.hasNext()) {
					Map.Entry<String, List<String>> e = (Map.Entry<String, List<String>>) mapIterator
							.next();
					if (e.getKey()!=null) {
						log += "   " + e.getKey() + ": ";
					}
					Iterator<String> val = e.getValue().iterator();
					while (val.hasNext()) {
						log += val.next() + " ";
					}
					log += "\n";
				}
				log += "   Authorization: AWS " + pwd.getUserName() + ":" + sign; // workaround, because HttipUrlConnection don't return the "Authorization" header for security reason
				logger.finer(log);
				log = "Response Headers...\n";
				map = con.getHeaderFields();
				mapSet = map.entrySet();
				mapIterator = mapSet.iterator();
				while (mapIterator.hasNext()) {
					Map.Entry<String, List<String>> e = (Map.Entry<String, List<String>>) mapIterator
							.next();
					if (e.getKey()!=null) {
						log += "   " + e.getKey() + ": ";
					} else log += "   ";
					Iterator<String> val = e.getValue().iterator();
					while (val.hasNext()) {
						log += val.next() + " ";
					}
					log += "\n";
				}
				logger.finer(log);
			}
			InputStream in = null;
			byte[] c = new byte[100]; // with increasing value speed goes up
			int read = 0;
			try {
				in = con.getInputStream();
				// Read (and print) till end of file.
				while ((read = in.read(c)) != -1) {
					// String result = new String(c);
					System.out.print(new String(c, 0, read));
				}
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				InputStream ein = con.getErrorStream();
				while ((read = ein.read(c)) != -1) {
					// String result = new String(c);
					System.out.write(c, 0, read);
					// System.out.println("e----");
				}
			} finally {
				if (in != null) {
					in.close();
				}
				con.disconnect();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
