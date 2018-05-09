/**
 * Live Contracts Event
 */
package LTO.core;

import java.util.Date;

import LTO.exceptions.BadMethodCallException;

import LTO.core.Account;
import LTO.core.EventChain;
import Util.core.JsonObject;
import Util.utils.*;

/**
 * @author moonbi
 *
 */
public class Event {
	/**
     * Base58 encoded JSON string with the body of the event.
     * 
     * @var string
     */
	public String body;
	
	/**
     * Time when the event was signed.
     * 
     * @var int
     */
	public Date timestamp;
	
	/**
     * Hash to the previous event
     * 
     * @var string
     */
    public String previous;
    
    /**
     * URI of the public key used to sign the event
     * 
     * @var string
     */
    public String signkey;
    
    /**
     * Base58 encoded signature of the event
     * 
     * @var string
     */
    public String signature;
    
    /**
     * Base58 encoded SHA256 hash of the event
     * 
     * @var string
     */
    public String hash;
    
    
    /**
     * Class constructor
     * 
     * @param object|array $body
     * @param string       $previous
     */
    public Event(JsonObject body, String previous)
    {
    	if (body != null) {
    		this.body = StringUtil.encodeBase58(body.toString());
    		this.timestamp = new Date();
    	}
    	
    	this.previous = previous;
    }
    public Event(JsonObject body)
    {
    	this(body, null);
    }
    
    public Event() {
    	this(null, null);
    }
    
    public String getMessage()
    {
    	if (this.body == null) {
    		throw new BadMethodCallException("Body unknown");
    	}
    	
    	if (this.signkey == null) {
    		throw new BadMethodCallException("First set signkey before creating message");
    	}
    	
    	String message = String.join("\n", new String[] {body, Integer.toString((int) timestamp.getTime()), previous, signkey});
    	
    	return message;
    }
    
    /**
     * Get the base58 encoded hash of the event
     * 
     * @return string
     */
    public String getHash()
    {
    	byte[] hash = StringUtil.SHA256(this.getMessage().getBytes());
    	
    	return StringUtil.encodeBase58(hash);
    }
    
    /**
     * Verify that the signature is valid
     * 
     * @return boolean
     */
    public boolean verifySignature()
    {
    	if (this.signature == null || this.signkey == null) {
    		throw new BadMethodCallException("Signature and/or signkey not set");
    	}
    	
    	String _signature = StringUtil.decodeBase58(signature);
    	String _signkey = StringUtil.decodeBase58(signkey);
    	return	_signature.length() == CryptoUtil.crypto_sign_bytes() &&
    			_signkey.length() == CryptoUtil.crypto_sign_publickeybytes() && 
    			CryptoUtil.crypto_sign_verify_detached(_signature, getMessage(),_signkey);
    }
    
    /**
     * Sign this event
     * 
     * @param Account $account
     * @return $this
     * @throws JSONException 
     */
    public Event signWith(Account account)
    {
    	return account.signEvent(this);
    }
    
    /**
     * Add this event to the chain
     * 
     * @param EventChain $chain
     * @return $this
     */
//    public Event addTo(EventChain chain)
//    {
//        return chain.add(this);
//    }
}
















