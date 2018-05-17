package legalthings.lto_api.utils.main;

import legalthings.lto_api.utils.core.JsonObject;

public class JsonUtil {
	public static String jsonEncode(JsonObject object) {
		return object.toString();
	}
	
	public static JsonObject jsonDecode(String input) {
		return new JsonObject(input);
	}
}
