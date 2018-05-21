package legalthings.lto_api.utils.main;

import org.abstractj.kalium.crypto.Box;
import org.abstractj.kalium.crypto.Hash;
import org.abstractj.kalium.crypto.Random;
import org.abstractj.kalium.crypto.Util;


import org.abstractj.kalium.keys.VerifyKey;

import static org.abstractj.kalium.NaCl.*;
import static org.abstractj.kalium.crypto.Util.zeros;

import legalthings.lto_api.utils.core.JsonObject;
//import org.abstractj.kalium.crypto.Hash;

import org.libsodium.jni.Sodium;
import org.libsodium.jni.NaCl;

import jnr.ffi.LibraryLoader;

public class CryptoUtil {
	public static interface Sodium {
		int crypto_sign_bytes();
		int crypto_sign_publickeybytes();
		int crypto_sign_secretkeybytes();
		int crypto_box_publickeybytes();
		int crypto_box_secretkeybytes();
		int crypto_box_noncebytes();
		int crypto_generichash_bytes();
		int crypto_scalarmult_curve25519_bytes();
		
		int crypto_sign_detached(byte[] sig, long siglen[], byte[] m, long mlen, byte[] sk);
		int crypto_sign_verify_detached(byte[] sig, byte[] m, long mlen, byte[] pk);
		int crypto_generichash(byte[] out, int outlen, byte[] in, int inlen, byte[] key, int keylen);
		int crypto_sign_ed25519_pk_to_curve25519(byte[] curve25519_pk, byte[] ed25519_pk);
		int crypto_sign_ed25519_sk_to_curve25519(byte[] curve25519_pk, byte[] ed25519_pk);
		int crypto_sign_seed_keypair(byte[] pk, byte[] sk, byte[] seed);
		int crypto_box_seed_keypair(byte[] pk, byte[] sk, byte[] seed);
    }
	
	private static Sodium sodium = null;
	
	static {
		sodium = LibraryLoader.create(Sodium.class).load("/usr/local/lib/libsodium.so");
	}
	
	public static byte[] random_bytes(int size) {
		return new Random().randomBytes(size);
	}
	
	public static int crypto_sign_bytes() {
		return sodium.crypto_sign_bytes();
	}
	
	public static int crypto_sign_publickeybytes() {
		return sodium.crypto_sign_publickeybytes();
	}
	
	public static int crypto_box_noncebytes() {
		return sodium.crypto_box_noncebytes();
	}
	
	public static byte[] crypto_sign_detached(byte[] message, byte[] secretkey) {
		byte[] signature = new byte[crypto_sign_bytes()];
		sodium.crypto_sign_detached(signature, null, message, message.length, secretkey);
		return signature;
	}
	
	public static boolean crypto_sign_verify_detached(byte[] signature, byte[] message, byte[] signkey) {
		return sodium.crypto_sign_verify_detached(signature, message, message.length, signkey) == 0;
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
		byte[] hash = zeros(sodium.crypto_generichash_bytes());
		sodium.crypto_generichash(hash, sodium.crypto_generichash_bytes(), message, message.length, null, 0);
		return hash;
	}
	
	public static JsonObject crypto_sign_seed_keypair(byte[] seed) {
		byte[] secretkey = zeros(sodium.crypto_sign_secretkeybytes());
		byte[] publickey = zeros(sodium.crypto_sign_publickeybytes());
		
		sodium.crypto_sign_seed_keypair(publickey, secretkey, seed);
		
		JsonObject keypair = new JsonObject();
		keypair.putByte("publickey", publickey);
		keypair.putByte("secretkey", secretkey);
		
		return keypair;
	}
	
	public static JsonObject crypto_box_seed_keypair(byte[] seed) {
		byte[] secretkey = zeros(sodium.crypto_box_secretkeybytes());
		byte[] publickey = zeros(sodium.crypto_box_publickeybytes());
		
		sodium.crypto_box_seed_keypair(publickey, secretkey, seed);
		
		JsonObject keypair = new JsonObject();
		keypair.putByte("publickey", publickey);
		keypair.putByte("secretkey", secretkey);
		
		return keypair;
	}
	
	public static byte[] crypto_sign_ed25519_pk_to_curve25519(byte[] publickey) {
		byte[] key = zeros(sodium.crypto_scalarmult_curve25519_bytes());
		sodium.crypto_sign_ed25519_pk_to_curve25519(key, publickey);
		return key;
	}
	
	public static byte[] crypto_sign_ed25519_sk_to_curve25519(byte[] secretkey) {
		byte[] key = zeros(sodium.crypto_scalarmult_curve25519_bytes());
		sodium.crypto_sign_ed25519_sk_to_curve25519(key, secretkey);
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
