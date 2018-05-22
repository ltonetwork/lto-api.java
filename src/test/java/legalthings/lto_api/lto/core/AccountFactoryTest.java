package legalthings.lto_api.lto.core;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import legalthings.lto_api.lto.exceptions.InvalidAccountException;
import legalthings.lto_api.utils.core.BinHex;
import legalthings.lto_api.utils.core.JsonObject;
import legalthings.lto_api.utils.main.JsonUtil;
import legalthings.lto_api.utils.main.StringUtil;

@RunWith(DataProviderRunner.class)
public class AccountFactoryTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	public String seedText = "manage manual recall harvest series desert melt police rose hollow moral pledge kitten position add";
	
	/**
     * Asserts variable is equals to Base58 encoded string.
     *
     * @param mixed  $encoded
     * @param mixed  $actual
     * @param string $message
     */
    public static void assertBase58Equals(String encoded, byte[] actual)
    {
    	String value = StringUtil.base58Encode(actual);
        
    	assertEquals(value, encoded);
    }
    
    /**
     * @see https://specs.livecontracts.io/cryptography.html#asymmetric-encryption
     */
	@Test
	public void testCreateAccountSeed() {
		AccountFactory factory = new AccountFactory("W", 0);
		byte[] seed = factory.createAccountSeed(seedText);
		assertBase58Equals("49mgaSSVQw6tDoZrHSr9rFySgHHXwgQbCRwFssboVLWX", seed);
	}
	
	@DataProvider
	public static Object[][] createAddressProvider() {
	    return new Object[][] {
	        { "3PPbMwqLtwBGcJrTA5whqJfY95GqnNnFMDX", "W" },
	        { "3PPbMwqLtwBGcJrTA5whqJfY95GqnNnFMDX", 0x57 },
	        { "3NBaYzWT2odsyrZ2u1ghsrHinBm4xFRAgLX", "T" },
	        { "3NBaYzWT2odsyrZ2u1ghsrHinBm4xFRAgLX", 0x54 }
	    };
	}
	
	/**
     * @dataProvider createAddressProvider
     * 
     * @param string     $expected
     * @param string|int $network
     */
	@Test
	@UseDataProvider("createAddressProvider")
    public void testCreateAddressEncrypt(String expected, Object network)
    {
        AccountFactory factory = new AccountFactory(network, 0);
        
        byte[] publickey = StringUtil.base58Decode("HBqhfdFASRQ5eBBpu2y6c6KKi1az6bMx8v1JxX4iW1Q8");
        byte[] address = factory.createAddress(publickey, "encrypt");
        
        assertEquals(expected, StringUtil.base58Encode(address));
    }
	
	/**
     * @dataProvider createAddressProvider
     * 
     * @param string     $expected
     * @param string|int $network
     */
	@Test
	@UseDataProvider("createAddressProvider")
	public void testCreateAddressSign(String expected, Object network)
	{
		AccountFactory factory = new AccountFactory(network, 0);
        
        byte[] publickey = StringUtil.base58Decode("BvEdG3ATxtmkbCVj9k2yvh3s6ooktBoSmyp8xwDqCQHp");
        byte[] address = factory.createAddress(publickey, "sign");
        
        assertEquals(expected, StringUtil.base58Encode(address));
	}
	
	@DataProvider
	public static Object[][] convertSignToEncryptProvider() {
		return new Object[][] {
	        { new KeyPair(StringUtil.base58Decode("EZa2ndj6h95m3xm7DxPQhrtANvhymNC7nWQ3o1vmDJ4x"), null), new KeyPair(StringUtil.base58Decode("gVVExGUK4J5BsxwUfYsFkkjpn6A7BcvYdmARL28GBRc"), null)},
	        { new KeyPair(StringUtil.base58Decode("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6"), null), new KeyPair(StringUtil.base58Decode("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y"), null)},
	        { new KeyPair(StringUtil.base58Decode("4Xpf8guEGD3ZnRJLuEu8JjpmKnHpXR49mFE4Zm9m9P1z"), null), new KeyPair(StringUtil.base58Decode("96yeNG1KYJKAVnfKqfkfktkXuPj1CLPEsgCDkm42VcaT"), null)},
	        { new KeyPair(StringUtil.base58Decode("Efv4wPdjfyVNvbp21xwiTXnirQti7jJy56W9doDVzfhG"), null), new KeyPair(StringUtil.base58Decode("7TecQdLbPuxt3mWukbZ1g1dTZeA6rxgjMxfS9MRURaEP"), null)},
	        { new KeyPair(StringUtil.base58Decode("Efv4wPdjfyVNvbp21xwiTXnirQti7jJy56W9doDVzfhG"), null), new KeyPair(StringUtil.base58Decode("7TecQdLbPuxt3mWukbZ1g1dTZeA6rxgjMxfS9MRURaEP"), null)},
	        { new KeyPair(null, StringUtil.base58Decode("ACsYcMff8UPUc5dvuCMAkqZxcRTjXHMnCc29TZkWLQsZ")), new KeyPair(null, StringUtil.base58Decode("5DteGKYVUUSSaruCK6H8tpd4oYWfcyNohyhJiYGYGBVzhuEmAmRRNcUJQzA2bk4DqqbtpaE51HTD1i3keTvtbCTL"))},
	        { new KeyPair(null, StringUtil.base58Decode("BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN")), new KeyPair(null, StringUtil.base58Decode("wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp"))},
	    };
	}
	
	/**
     * @dataProvider convertSignToEncryptProvider
     * 
     * @param object $expected
     * @param object $sign
     */
	@Test
	@UseDataProvider("convertSignToEncryptProvider")
    public void testConvertSignToEncrypt(KeyPair expected, KeyPair sign)
    {
        AccountFactory factory = new AccountFactory("W", 0);
        
        KeyPair encrypt = factory.convertSignToEncrypt(sign);
        
        assertEquals(StringUtil.base58Encode(expected.getPublickey()), StringUtil.base58Encode(encrypt.getPublickey()));
        assertEquals(StringUtil.base58Encode(expected.getSecretkey()), StringUtil.base58Encode(encrypt.getSecretkey()));
    }
	
	@Test
	public void testSeed()
	{
		AccountFactory factory = new AccountFactory("W", 0);
		
		Account account = factory.seed(seedText);
		
		assertTrue(account instanceof Account);
		
		assertBase58Equals("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y", account.sign.getPublickey());
		assertBase58Equals("wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp", account.sign.getSecretkey());
		
		assertBase58Equals("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6", account.encrypt.getPublickey());
		assertBase58Equals("BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN", account.encrypt.getSecretkey());
	}
	
	@DataProvider
	public static Object[][] createSecretProvider() {
		String sign = "{\"secretkey\":\"wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp\",\"publickey\":\"FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y\"}";
		String signSecret = "{\"secretkey\":\"wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp\"}";
		String encrypt = "{\"secretkey\":\"BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN\",\"publickey\":\"BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6\"}";
		String encryptSecret = "{\"secretkey\":\"BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN\"}";
		String address = "3PLSsSDUn3kZdGe8qWEDak9y8oAjLVecXV1";
		
		return new Object[][] {
	        { JsonUtil.jsonDecode("{\"sign\":"+sign+",\"encrypt\":"+encrypt+",\"address\":\""+address+"\"}"), true, true},
	        { JsonUtil.jsonDecode("{\"sign\":"+sign+",\"encrypt\":"+encrypt+"}"), true, true},
	        { JsonUtil.jsonDecode("{\"sign\":"+sign+",\"address\":\""+address+"\"}"), true, true},
	        { JsonUtil.jsonDecode("{\"sign\":"+sign+"}"), true, true},
	        { JsonUtil.jsonDecode("{\"encrypt\":"+encrypt+",\"address\":\""+address+"\"}"), false, true},
	        { JsonUtil.jsonDecode("{\"encrypt\":"+encrypt+"}"), false, true},
	        { JsonUtil.jsonDecode("{\"address\":\""+address+"\"}"), false, false},
	        { JsonUtil.jsonDecode("{\"sign\":"+signSecret+",\"encrypt\":"+encryptSecret+",\"address\":\""+address+"\"}"), true, true},
	        { JsonUtil.jsonDecode("{\"sign\":"+signSecret+",\"encrypt\":"+encryptSecret+"}"), true, true},
	        { JsonUtil.jsonDecode("{\"sign\":"+signSecret+"}"), true, true},
	        { JsonUtil.jsonDecode("{\"sign\":"+signSecret+"}"), true, true},
	        { JsonUtil.jsonDecode("{\"encrypt\":"+encryptSecret+"}"), false, true},
	    };
	}
	
	public static KeyPair getSign(JsonObject data) {
		try {
			if (data.has("sign")) {
				OrderedJSONObject sign = (OrderedJSONObject) data.get("sign");
				KeyPair key = new KeyPair();
				if (sign.has("publickey")) {
					key.setPublickey(StringUtil.base58Decode(sign.getString("publickey")));
				}
				if (sign.has("secretkey")) {
					key.setSecretkey(StringUtil.base58Decode(sign.getString("secretkey")));
				}
				return key;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static KeyPair getEncrypt(JsonObject data) {
		try {
			if (data.has("encrypt")) {
				OrderedJSONObject sign = (OrderedJSONObject) data.get("encrypt");
				KeyPair key = new KeyPair();
				if (sign.has("publickey")) {
					key.setPublickey(StringUtil.base58Decode(sign.getString("publickey")));
				}
				if (sign.has("secretkey")) {
					key.setSecretkey(StringUtil.base58Decode(sign.getString("secretkey")));
				}
				return key;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] getAddress(JsonObject data) {
		if (data.has("address")) {
			return StringUtil.base58Decode(data.getString("address"));
		}
		return null;
	}
	
	/**
     * @dataProvider createSecretProvider
     * 
     * @param array|string $data
     * @param boolean      $hasSign
     * @param boolean      $hasEncrypt
     */
	@Test
	@UseDataProvider("createSecretProvider")
	public void testCreate(JsonObject data, boolean hasSign, boolean hasEncrypt) {
		AccountFactory factory = new AccountFactory("W", 0);
		KeyPair sign = getSign(data);
		KeyPair encrypt = getEncrypt(data);
		byte[] address = getAddress(data);
		
		Account account = factory.create(sign, encrypt, address);
		
		assertTrue(account instanceof Account);
		
		if (hasSign) {
			assertTrue(account.sign instanceof KeyPair);
			
			assertBase58Equals("wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp", account.sign.getSecretkey());
			
			assertBase58Equals("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y", account.sign.getPublickey());
		} else {
			assertNull(account.sign);
		}
		
		if (hasEncrypt) {
			assertTrue(account.encrypt instanceof KeyPair);
			
			assertBase58Equals("BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN", account.encrypt.getSecretkey());
			
			assertBase58Equals("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6", account.encrypt.getPublickey());
		} else {
			assertNull(account.encrypt);
		}
		
		assertBase58Equals("3PLSsSDUn3kZdGe8qWEDak9y8oAjLVecXV1", account.address);
	}
	
	/**
     * @expectedException LTO\InvalidAccountException
     * @expectedExceptionMessage Public encrypt key doesn't match private encrypt key
     */
	@Test
	public void testCreateEncryptKeyMismatch()
	{
		thrown.expect(InvalidAccountException.class);
        thrown.expectMessage("Public encrypt key doesn't match private encrypt key");
        
		AccountFactory factory = new AccountFactory("W", 0);
		
		KeyPair encrypt = new KeyPair();
		encrypt.setPublickey(StringUtil.base58Decode("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6"));
		encrypt.setSecretkey(StringUtil.base58Decode("ACsYcMff8UPUc5dvuCMAkqZxcRTjXHMnCc29TZkWLQsZ"));
		
		Account account = factory.create(null, encrypt, null);
		
		assertTrue(account instanceof Account);
	}
	
	/**
     * @expectedException LTO\InvalidAccountException
     * @expectedExceptionMessage Public sign key doesn't match private sign key
     */
	@Test
	public void testCreateSignKeyMismatch()
	{
		thrown.expect(InvalidAccountException.class);
        thrown.expectMessage("Public sign key doesn't match private sign key");
        
        AccountFactory factory = new AccountFactory("W", 0);
		
		KeyPair sign = new KeyPair();
		sign.setPublickey(StringUtil.base58Decode("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y"));
		sign.setSecretkey(StringUtil.base58Decode("5DteGKYVUUSSaruCK6H8tpd4oYWfcyNohyhJiYGYGBVzhuEmAmRRNcUJQzA2bk4DqqbtpaE51HTD1i3keTvtbCTL"));
		
		Account account = factory.create(sign, null, null);
		
		assertTrue(account instanceof Account);
	}
	
	/**
     * @expectedException LTO\InvalidAccountException
     * @expectedExceptionMessage Sign key doesn't match encrypt key
     */
	@Test
	public void testCreateKeyMismatch()
	{
		thrown.expect(InvalidAccountException.class);
        thrown.expectMessage("Sign key doesn't match encrypt key");
        
        AccountFactory factory = new AccountFactory("W", 0);
		
		KeyPair sign = new KeyPair();
		sign.setPublickey(StringUtil.base58Decode("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y"));
		
		KeyPair encrypt = new KeyPair();
		encrypt.setPublickey(StringUtil.base58Decode("EZa2ndj6h95m3xm7DxPQhrtANvhymNC7nWQ3o1vmDJ4x"));
		
		Account account = factory.create(sign, encrypt, null);
		
		assertTrue(account instanceof Account);
	}
	
	/**
     * @expectedException LTO\InvalidAccountException
     * @expectedExceptionMessage Address doesn't match keypair; possible network mismatch
     */
	@Test
	public void testCreateAddressMismatch()
	{
		thrown.expect(InvalidAccountException.class);
        thrown.expectMessage("Address doesn't match keypair; possible network mismatch");
        
        AccountFactory factory = new AccountFactory("W", 0);
		
		KeyPair sign = new KeyPair();
		sign.setPublickey(StringUtil.base58Decode("gVVExGUK4J5BsxwUfYsFkkjpn6A7BcvYdmARL28GBRc"));
		
		KeyPair encrypt = new KeyPair();
		encrypt.setPublickey(StringUtil.base58Decode("EZa2ndj6h95m3xm7DxPQhrtANvhymNC7nWQ3o1vmDJ4x"));
		
		byte[] address = StringUtil.base58Decode("3PLSsSDUn3kZdGe8qWEDak9y8oAjLVecXV1");
		
		Account account = factory.create(sign, encrypt, address);
		
		assertTrue(account instanceof Account);
	}
	
	@DataProvider
	public static Object[][] createPublicProvider() {
		return new Object[][] {
	        { StringUtil.base58Decode("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y"), StringUtil.base58Decode("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6")},
	        { StringUtil.base58Decode("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y"), null},
	        { null, StringUtil.base58Decode("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6")},
	        { StringUtil.base64Decode("2yYhlEGdosg7QZC//hibHiZ1MHk2m7jp/EbUeFdzDis="), null},
	        { BinHex.hex2bin("DB262194419DA2C83B4190BFFE189B1E26753079369BB8E9FC46D47857730E2B"), null}
	    };
	}
	
	/**
     * @dataProvider createPublicProvider
     * 
     * @param string $signkey
     * @param string $encryptkey
     * @param string $encoding
     */
	@Test
	@UseDataProvider("createPublicProvider")
	public void testCreatePublic(byte[] signkey, byte[] encryptkey)
	{
		AccountFactory factory = new AccountFactory("W", 0);
		Account account = factory.createPublic(signkey, encryptkey);
		
		assertTrue(account instanceof Account);
		
		if (signkey != null) {
			assertTrue(account.sign.getSecretkey() == null);
			assertTrue(account.sign.getPublickey() != null);
			assertBase58Equals("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y", account.sign.getPublickey());
		} else {
			assertNull(account.sign);
		}
		
		assertTrue(account.encrypt.getSecretkey() == null);
		assertTrue(account.encrypt.getPublickey() != null);
		assertBase58Equals("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6", account.encrypt.getPublickey());
		
		assertBase58Equals("3PLSsSDUn3kZdGe8qWEDak9y8oAjLVecXV1", account.address);
	}
}
