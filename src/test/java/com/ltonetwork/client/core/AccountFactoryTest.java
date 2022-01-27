package com.ltonetwork.client.core;

import com.ltonetwork.client.exceptions.InvalidAccountException;
import com.ltonetwork.client.types.Key;
import com.ltonetwork.client.types.KeyPair;
import com.ltonetwork.client.utils.CryptoUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class AccountFactoryTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    AccountFactory af;
    KeyPair sign;
    KeyPair encrypt;
    String address;

    @Before
    public void init() {
        af = new AccountFactory((byte) 'T', 123);
        sign = CryptoUtil.signKeypair("test".getBytes(StandardCharsets.UTF_8), Key.KeyType.ED25519);
        encrypt = CryptoUtil.signToEncryptKeyPair(sign);
        address = "3MuAaEwy1NDU8E5Y6WocfFq5EpMCMkCsvhR";
    }

    @Test
    public void testCalcKeysWithKeyPair() {
        KeyPair kp = af.calcKeys(sign);
        assertEquals(sign.getPublicKey().getBase58(), kp.getPublicKey().getBase58());
        assertEquals(sign.getPrivateKey().getBase58(), kp.getPrivateKey().getBase58());

        KeyPair kp2 = af.calcKeys(encrypt);
        assertEquals(encrypt.getPublicKey().getBase58(), kp2.getPublicKey().getBase58());
        assertEquals(encrypt.getPrivateKey().getBase58(), kp2.getPrivateKey().getBase58());
    }

    @Test
    public void testCalcKeysWithKeyPairFail() {
        expectedEx.expect(InvalidAccountException.class);
        expectedEx.expectMessage("Public key doesn't match private key");

        KeyPair kp = new KeyPair(sign.getPublicKey(), encrypt.getPrivateKey());
        af.calcKeys(kp);
    }

    @Test
    public void testCalcKeysWithKeyBytes() {
        KeyPair kp = af.calcKeys(sign.getPrivateKey().getRaw(), sign.getPrivateKey().getType());
        assertEquals(sign.getPublicKey().getBase58(), kp.getPublicKey().getBase58());
        assertEquals(sign.getPrivateKey().getBase58(), kp.getPrivateKey().getBase58());

        KeyPair kp2 = af.calcKeys(encrypt.getPrivateKey().getRaw(), encrypt.getPrivateKey().getType());
        assertEquals(encrypt.getPublicKey().getBase58(), kp2.getPublicKey().getBase58());
        assertEquals(encrypt.getPrivateKey().getBase58(), kp2.getPrivateKey().getBase58());
    }

    @Test
    public void testCreateAddress() {
        String generatedAddress = af.createAddress(sign.getPublicKey()).getAddress();
        assertEquals(address, generatedAddress);
    }

    @Test
    public void testCreate() {
        Account created = af.create(sign.getPrivateKey());

        assertEquals(sign.getPublicKey().getBase58(), created.getPublicSignKey().getBase58());
        assertEquals(encrypt.getPublicKey().getBase58(), created.getPublicEncryptKey().getBase58());
        assertEquals(address, created.getAddress());
    }

    @Test
    public void testCreateFromSeed() {
        Account created = af.createFromSeed("test");

        assertEquals(sign.getPublicKey().getBase58(), created.getPublicSignKey().getBase58());
        assertEquals(encrypt.getPublicKey().getBase58(), created.getPublicEncryptKey().getBase58());
        assertEquals(address, created.getAddress());
    }

    @Test
    public void testCreatePublic() {
        Account created = af.createPublic(sign.getPublicKey());

        assertEquals(sign.getPublicKey().getBase58(), created.getPublicSignKey().getBase58());
        assertEquals(encrypt.getPublicKey().getBase58(), created.getPublicEncryptKey().getBase58());
        assertEquals(address, created.getAddress());
    }
}
