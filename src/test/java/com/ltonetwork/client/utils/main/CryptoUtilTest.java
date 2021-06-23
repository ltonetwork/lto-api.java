package com.ltonetwork.client.utils.main;

import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.client.types.Key;
import com.ltonetwork.client.types.KeyPair;
import com.ltonetwork.client.utils.CryptoUtil;
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
                new Key(new byte[]{32, -45, 3, 80, 65, -47, -102, 82, 86, -126, -68, -39, 34, -101, -66, -80, -75, -90, 93, -93, 86, 18, 98, 101, 115, -39, 76, -122, -4, 118, 118, -12}, Encoding.RAW),
                new Key(new byte[]{69, -111, -58, -98, -93, -51, 49, 82, 63, 54, -88, -32, -70, 63, 62, -13, 8, 73, 91, -104, -121, -34, 119, 58, -8, -50, 72, -46, 73, -117, -83, -95, 32, -45, 3, 80, 65, -47, -102, 82, 86, -126, -68, -39, 34, -101, -66, -80, -75, -90, 93, -93, 86, 18, 98, 101, 115, -39, 76, -122, -4, 118, 118, -12}, Encoding.RAW)
        );

        boxKp = new KeyPair(
                new Key(new byte[]{95, 63, 13, -4, 101, -122, -90, -9, 79, -94, -52, -23, -120, 37, 49, -79, 41, -103, 39, 36, 122, -120, 116, -75, 46, -66, -21, -57, -124, -97, 108, 22}, Encoding.RAW),
                new Key(new byte[]{-109, 64, 73, -28, -58, 57, 122, -49, -6, -101, 0, 66, 121, 98, 94, -82, 62, -109, -62, 54, 112, 68, -42, 65, 28, 123, 106, 124, 98, 77, -101, -89}, Encoding.RAW)
        );

        msg = "my random msg".getBytes(StandardCharsets.UTF_8);
    }

    @Test
    public void testKeypairCreation() {
        byte[] bytes = CryptoUtil.random_bytes(32);

        KeyPair newKp = CryptoUtil.crypto_sign_seed_keypair(bytes);

        assertEquals(newKp.getPublickey().getValueBytes().length, 32);
        assertEquals(newKp.getSecretkey().getValueBytes().length, 64);
    }

    @Test
    public void testSignDetached() {
        byte[] sig = CryptoUtil.crypto_sign_detached(msg, signKp.getSecretkey().getValueBytes());

        assertEquals(sig.length, 64);

        assertTrue(CryptoUtil.crypto_sign_verify_detached(sig, msg, signKp.getPublickey().getValueBytes()));
    }

    @Test
    public void testCryptoBox() {
        byte[] nonce = CryptoUtil.random_bytes(24);
        byte[] box = CryptoUtil.crypto_box(
                nonce,
                msg,
                signKp.getPublickey().getValueBytes(),
                signKp.getSecretkey().getValueBytes()
        );

        assertNotNull(box);
        // The size of the box depends on the message.
        // If you change the message make sure to change the expected value here as well.
        assertEquals(box.length, 58);

        byte[] openedBox = CryptoUtil.crypto_box_open(
                nonce,
                box,
                signKp.getPublickey().getValueBytes(),
                signKp.getSecretkey().getValueBytes()
        );

        assertArrayEquals(msg, openedBox);
    }

    @Test
    public void testGenericHash() {
        byte[] genericHash = CryptoUtil.crypto_generichash(msg, msg.length);
        assertEquals(genericHash.length, 32);
    }

    // Sign to encrypt keys
    @Test
    public void testEd25519toCurve25519() {
        byte[] encryptSecretKey = CryptoUtil.crypto_sign_ed25519_sk_to_curve25519(signKp.getSecretkey().getValueBytes());
        byte[] encryptPublicKey = CryptoUtil.crypto_sign_ed25519_pk_to_curve25519(signKp.getPublickey().getValueBytes());

        assertEquals(encryptSecretKey.length, 32);
        assertEquals(encryptPublicKey.length, 32);
    }

    @Test
    public void testSkToPk() {
        byte[] publickey = CryptoUtil.crypto_box_publickey_from_secretkey(boxKp.getSecretkey().getValueBytes());

        assertEquals(publickey.length, 32);
        assertArrayEquals(boxKp.getPublickey().getValueBytes(), publickey);
    }

    @Test
    public void testIsValidAddress() {
        assertTrue(CryptoUtil.isValidAddress("3N51gbw5W3xvSkcAXtLnXc3SQh2m9e6TBcy", Encoding.BASE58));
        assertTrue(CryptoUtil.isValidAddress("AVTvv73vv70KEwHvv73vv73vv73vv70c77+977+977+9H++/vRwv77+9RFfvv73vv73vv70O", Encoding.BASE64));
    }
}
