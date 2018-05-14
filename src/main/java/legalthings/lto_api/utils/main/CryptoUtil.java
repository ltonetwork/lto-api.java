package legalthings.lto_api.utils.main;

import org.abstractj.kalium.crypto.Random;
import org.abstractj.kalium.keys.AuthenticationKey;
import static org.abstractj.kalium.encoders.Encoder.HEX;
import static org.abstractj.kalium.NaCl.Sodium.CRYPTO_SIGN_ED25519_BYTES;
import static org.abstractj.kalium.NaCl.Sodium.CRYPTO_BOX_CURVE25519XSALSA20POLY1305_NONCEBYTES;
import static org.abstractj.kalium.NaCl.Sodium.CRYPTO_AUTH_HMACSHA512256_KEYBYTES;
import org.abstractj.kalium.keys.VerifyKey;
import static org.abstractj.kalium.crypto.Util.checkLength;

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
	
	public static String crypto_sign_detached(String message, String secretkey) {
		AuthenticationKey key = new AuthenticationKey(secretkey.getBytes());
		return key.sign(message, HEX);
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
}
