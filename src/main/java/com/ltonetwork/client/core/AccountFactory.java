package com.ltonetwork.client.core;

import com.google.common.primitives.Bytes;
import com.ltonetwork.client.exceptions.InvalidAccountException;
import com.ltonetwork.client.types.*;
import com.ltonetwork.client.utils.CryptoUtil;
import com.ltonetwork.client.utils.Encoder;
import com.ltonetwork.seasalt.hash.Blake2b256;
import com.ltonetwork.seasalt.hash.SHA256;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

public class AccountFactory {
    public static final char ADDRESS_VERSION = 0x1;

    protected byte network;
    protected int nonce;

    public AccountFactory(byte network, int nonce) {
        if (network != 'T' && network != 'L')
            throw new IllegalArgumentException("Expected network Testnet or LTO (mainnet)");
        this.network = network;
        this.nonce = nonce;
    }

    public AccountFactory(String network, int nonce) {
        byte networkByte = network.toUpperCase().substring(0, 1).getBytes(StandardCharsets.UTF_8)[0];
        if (networkByte != 'T' && networkByte != 'L')
            throw new IllegalArgumentException("Expected network Testnet or LTO (mainnet)");
        this.network = networkByte;
        this.nonce = nonce;
    }

    public AccountFactory(byte network) {
        this(network, new Random().nextInt(0xFFFF + 1));
    }

    public AccountFactory(String network) {
        this(network, new Random().nextInt(0xFFFF + 1));
    }

    public static byte mainnetByte() {
        return 'L';
    }

    public static byte testnetByte() {
        return 'T';
    }

    public KeyPair calcKeys(KeyPair keys) {
        if (keys.getPrivateKey() == null) {
            return new KeyPair(keys.getPublicKey(), null);
        }

        PublicKey calcPublicKey = CryptoUtil.publicFromPrivate(keys.getPrivateKey());

        if (keys.getPublicKey() != null && !keys.getPublicKey().getBase58().equals(calcPublicKey.getBase58())) {
            throw new InvalidAccountException("Public key doesn't match private key");
        }

        return new KeyPair(
                calcPublicKey,
                keys.getPrivateKey()
        );
    }

    public KeyPair calcKeys(PrivateKey key) {
        return calcKeys(new KeyPair(null, key));
    }

    public KeyPair calcKeys(byte[] privateKey, Key.KeyType keyType) {
        return calcKeys(new PrivateKey(privateKey, keyType));
    }

    public Address createAddress(PublicKey publicKey) {
        // if encrypt key
        if (publicKey.getType() == Key.KeyType.CURVE25519) {
            throw new IllegalArgumentException("Address can not be created with encrypting key of type Curve25519");
        }

        byte[] publicKeyHash = Arrays.copyOfRange(SHA256.hash(Blake2b256.hash(publicKey.getRaw())).getBytes(), 0, 20);

        byte[] packed = Bytes.concat(
                new byte[]{(byte) ADDRESS_VERSION},
                new byte[]{network},
                publicKeyHash
        );

        byte[] checkSum = Arrays.copyOfRange(SHA256.hash(Blake2b256.hash(packed)).getBytes(), 0, 4);

        byte[] addressBytes = Bytes.concat(
                packed,
                checkSum
        );

        String addressString = Encoder.base58Encode(addressBytes);

        return new Address(addressString);
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

    public Account createPublic(PublicKey publicKey) {
        if (publicKey == null) throw new IllegalArgumentException("Provided signing key is empty");

        KeyPair sign;
        KeyPair encrypt;
        Address address;

        switch (publicKey.getType()) {
            case ED25519:
                sign = new KeyPair(publicKey, null);
                encrypt = new KeyPair(CryptoUtil.signToEncryptPublicKey(publicKey.getRaw()), null);
                address = createAddress(publicKey);
                return new Account(address, encrypt, sign);
            case SECP256K1:
            case SECP256R1:
                sign = new KeyPair(publicKey, null);
                return new Account(null, null, sign);
            case CURVE25519:
                encrypt = new KeyPair(publicKey, null);
                return new Account(null, encrypt, null);
            default:
                throw new IllegalArgumentException("Unknown curve");
        }
    }

    public int getNonce() {
        return nonce;
    }
}
