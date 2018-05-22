package legalthings.lto_api.lto.core;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.api.easymock.PowerMock;

import legalthings.lto_api.lto.exceptions.BadMethodCallException;
import legalthings.lto_api.lto.exceptions.InvalidArgumentException;
import legalthings.lto_api.utils.core.JsonObject;
import legalthings.lto_api.utils.main.StringUtil;

public class EventChainTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testConstruct() 
	{
		EventChain chain = new EventChain();
		
		assertNull(chain.getLatestHash());
	}

	@Test
	public void testConstructId()
	{
		EventChain chain = new EventChain("L1hGimV7Pp2CFNUnTCitqWDbk9Zng3r3uc66dAG6hLwEx");
		
		assertEquals("L1hGimV7Pp2CFNUnTCitqWDbk9Zng3r3uc66dAG6hLwEx", chain.id);
		assertEquals("9HM1ykH7AxLgdCqBBeUhvoTH4jkq3zsZe4JGTrjXVENg", chain.getLatestHash());
	}
	
	@Test
	public void testConstructLatestHash()
	{
		EventChain chain = new EventChain("L1hGimV7Pp2CFNUnTCitqWDbk9Zng3r3uc66dAG6hLwEx", "3yMApqCuCjXDWPrbjfR5mjCPTHqFG8Pux1TxQrEM35jj");
		
		assertEquals("L1hGimV7Pp2CFNUnTCitqWDbk9Zng3r3uc66dAG6hLwEx", chain.id);
		assertEquals("3yMApqCuCjXDWPrbjfR5mjCPTHqFG8Pux1TxQrEM35jj", chain.getLatestHash());
	}
	
	@Test
	public void testAdd()
	{
		Event event = PowerMock.createMock(Event.class);
		EasyMock.expect(event.getHash()).andReturn("J26EAStUDXdRUMhm1UcYXUKtJWTkcZsFpxHRzhkStzbS");
		PowerMock.replayAll();
		
		EventChain chain = new EventChain("L1hGimV7Pp2CFNUnTCitqWDbk9Zng3r3uc66dAG6hLwEx", "3yMApqCuCjXDWPrbjfR5mjCPTHqFG8Pux1TxQrEM35jj");
		
		chain.add(event);
		assertEquals("J26EAStUDXdRUMhm1UcYXUKtJWTkcZsFpxHRzhkStzbS", chain.getLatestHash());
	}
	
	@Test
	public void testInitFor()
	{
		Account account = PowerMock.createMock(Account.class);
		KeyPair sign = new KeyPair();
		sign.setPublickey(StringUtil.base58Decode("8MeRTc26xZqPmQ3Q29RJBwtgtXDPwR7P9QNArymjPLVQ"));
		account.sign = sign; 
		
		EventChain chain = PowerMock.createPartialMock(EventChain.class, "getNonce");
		EasyMock.expect(chain.getNonce()).andReturn(StringUtil.repeat("\0", 8).getBytes());
		PowerMock.replayAll();
		
		chain.initFor(account);
		
		assertEquals("L1hGimV7Pp2CFNUnTCitqWDbk9Zng3r3uc66dAG6hLwEx", chain.id);
		assertEquals("9HM1ykH7AxLgdCqBBeUhvoTH4jkq3zsZe4JGTrjXVENg", chain.getLatestHash());
	}
	
	@Test
	public void testInitForExisting()
	{
		thrown.expect(BadMethodCallException.class);
		
		Account account = PowerMock.createMock(Account.class);
		
		EventChain chain = PowerMock.createPartialMock(EventChain.class, "getNonce");
		chain.id = "123";
		
		chain.initFor(account);
	}
	
	@Test
	public void testInitForInvalidAccount()
	{
		thrown.expect(InvalidArgumentException.class);
		
		Account account = PowerMock.createMock(Account.class);
		
		EventChain chain = PowerMock.createPartialMock(EventChain.class, "getNonce");
		
		chain.initFor(account);
	}
}
