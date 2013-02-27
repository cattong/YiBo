package net.dev123.yibome.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.DateTimeUtil;
import net.dev123.commons.util.ParseUtil;
import net.dev123.entity.BaseUser;
import net.dev123.entity.Gender;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.User;
import net.dev123.yibome.entity.UserExtInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class UserJSONConverter {

	public static User createUser(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return toUser(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR);
		}
	}

	public static UserExtInfo createUserExtInfo(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			UserExtInfo userExtInfo = new UserExtInfo();
			userExtInfo.setUserId(json.getString("id"));
			int spNo = json.getInt("service_provider");
			ServiceProvider sp = ServiceProvider.getServiceProvider(spNo);
			userExtInfo.setServiceProvider(sp);
			userExtInfo.setBirthday(ParseUtil.getDate("birthday", json));
			userExtInfo.setOriginalProfileImageUrl(json.getString("original_profile_image_url"));
			userExtInfo.setVerifyInfo(json.getString("verify_info"));

			return userExtInfo;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR);
		}
	}

	public static JSONObject toJSON(BaseUser user) throws JSONException {
		if (user == null) {
			return null;
		}
		JSONObject json = new JSONObject();
		json.put("id", user.getId());
		json.put("service_provider", user.getServiceProvider().getServiceProviderNo());
		json.put("screen_name", user.getScreenName());
		json.put("name", user.getName());
		int genderNo = user.getGender() == null ?
			Gender.UNKNOW.getGenderNo() : user.getGender().getGenderNo();
		json.put("gender", genderNo);
		json.put("location", user.getLocation());
		json.put("description", user.getDescription());
		json.put("verified", user.isVerified());
		String profileImageUrl = user.getProfileImageUrl() == null ? "" : user.getProfileImageUrl();
		json.put("profile_image_url", profileImageUrl);
		DateFormat dateFormat = DateTimeUtil.getGMTDateFormat();
		String createdAtString = user.getCreatedAt() == null ?
			dateFormat.format(new Date()) : dateFormat.format(user.getCreatedAt());
		json.put("created_at", createdAtString);
		return json;
	}

	public static User toUser(JSONObject json) throws JSONException, ParseException {
		if (json == null) {
			return null;
		}
		User user = new User();
		user.setId(ParseUtil.getRawString("id", json));
		int spNo = ParseUtil.getInt("service_provider", json);
		user.setServiceProvider(ServiceProvider.getServiceProvider(spNo));
		user.setScreenName(ParseUtil.getRawString("screen_name", json));
		user.setName(ParseUtil.getRawString("name", json));
		user.setGender(Gender.getGender(ParseUtil.getInt("gender", json)));
		user.setLocation(ParseUtil.getRawString("location", json));
		user.setDescription(ParseUtil.getRawString("description", json));
		user.setVerified(ParseUtil.getBoolean("verified", json));
		user.setProfileImageUrl(ParseUtil.getRawString("profile_image_url", json));
		user.setCreatedAt(ParseUtil.getDate("created_at", json));
		return user;
	}
}
