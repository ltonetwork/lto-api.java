package com.ltonetwork.client.utils;

import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import com.goterl.lazysodium.exceptions.SodiumException;
import com.goterl.lazysodium.interfaces.Box;
import com.goterl.lazysodium.interfaces.GenericHash;
import com.goterl.lazysodium.interfaces.Sign;
import com.goterl.lazysodium.utils.LibraryLoader;
import com.ltonetwork.client.types.*;
import com.ltonetwork.seasalt.sign.ECDSA;
import com.ltonetwork.seasalt.sign.Ed25519;
import com.ltonetwork.seasalt.sign.Signature;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class CryptoUtil {

    private static final LazySodiumJava sodium;
    private static final Ed25519 ed25519 = new Ed25519();
    private static final ECDSA secp256k1 = new ECDSA("secp256k1");
    private static final ECDSA secp256r1 = new ECDSA("secp256r1");

    static {
        sodium = new LazySodiumJava(new SodiumJava(LibraryLoader.Mode.BUNDLED_ONLY));
    }

    public static byte[] randomBytes(int size) {
        return new SecureRandom().generateSeed(size);
    }

    public static byte[] genericHash(byte[] message, int length) {
        byte[] hash = new byte[GenericHash.BYTES];
        sodium.cryptoGenericHash(hash, GenericHash.BYTES, message, length, null, 0);
        return hash;
    }

    public static boolean isValidAddress(String address, Encoding encoding) {
        if (encoding.equals(Encoding.BASE58) && !Encoder.isBase58Encoded(address)) return false;
        return !encoding.equals(Encoding.BASE64) || Encoder.isBase64Encoded(address);

//        return Encoder.decode(address, encoding).length() == 23;
    }

    // encrypting
    public static int cryptoBoxNoncebytes() {
        return Box.NONCEBYTES;
    }

    public static byte[] cryptoBox(byte[] nonce, byte[] message, byte[] publicKey, byte[] privatekey) {
        com.goterl.lazysodium.utils.KeyPair kp = lazySodiumKeypair(publicKey, privatekey);
        try {
            return sodium.cryptoBoxEasy(new String(message, StandardCharsets.UTF_8), nonce, kp).getBytes();
        } catch (SodiumException e) {
            throw new IllegalArgumentException("Unable to create crypto box");
        }
    }

    public static byte[] cryptoBoxOpen(byte[] nonce, byte[] ciphertext, byte[] publicKey, byte[] privatekey) {
        com.goterl.lazysodium.utils.KeyPair kp = lazySodiumKeypair(publicKey, privatekey);
        try {
            return sodium.cryptoBoxOpenEasy(new String(ciphertext, StandardCharsets.UTF_8), nonce, kp).getBytes();
        } catch (SodiumException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static KeyPair cryptoBoxSeedKeypair(byte[] seed) {
        byte[] privateKey = new byte[Box.SECRETKEYBYTES];
        byte[] publicKey = new byte[Box.PUBLICKEYBYTES];
        sodium.cryptoBoxSeedKeypair(publicKey, privateKey, seed);
        return new KeyPair(
                new PublicKey(publicKey),
                new PrivateKey(privateKey)
        );
    }

    public static KeyPair signToEncryptKeyPair(KeyPair signKeyPair) {
        return new KeyPair(
                new PublicKey(signToEncryptPublicKey(signKeyPair.getPublicKey().getRaw()), Key.KeyType.CURVE25519),
                new PrivateKey(signToEncryptPrivateKey(signKeyPair.getPrivateKey().getRaw()), Key.KeyType.CURVE25519)
        );
    }

    public static byte[] signToEncryptPublicKey(byte[] publicKey) {
        byte[] key = new byte[Sign.CURVE25519_PUBLICKEYBYTES];
        sodium.convertPublicKeyEd25519ToCurve25519(key, publicKey);
        return key;
    }

    public static byte[] signToEncryptPrivateKey(byte[] privateKey) {
        byte[] key = new byte[Sign.CURVE25519_SECRETKEYBYTES];
        sodium.convertSecretKeyEd25519ToCurve25519(key, privateKey);
        return key;
    }

    public static byte[] encryptPublicFromPrivate(byte[] privateKey) {
        byte[] publicKey = new byte[Box.PUBLICKEYBYTES];
        sodium.cryptoScalarMultBase(publicKey, privateKey);
        return publicKey;
    }

    // signing
    public static KeyPair signKeypair(byte[] seed, Key.KeyType keyType) {
        switch (keyType) {
            case ED25519:
                return new KeyPair(ed25519.keyPairFromSeed(seed));
            case SECP256K1:
                return new KeyPair(secp256k1.keyPairFromSeed(seed));
            case SECP256R1:
                return new KeyPair(secp256r1.keyPairFromSeed(seed));
            case CURVE25519:
                throw new IllegalArgumentException("Cannot create signing key pair for encryption CURVE25519 curve");
            default:
                throw new IllegalArgumentException("Unknown curve");
        }
    }

    public static KeyPair signKeypair(byte[] seed) {
        return signKeypair(seed, Key.KeyType.ED25519);
    }

    public static byte[] signPublicFromPrivate(byte[] privateKey) {
        byte[] publicKey = new byte[Sign.ED25519_PUBLICKEYBYTES];
        sodium.cryptoSignEd25519SkToPk(publicKey, privateKey);
        return publicKey;
    }

    public static Signature signDetached(byte[] message, PrivateKey privateKey) {
        switch (privateKey.getType()) {
            case ED25519:
                return ed25519.signDetached(message, privateKey.getRaw());
            case SECP256K1:
                return secp256k1.signDetached(message, privateKey.getRaw());
            case SECP256R1:
                return secp256r1.signDetached(message, privateKey.getRaw());
            case CURVE25519:
                throw new IllegalArgumentException("Cannot sign message with encryption CURVE25519 key pair");
            default:
                throw new IllegalArgumentException("Unknown curve");
        }
    }

    public static boolean verify(Signature signature, byte[] message, PublicKey publicKey) {
        switch (publicKey.getType()) {
            case ED25519:
                return ed25519.verify(message, signature.getBytes(), publicKey.getRaw());
            case SECP256K1:
                return secp256k1.verify(message, signature.getBytes(), publicKey.getRaw());
            case SECP256R1:
                return secp256r1.verify(message, signature.getBytes(), publicKey.getRaw());
            case CURVE25519:
                throw new IllegalArgumentException("Cannot verify message with encryption CURVE25519 key pair");
            default:
                throw new IllegalArgumentException("Unknown curve");
        }
    }


    private static com.goterl.lazysodium.utils.KeyPair lazySodiumKeypair(byte[] publicKey, byte[] privatekey) {
        com.goterl.lazysodium.utils.Key pk = com.goterl.lazysodium.utils.Key.fromBytes(publicKey);
        com.goterl.lazysodium.utils.Key sk = com.goterl.lazysodium.utils.Key.fromBytes(privatekey);
        return new com.goterl.lazysodium.utils.KeyPair(pk, sk);
    }

}
