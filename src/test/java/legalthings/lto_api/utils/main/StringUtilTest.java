package legalthings.lto_api.utils.main;

import static org.junit.Assert.*;

import org.junit.Test;
import legalthings.lto_api.utils.main.StringUtil;

public class StringUtilTest {

	@Test
	public void testBase58()
	{
		String case1 = "Hello";
		String retCase1 = StringUtil.base58Encode(case1);
		assertEquals("9Ajdvzr", retCase1);
		
		String retCase2 = StringUtil.base58Decode(retCase1); 
		assertEquals("Hello", retCase2);
		
		String case3 = "3PLSsSDUn3kZdGe8qWEDak9y8oAjLVecXV1";
		String encodedCase3 = StringUtil.base58Encode(case3);
		String decodedCase3 = StringUtil.base58Decode(encodedCase3);
		assertEquals(case3, decodedCase3);
	}

	@Test
	public void testBase64()
	{
		String case1 = "Hello";
		String retCase1 = StringUtil.base64Encode(case1);
		assertEquals("SGVsbG8=", retCase1);
		
		String retCase2 = StringUtil.base64Decode(retCase1); 
		assertEquals("Hello", retCase2);
		
		String case3 = "3PLSsSDUn3kZdGe8qWEDak9y8oAjLVecXV1";
		String encodedCase3 = StringUtil.base64Encode(case3);
		String decodedCase3 = StringUtil.base64Decode(encodedCase3);
		assertEquals(case3, decodedCase3);
	}
	
	@Test
	public void testRepeat()
	{
		String case1 = "Hi";
		String retCase1 = StringUtil.repeat(case1, 5);
		assertEquals("HiHiHiHiHi", retCase1);
	}
}
