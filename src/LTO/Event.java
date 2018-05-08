/**
 * Live Contracts Event
 */
package LTO;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.util.Date;

import LTO.Account;
//import LTO.EventChain;
import Util.JsonUtil;
import Util.StringUtil;

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
	public int timestamp;
	
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
    public Event(JSONObject body, String previous)
    {
    	if (body != null) {
    		this.body = StringUtil.encodeBase58(JsonUtil.jsonEncode(body));
    		this.timestamp = (int) (new Date().getTime() / 1000);
    	}
    	
    	this.previous = previous;
    }
    
    public Event(JSONArray body, String previous)
    {
    	if (body != null) {
    		this.body = JsonUtil.jsonEncode(body);
    		this.timestamp = (int) (new Date().getTime() / 1000);
    	}
    	
    	this.previous = previous;
    }
    
    public Event() {
    	this.body = null;
    	this.previous = null;
    }
    
    public String getMessage()
    {
    	if (this.body == null) {
    		throw new BadMethodCallException("Body unknown");
    	}
    	
    	if (this.signkey == null) {
    		throw new BadMethodCallException("First set signkey before creating message");
    	}
    	
    	String message = String.join("\n", new String[] {body, Integer.toString(timestamp), previous, signkey});
    	
    	return message;
    }
    
    /**
     * Get the base58 encoded hash of the event
     * 
     * @return string
     */
    public String getHash()
    {
    	String hash = StringUtil.SHA256(this.getMessage());
    	
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
    	
    	return _signature == _signkey;
//    	return strlen($signature) === \sodium\CRYPTO_SIGN_BYTES &&
//                strlen($signkey) === \sodium\CRYPTO_SIGN_PUBLICKEYBYTES &&
//                \sodium\crypto_sign_verify_detached($signature, $this->getMessage(), $signkey);
    }
    
    /**
     * Sign this event
     * 
     * @param Account $account
     * @return $this
     */
//    public Event signWith(Account account)
//    {
//    	return account.signEvent(this);
//    }
    
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
















