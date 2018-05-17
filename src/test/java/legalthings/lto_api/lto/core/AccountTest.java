package legalthings.lto_api.lto.core;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.api.easymock.PowerMock;

import legalthings.lto_api.utils.core.JsonObject;
import legalthings.lto_api.utils.main.StringUtil;

public class AccountTest {

private Account account;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUp() throws Exception {
		account = PowerMock.createPartialMock(Account.class, "getNonce");
		EasyMock.expect(account.getNonce()).andReturn(StringUtil.repeat("\0", 24).getBytes());
		PowerMock.replayAll();
		
		account.address = StringUtil.base58Decode("3PLSsSDUn3kZdGe8qWEDak9y8oAjLVecXV1");
		
		JsonObject sign = new JsonObject();
		sign.putByte("secretkey", StringUtil.base58Decode("wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp"));
		sign.putByte("publickey", StringUtil.base58Decode("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y"));
		account.sign = sign;
		
		JsonObject encrypt = new JsonObject();
		encrypt.putByte("secretkey", StringUtil.base58Decode("BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN"));
		encrypt.putByte("publickey", StringUtil.base58Decode("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6"));
		account.encrypt = encrypt;
	}

	@Test
	public void testGetAddress() throws UnsupportedEncodingException
    {
		assertEquals("3PLSsSDUn3kZdGe8qWEDak9y8oAjLVecXV1", account.getAddress());
    }
	
	@Test
	public void testGetPublicSignKey()
    {
		assertEquals("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y", account.getPublicSignKey());
    }
	
	@Test
	public void testGetPublicEncryptKey()
    {
		assertEquals("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6", account.getPublicEncryptKey());
    }
	
	@Test
	public void testSign()
    {
        String signature = account.sign("hello");
        
        assertEquals(
            "2DDGtVHrX66Ae8C4shFho4AqgojCBTcE4phbCRTm3qXCKPZZ7reJBXiiwxweQAkJ3Tsz6Xd3r5qgnbA67gdL5fWE",
            signature
        );
    }
	
	@Test
    public void testSignNoKey()
    {
		thrown.expect(RuntimeException.class);
        thrown.expectMessage("Unable to sign message; no secret sign key");
        
        Account _account = new Account();
        
        _account.sign("hello");
    }
	
	@Test
	public void testSignEvent()
    {
        String message = String.join("\n", new String[] {
            "HeFMDcuveZQYtBePVUugLyWtsiwsW4xp7xKdv",
            "2018-03-01T00:00:00+00:00",
            "72gRWx4C1Egqz9xvUBCYVdgh7uLc5kmGbjXFhiknNCTW",
            "FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y"
        });
        
        Event event = PowerMock.createMock(Event.class);
        EasyMock.expect(event.getMessage()).andReturn(message);
        EasyMock.expect(event.getHash()).andReturn("47FmxvJ4v1Bnk4SGSwrHcncX5t5u3eAjmc6QJgbR5nn8");
        PowerMock.replayAll();
          
        Event ret = account.signEvent(event);
        assertSame(event, ret);
        
        assertEquals("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y", event.signkey);
        assertEquals("Szr7uLhwirqEuVJ9SBPuAgvFAbuiMG23FbCsVNbptLbMH7uzrR5R23Yze83YGe98HawMzjvEMWgsJhdRQDXw8Br", event.signature);
        assertEquals("47FmxvJ4v1Bnk4SGSwrHcncX5t5u3eAjmc6QJgbR5nn8", event.hash);
    }
	
	@Test
	public void testVerify()
    {
        String signature = "2DDGtVHrX66Ae8C4shFho4AqgojCBTcE4phbCRTm3qXCKPZZ7reJBXiiwxweQAkJ3Tsz6Xd3r5qgnbA67gdL5fWE";
        
        assertTrue(account.verify(signature, "hello"));
    }
	
	@Test
	public void testVerifyFail()
    {
        String signature = "2DDGtVHrX66Ae8C4shFho4AqgojCBTcE4phbCRTm3qXCKPZZ7reJBXiiwxweQAkJ3Tsz6Xd3r5qgnbA67gdL5fWE";
        
        assertFalse(account.verify(signature, "not this"));
    }
	
