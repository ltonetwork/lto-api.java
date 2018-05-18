package legalthings.lto_api.utils.main;

import java.security.MessageDigest;

import legalthings.lto_api.utils.core.BinHex;
import org.ethereum.crypto.cryptohash.Keccak256;

public class HashUtil {	
	public static byte[] SHA256(byte[] input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input);
			return hash;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
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
	
	public static String Keccak256(String input) {
		try {
			Keccak256 digest =  new Keccak256();
		    digest.update(input.getBytes("UTF-8"));
		    return BinHex.bin2hex(digest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String Keccak256(byte[] input) {
		Keccak256 digest =  new Keccak256();
	    digest.update(input);
	    return BinHex.bin2hex(digest.digest());
	}
}
