/**
 * 
 */
package Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muquit.libsodiumjna.SodiumLibrary;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.sun.jna.Platform;

import java.nio.charset.StandardCharsets;

/**
 * @author moonbi
 *
 */
public class CryptoUtil {
	private static final Logger logger = LoggerFactory.getLogger(CryptoUtil.class);
	static {
		String libraryPath = null;
		System.out.println("hi form static");
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
	
	public String hashPassword(String password) throws SodiumLibraryException
	{
		byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8); // requires jdk 1.7+
		return SodiumLibrary.cryptoPwhashStr(passwordBytes);
	}

	public boolean check(String password, String hashedPassword)
	{
		byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8); // requires jdk 1.7+
		return SodiumLibrary.cryptoPwhashStrVerify(hashedPassword, passwordBytes);
	}
	
	public static int crypto_sign_bytes() {
		return SodiumLibrary.sodium().crypto_sign_bytes();
	}
	
	public static int crypto_sign_publickeybytes() {
		return (int) SodiumLibrary.sodium().crypto_sign_publickeybytes();
	}
	
	public static int crypto_sign_verify_detached(String signature, String message, String signkey) {
		try {
			return SodiumLibrary.sodium().crypto_sign_detached(signature.getBytes(), signature.length(), message.getBytes("UTF-8"), message.length(), signkey.getBytes());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
