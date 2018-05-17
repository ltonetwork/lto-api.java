package legalthings.lto_api.utils.main;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
	public static String base58Encode(byte[] string)
	{
		try {
			return Base58.encode(string);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
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
	public static byte[] base58Decode(String string)
	{
		return Base58.decode(string);
	}
	
	public static String base64Encode(String input) {
		try {
			return new String(Base64.getEncoder().encode(input.getBytes()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String base64Encode(byte[] input) {
		try {
			return new String(Base64.getEncoder().encode(input));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static byte[] base64Decode(String input) {
		return Base64.getDecoder().decode(input);
	}
	
	public static byte[] base64Decode(byte[] input) {
		return Base64.getDecoder().decode(input);
	}
	
	public static String repeat(String string, int times) {
		return new String(new char[times]).replace("\0", string);
	}
	
	static byte[] packN(int value) {
		byte[] bytes = ByteBuffer.allocate(4).putInt(value).array();
		bytes = toPositiveByteArray(bytes);
		return bytes;
	}
	 
	static int unpackN(byte[] value) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.order(ByteOrder.BIG_ENDIAN);
		buf.put(value);
		buf.flip();
		return buf.getInt();
	}
	
	// converts a byte[] like [0,0,19,-2] to [0,0,19,254]
	public static byte[] toPositiveByteArray(byte[] bytes) {
		for (int i = 0; i< bytes.length; i++) {
			bytes[i] = (byte) (bytes[i] < 0 ? bytes[i] + 256 : bytes[i]);
		}
		return bytes;
	}
	
//	public static char[] toPositiveByteArray(byte[] bytes) {
//		char[] ret = new char[bytes.length];
//		
//		for (int i = 0; i< bytes.length; i++) {
//			ret[i] = (char) (bytes[i] < 0 ? bytes[i] + 256 : bytes[i]);
//		}
//		return ret;
//	}
}
