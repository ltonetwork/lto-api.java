package UtilTests;

import static org.junit.Assert.*;

import org.junit.Test;

import Util.StringUtil;

public class StringUtilTest {

	@Test
	public void testSHA256() {
		
	}

	@Test
	public void testEncodeBase58() {
		String input = "I'm testing StringUtil.encodeBase58()";
		String encodedInput = StringUtil.encodeBase58(input);
		String expectedInput = "3TChyDxKkD6ticG3mSdB9n6ciTXmapsLaFQ9vXRKdRr8fRXBi5E";
		assertEquals(encodedInput, expectedInput);
		
		input = "{\"color\":\"red\",\"foo\":\"bar\"}";
		encodedInput = StringUtil.encodeBase58(input);
		expectedInput = "HeFMDcuveZQYtBePVUugLyWtsiwsW4xp7xKdv";
		assertEquals(expectedInput, encodedInput);
	}

	@Test
	public void testDecodeBase58() {
		String input = "3TChyDxKkD6ticG3mSdB9n6ciTXmapsLaFQ9vXRKdRr8fRXBi5E";
		String encodedInput = StringUtil.decodeBase58(input);
		String expectedInput = "I'm testing StringUtil.encodeBase58()";
		assertEquals(encodedInput, expectedInput);
	}
}
