package UtilTests;

import static org.junit.Assert.*;

import org.junit.Test;

import Util.core.Sha256Hash;
import Util.utils.StringUtil;

public class Sha256HashTest {

	@Test
	public void testHashString() {
		String string = "I'm testing Sha256Hash.hash";
		byte[] hashedString = Sha256Hash.hash(string);
		String expectedString = "7df6421c9033f9f361bc1b8f0b6cef457e21185ee0a05e44995d415d5876bddb";
		assertEquals(expectedString, hashedString);
	}

}
