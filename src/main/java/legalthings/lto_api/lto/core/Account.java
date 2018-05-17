package legalthings.lto_api.lto.core;

import legalthings.lto_api.utils.core.JsonObject;
import legalthings.lto_api.utils.main.CryptoUtil;
import legalthings.lto_api.utils.main.StringUtil;

public class Account {
	/**
	 * Account public address
	 * @var string
	 */
	public byte[] address;
	
	/**
	 * Sign kyes
	 * @var object
	 */
	public JsonObject sign;
	
	/**
	 * Encryption keys
	 * @var object
	 */	
	public JsonObject encrypt;
	
	/**
	 * Get a random nonce
	 * @codeCoverageIgnore
	 * 
	 * @return string
	 */
	protected byte[] getNonce()
	{
		return CryptoUtil.random_bytes(CryptoUtil.crypto_box_noncebytes());
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
		return sign != null ? encode(sign.getByte("publickey"), encoding) : null;
	}
	public String getPublicSignKey()
	{
		return getPublicSignKey("base58");
	}
	
	/**
     * Get base58 encoded public encryption key
     * 
     * @param string $encoding  'raw', 'base58' or 'base64'
     * @return string
     */
    public String getPublicEncryptKey(String encoding)
    {
		return encrypt != null ? encode(encrypt.getByte("publickey"), encoding) : null;		
    }
    public String getPublicEncryptKey()
    {
    	return getPublicEncryptKey("base58");
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
		if (sign == null || sign.getByte("secretkey") == null) {
			throw new RuntimeException("Unable to sign message; no secret sign key");
		}
		byte[] signature = CryptoUtil.crypto_sign_detached(message.getBytes(), sign.getByte("secretkey"));
		return encode(signature, encoding);
	}
	public String sign(String message)
	{
		if (sign == null || sign.getByte("secretkey") == null) {
			throw new RuntimeException("Unable to sign message; no secret sign key");
		}
		byte[] signature = CryptoUtil.crypto_sign_detached(message.getBytes(), sign.getByte("secretkey"));
		return encode(signature, "base58");
	}
	
	/**
     * Sign an event
     * 
     * @param Event $event
     * @return $event
     */
    public Event signEvent(Event event)
    {
        event.signkey = getPublicSignKey();
        event.signature = sign(event.getMessage());
        event.hash = event.getHash();
        
        return event;
    }
    
    /**
     * Verify a signature of a message
     * 
     * @param string $signature
     * @param string $message
     * @param string $encoding   signature encoding 'raw', 'base58' or 'base64'
     * @return boolean
     */
    public boolean verify(String signature, String message, String encoding)
    {
    	if (sign == null || sign.getByte("publickey") == null) {
    		throw new RuntimeException("Unable to verify message; no public sign key");
    	}
        
    	byte[] rawSignature = decode(signature, encoding);
    	
    	return rawSignature.length == CryptoUtil.crypto_sign_bytes() &&
    			sign.getByte("publickey").length == CryptoUtil.crypto_sign_publickeybytes() &&
    			CryptoUtil.crypto_sign_verify_detached(rawSignature, message.getBytes(), sign.getByte("publickey"));	
    }
    public boolean verify(String signature, String message)
    {
    	return verify(signature, message, "base58");
    }
    
    /**
     * Encrypt a message for another account.
     * The nonce is appended.
     * 
     * @param Account $recipient 
     * @param string  $message
     * @return string
     */
    public byte[] encryptFor(Account recipient, String message)
    {
    	if (encrypt == null || encrypt.getByte("secretkey") == null) {
    		throw new RuntimeException("Unable to encrypt message; no secret encryption key");
    	}
    	if (recipient.encrypt == null || recipient.encrypt.getByte("publickey") == null) {
    		throw new RuntimeException("Unable to encrypt message; no public encryption key for recipient");
    	}
    	
    	byte[] nonce = getNonce();
    	
    	byte[] retEncrypt = CryptoUtil.crypto_box(nonce, message.getBytes(), recipient.encrypt.getByte("publickey"), encrypt.getByte("secretkey"));
    	
    	byte[] ret = new byte[retEncrypt.length + nonce.length];
    	System.arraycopy(retEncrypt, 0, ret, 0, retEncrypt.length);
    	System.arraycopy(nonce, 0, ret, retEncrypt.length, nonce.length);
    	
    	return ret;
    }
    
    /**
     * Decrypt a message from another account.
     * 
     * @param Account $sender 
     * @param string  $cyphertext
     * @return string
     */
    public byte[] decryptFrom(Account sender, byte[] ciphertext)
    {
    	if (encrypt == null || encrypt.getByte("secretkey") == null) {
    		throw new RuntimeException("Unable to decrypt message; no secret encryption key");
    	}
    	if (sender.encrypt == null || sender.encrypt.getByte("publickey") == null) {
    		throw new RuntimeException("Unable to decrypt message; no public encryption key for recipient");
    	}
        
        byte[] encryptedMessage = new byte[ciphertext.length - 24];
        System.arraycopy(ciphertext, 0, encryptedMessage, 0, ciphertext.length - 24);
        
        byte[] nonce = new byte[24];
        System.arraycopy(ciphertext, ciphertext.length - 24, nonce, 0, 24);
        
        return CryptoUtil.crypto_box_open(nonce, encryptedMessage, encrypt.getByte("publickey"), sender.encrypt.getByte("secretkey"));
    }
    
    /**
     * Create a new event chain for this account
     * 
     * @return EventChain
     * @throws \BadMethodCallException
     */
    public EventChain createEventChain()
    {
    	EventChain chain = new EventChain();
    	chain.initFor(this);
    	return chain;
    }
	
	protected static String encode(String string, String encoding ) {
		if (encoding == "base58") {
			string = StringUtil.base58Encode(string);
		}
		
		if (encoding == "base64" ) {
			string = StringUtil.base64Encode(string);
		}
		
		return string;
	}
	protected static String encode(byte[] string, String encoding ) {
		if (encoding == "base58") {
			return StringUtil.base58Encode(string);
		}
		
		if (encoding == "base64" ) {
			return StringUtil.base64Encode(string);
		}
		return null;
	}
	protected static String encode(String string) {
		return encode(string, "base58");
	}
	protected static String encode(byte[] string) {
		return encode(string, "base58");
	}
	
	/**
     * Base58 or base64 decode a string
     * 
     * @param string $string
     * @param string $encoding  'raw', 'base58' or 'base64'
     * @return string
     */
    protected static byte[] decode(String string, String encoding)
    {
    	if (encoding == "base58" ) {
    		return StringUtil.base58Decode(string);
    	}
    	
    	if (encoding == "base64" ) {
    		return StringUtil.base64Decode(string);
    	}
    	
    	return null;
    }
    protected static byte[] decode(String string) {
    	return decode(string, "base58");
    }
}