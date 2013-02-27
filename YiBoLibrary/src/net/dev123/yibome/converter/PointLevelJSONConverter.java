package net.dev123.yibome.converter;

import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.yibome.entity.PointLevel;

import org.json.JSONException;
import org.json.JSONObject;

public class PointLevelJSONConverter {

	public static PointLevel createPointLevel(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createPointLevel(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}
	
	static PointLevel createPointLevel(JSONObject json) throws LibException {
	    try {
	    	PointLevel pointLevel = new PointLevel();
			pointLevel.setPoints(json.getInt("points"));
			pointLevel.setPointLevel(json.getString("point_level"));
			pointLevel.setTitle(json.getString("title"));
			return pointLevel;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}
}
