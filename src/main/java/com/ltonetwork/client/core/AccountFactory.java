package com.ltonetwork.client.core;

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

    protected String network;
    protected int nonce;

    public AccountFactory(int network, int nonce) {
        this.network = Character.toString((char) network).substring(0, 1);
        this.nonce = nonce;
    }

    public AccountFactory(String network, int nonce) {
        this.network = network.substring(0, 1);
        this.nonce = nonce;
    }

    public AccountFactory(int network) {
        this(network, new Random().nextInt(0xFFFF + 1));
    }

    public AccountFactory(String network) {
        this(network, new Random().nextInt(0xFFFF + 1));
    }

    public AccountFactory(Object network) {
        this(network, new Random().nextInt(0xFFFF + 1));
    }

    public AccountFactory(Object network, int nonce) {
        if (network instanceof String) {
            this.network = network.toString().substring(0, 1);
        }
        if (network instanceof Number) {
            this.network = Character.toString((char) ((Number) network).intValue());
        }
        this.nonce = nonce;
    }

    public byte[] createAccountSeed(String seedText) {
        byte[] seedBase = PackUtil.packLaStar(nonce, seedText);

        byte[] secureSeed = Encoder.hexDecode(Keccak256.hash(CryptoUtil.genericHash(seedBase, 32)).getBytes());

        return SHA256.hash(secureSeed).getBytes();
    }

    public byte[] createAddress(PublicKey publickey) {
        // if signing key
        if (publickey.getType() != Key.KeyType.CURVE25519) {
            publickey = new PublicKey(CryptoUtil.signToEncryptPublicKey(publickey.getRaw()));
        }

        String publicKeyHash = new String(
                Keccak256.hash(CryptoUtil.genericHash(publickey.getRaw(), 32)).getBytes(),
                StandardCharsets.UTF_8
        ).substring(0, 40);

        byte[] packed = PackUtil.packCaH40(ADDRESS_VERSION, network, publicKeyHash);
        String checkSum = new String(
                Keccak256.hash(CryptoUtil.genericHash(packed, packed.length)).getBytes(),
                StandardCharsets.UTF_8
        ).substring(0, 8);

        return PackUtil.packCaH40H8(ADDRESS_VERSION, network, publicKeyHash, checkSum);
    }

    public Account seed(String seedText) {
        byte[] seed = createAccountSeed(seedText);
        KeyPair signKeys = createSignKeys(seed);

        return new Account(
                new Address(signKeys.getPublicKey().getBase58()),
                createEncryptKeys(seed),
                signKeys
        );
    }

    public KeyPair convertSignToEncrypt(KeyPair sign) {
        byte[] privateKey = CryptoUtil.signToEncryptPrivateKey(sign.getPrivateKey().getRaw());

        int last = privateKey.length - 1;
        privateKey[last] = privateKey[last] % 2 == 1 ? ((byte) ((privateKey[last] | 0x80) & ~0x40)) : privateKey[last];

        PrivateKey encryptPrivateKey = new PrivateKey(privateKey);

        PublicKey encryptPublicKey = new PublicKey(CryptoUtil.signToEncryptPublicKey(sign.getPublicKey().getRaw()));

        return new KeyPair(encryptPublicKey, encryptPrivateKey);
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

    public Account create(KeyPair sign, KeyPair encrypt, Address address) {
        KeyPair signKeys = sign != null ? calcKeys(sign) : null;
        KeyPair encryptKeys = encrypt != null ? calcKeys(encrypt) : (sign != null ? convertSignToEncrypt(signKeys) : null);

        return new Account(address, encryptKeys, signKeys);
    }

    public Account create(KeyPair sign) {
        KeyPair signKeys = sign != null ? calcKeys(sign) : null;
        KeyPair encryptKeys = sign != null ? calcKeys(convertSignToEncrypt(sign)) : null;

        return create(signKeys, encryptKeys, null);
    }

    public Account createPublic(PublicKey signPublicKey) {
        if (signPublicKey == null || signPublicKey.getRaw().length == 0)
            throw new IllegalArgumentException("Provided signing key is empty");
        return create(new KeyPair(signPublicKey, null));
    }

    protected int getNonce() {
        return nonce++;
    }

    protected KeyPair createSignKeys(byte[] seed) {
        return CryptoUtil.signKeypair(seed);
    }

    protected KeyPair createEncryptKeys(byte[] seed) {
        return CryptoUtil.cryptoBoxSeedKeypair(seed);
    }

    protected byte[] calcAddress(byte[] address, KeyPair sign, KeyPair encrypt) {
        byte[] _address = null;

        byte[] addrSign = (sign != null && sign.getPublicKey() != null) ? createAddress(sign.getPublicKey()) : null;
        byte[] addrEncrypt = (encrypt != null && encrypt.getPublicKey() != null) ? createAddress(encrypt.getPublicKey()) : null;

        if (addrSign != null && addrEncrypt != null && !Arrays.equals(addrSign, addrEncrypt)) {
            throw new InvalidAccountException("Sign key doesn't match encrypt key");
        }

        if (address != null) {
            if ((addrSign != null && !Arrays.equals(address, addrSign)) || (addrEncrypt != null && !Arrays.equals(address, addrEncrypt))) {
                throw new InvalidAccountException("Address doesn't match keypair; possible network mismatch");
            }

            _address = new byte[address.length];
            System.arraycopy(address, 0, _address, 0, address.length);
        } else {
            if (addrSign != null) {
                _address = new byte[addrSign.length];
                System.arraycopy(addrSign, 0, _address, 0, addrSign.length);
            } else if (addrEncrypt != null) {
                _address = new byte[addrEncrypt.length];
                System.arraycopy(addrEncrypt, 0, _address, 0, addrEncrypt.length);
            }
        }
        return _address;
    }

    protected byte getNetworkByte() {
        if (this.network.equals("T")) return (byte) 84;
        else return (byte) 76;
    }
}
