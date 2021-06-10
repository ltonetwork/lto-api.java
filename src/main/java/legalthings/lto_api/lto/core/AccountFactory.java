package legalthings.lto_api.lto.core;

import java.util.Arrays;
import java.util.Random;

import legalthings.lto_api.lto.exceptions.InvalidAccountException;
import legalthings.lto_api.utils.core.BinHex;
import legalthings.lto_api.utils.main.CryptoUtil;
import legalthings.lto_api.utils.main.HashUtil;
import legalthings.lto_api.utils.main.PackUtil;

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

        byte[] secureSeed = BinHex.hex2bin(HashUtil.Keccak256(CryptoUtil.crypto_generichash(seedBase, 32)));

        return HashUtil.SHA256(secureSeed);
    }

    public byte[] createAddress(byte[] publickey, String type) {
        if (type.equals("sign")) {
            publickey = CryptoUtil.crypto_sign_ed25519_pk_to_curve25519(publickey);
        }

        String publickeyHash = HashUtil.Keccak256(CryptoUtil.crypto_generichash(publickey, 32)).substring(0, 40);

        byte[] packed = PackUtil.packCaH40(ADDRESS_VERSION, network, publickeyHash);
        String chksum = HashUtil.Keccak256(CryptoUtil.crypto_generichash(packed, packed.length)).substring(0, 8);

        return PackUtil.packCaH40H8(ADDRESS_VERSION, network, publickeyHash, chksum);
    }

    public byte[] createAddress(byte[] publickey) {
        return createAddress(publickey, "encrypt");
    }

    public Account seed(String seedText) {
        byte[] seed = createAccountSeed(seedText);
        KeyPair signKeys = createSignKeys(seed);
        byte chainId = 'T';

        return new Account(signKeys.getPublickey(), chainId, createEncryptKeys(seed), signKeys);
    }

    public KeyPair convertSignToEncrypt(KeyPair sign) {
        KeyPair encrypt = new KeyPair();

        if (sign != null && sign.getSecretkey() != null) {
            byte[] secretkey = CryptoUtil.crypto_sign_ed25519_sk_to_curve25519(sign.getSecretkey());

            int last = secretkey.length - 1;
            secretkey[last] = secretkey[last] % 2 == 1 ? ((byte) ((secretkey[last] | 0x80) & ~0x40)) : secretkey[last];

            encrypt.setSecretkey(secretkey);
        }

        if (sign != null && sign.getPublickey() != null) {
            encrypt.setPublickey(CryptoUtil.crypto_sign_ed25519_pk_to_curve25519(sign.getPublickey()));
        }

        return encrypt;
    }

    public KeyPair calcKeys(KeyPair keys, String type) {
        if (keys.getSecretkey() == null) {
            return new KeyPair(keys.getPublickey(), null);
        }

        byte[] secretkey = keys.getSecretkey();

        byte[] publickey = type.equals("sign") ? CryptoUtil.crypto_sign_publickey_from_secretkey(secretkey) : CryptoUtil.crypto_box_publickey_from_secretkey(secretkey);

        if (keys.getPublickey() != null && !Arrays.equals(keys.getPublickey(), publickey)) {
            throw new InvalidAccountException("Public " + type + " key doesn't match private " + type + " key");
        }

        return new KeyPair(publickey, secretkey);
    }

    public Account create(KeyPair sign, byte chainId, KeyPair encrypt, byte[] address) {
        KeyPair signKeys = sign != null ? calcKeys(sign, "sign") : null;
        KeyPair encryptKeys = encrypt != null ? calcKeys(encrypt, "encrypt") : (sign != null ? convertSignToEncrypt(signKeys) : null);
        byte[] accountAddress = calcAddress(address, signKeys, encryptKeys);

        return new Account(accountAddress, chainId, encryptKeys, signKeys);
    }

    public Account createPublic(byte[] signkey, byte chainId, byte[] encryptkey) {
        KeyPair sign = null;
        if (signkey != null) {
            sign = new KeyPair(signkey, null);
        }

        KeyPair encrypt = null;
        if (encryptkey != null) {
            encrypt = new KeyPair(encryptkey, null);
        }

        return create(sign, chainId, encrypt, null);
    }

    protected int getNonce() {
        return nonce++;
    }

    protected KeyPair createSignKeys(byte[] seed) {
        return CryptoUtil.crypto_sign_seed_keypair(seed);
    }

    protected KeyPair createEncryptKeys(byte[] seed) {
        return CryptoUtil.crypto_box_seed_keypair(seed);
    }

    protected byte[] calcAddress(byte[] address, KeyPair sign, KeyPair encrypt) {
        byte[] _address = null;

        byte[] addrSign = (sign != null && sign.getPublickey() != null) ? createAddress(sign.getPublickey(), "sign") : null;
        byte[] addrEncrypt = (encrypt != null && encrypt.getPublickey() != null) ? createAddress(encrypt.getPublickey(), "encrypt") : null;

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
}
