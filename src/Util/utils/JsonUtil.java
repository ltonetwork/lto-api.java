/**
 * 
 */
package Util.utils;

import java.io.StringWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;

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
	
	public static String jsonEncode(OrderedJSONObject object) {
		try {
			return object.write(false);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
