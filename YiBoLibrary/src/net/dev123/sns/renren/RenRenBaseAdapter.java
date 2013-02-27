package net.dev123.sns.renren;

import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;

import org.json.JSONException;
import org.json.JSONObject;

public class RenRenBaseAdapter {

	public static int createIntegerResult(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			int result = json.getInt("result");
			return result;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}
}
