package legalthings.lto_api.lto.core;

import java.util.Arrays;
import java.util.Random;

import legalthings.lto_api.lto.exceptions.InvalidAccountException;
import legalthings.lto_api.utils.core.BinHex;
import legalthings.lto_api.utils.core.JsonObject;
import legalthings.lto_api.utils.main.CryptoUtil;
import legalthings.lto_api.utils.main.HashUtil;
import legalthings.lto_api.utils.main.PackUtil;
import legalthings.lto_api.utils.main.StringUtil;

public class AccountFactory {
	public static final char ADDRESS_VERSION = 0x1;
	
	/**
     * Address scheme
     * @var string 
     */
    protected String network;
    
    /**
     * Incrementing nonce (4 bytes)
     * @var string 
     */
    protected int nonce;
    
    /**
     * Class constructor
     * 
     * @param int|string $network 'W' or 'T' (1 byte)
     * @param int        $nonce   (4 bytes)
     */
    public AccountFactory(int network, int nonce)
    {
    	this.network = Character.toString((char) network).substring(0, 1);
    	this.nonce = nonce;
    }
    public AccountFactory(String network, int nonce)
    {
    	this.network = network.substring(0, 1);
    	this.nonce = nonce;
    }
    public AccountFactory(int network)
    {
    	this(network, new Random().nextInt(0xFFFF + 1));
    }
    public AccountFactory(String network)
    {
    	this(network, new Random().nextInt(0xFFFF + 1));
    }
    
    /**
     * Get the new nonce.
     * 
     * @return int
     */
    protected int getNonce()
    {
    	return nonce++;
    }
    
    /**
     * Create the account seed using several hashing algorithms.
     * 
     * @param string $seedText  Brainwallet seed string
     * @return string  raw seed (not encoded)
     */
    public byte[] createAccountSeed(String seedText)
    {
    	byte[] seedBase = PackUtil.packLaStar(nonce, seedText);
    	
    	byte[] secureSeed = BinHex.hex2bin(HashUtil.Keccak256(CryptoUtil.crypto_generichash(seedBase, 32))); //raw output
    	byte[] seed = HashUtil.SHA256(secureSeed);
    	
    	return seed;    	
    }
    
    /**
     * Create ED25519 sign keypairs
     * 
     * @param string $seed
     * @return object
     */
    protected JsonObject createSignKeys(byte[] seed)
    {
    	JsonObject keypair = CryptoUtil.crypto_sign_seed_keypair(seed);
    	return keypair;
    }
    
    /**
     * Create X25519 encrypt keypairs
     * 
     * @param string $seed
     * @return object
     */
    protected JsonObject createEncryptKeys(byte[] seed)
    {
    	JsonObject keypair = CryptoUtil.crypto_box_seed_keypair(seed);
    	return keypair;
    }
    
    /**
     * Create an address from a public key
     * 
     * @param string $publickey  Raw public key (not encoded)
     * @param string $type       Type of key 'sign' or 'encrypt'
     * @return string  raw (not encoded)
     */
    public byte[] createAddress(byte[] publickey, String type)
    {
    	if (type == "sign") {
    		publickey = CryptoUtil.crypto_sign_ed25519_pk_to_curve25519(publickey);
    	}
    	
    	String publickeyHash = HashUtil.Keccak256(CryptoUtil.crypto_generichash(publickey, 32)).substring(0, 40);
    	
    	byte[] packed = PackUtil.packCaH40(ADDRESS_VERSION, network, publickeyHash);
    	String chksum = HashUtil.Keccak256(CryptoUtil.crypto_generichash(packed, packed.length)).substring(0, 8);
    	
    	return PackUtil.packCaH40H8(ADDRESS_VERSION, network, publickeyHash, chksum);
    }
    public byte[] createAddress(byte[] publickey)
    {
    	return createAddress(publickey, "encrypt");
    }
    
    /**
     * Create a new account from a seed
     * 
     * @param string $seedText  Brainwallet seed string
     * @return Account
     */
    public Account seed(String seedText)
    {
    	byte[] seed = createAccountSeed(seedText);
    	
    	Account account = new Account();
    	
    	account.sign = createSignKeys(seed);
    	account.encrypt = createEncryptKeys(seed);
    	account.address = createAddress(account.sign.getByte("publickey"));
    	
    	return account;
    }
    
