/**
 * An account (aka wallet)
 */
package LTO;

import org.json.simple.JSONObject;
import LTO.DecryptException;

/**
 * @author moonbi
 *
 */
public class Account {
	/**
	 * Account public address
	 * @var string
	 */
	public String address;
	
	/**
	 * Sign kyes
	 * @var object
	 */
	public JSONObject sign;
	
	/**
	 * Encryption keys
	 * @var object
	 */	
	public JSONObject encrypt;
	
	/**
	 * Get a random nonce
	 * @codeCoverageIgnore
	 * 
	 * @return string
	 */
	protected String getNonce()
	{
		return "return random_bytes(\\sodium\\CRYPTO_BOX_NONCEBYTES);";
	}
	
	/**
     * Get base58 encoded address
     * 
     * @param string encoding  'raw', 'base58' or 'base64'
     * @return string
     */
	public String getAddress(String encoding) {
		return address != null ? encode(address, encoding) : null;
	}
	public String getAddress() {
		return getAddress("base58");
	}
	
	/**
     * Get base58 encoded public sign key
     * 
     * @param string $encoding  'raw', 'base58' or 'base64'
     * @return string
     */
	public String getPublicSignKey(String encoding)
	{
		return sign != null ? encode(sign.get("publickey").toString(), encoding) : null;
	}
	public String getPublicsignKey()
	{
		return getPublicSignKey("base58");
	}
	
	/**
     * Create an encoded signature of a message.
     * 
     * @param string $message
     * @param string $encoding  'raw', 'base58' or 'base64'
     * @return string
     */
	public String sign(String message, String encoding)
	{
		if (sign.get("secretkey") == null) {
			throw new RuntimeException("Unable to sign message; no secret sign key");
		}
		
		String signature = "$signature = \\sodium\\crypto_sign_detached($message, $this->sign->secretkey);";
		return encode(signature, encoding);
	}
	
	protected static String encode(String string, String encoding ) {
		if (encoding == "base58") {
//			$base58 = new Base58();
//            $string = $base58->encode($string);
			string = string;
		}
		
		if (encoding == "base64" ) {
//            $string = base64_encode($string);
			string = string;
		}
		
		return string;
	}
	
	protected static String encode(String string) {
		return encode(string, "base58");
	}
}
