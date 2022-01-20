package com.ltonetwork.client.utils.main;

import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.KeyPair;
import com.ltonetwork.client.types.PrivateKey;
import com.ltonetwork.client.types.PublicKey;
import com.ltonetwork.client.utils.CryptoUtil;
import com.ltonetwork.seasalt.sign.Signature;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class CryptoUtilTest {
    KeyPair signKp;
    KeyPair boxKp;
    byte[] msg;

    @Before
    public void init() {
        signKp = new KeyPair(
                new PublicKey(new byte[]{32, -45, 3, 80, 65, -47, -102, 82, 86, -126, -68, -39, 34, -101, -66, -80, -75, -90, 93, -93, 86, 18, 98, 101, 115, -39, 76, -122, -4, 118, 118, -12}),
                new PrivateKey(new byte[]{69, -111, -58, -98, -93, -51, 49, 82, 63, 54, -88, -32, -70, 63, 62, -13, 8, 73, 91, -104, -121, -34, 119, 58, -8, -50, 72, -46, 73, -117, -83, -95, 32, -45, 3, 80, 65, -47, -102, 82, 86, -126, -68, -39, 34, -101, -66, -80, -75, -90, 93, -93, 86, 18, 98, 101, 115, -39, 76, -122, -4, 118, 118, -12})
        );

        boxKp = new KeyPair(
                new PublicKey(new byte[]{95, 63, 13, -4, 101, -122, -90, -9, 79, -94, -52, -23, -120, 37, 49, -79, 41, -103, 39, 36, 122, -120, 116, -75, 46, -66, -21, -57, -124, -97, 108, 22}),
                new PrivateKey(new byte[]{-109, 64, 73, -28, -58, 57, 122, -49, -6, -101, 0, 66, 121, 98, 94, -82, 62, -109, -62, 54, 112, 68, -42, 65, 28, 123, 106, 124, 98, 77, -101, -89})
        );

        msg = "my random msg".getBytes(StandardCharsets.UTF_8);
    }

    @Test
    public void testKeypairCreation() {
        byte[] bytes = CryptoUtil.randomBytes(32);

        KeyPair newKp = CryptoUtil.signKeypair(bytes);

        assertEquals(newKp.getPublicKey().getRaw().length, 32);
        assertEquals(newKp.getPrivateKey().getRaw().length, 64);
    }

    @Test
    public void testSignDetached() {
        Signature sig = CryptoUtil.signDetached(msg, signKp.getPrivateKey());

        assertEquals(sig.getBytes().length, 64);

        assertTrue(CryptoUtil.verify(sig, msg, signKp.getPublicKey()));
    }

    @Test
    public void testCryptoBox() {
        byte[] nonce = CryptoUtil.randomBytes(24);
        byte[] box = CryptoUtil.cryptoBox(
                nonce,
                msg,
                signKp.getPublicKey().getRaw(),
                signKp.getPrivateKey().getRaw()
        );

        assertNotNull(box);
        // The size of the box depends on the message.
        // If you change the message make sure to change the expected value here as well.
        assertEquals(box.length, 58);

        byte[] openedBox = CryptoUtil.cryptoBoxOpen(
                nonce,
                box,
                signKp.getPublicKey().getRaw(),
                signKp.getPrivateKey().getRaw()
        );

        assertArrayEquals(msg, openedBox);
    }

    @Test
    public void testGenericHash() {
        byte[] genericHash = CryptoUtil.genericHash(msg, msg.length);
        assertEquals(genericHash.length, 32);
    }

    // Sign to encrypt keys
    @Test
    public void testEd25519toCurve25519() {
        byte[] encryptSecretKey = CryptoUtil.signToEncryptPrivateKey(signKp.getPrivateKey().getRaw());
        byte[] encryptPublicKey = CryptoUtil.signToEncryptPublicKey(signKp.getPublicKey().getRaw());

        assertEquals(encryptSecretKey.length, 32);
        assertEquals(encryptPublicKey.length, 32);
    }

    @Test
    public void testSkToPk() {
        byte[] publickey = CryptoUtil.encryptPublicFromPrivate(boxKp.getPrivateKey().getRaw());

        assertEquals(publickey.length, 32);
        assertArrayEquals(boxKp.getPublicKey().getRaw(), publickey);
    }

    @Test
    public void testIsValidAddress() {
        assertTrue(CryptoUtil.isValidAddress("3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy", Encoding.BASE58));
        assertTrue(CryptoUtil.isValidAddress("AVTvv73vv70KEwHvv73vv73vv73vv70c77+977+977+9H++/vRwv77+9RFfvv73vv73vv70O", Encoding.BASE64));
    }
}
