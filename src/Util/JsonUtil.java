/**
 * 
 */
package Util;

import java.io.StringWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author moonbi
 *
 */
public class JsonUtil {
	public static String jsonEncode(JSONObject object) {
		try {
			StringWriter out = new StringWriter();
			object.writeJSONString(out);
			return out.toString();
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}		
	}
	
	public static String jsonEncode(JSONArray array) {
		try {
			StringWriter out = new StringWriter();
			array.writeJSONString(out);
			return out.toString();
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}
}
