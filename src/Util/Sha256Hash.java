package Util;

import java.security.MessageDigest;

public class Sha256Hash {
	public static String hash(String input) {
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
	
	public static String hash(byte[] input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input);
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
	
	public static String hashTwice(String input) {
		return hash(hash(input));
	}
	
	public static String hashTwice(byte[] input) {
		return hash(hash(input));
	}
	
	public static byte[] hashTwice(byte[] input, int offset, int length) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        digest.update(input, offset, length);
	        return digest.digest(digest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
    }
}
