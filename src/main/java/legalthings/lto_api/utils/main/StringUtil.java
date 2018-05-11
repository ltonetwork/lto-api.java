package legalthings.lto_api.utils.main;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import legalthings.lto_api.utils.core.Base58;

public class StringUtil {
	public static String base58Encode(String string, String charset)
	{
		try {
			return Base58.encode(string.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String base58Encode(String string)
	{
		return base58Encode(string, "UTF-8");
	}
	
	public static String base58Decode(String string, String charset)
	{
		try {
			return new String(Base58.decode(string), charset);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String base58Decode(String string)
	{
		return base58Decode(string, "UTF-8");
	}
	
	public static String base64Encode(String input) {
		try {
			return Base64.getEncoder().encodeToString(input.getBytes("UTF-8"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String base64Decode(String input) {
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
