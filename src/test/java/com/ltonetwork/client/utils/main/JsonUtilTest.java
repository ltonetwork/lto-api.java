package com.ltonetwork.client.utils.main;

import static org.junit.Assert.*;

import com.ltonetwork.client.utils.JsonUtil;
import org.junit.Test;

import com.ltonetwork.client.utils.JsonObject;

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
