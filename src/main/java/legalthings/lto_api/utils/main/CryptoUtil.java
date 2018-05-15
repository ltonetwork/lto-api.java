package legalthings.lto_api.utils.main;

import org.abstractj.kalium.crypto.Box;
import org.abstractj.kalium.crypto.Hash;
import org.abstractj.kalium.crypto.Random;
import org.abstractj.kalium.crypto.Util;

import static org.abstractj.kalium.NaCl.Sodium.CRYPTO_SIGN_ED25519_BYTES;
import static org.abstractj.kalium.NaCl.Sodium.CRYPTO_BOX_CURVE25519XSALSA20POLY1305_NONCEBYTES;
import static org.abstractj.kalium.NaCl.Sodium.CRYPTO_AUTH_HMACSHA512256_KEYBYTES;
import org.abstractj.kalium.keys.VerifyKey;

import static org.abstractj.kalium.NaCl.*;
import static org.abstractj.kalium.crypto.Util.slice;

import jnr.ffi.byref.LongLongByReference;
//import org.abstractj.kalium.crypto.Hash;


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
}
