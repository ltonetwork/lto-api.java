package com.ltonetwork.client.utils;

import com.ltonetwork.client.types.JsonObject;

public class JsonUtil {
    public static String jsonEncode(JsonObject object) {
        return object.toString();
    }

    public static JsonObject jsonDecode(String input) {
        return new JsonObject(input);
    }
}
