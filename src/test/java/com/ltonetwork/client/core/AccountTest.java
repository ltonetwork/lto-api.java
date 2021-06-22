package com.ltonetwork.client.core;

import com.goterl.lazysodium.exceptions.SodiumException;
import com.ltonetwork.client.types.*;
import com.ltonetwork.client.utils.Encoder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    KeyPair sign = new KeyPair(
            new Key(Encoder.base58Decode("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y"), Encoding.RAW),
            new Key(Encoder.base58Decode("wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp"), Encoding.RAW)
    );

    KeyPair encrypt = new KeyPair(
            new Key(Encoder.base58Decode("BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN"), Encoding.RAW),
            new Key(Encoder.base58Decode("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6"), Encoding.RAW)
    );

    // Test network
    byte b = 84;
    Address address = new Address("3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy", b);

    Account account = new Account(address, encrypt, sign);

    @Test
    public void testGetAddress() {
        assertEquals("3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy", account.getAddress());
    }

    @Test
    public void testGetChainId() {
        assertEquals(84, account.getChainId());
    }

    @Test
    public void testGetEncrypt() {
        assertEquals(encrypt, account.getEncrypt());
    }

    @Test
    public void testGetSign() {
        assertEquals(sign, account.getSign());
    }

    @Test
    public void testGetPublicSignKey() {
        assertEquals(encrypt.getPublickey(), account.getPublicEncryptKey());
    }

    @Test
    public void testGetPublicEncryptKey() {
        assertEquals(sign.getPublickey(), account.getPublicSignKey());
    }

    @Test
    public void testSignString() {
        Signature signature = account.sign("hello");

        assertEquals(
                "2DDGtVHrX66Ae8C4shFho4AqgojCBTcE4phbCRTm3qXCKPZZ7reJBXiiwxweQAkJ3Tsz6Xd3r5qgnbA67gdL5fWE",
                signature.base58()
        );
    }

    @Test
    public void testSignStringEmpty() {
        Signature signature = account.sign("");

        assertEquals(
                "2SUPrUfQZBMXVAoY5p5Z1oYe8a8v4SN6gGk7FtPLHkTNmSN1Epj47KfLsmSv6MoExpF7GY8EBtroSV2yHDiG7HdS",
                signature.base58()
        );
    }

    @Test
    public void testSignByteEmpty() {

        Signature signature = account.sign(new byte[0]);

        assertEquals(
                "2SUPrUfQZBMXVAoY5p5Z1oYe8a8v4SN6gGk7FtPLHkTNmSN1Epj47KfLsmSv6MoExpF7GY8EBtroSV2yHDiG7HdS",
                signature.base58()
        );
    }

    @Test
    public void testVerifyString() {
        Signature signature = new Signature(Encoder.base58Decode("2DDGtVHrX66Ae8C4shFho4AqgojCBTcE4phbCRTm3qXCKPZZ7reJBXiiwxweQAkJ3Tsz6Xd3r5qgnbA67gdL5fWE"));

        assertTrue(account.verify(signature, "hello"));
    }

    @Test
    public void testVerifyByte() {
        Signature signature = new Signature(Encoder.base58Decode("2DDGtVHrX66Ae8C4shFho4AqgojCBTcE4phbCRTm3qXCKPZZ7reJBXiiwxweQAkJ3Tsz6Xd3r5qgnbA67gdL5fWE"));

        assertTrue(account.verify(signature, "hello".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void testVerifyFail() {
        Signature signature = new Signature(Encoder.base58Decode("2DDGtVHrX66Ae8C4shFho4AqgojCBTcE4phbCRTm3qXCKPZZ7reJBXiiwxweQAkJ3Tsz6Xd3r5qgnbA67gdL5fWE"));

        assertFalse(account.verify(signature, "fail"));
    }

    @Test
    public void testEncryptDecrypt() {
        KeyPair sign2 = new KeyPair(
                new Key(Encoder.base58Decode("BvEdG3ATxtmkbCVj9k2yvh3s6ooktBoSmyp8xwDqCQHp"), Encoding.RAW),
                new Key(Encoder.base58Decode("pLX2GgWzkjiiPp2SsowyyHZKrF4thkq1oDLD7tqBpYDwfMvRsPANMutwRvTVZHrw8VzsKjiN8EfdGA9M84smoEz"), Encoding.RAW)
        );

        KeyPair encrypt2 = new KeyPair(
                new Key(Encoder.base58Decode("HBqhfdFASRQ5eBBpu2y6c6KKi1az6bMx8v1JxX4iW1Q8"), Encoding.RAW),
                new Key(Encoder.base58Decode("3kMEhU5z3v8bmer1ERFUUhW58Dtuhyo9hE5vrhjqAWYT"), Encoding.RAW)
        );

        Address address2 = new Address("3PPbMwqLtwBGcJrTA5whqJfY95GqnNnFMDX");

        Account account2 = new Account(address2, encrypt2, sign2);

        byte[] ciphertext = account.encrypt(account2, "hello");
        byte[] hello = account2.decrypt(account, ciphertext);

        assertEquals("hello", new String(hello));
    }

//    @Test
//    public void testDecryptFail() {
//        SodiumException thrown = assertThrows(
//                SodiumException.class,
//                () -> account.decrypt(account, new byte[64]),
//                "opalqqqqqqqq"
//        );
//
//        assertTrue(thrown.getMessage().contains("Could not decrypt your message."));
//    }
}
