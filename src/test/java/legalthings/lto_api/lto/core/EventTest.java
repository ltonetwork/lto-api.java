package legalthings.lto_api.lto.core;

import static org.junit.Assert.*;

import java.util.Date;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import legalthings.lto_api.lto.exceptions.BadMethodCallException;
import legalthings.lto_api.utils.core.JsonObject;
import org.powermock.api.easymock.PowerMock;

public class EventTest {
	private Event event;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUp() {
		JsonObject data = new JsonObject();
		data.put("foo", "bar");
		data.put("color", "red");
		
		event = new Event(data, "72gRWx4C1Egqz9xvUBCYVdgh7uLc5kmGbjXFhiknNCTW");
		
		assertEquals("HeFMDcuveZQYtBePVUugLyWtsiwsW4xp7xKdv", event.body);
		assertTrue(event.timestamp instanceof Date);
		assertEquals("72gRWx4C1Egqz9xvUBCYVdgh7uLc5kmGbjXFhiknNCTW", event.previous);
	}
	
	@Test
    public void testGetMessage()
    {
        event.timestamp = new Date(1519862400);
        event.signkey = "FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y";
        
        String expected = String.join("\n", new String[] {
            "HeFMDcuveZQYtBePVUugLyWtsiwsW4xp7xKdv",
            "1519862400",
            "72gRWx4C1Egqz9xvUBCYVdgh7uLc5kmGbjXFhiknNCTW",
            "FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y"
        });
        assertEquals(expected, event.getMessage());
    }
	
	@Test
    public void testGetMessageNoBody()
    {
		thrown.expect(BadMethodCallException.class);
        thrown.expectMessage("Body unknown");
        
        Event _event = new Event();
        _event.getMessage();
    }
	
	@Test
	public void testGetMessageNoSignkey()
    {
		thrown.expect(BadMethodCallException.class);
        thrown.expectMessage("First set signkey before creating message");
        
		JsonObject data = new JsonObject();
		data.put("foo", "bar");
		data.put("color", "red");
		
        Event _event = new Event(data);
        _event.getMessage();
    }
	
	@Test
	public void testGetHash()
    {
		event.timestamp = new Date(1519862400);
        event.signkey = "FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y";

        assertEquals("Bpq9rZt12Gv44dkXFw8RmLYzbaH2HBwPQJ6KihdLe5LG", event.getHash());
    }
	
	@Test
	public void testVerifySignatureFail()
	{
		event.timestamp = new Date(1519862400);
        event.signkey = "FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y";
        event.timestamp = new Date(1519084800);
        event.signature = "258KnaZxcx4cA9DUWSPw8QwBokRGzFDQmB4BH9MRJhoPJghsXoAZ7KnQ2DWR7ihtjXzUjbsXtSeup4UDcQ2L6RDL";
        assertFalse(event.verifySignature());
	}
	
	@Test
	public void testVerifySignatureNoSignature()
	{
		thrown.expect(BadMethodCallException.class);
		Event _event = new Event();
		event.signkey = "FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y";
		
		event.verifySignature();
	}
	
	@Test
	public void testVerifySignatureNoSignkey()
	{
		thrown.expect(BadMethodCallException.class);
		Event _event = new Event();
		event.signature = "258KnaZxcx4cA9DUWSPw8QwBokRGzFDQmB4BH9MRJhoPJghsXoAZ7KnQ2DWR7ihtjXzUjbsXtSeup4UDcQ2L6RDL";
		
		event.verifySignature();
	}
	
	@Test
	public void testSignWith() throws Exception
	{
		JsonObject data = new JsonObject(true);
		Event _event = new Event(data, "");
		
		Account account = PowerMock.createMock(Account.class);
		EasyMock.expect(account.signEvent(_event)).andReturn(_event);
		PowerMock.replayAll();
		
		Event ret = _event.signWith(account);
		
		assertSame(_event, ret);
	}
	
	@Test
	public void testAddTo()
	{
		JsonObject data = new JsonObject(true);
		Event _event = new Event(data, "");
		
		EventChain chain = PowerMock.createMock(EventChain.class);
		EasyMock.expect(chain.add(_event)).andReturn(_event);
		PowerMock.replayAll();
		
		Event ret = _event.addTo(chain);
		
		assertSame(_event, ret);
	}
}
