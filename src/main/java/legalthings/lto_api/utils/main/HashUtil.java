package legalthings.lto_api.utils.main;

import java.security.MessageDigest;
import java.util.Formatter;

import legalthings.lto_api.utils.core.Keccak;
import legalthings.lto_api.utils.core.Keccak1;

import static legalthings.lto_api.utils.core.Parameters.KECCAK_256;

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
			String s = HexUtil.getHex(input.getBytes());
			
			Keccak keccak = new Keccak();
			
			return keccak.getHash(s, KECCAK_256);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String Keccak256(byte[] input) {
        String s = getHexStringByByteArray(input);
        Keccak1 keccak1 = new Keccak1(1600);
        return keccak1.getHash(s, 1088, 32);
	}
	
	public static String Keccak256(char[] input) {		
		String s = HexUtil.getHex(input);
		Keccak keccak = new Keccak();
		return keccak.getHash(s, KECCAK_256);
	}
	
	public static byte[] getByteArray(String s) {
        return (s != null) ? s.getBytes(): null;
    }
	
	public static String getHexStringByByteArray(byte[] array) {
        if (array == null)
            return null;

        StringBuilder stringBuilder = new StringBuilder(array.length * 2);
        @SuppressWarnings("resource")
        Formatter formatter = new Formatter(stringBuilder);
        for (byte tempByte : array)
            formatter.format("%02x", tempByte);

        return stringBuilder.toString();
    }
}
