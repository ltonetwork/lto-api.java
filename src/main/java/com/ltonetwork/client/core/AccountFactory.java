package com.ltonetwork.client.core;

import com.google.common.primitives.Bytes;
import com.ltonetwork.client.exceptions.InvalidAccountException;
import com.ltonetwork.client.types.*;
import com.ltonetwork.client.utils.CryptoUtil;
import com.ltonetwork.client.utils.Encoder;
import com.ltonetwork.client.utils.PackUtil;
import com.ltonetwork.seasalt.hash.Keccak256;
import com.ltonetwork.seasalt.hash.SHA256;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

public class AccountFactory {
    public static final char ADDRESS_VERSION = 0x1;

    protected byte network;
    protected int nonce;

    public AccountFactory(byte network, int nonce) {
        this.network = network;
        this.nonce = nonce;
    }

    public AccountFactory(String network, int nonce) {
        this.network = network.toUpperCase().substring(0, 1).getBytes(StandardCharsets.UTF_8)[0];
        this.nonce = nonce;
    }

    public AccountFactory(byte network) {
        this(network, new Random().nextInt(0xFFFF + 1));
    }

    public AccountFactory(String network) {
        this(network, new Random().nextInt(0xFFFF + 1));
    }

    public byte[] createAccountSeed(String seedText) {
        byte[] seedBase = PackUtil.packLaStar(nonce, seedText);

        byte[] secureSeed = Encoder.hexDecode(Keccak256.hash(CryptoUtil.genericHash(seedBase, 32)).getBytes());

        return SHA256.hash(secureSeed).getBytes();
    }

    public KeyPair calcKeys(KeyPair keys) {
        if (keys.getPrivateKey() == null) {
            return new KeyPair(keys.getPublicKey(), null);
        }

        byte[] privateKey = keys.getPrivateKey().getRaw();

        byte[] publicKey = keys.getPublicKey().getType() != Key.KeyType.CURVE25519 ? CryptoUtil.signPublicFromPrivate(privateKey) : CryptoUtil.encryptPublicFromPrivate(privateKey);

        if (keys.getPublicKey() != null && !Arrays.equals(keys.getPublicKey().getRaw(), publicKey)) {
            throw new InvalidAccountException("Public key doesn't match private key");
        }

        return new KeyPair(
                new PublicKey(publicKey),
                new PrivateKey(privateKey)
        );
    }

    public KeyPair calcKeys(PrivateKey key) {
        KeyPair kp = new KeyPair(null, key);
        return calcKeys(kp);
    }

    public Address createAddress(PublicKey publicKey) {
        // if signing key
        if (publicKey.getType() != Key.KeyType.CURVE25519) {
            publicKey = new PublicKey(CryptoUtil.signToEncryptPublicKey(publicKey.getRaw()));
        }

        String publicKeyHash = new String(
                Keccak256.hash(CryptoUtil.genericHash(publicKey.getRaw(), 32)).getBytes(),
                StandardCharsets.UTF_8
        ).substring(0, 40);

        byte[] packed = Bytes.concat(
                new byte[]{(byte) ADDRESS_VERSION},
                new byte[]{network},
                publicKeyHash.getBytes(StandardCharsets.UTF_8)
        );

        String checkSum = new String(
                Keccak256.hash(CryptoUtil.genericHash(packed, packed.length)).getBytes(),
                StandardCharsets.UTF_8
        ).substring(0, 8);

        byte[] addressBytes = Bytes.concat(
                new byte[]{(byte) ADDRESS_VERSION},
                new byte[]{network},
                packed,
                checkSum.getBytes(StandardCharsets.UTF_8)
        );

        return new Address(new String(addressBytes));
    }

    public Address createAddress(byte[] publicKey) {
        return createAddress(new PublicKey(publicKey, Key.KeyType.ED25519));
    }

    public Account create(KeyPair sign, KeyPair encrypt, Address address) {
        KeyPair signKeys = calcKeys(sign);
        KeyPair encryptKeys = calcKeys(encrypt);

        return new Account(address, encryptKeys, signKeys);
    }

    public Account create(KeyPair sign) {
        KeyPair signKeys = calcKeys(sign);
        KeyPair encryptKeys = calcKeys(CryptoUtil.signToEncryptKeyPair(sign));
        Address address = createAddress(signKeys.getPublicKey());

        return create(signKeys, encryptKeys, address);
    }

    public Account create(PrivateKey signPrivateKey) {
        if (signPrivateKey.getType() == Key.KeyType.CURVE25519)
            throw new IllegalArgumentException("Private key should not be encrypting of type Curve25519");
        KeyPair signKeys = calcKeys(signPrivateKey);
        KeyPair encryptKeys = calcKeys(CryptoUtil.signToEncryptKeyPair(signKeys));
        Address address = createAddress(signKeys.getPublicKey());

        return create(signKeys, encryptKeys, address);
    }

    public Account createFromSeed(byte[] seedText) {
        KeyPair signKeys = CryptoUtil.signKeypair(seedText);
        KeyPair encryptKeys = CryptoUtil.signToEncryptKeyPair(signKeys);
        Address address = createAddress(signKeys.getPublicKey());

        return new Account(
                address,
                encryptKeys,
                signKeys
        );
    }

    public Account createFromSeed(String seedText) {
        return createFromSeed(seedText.getBytes(StandardCharsets.UTF_8));
    }

    public Account createPublic(PublicKey signPublicKey) {
        if (signPublicKey == null || signPublicKey.getRaw().length == 0)
            throw new IllegalArgumentException("Provided signing key is empty");
        return create(new KeyPair(signPublicKey, null));
    }

    public int getNonce() {
        return nonce;
    }

    public static byte mainnetByte(){
        return 'L';
    }

    public static byte testnetByte(){
        return 'T';
    }
}
