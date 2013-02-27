package net.dev123.yibome.converter;

import java.text.DateFormat;
import java.text.ParseException;

import net.dev123.commons.util.DateTimeUtil;
import net.dev123.commons.util.ParseUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.yibome.entity.Passport;
import net.dev123.yibome.entity.PointLevel;

import org.json.JSONException;
import org.json.JSONObject;

public class PassportJSONConverter {

	public static JSONObject toJSON(Passport passport) throws JSONException {
		if (passport == null) {
			return null;
		}
		JSONObject json = new JSONObject();
		json.put("username", passport.getUsername());
		json.put("access_token", passport.getAuthToken());
		json.put("token_secret", passport.getAuthSecret());
		DateFormat dateFormat = DateTimeUtil.getGMTDateFormat();
		json.put("created_at", dateFormat.format(passport.getCreatedAt()));
		json.put("email", passport.getEmail());
		json.put("state", passport.getState());
		json.put("is_vip", passport.isVip());
		
		JSONObject pointLevelJson = new JSONObject();
		PointLevel pointLevel = passport.getPointLevel();
		if (pointLevel != null) {
		    pointLevelJson.put("points", pointLevel.getPoints());
		    pointLevelJson.put("title", pointLevel.getTitle());
		    pointLevelJson.put("pointLevel", pointLevel.getPointLevel());
		}
		json.put("point_level", pointLevelJson);
		
		return json;
	}

	public static Passport toPassport(JSONObject json) throws LibException {
		if (json == null) {
			return null;
		}

		Passport passport = new Passport();
		try {
			passport.setUsername(json.getString("username"));
			passport.setAuthToken(json.getString("access_token"));
			passport.setAuthSecret(json.getString("token_secret"));
			passport.setCreatedAt(ParseUtil.getDate("created_at", json));
			passport.setEmail(json.getString("email"));
			passport.setState(json.getInt("state"));
			passport.setVip(json.getBoolean("is_vip"));
			PointLevel pointLevel = null;
			try {
				String levelJsonString = json.getString("point_level");
			    pointLevel = PointLevelJSONConverter.createPointLevel(levelJsonString);
			} catch (Exception e) {}
			
			passport.setPointLevel(pointLevel);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR);
		}
		
		return passport;
	}
}
