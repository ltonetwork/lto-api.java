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

    public byte[] createAddress(PublicKey publickey, String type) {
        if (type.equals("sign")) {
            publickey = new PublicKey(CryptoUtil.signToEncryptPublicKey(publickey.getRaw()));
        }

        String publickeyHash = new String(
                Keccak256.hash(CryptoUtil.genericHash(publickey.getRaw(), 32)).getBytes(),
                StandardCharsets.UTF_8
        ).substring(0, 40);

        byte[] packed = PackUtil.packCaH40(ADDRESS_VERSION, network, publickeyHash);
        String chksum = new String(
                Keccak256.hash(CryptoUtil.genericHash(packed, packed.length)).getBytes(),
                StandardCharsets.UTF_8
        ).substring(0, 8);

        return PackUtil.packCaH40H8(ADDRESS_VERSION, network, publickeyHash, chksum);
    }

    public byte[] createAddress(PublicKey publickey) {
        return createAddress(publickey, "encrypt");
    }

    public Account seed(String seedText) {
        byte[] seed = createAccountSeed(seedText);
        KeyPair signKeys = createSignKeys(seed);
        byte chainId = 'T';

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

    public KeyPair calcKeys(KeyPair keys, String type) {
        if (keys.getPrivateKey() == null) {
            return new KeyPair(keys.getPublicKey(), null);
        }

        byte[] secretkey = keys.getPrivateKey().getRaw();

        byte[] publickey = type.equals("sign") ? CryptoUtil.signPublicFromPrivate(secretkey) : CryptoUtil.encryptPublicFromPrivate(secretkey);

        if (keys.getPublicKey() != null && !Arrays.equals(keys.getPublicKey().getRaw(), publickey)) {
            throw new InvalidAccountException("Public " + type + " key doesn't match private " + type + " key");
        }

        return new KeyPair(
                new PublicKey(publickey),
                new PrivateKey(secretkey)
        );
    }

    public KeyPair calcKeys(PrivateKey key, String type) {
        KeyPair kp = new KeyPair(null, key);
        return calcKeys(kp, type);
    }

    public Account create(KeyPair sign, KeyPair encrypt, Address address) {
        KeyPair signKeys = sign != null ? calcKeys(sign, "sign") : null;
        KeyPair encryptKeys = encrypt != null ? calcKeys(encrypt, "encrypt") : (sign != null ? convertSignToEncrypt(signKeys) : null);

        return new Account(address, encryptKeys, signKeys);
    }

    public Account create(KeyPair sign) {
        KeyPair signKeys = sign != null ? calcKeys(sign, "sign") : null;
        KeyPair encryptKeys = sign != null ? calcKeys(convertSignToEncrypt(sign), "encrypt") : null;

        return create(signKeys, encryptKeys, null);
    }

    public Account create(PrivateKey signPrivateKey) {
        return create(new KeyPair(null, signPrivateKey));
    }

    public Account createPublic(PublicKey signkey) {
        KeyPair sign = null;
        if (signkey != null) {
            sign = new KeyPair(
                    signkey,
                    null
            );
        }

        KeyPair encrypt = convertSignToEncrypt(sign);

        return create(sign, encrypt, null);
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

        byte[] addrSign = (sign != null && sign.getPublicKey() != null) ? createAddress(sign.getPublicKey(), "sign") : null;
        byte[] addrEncrypt = (encrypt != null && encrypt.getPublicKey() != null) ? createAddress(encrypt.getPublicKey(), "encrypt") : null;

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
