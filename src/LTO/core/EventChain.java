/**
 * Live contracts event chain
 */
package LTO.core;

import LTO.core.Event;

import org.apache.wink.json4j.JSONException;

import LTO.core.Account;

import LTO.exceptions.BadMethodCallException;
import LTO.exceptions.InvalidArgumentException;

import Util.utils.*;


/**
 * @author moonbi
 *
 */
public class EventChain {
	public final short ADDRESS_VERSION = 0x40;
	
	/**
     * Unique identifier
     * @var string
     */
    public String id;
    
    /**
     * List of event
     * @var Event[]
     */
    public Event[] $events;

    /**
     * Hash of the latest event on the chain
     * @var string
     */
    protected String latestHash;
    
    /**
     * Class constructor
     * 
     * @param string $id
     * @param string $latestHash
     */
    public EventChain(String id, String latestHash)
    {
    	this.id = id;
    	this.latestHash = latestHash;
    }
    public EventChain()
    {
    	this(null, null);
    }
    
    /**
     * Generate an 8 byte random nonce for the id
     * @codeCoverageIgnore
     * 
     * @return string
     */
    protected String getNonce()
    {
        return new String(CryptoUtil.random_bytes(8));
    }
    
    /**
     * Initialize a new event chain
     * 
     * @param Account $account
     * @throws JSONException 
     */
    public void initFor(Account account)
    {
    	if (id == null) {
    		throw new BadMethodCallException("Chain id already set");
    	}
    	if (account.sign.get("publickey") == null) {
    		throw new InvalidArgumentException("Unable to create new event chain; public sign key unknown");
    	}
    	
    	String signkey = account.sign.get("publickey").toString();
    	String signkeyHashed = "";
    	
    	String nonce = getNonce();
    	
    }
}
