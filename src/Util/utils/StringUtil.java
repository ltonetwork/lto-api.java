/**
 * 
 */
package Util.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import Util.core.Base58;
import java.util.Base64;

/**
 * @author moonbi
 *
 */
public class StringUtil {	
	public static String encodeBase58(String input) {
		try {
			return Base58.encode(input.getBytes("UTF-8"));
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
	
	public static String encodeBase64(String input) {
		try {
			return Base64.getEncoder().encodeToString(input.getBytes("UTF-8"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String decodeBase64(String input) {
		try {
			return new String(Base64.getDecoder().decode(input), "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String repeat(String string, int times) {
		return new String(new char[times]).replace("\0", string);
	}
}