package com.ltonetwork.client.utils.main;

import static org.junit.Assert.*;

import com.ltonetwork.client.utils.Encoder;
import org.junit.Test;

public class EncoderTest {

    @Test
    public void testBase58() {
        String case1 = "Hello";
        String retCase1 = Encoder.base58Encode(case1);
        assertEquals("9Ajdvzr", retCase1);

        String retCase2 = new String(Encoder.base58Decode(retCase1));
        assertEquals("Hello", retCase2);

        String case3 = "3PLSsSDUn3kZdGe8qWEDak9y8oAjLVecXV1";
        assertEquals(case3, Encoder.base58Encode(Encoder.base58Decode(case3)));
    }

    @Test
    public void testBase64() {
        String case1 = "Hello";
        String retCase1 = new String(Encoder.base64Encode(case1));
        assertEquals("SGVsbG8=", retCase1);

        String retCase2 = new String(Encoder.base64Decode(retCase1));
        assertEquals("Hello", retCase2);

        String case3 = "3PLSsSDUn3kZdGe8qWEDak9y8oAjLVecXV1";
        String encodedCase3 = new String(Encoder.base64Encode(case3));
        String decodedCase3 = new String(Encoder.base64Decode(encodedCase3));
        assertEquals(case3, decodedCase3);
    }

    @Test
    public void testRepeat() {
        String case1 = "Hi";
        String retCase1 = Encoder.repeat(case1, 5);
        assertEquals("HiHiHiHiHi", retCase1);
    }
}
