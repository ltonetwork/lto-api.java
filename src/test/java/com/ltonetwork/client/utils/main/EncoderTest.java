package com.ltonetwork.client.utils.main;

import com.ltonetwork.client.utils.Encoder;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class EncoderTest {
    String hello;
    String complex;

    @Before
    public void init() {
        hello = "Hello";
        complex = "nф!#+3ϟϪЂʧ";
    }

    @Test
    public void testBase58EncodeSimple() {
        String helloEncoded = "9Ajdvzr";

        String retCase = Encoder.base58Encode(hello);
        assertEquals(helloEncoded, retCase);

        String retCase2 = Encoder.base58Encode(hello.getBytes(StandardCharsets.UTF_8));
        assertEquals(helloEncoded, retCase2);
    }

    @Test
    public void testBase58EncodeComplex() {
        String complexEncoded = "46pbidXQtvZ6tvvRhDVok";

        String retCase = Encoder.base58Encode(complex);
        assertEquals(complexEncoded, retCase);

        String retCase2 = Encoder.base58Encode(complex.getBytes(StandardCharsets.UTF_8));
        assertEquals(complexEncoded, retCase2);
    }

    @Test
    public void testBase58DecodeSimple() {
        String helloEncoded = "9Ajdvzr";
        byte[] helloBytesEncoded = "9Ajdvzr".getBytes(StandardCharsets.UTF_8);

        byte[] retCase1 = Encoder.base58Decode(helloEncoded);
        assertArrayEquals(hello.getBytes(StandardCharsets.UTF_8), retCase1);

        byte[] retCase2 = Encoder.base58Decode(helloBytesEncoded);
        assertArrayEquals(hello.getBytes(StandardCharsets.UTF_8), retCase2);
    }

    @Test
    public void testBase58DecodeComplex() {
        String complexEncoded = "46pbidXQtvZ6tvvRhDVok";
        byte[] complexBytesEncoded = "46pbidXQtvZ6tvvRhDVok".getBytes(StandardCharsets.UTF_8);

        byte[] retCase1 = Encoder.base58Decode(complexEncoded);
        assertArrayEquals(complex.getBytes(StandardCharsets.UTF_8), retCase1);

        byte[] retCase2 = Encoder.base58Decode(complexBytesEncoded);
        assertArrayEquals(complex.getBytes(StandardCharsets.UTF_8), retCase2);
    }

    @Test
    public void testBase64EncodeSimple() {
        String helloEncoded = "SGVsbG8=";

        String retCase = Encoder.base64Encode(hello);
        assertEquals(helloEncoded, retCase);

        String retCase2 = Encoder.base64Encode(hello.getBytes(StandardCharsets.UTF_8));
        assertEquals(helloEncoded, retCase2);
    }

    @Test
    public void testBase64EncodeComplex() {
        String complexEncoded = "btGEISMrM8+fz6rQgsqn";

        String retCase = Encoder.base64Encode(complex);
        assertEquals(complexEncoded, retCase);

        String retCase2 = Encoder.base64Encode(complex.getBytes(StandardCharsets.UTF_8));
        assertEquals(complexEncoded, retCase2);
    }

    @Test
    public void testBase64DecodeSimple() {
        String helloEncoded = "SGVsbG8=";
        byte[] helloBytesEncoded = "SGVsbG8=".getBytes(StandardCharsets.UTF_8);

        byte[] retCase1 = Encoder.base64Decode(helloEncoded);
        assertArrayEquals(hello.getBytes(StandardCharsets.UTF_8), retCase1);

        byte[] retCase2 = Encoder.base64Decode(helloBytesEncoded);
        assertArrayEquals(hello.getBytes(StandardCharsets.UTF_8), retCase2);
    }

    @Test
    public void testBase64DecodeComplex() {
        String complexEncoded = "btGEISMrM8+fz6rQgsqn";
        byte[] complexBytesEncoded = "btGEISMrM8+fz6rQgsqn".getBytes(StandardCharsets.UTF_8);

        byte[] retCase1 = Encoder.base64Decode(complexEncoded);
        assertArrayEquals(complex.getBytes(StandardCharsets.UTF_8), retCase1);

        byte[] retCase2 = Encoder.base64Decode(complexBytesEncoded);
        assertArrayEquals(complex.getBytes(StandardCharsets.UTF_8), retCase2);
    }

    @Test
    public void testHexEncodeSimple() {
        String helloEncoded = "48656c6c6f";

        String retCase = Encoder.hexEncode(hello);
        assertEquals(helloEncoded, retCase);

        String retCase2 = Encoder.hexEncode(hello.getBytes(StandardCharsets.UTF_8));
        assertEquals(helloEncoded, retCase2);
    }

    @Test
    public void testHexEncodeComplex() {
        String complexEncoded = "6ed18421232b33cf9fcfaad082caa7";

        String retCase = Encoder.hexEncode(complex);
        assertEquals(complexEncoded, retCase);
        System.out.println(retCase);

        String retCase2 = Encoder.hexEncode(complex.getBytes(StandardCharsets.UTF_8));
        assertEquals(complexEncoded, retCase2);
    }

    @Test
    public void testHexDecodeSimple() {
        String helloEncoded = "48656c6c6f";
        byte[] helloBytesEncoded = "48656c6c6f".getBytes(StandardCharsets.UTF_8);

        byte[] retCase1 = Encoder.hexDecode(helloEncoded);
        assertArrayEquals(hello.getBytes(StandardCharsets.UTF_8), retCase1);

        byte[] retCase2 = Encoder.hexDecode(helloBytesEncoded);
        assertArrayEquals(hello.getBytes(StandardCharsets.UTF_8), retCase2);
    }

    @Test
    public void testHexDecodeComplex() {
        String complexEncoded = "6ed18421232b33cf9fcfaad082caa7";
        byte[] complexBytesEncoded = "6ed18421232b33cf9fcfaad082caa7".getBytes(StandardCharsets.UTF_8);

        byte[] retCase1 = Encoder.hexDecode(complexEncoded);
        assertArrayEquals(complex.getBytes(StandardCharsets.UTF_8), retCase1);

        byte[] retCase2 = Encoder.hexDecode(complexBytesEncoded);
        assertArrayEquals(complex.getBytes(StandardCharsets.UTF_8), retCase2);
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

    @Test
    public void testIsHexEncodedGood() {
        String helloEncoded = "48656c6c6f";
        boolean retCase1 = Encoder.isHexEncoded(helloEncoded);
        assertTrue(retCase1);
    }

    @Test
    public void testIsHexEncodedBad() {
        String helloEncoded = "48656c6c6fQ";
        boolean retCase1 = Encoder.isHexEncoded(helloEncoded);
        assertFalse(retCase1);
    }
}
