package legalthings.lto_api.lto.core;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import legalthings.lto_api.utils.core.JsonObject;
import legalthings.lto_api.utils.main.JsonUtil;
import legalthings.lto_api.utils.main.StringUtil;

@RunWith(DataProviderRunner.class)
public class AccountFactoryTest {
	public String seedText = "manage manual recall harvest series desert melt police rose hollow moral pledge kitten position add";
	
	class Address {
		String val1;
		String val2;
		int val3;
	}
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
	        { "publickey", "EZa2ndj6h95m3xm7DxPQhrtANvhymNC7nWQ3o1vmDJ4x", "publickey", "gVVExGUK4J5BsxwUfYsFkkjpn6A7BcvYdmARL28GBRc" },
	        { "publickey", "BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6", "publickey", "FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y" },
	        { "publickey", "4Xpf8guEGD3ZnRJLuEu8JjpmKnHpXR49mFE4Zm9m9P1z", "publickey", "96yeNG1KYJKAVnfKqfkfktkXuPj1CLPEsgCDkm42VcaT" },
	        { "publickey", "Efv4wPdjfyVNvbp21xwiTXnirQti7jJy56W9doDVzfhG", "publickey", "7TecQdLbPuxt3mWukbZ1g1dTZeA6rxgjMxfS9MRURaEP" },
	        { "secretkey", "ACsYcMff8UPUc5dvuCMAkqZxcRTjXHMnCc29TZkWLQsZ", "secretkey", "5DteGKYVUUSSaruCK6H8tpd4oYWfcyNohyhJiYGYGBVzhuEmAmRRNcUJQzA2bk4DqqbtpaE51HTD1i3keTvtbCTL" },
	        { "secretkey", "BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN", "secretkey", "wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp" },
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
    public void testConvertSignToEncrypt(String expectedType, String expected, String signType, String sign)
    {		
        byte[] _sign = StringUtil.base58Decode(sign);
        
        AccountFactory factory = new AccountFactory("W", 0);
        
        JsonObject signObject = new JsonObject();
        signObject.putByte(signType, _sign);
        
        JsonObject encryptObject = factory.convertSignToEncrypt(signObject);
        Iterator<?> keys = encryptObject.keys();
        
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            byte[] value = encryptObject.getByte(key);
            encryptObject.put(key, StringUtil.base58Encode(value));
        }
        
        assertEquals(expected, encryptObject.getString(expectedType));
    }
	
	@Test
	public void testSeed()
	{
		AccountFactory factory = new AccountFactory("W", 0);
		
		Account account = factory.seed(seedText);
		
		assertTrue(account instanceof Account);
		
		assertBase58Equals("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y", account.sign.getByte("publickey"));
		assertBase58Equals("wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp", account.sign.getByte("secretkey"));
		
		assertBase58Equals("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6", account.encrypt.getByte("publickey"));
		assertBase58Equals("BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN", account.encrypt.getByte("secretkey"));
	}
	
//	@DataProvider
//	public static Object[][] createSecretProvider() {
//		String sign = "{\"secretkey\":\"wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp\",\"publickey\":\"FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y\"}";
//		String signSecret = "{\"secretkey\":\"wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp\"}";
//		String encrypt = "{\"secretkey\":\"BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN\",\"publickey\":\"BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6\"}";
//		String encryptSecret = "{\"secretkey\":\"BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN\"}";
//		String address = "3PLSsSDUn3kZdGe8qWEDak9y8oAjLVecXV1";
//		
//		return new Object[][] {
//	        { JsonUtil.jsonDecode("{\"sign\":"+sign+",\"encrypt\":"+encrypt+",\"address\":\""+address+"\"}"), true, true},
////	        { JsonUtil.jsonDecode("{\"sign\":"+sign+",\"encrypt\":"+encrypt+"}"), true, true},
////	        { JsonUtil.jsonDecode("{\"sign\":"+sign+",\"address\":\""+address+"\"}"), true, true},
////	        { JsonUtil.jsonDecode("{\"sign\":"+sign+"}"), true, true},
////	        { JsonUtil.jsonDecode("{\"encrypt\":"+encrypt+",\"address\":\""+address+"\"}"), false, true},
////	        { JsonUtil.jsonDecode("{\"encrypt\":"+encrypt+"}"), false, true},
////	        { JsonUtil.jsonDecode("{\"address\":"+encrypt+"}"), false, false},
////	        { JsonUtil.jsonDecode("{\"sign\":"+signSecret+",\"encrypt\":"+encryptSecret+",\"address\":\""+address+"\"}"), true, true},
////	        { JsonUtil.jsonDecode("{\"sign\":"+signSecret+",\"encrypt\":"+encryptSecret+"}"), true, true},
////	        { JsonUtil.jsonDecode("{\"sign\":"+signSecret+"}"), true, true},
////	        { "wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp", true, true},
////	        { JsonUtil.jsonDecode("{\"encrypt\":"+encryptSecret+"}"), false, true},
//	    };
//	}
//	
//	/**
//     * @dataProvider createSecretProvider
//     * 
//     * @param array|string $data
//     * @param boolean      $hasSign
//     * @param boolean      $hasEncrypt
//     */
//	@Test
//	@UseDataProvider("createSecretProvider")
//	public void testCreate(Object data, boolean hashSign, boolean hashEncrypt) {
//		AccountFactory factory = new AccountFactory("W", 0);
//		Account account = factory.create(data);
//	}
}
