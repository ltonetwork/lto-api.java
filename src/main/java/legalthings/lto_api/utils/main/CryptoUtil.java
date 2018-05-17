package legalthings.lto_api.utils.main;

import org.abstractj.kalium.crypto.Box;
import org.abstractj.kalium.crypto.Hash;
import org.abstractj.kalium.crypto.Random;
import org.abstractj.kalium.crypto.Util;

import static org.abstractj.kalium.NaCl.Sodium.CRYPTO_SIGN_ED25519_BYTES;
import static org.abstractj.kalium.NaCl.Sodium.CRYPTO_BOX_CURVE25519XSALSA20POLY1305_NONCEBYTES;
import static org.abstractj.kalium.NaCl.Sodium.CRYPTO_BOX_CURVE25519XSALSA20POLY1305_SECRETKEYBYTES;
import static org.abstractj.kalium.NaCl.Sodium.CRYPTO_BOX_CURVE25519XSALSA20POLY1305_PUBLICKEYBYTES;
import static org.abstractj.kalium.NaCl.Sodium.CRYPTO_AUTH_HMACSHA512256_KEYBYTES;

import org.abstractj.kalium.keys.VerifyKey;

import static org.abstractj.kalium.NaCl.*;
import static org.abstractj.kalium.crypto.Util.slice;
import static org.abstractj.kalium.crypto.Util.zeros;

import jnr.ffi.byref.LongLongByReference;
import legalthings.lto_api.utils.core.JsonObject;
//import org.abstractj.kalium.crypto.Hash;

import org.libsodium.jni.Sodium;
import org.libsodium.jni.NaCl;

public class CryptoUtil {	
	public static byte[] random_bytes(int size) {
		return new Random().randomBytes(size);
	}
	
	public static int crypto_sign_bytes() {
		return CRYPTO_SIGN_ED25519_BYTES;
	}
	
	public static int crypto_sign_publickeybytes() {
		return CRYPTO_AUTH_HMACSHA512256_KEYBYTES;
	}
	
	public static int crypto_box_noncebytes() {
		return CRYPTO_BOX_CURVE25519XSALSA20POLY1305_NONCEBYTES;
	}
	
	public static byte[] crypto_sign_detached(byte[] message, byte[] secretkey) {
		byte[] signature = Util.prependZeros(CRYPTO_SIGN_ED25519_BYTES, message);
		LongLongByReference bufferLen = new LongLongByReference(0);
		sodium().crypto_sign_ed25519(signature, bufferLen, message, message.length, secretkey);
		signature = slice(signature, 0, CRYPTO_SIGN_ED25519_BYTES);
        return signature;
	}
	
	public static boolean crypto_sign_verify_detached(byte[] signature, byte[] message, byte[] signkey) {
		boolean ret;
		try {
			VerifyKey key = new VerifyKey(signkey);
			ret = key.verify(message, signature);
		} catch (RuntimeException e) {
			ret = false;
		}
		return ret;
	}
	
	public static byte[] crypto_box(byte[] nonce, byte[] message, byte[] publickey, byte[] privatekey) {
		Box box = new Box(publickey, privatekey);
		return box.encrypt(nonce, message);
	}
	
	public static byte[] crypto_box_open(byte[] nonce, byte[] ciphertext, byte[] publickey, byte[] privatekey) {
		Box box = new Box(publickey, privatekey);
		return box.decrypt(nonce, ciphertext);
	}
	
	public static byte[] crypto_generichash(byte[] message, int length) {
		Hash hash = new Hash();
		return hash.blake2(message);
	}
	
	public static JsonObject crypto_sign_seed_keypair(byte[] seed) {
		byte[] secretkey = zeros(CRYPTO_BOX_CURVE25519XSALSA20POLY1305_SECRETKEYBYTES * 2);
		byte[] publickey = zeros(CRYPTO_BOX_CURVE25519XSALSA20POLY1305_PUBLICKEYBYTES);
		
		sodium().crypto_sign_ed25519_seed_keypair(publickey, secretkey, seed);
		
		JsonObject keypair = new JsonObject();
		keypair.putByte("publickey", publickey);
		keypair.putByte("secretkey", secretkey);
		
		return keypair;
	}
	
	public static JsonObject crypto_box_seed_keypair(byte[] seed) {
		byte[] secretkey = zeros(CRYPTO_BOX_CURVE25519XSALSA20POLY1305_SECRETKEYBYTES);
		byte[] publickey = zeros(CRYPTO_BOX_CURVE25519XSALSA20POLY1305_PUBLICKEYBYTES);
		
		NaCl.sodium();
		Sodium.crypto_box_seed_keypair(publickey, secretkey, seed);
		
		JsonObject keypair = new JsonObject();
		keypair.putByte("publickey", publickey);
		keypair.putByte("secretkey", secretkey);
		
		return keypair;
	}
	
	public static byte[] crypto_sign_ed25519_pk_to_curve25519(byte[] publickey) {
		byte[] key = new byte[publickey.length];
		
		NaCl.sodium();
		Sodium.crypto_sign_ed25519_pk_to_curve25519(key, publickey);
		return key;
	}
	
	public static byte[] crypto_sign_ed25519_sk_to_curve25519(byte[] publickey) {
		byte[] key = new byte[publickey.length];
		
		NaCl.sodium();
		Sodium.crypto_sign_ed25519_sk_to_curve25519(key, publickey);
		return key;
	}
	
	public static byte[] crypto_sign_publickey_from_secretkey(byte[] secretkey) {
		NaCl.sodium();
		return secretkey;
//		Sodium.public
	}
	
	public static byte[] crypto_box_publickey_from_secretkey(byte[] secretkey) {
		NaCl.sodium();
		return secretkey;
	}
}
