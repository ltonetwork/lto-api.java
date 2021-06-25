package com.ltonetwork.client.utils.main;

import com.ltonetwork.client.utils.Encoder;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class EncoderTest {
    String hello;
    byte[] helloBytes;
    String complex;
    byte[] complexBytes;

    @Before
    public void init() {
        hello = "Hello";
        helloBytes = "Hello".getBytes(StandardCharsets.UTF_8);

        complex = "nф!#+3ϟϪЂʧ";
        complexBytes = "nф!#+3ϟϪЂʧ".getBytes(StandardCharsets.UTF_8);
    }

    @Test
    public void testBase58EncodeSimple() {
        String helloEncoded = "9Ajdvzr";

        String retCase1 = Encoder.base58Encode(hello, StandardCharsets.US_ASCII);
        assertEquals(helloEncoded, retCase1);

        String retCase2 = Encoder.base58Encode(hello);
        assertEquals(helloEncoded, retCase2);

        String retCase3 = Encoder.base58Encode(hello.getBytes(StandardCharsets.UTF_8));
        assertEquals(helloEncoded, retCase3);
    }

    @Test
    public void testBase58EncodeComplex() {
        String retCase = Encoder.base58Encode(complex, StandardCharsets.UTF_8);
        assertEquals("46pbidXQtvZ6tvvRhDVok", retCase);
    }

    @Test
    public void testBase58DecodeSimple() {
        String helloEncoded = "9Ajdvzr";
        byte[] helloBytesEncoded = "9Ajdvzr".getBytes(StandardCharsets.UTF_8);

        String retCase1 = Encoder.base58Decode(helloEncoded, StandardCharsets.UTF_8);
        assertEquals(hello, retCase1);

        String retCase2 = Encoder.base58Decode(helloBytesEncoded, StandardCharsets.UTF_8);
        assertEquals(hello, retCase2);

        byte[] retCase3 = Encoder.base58Decode(helloEncoded);
        assertArrayEquals(helloBytes, retCase3);

        byte[] retCase4 = Encoder.base58Decode(helloBytesEncoded);
        assertArrayEquals(helloBytes, retCase4);
    }

    @Test
    public void testBase58DecodeComplex() {
        String complexEncoded = "46pbidXQtvZ6tvvRhDVok";
        byte[] complexBytesEncoded = "46pbidXQtvZ6tvvRhDVok".getBytes(StandardCharsets.UTF_8);

        String retCase1 = Encoder.base58Decode(complexEncoded, StandardCharsets.UTF_8);
        assertEquals(complex, retCase1);

        byte[] retCase2 = Encoder.base58Decode(complexBytesEncoded);
        assertArrayEquals(complexBytes, retCase2);
    }

    @Test
    public void testBase64EncodeSimple() {
        String helloEncoded = "SGVsbG8=";

        String retCase1 = Encoder.base64Encode(hello);
        assertEquals(helloEncoded, retCase1);

        String retCase2 = Encoder.base64Encode(helloBytes);
        assertEquals(helloEncoded, retCase2);
    }

    @Test
    public void testBase64DecodeSimple() {
        String helloEncoded = "SGVsbG8=";
        byte[] helloBytesEncoded = "SGVsbG8=".getBytes(StandardCharsets.UTF_8);

        String retCase1 = Encoder.base64Decode(helloEncoded, StandardCharsets.UTF_8);
        assertEquals(hello, retCase1);

        String retCase2 = Encoder.base64Decode(helloBytesEncoded, StandardCharsets.UTF_8);
        assertEquals(hello, retCase2);

        byte[] retCase3 = Encoder.base64Decode(helloEncoded);
        assertArrayEquals(helloBytes, retCase3);

        byte[] retCase4 = Encoder.base64Decode(helloBytesEncoded);
        assertArrayEquals(helloBytes, retCase4);
    }

    @Test
    public void testBase64DecodeComplex() {
        String complexEncoded = "btGEISMrM8+fz6rQgsqn";
        byte[] complexBytesEncoded = "btGEISMrM8+fz6rQgsqn".getBytes(StandardCharsets.UTF_8);

        String retCase1 = Encoder.base64Decode(complexEncoded, StandardCharsets.UTF_8);
        assertEquals(complex, retCase1);

        byte[] retCase2 = Encoder.base64Decode(complexBytesEncoded);
        assertArrayEquals(complexBytes, retCase2);
    }

    @Test
    public void testHexEncodeSimple() {
        String helloEncoded = "48656c6c6f";

        String retCase1 = Encoder.hexEncode(hello);
        assertEquals(helloEncoded, retCase1);

        String retCase2 = Encoder.hexEncode(helloBytes);
        assertEquals(helloEncoded, retCase2);
    }

    @Test
    public void testHexDecodeSimple() {
        String helloEncoded = "48656c6c6f";
        byte[] helloBytesEncoded = "48656c6c6f".getBytes(StandardCharsets.UTF_8);

        String retCase1 = Encoder.hexDecode(helloEncoded, StandardCharsets.UTF_8);
        assertEquals(hello, retCase1);

        String retCase2 = Encoder.hexDecode(helloBytesEncoded, StandardCharsets.UTF_8);
        assertEquals(hello, retCase2);

        byte[] retCase3 = Encoder.hexDecode(helloEncoded);
        assertArrayEquals(helloBytes, retCase3);

        byte[] retCase4 = Encoder.hexDecode(helloBytesEncoded);
        assertArrayEquals(helloBytes, retCase4);
    }

    @Test
    public void testIsBase58EncodedGood() {
        String helloEncoded = "9Ajdvzr";
        boolean retCase1 = Encoder.isBase58Encoded(helloEncoded);
        assertTrue(retCase1);
    }

    @Test
    public void testIsBase58EncodedBad() {
        String helloEncoded = "9Ajdvzr=";
        boolean retCase1 = Encoder.isBase58Encoded(helloEncoded);
        assertFalse(retCase1);
    }

    @Test
    public void testIsBase64EncodedGood() {
        String helloEncoded = "SGVsbG8=";
        boolean retCase1 = Encoder.isBase64Encoded(helloEncoded);
        assertTrue(retCase1);
    }

    @Test
    public void testIsBase64EncodedBad() {
        String helloEncoded = "SGVsbG8=л";
        boolean retCase1 = Encoder.isBase64Encoded(helloEncoded);
        assertFalse(retCase1);
    }
}
