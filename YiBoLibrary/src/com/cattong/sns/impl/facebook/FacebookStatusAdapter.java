package com.cattong.sns.impl.facebook;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.util.ParseUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.sns.entity.Status;

public class FacebookStatusAdapter {

	public static Status createStatus(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createStatus(json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<Status> createStatusList(String jsonString)
			throws LibException {
		try {
			if (StringUtil.isEquals("{}", jsonString)
					|| StringUtil.isEquals("[]", jsonString)) {
				return new ArrayList<Status>(0);
			}
			JSONArray jsonArray = new JSONArray(jsonString);
			int length = jsonArray.length();
			List<Status> statuses = new ArrayList<Status>(length);
			for (int i = 0; i < length; i++) {
				statuses.add(createStatus(jsonArray.getJSONObject(i)));
			}
			return statuses;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	public static Status createStatus(JSONObject json) throws LibException {
		if (json == null) {
			return null;
		}
		try {
			Status status = new Status();
			status.setId(ParseUtil.getRawString("id", json));
			status.setText(ParseUtil.getRawString("message", json));
			status.setUpdatedTime(ParseUtil.getDate("update_time", json, Facebook.DATE_FORMAT));
			if (json.has("comments")) {
				status.setCommentsCount(json.getJSONObject("comments").getJSONArray("data").length());
			}
			return status;
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR, e);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}


}
