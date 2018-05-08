package LTOTests;

import static org.junit.Assert.*;

import java.util.Date;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import LTO.core.Event;

public class EventTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testConstruct() {
		JSONObject data = new JSONObject();
		data.put("foo", "bar");
		data.put("color", "red");
		Event event = new Event(data, "72gRWx4C1Egqz9xvUBCYVdgh7uLc5kmGbjXFhiknNCTW");
		
		assertEquals("HeFMDcuveZQYtBePVUugLyWtsiwsW4xp7xKdv", event.body);
		assertTrue(event.timestamp instanceof Date);
		assertEquals("72gRWx4C1Egqz9xvUBCYVdgh7uLc5kmGbjXFhiknNCTW", event.previous);
	}

}
