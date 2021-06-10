package legalthings.lto_api.utils.main;

import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import com.goterl.lazysodium.exceptions.SodiumException;
import com.goterl.lazysodium.interfaces.*;
import com.goterl.lazysodium.utils.LibraryLoader;

import legalthings.lto_api.lto.core.KeyPair;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class CryptoUtil {

    private static final LazySodiumJava sodium;

    static {
        sodium = new LazySodiumJava(new SodiumJava(LibraryLoader.Mode.BUNDLED_ONLY));
    }

    private static com.goterl.lazysodium.utils.KeyPair lazySodiumKeypair(byte[] publickey, byte[] privatekey) {
        com.goterl.lazysodium.utils.Key pk = com.goterl.lazysodium.utils.Key.fromBytes(publickey);
        com.goterl.lazysodium.utils.Key sk = com.goterl.lazysodium.utils.Key.fromBytes(privatekey);
        return new com.goterl.lazysodium.utils.KeyPair(pk, sk);
    }

    public static byte[] random_bytes(int size) {
        return sodium.randomBytesBuf(size);
    }

    public static int crypto_sign_bytes() {
        return Sign.BYTES;
    }

    public static int crypto_sign_publickeybytes() {
        return Sign.PUBLICKEYBYTES;
    }

    public static int crypto_box_noncebytes() {
        return Box.NONCEBYTES;
    }

    public static byte[] crypto_sign_detached(byte[] message, byte[] secretkey) {
        byte[] signature = new byte[crypto_sign_bytes()];
        sodium.cryptoSignDetached(signature, message, message.length, secretkey);
        return signature;
    }

    public static boolean crypto_sign_verify_detached(byte[] signature, byte[] message, byte[] signkey) {
        return sodium.cryptoSignVerifyDetached(signature, message, message.length, signkey);
    }

    public static byte[] crypto_box(byte[] nonce, byte[] message, byte[] publickey, byte[] privatekey) {
        com.goterl.lazysodium.utils.KeyPair kp = lazySodiumKeypair(publickey, privatekey);
        try {
            return sodium.cryptoBoxEasy(new String(message, StandardCharsets.UTF_8), nonce, kp).getBytes();
        } catch (SodiumException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] crypto_box_open(byte[] nonce, byte[] ciphertext, byte[] publickey, byte[] privatekey) {
        com.goterl.lazysodium.utils.KeyPair kp = lazySodiumKeypair(publickey, privatekey);
        try {
            return sodium.cryptoBoxOpenEasy(new String(ciphertext, StandardCharsets.UTF_8), nonce, kp).getBytes();
        } catch (SodiumException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] crypto_generichash(byte[] message, int length) {
        byte[] hash = new byte[GenericHash.BYTES];
        sodium.cryptoGenericHash(hash, GenericHash.BYTES, message, length, null, 0);
        return hash;
    }

    public static KeyPair crypto_sign_seed_keypair(byte[] seed) {
        byte[] secretkey = new byte[Sign.SECRETKEYBYTES];
        byte[] publickey = new byte[Sign.PUBLICKEYBYTES];

        sodium.cryptoSignSeedKeypair(publickey, secretkey, seed);

        return new KeyPair(publickey, secretkey);
    }

    public static KeyPair crypto_box_seed_keypair(byte[] seed) {
        byte[] secretkey = new byte[Box.SECRETKEYBYTES];
        byte[] publickey = new byte[Box.PUBLICKEYBYTES];
        sodium.cryptoBoxSeedKeypair(publickey, secretkey, seed);
        return new KeyPair(publickey, secretkey);
    }

    public static byte[] crypto_sign_ed25519_pk_to_curve25519(byte[] publickey) {
        byte[] key = new byte[Sign.CURVE25519_PUBLICKEYBYTES];
        sodium.convertPublicKeyEd25519ToCurve25519(key, publickey);
        return key;
    }

    public static byte[] crypto_sign_ed25519_sk_to_curve25519(byte[] secretkey) {
        byte[] key = new byte[Sign.CURVE25519_SECRETKEYBYTES];
        sodium.convertPublicKeyEd25519ToCurve25519(key, secretkey);
        return key;
    }

    public static byte[] crypto_sign_publickey_from_secretkey(byte[] secretkey) {
        byte[] publickey = new byte[Sign.ED25519_PUBLICKEYBYTES];
        sodium.cryptoSignEd25519SkToPk(publickey, secretkey);
        return publickey;
    }

    public static byte[] crypto_box_publickey_from_secretkey(byte[] secretkey) {
        byte[] publickey = new byte[Box.PUBLICKEYBYTES];
        sodium.cryptoScalarMultBase(publickey, secretkey);
        return publickey;
    }

    public static boolean isValidAddress(String address, String encoding) {
        if(encoding.equals("base58") && Pattern.matches("^[1-9A-HJ-NP-Za-km-z]+$", address)) return false;
        if(encoding.equals("base64") && Pattern.matches("^[A-Za-z0-9+/]+={0,2}$", address)) return false;

        return Encoder.decode(address, encoding).length() == 26;
    }
}
