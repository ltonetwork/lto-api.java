package legalthings.lto_api.lto.core;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;

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
    public AccountFactory(Object network)
    {
    	this(network, new Random().nextInt(0xFFFF + 1));
    }
    public AccountFactory(Object network, int nonce)
    {
    	if (network instanceof String) {
    		this.network = network.toString().substring(0, 1);
    	}
    	if (network instanceof Number) {
    		this.network = Character.toString((char) ((Number) network).intValue());
    	}
    	this.nonce = nonce;
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
    	
    	byte[] secureSeed = BinHex.hex2bin(HashUtil.Keccak256(CryptoUtil.crypto_generichash(seedBase, 32)));
    	byte[] seed = HashUtil.SHA256(secureSeed);
    	
    	return seed;
    }
    
    /**
     * Create ED25519 sign keypairs
     * 
     * @param string $seed
     * @return object
     */
    protected KeyPair createSignKeys(byte[] seed)
    {
    	return CryptoUtil.crypto_sign_seed_keypair(seed);
    }
    
    /**
     * Create X25519 encrypt keypairs
     * 
     * @param string $seed
     * @return object
     */
    protected KeyPair createEncryptKeys(byte[] seed)
    {
    	return CryptoUtil.crypto_box_seed_keypair(seed);
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
    	account.address = createAddress(account.sign.getPublickey());
    	
    	return account;
    }
    
    /**
     * Convert sign keys to encrypt keys.
     * 
     * @param object|string $sign
     * @return object
     */
    public KeyPair convertSignToEncrypt(KeyPair sign)
    {
    	KeyPair encrypt = new KeyPair();
    	
    	if (sign != null && sign.getSecretkey() != null) {
    		byte[] secretkey = CryptoUtil.crypto_sign_ed25519_sk_to_curve25519(sign.getSecretkey());
    		
    		int last = secretkey.length - 1;
    		secretkey[last] = secretkey[last] % 2 == 1 ? ((byte) ((secretkey[last] | 0x80) & ~0x40)) : secretkey[last];
    		
    		encrypt.setSecretkey(secretkey);
    	}
    	
    	if (sign != null && sign.getPublickey() != null) {
    		encrypt.setPublickey(CryptoUtil.crypto_sign_ed25519_pk_to_curve25519(sign.getPublickey()));
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
    public KeyPair calcKeys(KeyPair keys, String type)
    {
    	if (keys == null || keys.getSecretkey() == null) {
    		return new KeyPair(keys.getPublickey(), null);
    	}
    	
    	byte[] secretkey = keys.getSecretkey();
    	
    	byte[] publickey = type == "sign" ? CryptoUtil.crypto_sign_publickey_from_secretkey(secretkey) : CryptoUtil.crypto_box_publickey_from_secretkey(secretkey);
    	
    	if (keys != null && keys.getPublickey() != null && !Arrays.equals(keys.getPublickey(), publickey)) {
    		throw new InvalidAccountException("Public " + type + " key doesn't match private " + type + " key");
    	}
    	
    	return new KeyPair(publickey, secretkey);
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
    protected byte[] calcAddress(byte[] address, KeyPair sign, KeyPair encrypt)
    {    	
    	byte[] _address = null;
    	
//    	System.out.println(sign.getPublickey());
    	byte[] addrSign = (sign != null && sign.getPublickey() != null) ? createAddress(sign.getPublickey(), "sign") : null;
    	byte[] addrEncrypt = (encrypt != null && encrypt.getPublickey() != null) ? createAddress(encrypt.getPublickey(), "encrypt") : null;
    	
    	if (addrSign != null && addrEncrypt != null && !Arrays.equals(addrSign, addrEncrypt)) {
    		throw new InvalidAccountException("Sign key doesn't match encrypt key");
    	}
    	
    	if (address != null) {
    		if ((addrSign != null && !Arrays.equals(address, addrSign)) || (addrEncrypt != null && !Arrays.equals(address, addrEncrypt))) {
    			throw new InvalidAccountException("Address doesn't match keypair; possible network mismatch");
    		}
    		
    		_address = new byte[address.length];
    		System.arraycopy(address, 0, _address, 0, address.length);
    	} else {
    		if (addrSign != null) {
    			_address = new byte[addrSign.length];
    			System.arraycopy(addrSign, 0, _address, 0, addrSign.length);
    		} else if (addrEncrypt != null) {
    			_address = new byte[addrEncrypt.length];
    			System.arraycopy(addrEncrypt, 0, _address, 0, addrEncrypt.length);
    		}
    	}
    	return _address;
    }
    
    /**
     * Create an account from base58 encoded keys.
     * 
     * @param array|string $keys  All keys (array) or private sign key (string)
     * @return Account
     */
    public Account create(KeyPair sign, KeyPair encrypt, byte[] address, String encoding)
    {
    	Account account = new Account();
    	
    	account.sign = sign != null ? calcKeys(sign, "sign") : null;
    	account.encrypt = encrypt != null ? calcKeys(encrypt, "encrypt") : (sign != null ? convertSignToEncrypt(account.sign) : null);
    	account.address = calcAddress(address, account.sign, account.encrypt);
    	
    	return account;
    }
    public Account create(KeyPair sign, KeyPair encrypt, byte[] address)
    {
    	return create(sign, encrypt, address, "base58");
    }
    
    /**
     * Create an account from public keys.
     * 
     * @param string $sign
     * @param string $encrypt
     * @param string $encoding  Encoding of keys 'raw', 'base58' or 'base64'
     * @return Account
     */
    public Account createPublic(byte[] signkey, byte[] encryptkey, String encoding)
    {
    	KeyPair sign = null;
    	if (signkey != null) {
    		sign = new KeyPair(signkey, null);
    	}
    	
    	KeyPair encrypt = null;
    	if (encryptkey != null) {
    		encrypt = new KeyPair(encryptkey, null);
    	}
    	
        return create(sign, encrypt, null, encoding);
    }
    public Account createPublic(byte[] signkey, byte[] encryptkey)
    {
    	return createPublic(signkey, encryptkey, "base58");
    }
}
