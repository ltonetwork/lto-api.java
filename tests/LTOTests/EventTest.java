package LTOTests;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import LTO.core.Event;
import LTO.exceptions.BadMethodCallException;
import Util.core.JsonObject;

import static org.easymock.EasyMock.*;

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
	public void testSignWith()
	{
		JsonObject data = new JsonObject(true);
		Event _event = new Event(data, "");
	}
}
