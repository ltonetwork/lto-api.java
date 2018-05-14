package legalthings.lto_api.lto.core;

import java.util.ArrayList;

import legalthings.lto_api.lto.exceptions.BadMethodCallException;
import legalthings.lto_api.lto.exceptions.InvalidArgumentException;
import legalthings.lto_api.utils.main.CryptoUtil;
import legalthings.lto_api.utils.main.HashUtil;
import legalthings.lto_api.utils.main.PackUtil;
import legalthings.lto_api.utils.main.StringUtil;

public class EventChain {
	public static final char ADDRESS_VERSION = 0x40;
	
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
    protected byte[] getNonce()
    {
        return CryptoUtil.random_bytes(8);
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
    	if (account.sign == null || account.sign.getByte("publickey") == null) {
    		throw new InvalidArgumentException("Unable to create new event chain; public sign key unknown");
    	}
    	
    	byte[] signkey = account.sign.getByte("publickey");
    	String signkeyHashed = HashUtil.Keccak256(CryptoUtil.crypto_generichash(signkey)).substring(0, 40);
    	
    	byte[] nonce = getNonce();
    	
    	byte[] packed = PackUtil.packCa8H40(ADDRESS_VERSION, nonce, signkeyHashed);
    	String chksum = HashUtil.Keccak256(CryptoUtil.crypto_generichash(packed)).substring(0, 8);
    	
    	byte[] idBinary = PackUtil.packCa8H40H8(ADDRESS_VERSION, nonce, signkeyHashed, chksum);
    	id = StringUtil.base58Encode(idBinary);
    	latestHash = getInitialHash();
    }
    
    /**
     * Get the initial hash which is based on the event chain id
     */
    public String getInitialHash()
    {
        byte[] rawId = StringUtil.base58Decode(id);
        
        return StringUtil.base58Encode(HashUtil.SHA256(rawId));
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
