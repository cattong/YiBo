package com.cattong.socialcat.converter;

import static com.cattong.commons.util.ParseUtil.escapeAngleBrackets;
import static com.cattong.commons.util.ParseUtil.getBoolean;
import static com.cattong.commons.util.ParseUtil.getDate;
import static com.cattong.commons.util.ParseUtil.getRawString;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.ServiceProvider;
import com.cattong.entity.Location;
import com.cattong.entity.Status;
import com.cattong.entity.StatusExtInfo;

public class StatusJSONConverter {

	/**
	 * 从JSON字符串创建Status对象
	 *
	 * @param jsonString
	 *            JSON字符串
	 * @return Status对象
	 * @throws LibException
	 */
	public static Status createStatus(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createStatus(json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	public static StatusExtInfo createStatusExtInfo(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createStatusExtInfo(json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}
	
	/**
	 * 从JSON字符串创建Status列表
	 *
	 * @param jsonString
	 *            JSON字符串
	 * @return Status对象列表
	 * @throws LibException
	 */
	public static ArrayList<Status> createStatusList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<Status>(0);
			}
			JSONArray jsonList = new JSONArray(jsonString);
			int size = jsonList.length();
			ArrayList<Status> statusList = new ArrayList<Status>(size);
			for (int i = 0; i < size; i++) {
				statusList.add(createStatus(jsonList.getJSONObject(i)));
			}
			return statusList;

		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	/**
	 * 从JSON对象创建Status对象，包级别访问控制
	 *
	 * @param json
	 *            JSON对象
	 * @return Status对象
	 * @throws LibException
	 */
	static Status createStatus(JSONObject json) throws LibException {
		try {
			Status status = new Status();
			status.setStatusId(getRawString("statusId", json));
			status.setText(escapeAngleBrackets(getRawString("text", json)));
			status.setSource(getRawString("source", json));
			status.setCreatedAt(getDate("createdAt", json));
			status.setTruncated(getBoolean("isTruncated", json));
			status.setInReplyToStatusId(getRawString("inReplyToStatusId", json));
			//status.setInReplyToUserId(getRawString("inReplyToUserId", json));
			status.setFavorited(getBoolean("isFavorited", json));
			//status.setInReplyToScreenName(getRawString("inReplyToScreenName", json));
			if (!json.isNull("user")) {
				status.setUser(UserJSONConverter.toUser(json.getJSONObject("user")));
			}
			if (!json.isNull("retweetedStatus")) {
				status.setRetweetedStatus(createStatus(json.getJSONObject("retweeted_status")));
			}
			if (!json.isNull("thumbnailPictureUrl")) {
				status.setThumbnailPictureUrl(getRawString("thumbnailPictureUrl", json));
			}
			if (!json.isNull("middlePictureUrl")) {
				status.setMiddlePictureUrl(getRawString("middlePictureUrl", json));
			}
			if (!json.isNull("originalPictureUrl")) {
				status.setOriginalPictureUrl(getRawString("originalPictureUrl", json));
			}
			if (!json.isNull("geo")) {
				JSONObject geo = json.getJSONObject("geo");
				//String type = geo.getString("type"); //Point ...
				JSONArray coordinates = geo.getJSONArray("coordinates");
				double latitude = coordinates.getDouble(0);
				double longitude = coordinates.getDouble(1);
				Location location = new Location(latitude, longitude);
				status.setLocation(location);				
			}
			int serviceProviderNo = json.getInt("serviceProviderNo");
			status.setServiceProvider(ServiceProvider.getServiceProvider(serviceProviderNo));

			return status;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR);
		}
	}
	
	static StatusExtInfo createStatusExtInfo(JSONObject json) throws LibException {
		try {
			StatusExtInfo statusExtInfo = new StatusExtInfo();
			statusExtInfo.setGlobalStatusId(json.getString("glboalStatusId"));
			int serviceProviderNo = json.getInt("serviceProviderNo");
			statusExtInfo.setServiceProvider(ServiceProvider.getServiceProvider(serviceProviderNo));
			statusExtInfo.setStatusId(getRawString("statusId", json));
			statusExtInfo.setRetweetCount(json.getInt("rewteetCount"));
			statusExtInfo.setCommentCount(json.getInt("commentCount"));
			statusExtInfo.setLikeCount(json.getInt("likeCount"));
			statusExtInfo.setHateCount(json.getInt("hateCount"));
			statusExtInfo.setStatusCatalogNo(json.getInt("statusCatalogNo"));
			statusExtInfo.setContainPicture(json.getBoolean("isContainPicture"));
			
			if (!json.isNull("status")) {
				Status status = createStatus(json.getJSONObject("status"));
				statusExtInfo.setStatus(status);
			}			

			return statusExtInfo;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}
}
