/**
 * 
 */
package Util.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import Util.core.Base58;

/**
 * @author moonbi
 *
 */
public class StringUtil {
	public static String SHA256(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static byte[] SHA256(byte[] input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input);
			return hash;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String encodeBase58(String input) {
		try {
			return Base58.encode(input.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String encodeBase58(byte[] input) {
		try {
			return Base58.encode(input);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String decodeBase58(String input) {
		try {
			return new String(Base58.decode(input), "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}