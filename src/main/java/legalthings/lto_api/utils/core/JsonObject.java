package legalthings.lto_api.utils.core;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;

import legalthings.lto_api.utils.main.StringUtil;

public class JsonObject {
	private static final int OBJECT = 1;
	private static final int ARRAY = 2;
	
	private int type;
	
	private OrderedJSONObject object;
	private JSONArray array;

	public JsonObject(boolean isArray) {
		if (isArray) {
			this.type = ARRAY;
		} else {
			this.type = OBJECT;
		}
		
		init();
	}
	public JsonObject() {
		this(false);
	}
	public JsonObject(String string, boolean isArray) {
		if (isArray) {
			this.type = ARRAY;
		} else {
			this.type = OBJECT;
		}
		
		init(string);
	}
	public JsonObject(String string) {
		this(string, false);
	}
	
	private void init() {
		if (type == OBJECT) {
			object = new OrderedJSONObject();
		}
		if (type == ARRAY) {
			array = new JSONArray();
		}
	}
	private void init(String string) {
		try {
			if (type == OBJECT) {
				object = new OrderedJSONObject(string);
			}
			if (type == ARRAY) {
				array = new JSONArray(string);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		try {
			if (type == OBJECT) {
				return object.write(false);
			}
			if (type == ARRAY) {
				return array.write(false);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String get(String key) {
		try {
			if (type == OBJECT) {
				return object.get(key).toString();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String get(int index) {
		if (type == ARRAY) {
			return array.get(index).toString();
		}
		return null;
	}
	
	public void put(String key, String value) {
		try {
			if (type == OBJECT) {
				object.put(key, value);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void putByte(String key, byte[] value) {
		try {
			if (type == OBJECT) {
				object.put(key, StringUtil.base58Encode(value));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public byte[] getByte(String key) {
		if (type == OBJECT) {
			return StringUtil.base58Decode(get(key));
		}
		return null;
	}
	
	public void put(int index, String value) {
		try {
			if (type == ARRAY) {
				array.put(index, value);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
