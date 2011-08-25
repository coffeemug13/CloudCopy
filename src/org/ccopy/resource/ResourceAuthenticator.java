/**
 * 
 */
package org.ccopy.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * @author coffeemug13
 */
public class ResourceAuthenticator extends Authenticator {
	private static Logger logger = Logger.getLogger("org.ccopy");
	protected static String username = null;
	protected static String passwd = null;
	private File file;
	/**
	 * This is the private key for the passwordfile
	 */
	private byte[] keyByte = { (byte) 0x09, (byte) 0xCB, (byte) 0x0E, (byte) 0x6A, (byte) 0x0F,
			(byte) 0xFF, (byte) 0xAF, (byte) 0x30 };
	/**
	 * This is the salt for the cipher
	 */
	private byte[] saltByte = { (byte) 0x9A, (byte) 0xB9, (byte) 0x80, (byte) 0xA0, (byte) 0x0A,
			(byte) 0x53, (byte) 0x3E, (byte) 0x30 };

	/**
	 * Construct the Authenticator with username & password
	 * 
	 * @param username
	 * @param passwd
	 */
	public ResourceAuthenticator(String username, String passwd) {
		// TODO implement setting multiple credentials for different hosts
		ResourceAuthenticator.username = username;
		ResourceAuthenticator.passwd = passwd;
		logger.fine("using following Credentials for S3 Authentication: " + username + " & "
				+ passwd);
	}
	/**
	 * Construct the Authenticator from a password file
	 * @param file
	 * @throws IOException
	 */
	public ResourceAuthenticator(File file) throws IOException {
		this.file = file;
		if (file.exists())
			if (file.length()>0)
				load();
	}

	// This method is called when a password-protected URL is accessed
	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		// Get information about the request
		// logger.fine("using: " + username + " & " + passwd);
		// Return the information
		if (this.getRequestingHost().equals("s3.amazonaws.com"))
			return new PasswordAuthentication(username, passwd.toCharArray());
		else
			return new PasswordAuthentication(null, null);
	}
	/**
	 * Add Credentials to this class. Don't forget to call {@link ResourceAuthenticator#save()} afterwards to update the password file
	 * @param username
	 * @param passwd
	 */
	public void addCredentials(String username, String passwd) {
		ResourceAuthenticator.username = username;
		ResourceAuthenticator.passwd = passwd;

	}

	protected void load() throws IOException {
		InputStream in = null;
		try {
			/*
			 * Create the key
			 */
			// SecretKey key = KeyGenerator.getInstance("DES").generateKey();
			KeySpec keySpec = new DESKeySpec(keyByte);
			SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(saltByte);
			/*
			 * Decrypt
			 */
			Cipher dcipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
			// Decode base64 to get bytes
			byte[] dec;
			in = new FileInputStream(file);
			dec = new sun.misc.BASE64Decoder().decodeBuffer(in);
			// Decrypt
			byte[] utf8 = dcipher.doFinal(dec);
			// Decode using utf-8
			String[] secret = new String(utf8, "UTF8").split(":");
			System.out.println(secret[0] + ":" + secret[1]);
			ResourceAuthenticator.username = secret[0];
			ResourceAuthenticator.passwd = secret[1];
			logger.fine("successfully loaded credentials from file:" + file);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (BadPaddingException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			if (null != in)
				in.close();
		}
	}

	public void save() throws IOException {
		FileWriter out = null;
		try {
			// open the Outputstream for writing
			out = new FileWriter(file);
			/*
			 * Create the key
			 */
			// SecretKey key = KeyGenerator.getInstance("DES").generateKey();
			KeySpec keySpec = new DESKeySpec(keyByte);
			SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(saltByte);
			/*
			 * Create the secret
			 */
			String secret = ResourceAuthenticator.username + ":" + ResourceAuthenticator.passwd;
			/*
			 * Encrypt
			 */
			Cipher ecipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			// Encode the string into bytes using utf-8
			byte[] utf8 = secret.getBytes("UTF8");
			// Encrypt
			byte[] enc = ecipher.doFinal(utf8);
			// Encode bytes to base64 to get a string
			String s = new sun.misc.BASE64Encoder().encode(enc);
			System.out.println(s);
			out.write(s);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (BadPaddingException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			if (null != out)
				out.close();
		}
	}
}
