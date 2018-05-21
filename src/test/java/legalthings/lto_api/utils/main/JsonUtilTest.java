package legalthings.lto_api.utils.main;

import static org.junit.Assert.*;

import org.junit.Test;

import legalthings.lto_api.utils.core.JsonObject;

public class JsonUtilTest {

	@Test
	public void testEncode() {
		JsonObject object = new JsonObject();
		
		object.put("foo", "bar");
		object.put("color", "red");
		
		String encodedString = JsonUtil.jsonEncode(object);
		assertEquals("{\"foo\":\"bar\",\"color\":\"red\"}", encodedString);
	}

	@Test
	public void testDecode() {
		String string = "{\"foo\":\"bar\",\"color\":\"red\"}";
		
		JsonObject object = JsonUtil.jsonDecode(string);
		
		assertEquals("bar", object.getString("foo"));
		assertEquals("red", object.getString("color"));
	}
}
