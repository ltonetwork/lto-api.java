package com.ltonetwork.client.utils;

public class JsonUtil {
    public static String jsonEncode(JsonObject object) {
        return object.toString();
    }

    public static JsonObject jsonDecode(String input) {
        return new JsonObject(input);
    }
}
