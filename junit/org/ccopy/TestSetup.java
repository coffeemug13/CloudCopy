package org.ccopy;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;

import org.ccopy.resource.ResourceAuthenticator;
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
	}

}
