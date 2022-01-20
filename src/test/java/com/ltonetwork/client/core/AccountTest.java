package com.ltonetwork.client.core;

import com.ltonetwork.client.types.*;
import com.ltonetwork.seasalt.Binary;
import com.ltonetwork.seasalt.sign.Signature;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class AccountTest {
    Account account;
    KeyPair sign;
    KeyPair encrypt;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void init() {
        sign = new KeyPair(
                new PublicKey("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y", Encoding.BASE58),
                new PrivateKey("wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp", Encoding.BASE58)
        );

        encrypt = new KeyPair(
                new PublicKey("BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN", Encoding.BASE58),
                new PrivateKey("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6", Encoding.BASE58)
        );

        Address address = new Address("3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy");

        account = new Account(address, encrypt, sign);
    }

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
        assertEquals(encrypt.getPublicKey(), account.getPublicEncryptKey());
    }

    @Test
    public void testGetPublicEncryptKey() {
        assertEquals(sign.getPublicKey(), account.getPublicSignKey());
    }

    @Test
    public void testSignString() {
        Signature signature = account.sign("hello");

        assertEquals(
                "2DDGtVHrX66Ae8C4shFho4AqgojCBTcE4phbCRTm3qXCKPZZ7reJBXiiwxweQAkJ3Tsz6Xd3r5qgnbA67gdL5fWE",
                signature.getBase58()
        );
    }

    @Test
    public void testSignStringEmpty() {
        Signature signature = account.sign("");

        assertEquals(
                "2SUPrUfQZBMXVAoY5p5Z1oYe8a8v4SN6gGk7FtPLHkTNmSN1Epj47KfLsmSv6MoExpF7GY8EBtroSV2yHDiG7HdS",
                signature.getBase58()
        );
    }

    @Test
    public void testSignByteEmpty() {

        Signature signature = account.sign(new byte[0]);

        assertEquals(
                "2SUPrUfQZBMXVAoY5p5Z1oYe8a8v4SN6gGk7FtPLHkTNmSN1Epj47KfLsmSv6MoExpF7GY8EBtroSV2yHDiG7HdS",
                signature.getBase58()
        );
    }

    @Test
    public void testVerify() {
        String message = "hello";
        Signature signature = account.sign(message);

        assertTrue(account.verify(signature, message));
    }

    @Test
    public void testVerifyFail() {
        Signature signature = new Signature(Binary.fromBase58("2DDGtVHrX66Ae8C4shFho4AqgojCBTcE4phbCRTm3qXCKPZZ7reJBXiiwxweQAkJ3Tsz6Xd3r5qgnbA67gdL5fWE").getBytes());

        assertFalse(account.verify(signature, "fail"));
    }

    @Test
    public void testSignAndVerify() {
        String message = "hello";
        Signature signature = account.sign(message);

        assertTrue(account.verify(signature, message));
    }

    @Test
    public void testEncryptDecrypt() {
        KeyPair sign2 = new KeyPair(
                new PublicKey("BvEdG3ATxtmkbCVj9k2yvh3s6ooktBoSmyp8xwDqCQHp", Encoding.BASE58),
                new PrivateKey("pLX2GgWzkjiiPp2SsowyyHZKrF4thkq1oDLD7tqBpYDwfMvRsPANMutwRvTVZHrw8VzsKjiN8EfdGA9M84smoEz", Encoding.BASE58)
        );

        KeyPair encrypt2 = new KeyPair(
                new PublicKey("HBqhfdFASRQ5eBBpu2y6c6KKi1az6bMx8v1JxX4iW1Q8", Encoding.BASE58),
                new PrivateKey("3kMEhU5z3v8bmer1ERFUUhW58Dtuhyo9hE5vrhjqAWYT", Encoding.BASE58)
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