    /**
     * Convert sign keys to encrypt keys.
     * 
     * @param object|string $sign
     * @return object
     */
    public JsonObject convertSignToEncrypt(JsonObject sign)
    {
    	JsonObject encrypt = new JsonObject();
    	
    	if (sign != null && sign.getByte("secretkey") != null) {
    		byte[] secretkey = CryptoUtil.crypto_sign_ed25519_pk_to_curve25519(sign.getByte("publickey"));
    		
    		// Swap bits, on uneven???
    		byte[] bytes = StringUtil.toPositiveByteArray(secretkey);
//    		int i = bytes.length;
//    		$bytes[$i] = $bytes[$i] % 2 ? ($bytes[$i] | 0x80) & ~0x40 : $bytes[$i];
    		
//    		$encrypt->secretkey = pack('C*', ...$bytes);
    		encrypt.putByte("secretkey", bytes);
    	}
    	
    	if (sign != null && sign.getByte("publickey") != null) {
    		encrypt.putByte("publickey", CryptoUtil.crypto_sign_ed25519_pk_to_curve25519(sign.getByte("publickey")));
    	}
    	
    	return encrypt;
    }
    
    /**
     * Get and verify the raw public and private key.
     * 
     * @param array  $keys
     * @param string $type  'sign' or 'encrypt'
     * @return object
     * @throws InvalidAccountException  if keys don't match
     */
    public JsonObject calcKeys(JsonObject keys, String type)
    {
    	if (keys == null || keys.getByte("secretkey") == null) {
    		JsonObject key = new JsonObject();
    		key.putByte("publickey", keys.getByte("publickey"));
    	}
    	
    	byte[] secretkey = keys.getByte("secretkey");
    	
    	byte[] publickey = type == "sign" ? CryptoUtil.crypto_sign_publickey_from_secretkey(secretkey) : CryptoUtil.crypto_box_publickey_from_secretkey(secretkey);
    	
    	if (keys != null && !Arrays.equals(keys.getByte("publickey"),publickey)) {
    		throw new InvalidAccountException("Public " + type + " key doesn't match private " + type + " key");
    	}
    	
    	JsonObject key = new JsonObject();
    	key.putByte("secretkey", secretkey);
    	key.putByte("publickey", publickey);
    	
    	return key;
    }
    
    /**
     * Get and verify raw address.
     * 
     * @param string $address  Raw address
     * @param object $sign     Sign keys
     * @param object $encrypt  Encrypt keys
     * @return string
     * @throws InvalidAccountException  if address doesn't match
     */
    protected byte[] calcAddress(byte[] address, JsonObject sign, JsonObject encrypt)
    {
    	byte[] addrSign = (sign != null && sign.getByte("publickey") != null) ? createAddress(sign.getByte("publickey"), "sign") : null;
    	byte[] addrEncrypt = (encrypt != null && encrypt.getByte("publickey") != null) ? createAddress(encrypt.getByte("publickey"), "encrypt") : null;
    	
    	if (addrSign != null && addrEncrypt != null && addrSign != addrEncrypt) {
    		throw new InvalidAccountException("Sign key doesn't match encrypt key");
    	}
    	
    	if (address != null) {
    		if ((addrSign != null && !Arrays.equals(address, addrSign)) || (addrEncrypt != null && !Arrays.equals(address, addrEncrypt))) {
    			throw new InvalidAccountException("Address doesn't match keypair; possible network mismatch");
    		}
    	} else {
    		address = addrSign != null ? addrSign : addrEncrypt;
    	}
    	
    	return address;
    }
    
    /**
     * Create an account from base58 encoded keys.
     * 
     * @param array|string $keys  All keys (array) or private sign key (string)
     * @return Account
     */
    public Account create(JsonObject sign, JsonObject encrypt, byte[] address, String encoding)
    {
    	Account account = new Account();
    	
    	account.sign = sign != null ? calcKeys(sign, "sign") : null;
    	account.encrypt = encrypt != null ? calcKeys(encrypt, "encrypt") : (sign != null ? convertSignToEncrypt(sign) : null);
    	account.address = address != null ? calcAddress(address, sign, encrypt) : null;
    	
    	return account;
    }
    
    /**
     * Create an account from public keys.
     * 
     * @param string $sign
     * @param string $encrypt
     * @param string $encoding  Encoding of keys 'raw', 'base58' or 'base64'
     * @return Account
     */
    public Account createPublic(JsonObject sign, JsonObject encrypt, String encoding)
    {
        return create(sign, encrypt, null, encoding);
    }
}
