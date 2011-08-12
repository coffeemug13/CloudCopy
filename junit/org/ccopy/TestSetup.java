package org.ccopy;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccopy.resource.ResourceAuthenticator;
import org.ccopy.resource.util.LoggingDateFormatter;
import org.ccopy.util.HttpMethod;

public class TestSetup {
	static public void initialSetup() {
		/**
		 * Setup the default Authenticator for http requests
		 */
		Authenticator.setDefault(new ResourceAuthenticator(
				"AKIAIGZKXWFKU74XTWAA",
				"q5If10+UBO8Gu4jlD5Lno038Y9TXF06fj98CWn8L"));
		/**
		 * Setup the proxy if needed
		 */
		SocketAddress addr = new InetSocketAddress("proxy.sozvers.at", 8080);
		//HttpMethod.proxy = new Proxy(Type.HTTP, addr);
		
		// set the Log Format and Level
		Logger logger = Logger.getLogger("org.ccopy");
		ConsoleHandler ch = new ConsoleHandler();
		ch.setLevel(Level.FINEST);
		ch.setFormatter(new LoggingDateFormatter());
		// add to logger
		logger.addHandler(ch);
		logger.setLevel(Level.FINEST);
	}

}
