package com.cattong.sns.impl.renren;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ParseUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.sns.entity.Status;

public class RenRenStatusAdapter {

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
			status.setId(ParseUtil.getRawString("status_id", json));
			status.setText(ParseUtil.getRawString("message", json));
			status.setUpdatedTime(ParseUtil.getDate("time", json, "yyyy-MM-dd hh:mm:ss"));
			status.setCommentsCount(ParseUtil.getInt("comment_count", json));
			status.setServiceProvider(ServiceProvider.RenRen);
			return status;
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR, e);
		}
	}


}
