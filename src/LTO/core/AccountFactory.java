package LTO.core;

import java.security.CryptoPrimitive;
import java.util.Random;

import com.muquit.libsodiumjna.SodiumKeyPair;

import Util.utils.CryptoUtil;
import Util.utils.HashUtil;

public class AccountFactory {
	private static final short ADDRESS_VERSION = 0x1;
	
	/**
     * Address scheme
     * @var string 
     */
    protected String network;
    
    /**
     * Incrementing nonce (4 bytes)
     * @var string 
     */
    protected String nonce;
    
    /**
     * Class constructor
     * 
     * @param int|string $network 'W' or 'T' (1 byte)
     * @param int        $nonce   (4 bytes)
     */
    public AccountFactory(String network, String nonce)
    {
    	this.network = network.substring(0, 1);
    	this.nonce = nonce;
    }
    public AccountFactory(int network, String nonce)
    {
    	this.network = Character.toString((char) network);
    	this.nonce = nonce;
    }
    public AccountFactory(String network)
    {
    	this(network, Integer.toString(new Random().nextInt(0xFFFF + 1)));
    }
    public AccountFactory(int network)
    {
    	this(network, Integer.toString(new Random().nextInt(0xFFFF + 1)));
    }
    
    /**
     * Get the new nonce.
     * 
     * @return int
     */
    protected String getNonce()
    {
    	String ret = nonce;
    	int _nonce = Integer.parseInt(nonce);
    	_nonce++;
    	nonce = Integer.toString(_nonce);
    	return ret;
    }
    
    /**
     * Create the account seed using several hashing algorithms.
     * 
     * @param string $seedText  Brainwallet seed string
     * @return string  raw seed (not encoded)
     */
    public String createAccountSeed(String seedText)
    {
    	String seedBase = ""; //pack('La*', $this->getNonce(), $seedText);
    	
    	String secureSeed = HashUtil.Keccak256(CryptoUtil.crypto_generichash(seedBase, 32)); //raw
    	String seed = HashUtil.SHA256(secureSeed); //raw
    	
    	return seed;
    }
    
    /**
     * Create ED25519 sign keypairs
     * 
     * @param string $seed
     * @return object
     */
    protected SodiumKeyPair createSignKeys($seed)
    {
    	
        $keypair = \sodium\crypto_sign_seed_keypair($seed);
        $publickey = \sodium\crypto_sign_publickey($keypair);
        $secretkey = \sodium\crypto_sign_secretkey($keypair);

        return kp;
    }
}
