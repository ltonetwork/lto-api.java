package legalthings.lto_api.utils.main;

import java.security.MessageDigest;

import legalthings.lto_api.utils.core.Keccak;
import static legalthings.lto_api.utils.core.Parameters.KECCAK_256;

public class HashUtil {
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
			String s = HexUtil.getHex(input.getBytes("UTF-8"));
			
			Keccak keccak = new Keccak();
			
			return keccak.getHash(s, KECCAK_256);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
