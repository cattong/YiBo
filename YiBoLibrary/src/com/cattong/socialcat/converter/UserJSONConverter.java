package com.cattong.socialcat.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.DateTimeUtil;
import com.cattong.commons.util.ParseUtil;
import com.cattong.entity.BaseUser;
import com.cattong.entity.Gender;
import com.cattong.entity.User;
import com.cattong.entity.UserExtInfo;

public class UserJSONConverter {

	public static User createUser(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return toUser(json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR);
		}
	}

	public static UserExtInfo createUserExtInfo(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			UserExtInfo userExtInfo = new UserExtInfo();
			userExtInfo.setUserId(json.getString("userId"));
			int spNo = json.getInt("serviceProviderNo");
			ServiceProvider sp = ServiceProvider.getServiceProvider(spNo);
			userExtInfo.setServiceProvider(sp);
			userExtInfo.setBirthday(ParseUtil.getDate("birthday", json));
			userExtInfo.setOriginalProfileImageUrl(json.getString("originalProfileImageUrl"));
			userExtInfo.setVerifyInfo(json.getString("verifyInfo"));

			return userExtInfo;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR);
		}
	}

	public static JSONObject toJSON(BaseUser user) throws JSONException {
		if (user == null) {
			return null;
		}
		
		JSONObject json = new JSONObject();
		json.put("userId", user.getUserId());
		json.put("serviceProviderNo", user.getServiceProvider().getSpNo());
		json.put("screenName", user.getScreenName());
		json.put("name", user.getName());
		
		Gender gender = user.getGender();
		int genderNo = gender == null ? Gender.Unkown.getGenderNo() : gender.getGenderNo();

		json.put("gender", genderNo);
		json.put("location", user.getLocation());
		json.put("description", user.getDescription());
		json.put("isVerified", user.isVerified());
		String profileImageUrl = user.getProfileImageUrl() == null ? "" : user.getProfileImageUrl();
		json.put("profileImageUrl", profileImageUrl);
		DateFormat dateFormat = DateTimeUtil.getGMTDateFormat();
		String createdAtString = user.getCreatedAt() == null ?
			dateFormat.format(new Date()) : dateFormat.format(user.getCreatedAt());
		json.put("createdAt", createdAtString);
		
		return json;
	}

	public static User toUser(JSONObject json) throws JSONException, ParseException {
		if (json == null) {
			return null;
		}
		
		User user = new User();
		user.setUserId(ParseUtil.getRawString("userId", json));
		int spNo = ParseUtil.getInt("serviceProviderNo", json);
		user.setServiceProvider(ServiceProvider.getServiceProvider(spNo));
		user.setScreenName(ParseUtil.getRawString("screenName", json));
		user.setName(ParseUtil.getRawString("name", json));
		user.setGender(Gender.getGender(ParseUtil.getInt("genderNo", json)));
		user.setLocation(ParseUtil.getRawString("location", json));
		user.setDescription(ParseUtil.getRawString("description", json));
		user.setVerified(ParseUtil.getBoolean("isVerified", json));
		user.setProfileImageUrl(ParseUtil.getRawString("profileImageUrl", json));
		user.setCreatedAt(ParseUtil.getDate("createdAt", json));
		
		return user;
	}
}
