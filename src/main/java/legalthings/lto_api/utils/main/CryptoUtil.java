package legalthings.lto_api.utils.main;

import org.abstractj.kalium.crypto.Random;
import org.abstractj.kalium.keys.AuthenticationKey;
import static org.abstractj.kalium.encoders.Encoder.HEX;
import static org.abstractj.kalium.NaCl.Sodium.CRYPTO_SIGN_ED25519_BYTES;
import static org.abstractj.kalium.NaCl.Sodium.CRYPTO_SIGN_ED25519_PUBLICKEYBYTES;
import static org.abstractj.kalium.NaCl.Sodium.CRYPTO_BOX_CURVE25519XSALSA20POLY1305_NONCEBYTES;

public class CryptoUtil {
	public static byte[] random_bytes(int size) {
		return new Random().randomBytes(size);
	}
	
	public static int crypto_sign_bytes() {
		return CRYPTO_SIGN_ED25519_BYTES;
	}
	
	public static int crypto_sign_publickeybytes() {
		return CRYPTO_SIGN_ED25519_PUBLICKEYBYTES;
	}
	
	public static int crypto_box_noncebytes() {
		return CRYPTO_BOX_CURVE25519XSALSA20POLY1305_NONCEBYTES;
	}
	
	public static String crypto_sign_detached(String message, String secretkey) {
		AuthenticationKey key = new AuthenticationKey(secretkey.getBytes());
		return key.sign(message, HEX);
	}
	
	public static boolean crypto_sign_verify_detached(String signature, String message, String signkey) {
		AuthenticationKey key = new AuthenticationKey(signkey.getBytes());
		return key.verify(message, signature, HEX);
	}
}
