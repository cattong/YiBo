package com.cattong.sns.impl.renren;


import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;

public class RenRenBaseAdapter {

	public static int createIntegerResult(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			int result = json.getInt("result");
			return result;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}
}
