/**
 * Live contracts event chain
 */
package LTO.core;

import LTO.core.Event;

import java.util.ArrayList;

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
	public static final short ADDRESS_VERSION = 0x40;
	
	/**
     * Unique identifier
     * @var string
     */
    public String id;
    
    /**
     * List of event
     * @var Event[]
     */
    public ArrayList<Event> events;

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
    public EventChain(String id)
    {
    	this(id, null);
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
    	String signkeyHashed = HashUtil.Keccak256(CryptoUtil.crypto_generichash(signkey, 32)).substring(0, 40);
    	
    	String nonce = getNonce();
    	
//    	$packed = pack('Ca8H40', self::ADDRESS_VERSION, $nonce, $signkeyHashed);
//        $chksum = substr(Keccak::hash(\sodium\crypto_generichash($packed), 256), 0, 8);
//        
//        $idBinary = pack('Ca8H40H8', self::ADDRESS_VERSION, $nonce, $signkeyHashed, $chksum);
//        
//        $base58 = new \StephenHill\Base58();
//        
//        $this->id = $base58->encode($idBinary);
//        $this->latestHash = $this->getInitialHash();
    }
    
    /**
     * Get the initial hash which is based on the event chain id
     */
    public String getInitialHash()
    {
        String rawId = StringUtil.decodeBase58(id);
        
        return StringUtil.encodeBase58(HashUtil.SHA256(rawId));
    }
    
    /**
     * Get the latest hash.
     * Expecting a new event to use this as previous property.
     * 
     * @return string
     */
    public String getLatestHash()
    {
        if (events.size() == 0) {
            return latestHash;
        }

        Event lastEvent = events.get(events.size() - 1);
        return lastEvent.getHash();
    }
    
    /**
     * Add a new event
     * 
     * @param Event $event
     * @return Event
     */
    public Event add(Event event)
    {
        event.previous = getLatestHash();
        
        events.add(event);
        latestHash = null;
        
        return event;
    }
}
