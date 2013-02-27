package net.dev123.yibome.converter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class StringListJSONConverter {

	public static JSONArray toJSONArray(List<String> strings) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for (String str : strings) {
			jsonArray.put(str);
		}
		return jsonArray;
	}

	public static List<String> toStringList(JSONArray jsonArray) throws JSONException {
		List<String> strings = new ArrayList<String>();
		int length = jsonArray.length();
		for (int i = 0; i < length; i++) {
			strings.add(jsonArray.getString(i));
		}
		return strings;
	}
}
