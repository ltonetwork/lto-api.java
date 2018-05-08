package UtilTests;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Test;

import Util.utils.JsonUtil;

public class JsonUtilTest {

	@Test
	public void testJsonEncodeJSONObject() {
		JSONObject data = new JSONObject();
		data.put("foo", "bar");
		data.put("color", "red");
		assertEquals("{\"color\":\"red\",\"foo\":\"bar\"}", JsonUtil.jsonEncode(data));
	}

	@Test
	public void testJsonEncodeJSONArray() {
		
	}

}
