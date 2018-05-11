package LTOTests;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.api.easymock.PowerMock;

import LTO.core.Account;
import LTO.core.Event;
import LTO.core.EventChain;
import LTO.exceptions.BadMethodCallException;
import LTO.exceptions.InvalidArgumentException;
import Util.core.JsonObject;
import Util.utils.StringUtil;

public class EventChainTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testConstruct() {
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
    public void testAdd() throws Exception
    {
        Event event = PowerMock.createMock(Event.class);
        PowerMock.expectPrivate(event, "getHash").andReturn("J26EAStUDXdRUMhm1UcYXUKtJWTkcZsFpxHRzhkStzbS");
        
        EventChain chain = new EventChain("L1hGimV7Pp2CFNUnTCitqWDbk9Zng3r3uc66dAG6hLwEx", "3yMApqCuCjXDWPrbjfR5mjCPTHqFG8Pux1TxQrEM35jj");
        
        chain.add(event);
        assertEquals("J26EAStUDXdRUMhm1UcYXUKtJWTkcZsFpxHRzhkStzbS", chain.getLatestHash());
    }
    
    @Test
    public void testInitFor() throws Exception
    {
        Account account = PowerMock.createMock(Account.class);
        
        JsonObject sign = new JsonObject();
        sign.put("publickey", StringUtil.decodeBase58("8MeRTc26xZqPmQ3Q29RJBwtgtXDPwR7P9QNArymjPLVQ"));
        account.sign = sign;
        
        EventChain chain = PowerMock.createPartialMock(EventChain.class, "getNonce");
        PowerMock.expectPrivate(chain, "getNonce").andReturn(StringUtil.repeat("\0", 8));
        
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
