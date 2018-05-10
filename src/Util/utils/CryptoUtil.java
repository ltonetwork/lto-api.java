/**
 * 
 */
package Util.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muquit.libsodiumjna.SodiumKeyPair;
import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.sun.jna.Platform;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author moonbi
 *
 */
public class CryptoUtil {
	private static final Logger logger = LoggerFactory.getLogger(CryptoUtil.class);
	static {
		String libraryPath = null;
		String platform = System.getProperty("os.name");
		logger.info("Platform: " + platform);
		if (Platform.isMac())
		{
			libraryPath = "/usr/local/lib/libsodium.dylib";
			logger.info("Library path in Mac: " + libraryPath);
		}
		else if (Platform.isWindows())
		{
			libraryPath = "C:/libsodium/libsodium.dll";
			logger.info("Library path in Windows: " + libraryPath);
		}
		else
		{
			// Possibly Linux
			libraryPath = "/usr/local/lib/libsodium.so";
			logger.info("Library path in "  + "platform: " + platform + " " + libraryPath);
			
		}
		logger.info("Initialize libsodium...");
		SodiumLibrary.setLibraryPath(libraryPath);

	}
	
	public static byte[] random_bytes(int size) {
		return SodiumLibrary.randomBytes(size);
	}
	
	public static int crypto_sign_bytes() {
		return SodiumLibrary.sodium().crypto_sign_bytes();
	}
	
	public static int crypto_sign_publickeybytes() {
		return (int) SodiumLibrary.sodium().crypto_sign_publickeybytes();
	}
	
	public static int crypto_box_noncebytes() {
		return SodiumLibrary.cryptoBoxNonceBytes().intValue();
	}
	
	public static boolean crypto_sign_verify_detached(String signature, String message, String signkey) {
		try {
			return SodiumLibrary.cryptoSignVerifyDetached(signature.getBytes(), message.getBytes("UTF-8"), signkey.getBytes());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SodiumLibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;		
	}	
	
	public static byte[] crypto_sign_detached(String message, String secretkey) {
		try {
			return SodiumLibrary.cryptoSignDetached(message.getBytes("UTF-8"), secretkey.getBytes());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SodiumLibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static SodiumKeyPair crypto_box_keypair_from_secretkey_and_publickey(String publickey, String privatekey) {
		SodiumKeyPair kp = new SodiumKeyPair();
		kp.setPublicKey(publickey.getBytes());
		kp.setPrivateKey(privatekey.getBytes());
		return kp;
	}
	
	public static byte[] crypto_box(String message, String nonce, SodiumKeyPair kp) {
		try {
			return SodiumLibrary.cryptoBoxEasy(message.getBytes("UTF-8"), nonce.getBytes(), kp.getPublicKey(), kp.getPrivateKey());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SodiumLibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] crypto_box_open(String cyphertext, String nonce, SodiumKeyPair kp) {
		try {
			return SodiumLibrary.cryptoBoxOpenEasy(cyphertext.getBytes("UTF-8"), nonce.getBytes(), kp.getPublicKey(), kp.getPrivateKey());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SodiumLibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] crypto_generichash(String input, int length) {
		try {
			return SodiumLibrary.cryptoGenerichash(input.getBytes("UTF-8"), length);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SodiumLibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
