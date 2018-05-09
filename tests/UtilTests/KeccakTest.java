package UtilTests;

import static org.junit.Assert.*;

import org.junit.Test;

import Util.core.Keccak;
import static Util.core.Parameters.KECCAK_256;
import Util.utils.HexUtil;

public class KeccakTest {

	@Test
	public void test() {
		String s = HexUtil.getHex("The quick brown fox jumps over the lazy dog".getBytes());
		
		Keccak keccak = new Keccak();
		
		String expected = "4d741b6f1eb29cb2a9b9911c82f56fa8d73b04959d3d9d222895df6c0b28aa15";
		String hash = keccak.getHash(s, KECCAK_256);
		assertEquals(expected, hash);
	}

}