	@Test
    public void testVerifyInvalid()
    {
		thrown.expect(RuntimeException.class);
        String signature = "not a real signature";
        
        assertTrue(account.verify(signature, "hello"));
    }
	
	public Account createSecondaryAccount() throws Exception
    {        
        Account _account = PowerMock.createPartialMock(Account.class, "getNonce");
        EasyMock.expect(_account.getNonce()).andReturn(StringUtil.repeat("\0", 24).getBytes());
		PowerMock.replayAll();

        _account.address = StringUtil.base58Decode("3PPbMwqLtwBGcJrTA5whqJfY95GqnNnFMDX");
        
        JsonObject sign = new JsonObject();
		sign.putByte("secretkey", StringUtil.base58Decode("pLX2GgWzkjiiPp2SsowyyHZKrF4thkq1oDLD7tqBpYDwfMvRsPANMutwRvTVZHrw8VzsKjiN8EfdGA9M84smoEz"));
		sign.putByte("publickey", StringUtil.base58Decode("BvEdG3ATxtmkbCVj9k2yvh3s6ooktBoSmyp8xwDqCQHp"));
		_account.sign = sign;
		
		JsonObject encrypt = new JsonObject();
		encrypt.putByte("secretkey", StringUtil.base58Decode("3kMEhU5z3v8bmer1ERFUUhW58Dtuhyo9hE5vrhjqAWYT"));
		encrypt.putByte("publickey", StringUtil.base58Decode("HBqhfdFASRQ5eBBpu2y6c6KKi1az6bMx8v1JxX4iW1Q8"));
		_account.encrypt = encrypt;
        
        return _account;
    }
	
	@Test
	public void testEncryptFor() throws Exception
    {        
        Account recipient = createSecondaryAccount();
        
        byte[] cyphertext = account.encryptFor(recipient, "hello");
        
        assertEquals("3NQBM8qd7nbLjABMf65jdExWt3xSAtAW2Sonjc7ZTLyqWAvDgiJNq7tW1XFX5H", StringUtil.base58Encode(cyphertext));
    }
	
	@Test
	public void testDecryptFrom() throws Exception
    {        
		Account recipient = createSecondaryAccount();
        byte[] ciphertext = StringUtil.base58Decode("3NQBM8qd7nbLjABMf65jdExWt3xSAtAW2Sonjc7ZTLyqWAvDgiJNq7tW1XFX5H");
        
        byte[] message = recipient.decryptFrom(account, ciphertext);
        
        assertEquals("hello", new String(message));
    }
	
	@Test
    public void testDecryptFromFail()
    {
		thrown.expect(RuntimeException.class);
        byte[] ciphertext = StringUtil.base58Decode("3NQBM8qd7nbLjABMf65jdExWt3xSAtAW2Sonjc7ZTLyqWAvDgiJNq7tW1XFX5H");
        
        account.decryptFrom(account, ciphertext);
    }
	
//    protected void assertValidId(byte[] signkey, EventChain chain)
//    {
//        String signkeyHashed = HashUtil.Keccak256(CryptoUtil.crypto_generichash(signkey, 32)).substring(0, 40);
        
//        String decodedId = StringUtil.decodeBase58(chain.id);
        
//        $vars = (object)unpack('Cversion/H16random/H40keyhash/H8checksum', $decodedId);
        
//        assertEquals(EventChain.ADDRESS_VERSION, vars.version);
//        assertEquals(signkeyHashed.substring(0,  40), vars.keyhash);
//        assertEquals(BinHex.bin2hex(decodedId.getBytes()).substring(BinHex.bin2hex(decodedId.getBytes()).length() - 8), vars.checksum);
//    }
//    
//    @Test
//    public void testCreateEventChain()
//    {
//        EventChain chain = account.createEventChain();
//        
//        assertThat(chain, instanceOf(EventChain.class));
//        assertValidId(account.sign.get("publickey"), chain);
//    }

}
