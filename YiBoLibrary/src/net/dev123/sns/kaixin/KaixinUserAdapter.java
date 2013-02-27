package net.dev123.sns.kaixin;

import static net.dev123.commons.util.ParseUtil.getRawString;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.entity.Education;
import net.dev123.entity.Gender;
import net.dev123.entity.Location;
import net.dev123.entity.Work;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KaixinUserAdapter {

	public static User createSimpleUser(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createSimpleUser(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static User createUser(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createUser(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	/**
	 * 从JSON字符串创建User对象列表
	 *
	 * @param jsonString
	 *            JSON字符串
	 * @return User对象列表
	 * @throws LibException
	 */
	public static List<User> createSimpleUserList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<User>(0);
			}
			JSONArray jsonArray = new JSONArray(jsonString);
			int size = jsonArray.length();
			List<User> userList = new ArrayList<User>(size);
			for (int i = 0; i < size; i++) {
				userList.add(createSimpleUser(jsonArray.getJSONObject(i)));
			}
			return userList;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<User> createUserList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<User>(0);
			}
			JSONArray jsonArray = new JSONArray(jsonString);
			int size = jsonArray.length();
			List<User> userList = new ArrayList<User>(size);
			for (int i = 0; i < size; i++) {
				userList.add(createUser(jsonArray.getJSONObject(i)));
			}
			return userList;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<Long> createUserIdList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<Long>(0);
			}
			JSONArray jsonArray = new JSONArray(jsonString);
			int size = jsonArray.length();
			List<Long> listUserId = new ArrayList<Long>(size);
			for (int i = 0; i < size; i++) {
				listUserId.add(jsonArray.getLong(i));
			}
			return listUserId;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static String createUserId(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return null;
			}
			JSONObject json = new JSONObject(jsonString);
			String userId = json.getString("uid");
			return userId;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	/**
	 * 从JSON对象创建User对象，包级别访问权限控制
	 *
	 * @param json
	 *            JSON对象
	 * @return User对象
	 * @throws LibException
	 */
	static User createSimpleUser(JSONObject json) throws LibException {
		try {
			User user = new User();
			user.setId(json.getString("uid"));
			user.setScreenName(getRawString("name", json));
			user.setName(user.getScreenName());

			user.setHeadUrl(getRawString("logo50", json));

			if (!json.isNull("gender")) {
				int gender = json.getInt("gender");
				if (gender == 0) {
					user.setGender(Gender.MALE);
				} else if (gender == 1) {
					user.setGender(Gender.FEMALE);
				} else {
					user.setGender(Gender.UNKNOW);
				}
			}
			user.setServiceProvider(ServiceProvider.KaiXin);
			return user;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	static User createUser(JSONObject json) throws LibException {
		try {
			User user = new User();
			user.setServiceProvider(ServiceProvider.KaiXin);
			user.setId(json.getString("uid"));
			user.setScreenName(getRawString("name", json));
			user.setName(user.getScreenName());

			user.setTinyUrl(getRawString("logo50", json));
			user.setHeadUrl(getRawString("logo50", json));
			user.setMainUrl(getRawString("logo120", json));
			if (!json.isNull("gender")) {
				int gender = json.getInt("gender");
				if (gender == 0) {
					user.setGender(Gender.MALE);
				} else if (gender == 1) {
					user.setGender(Gender.FEMALE);
				} else {
					user.setGender(Gender.UNKNOW);
				}
			}

			if (!json.isNull("isStar")) {
				int star = json.getInt("isStar");
				user.setStar(false);
				if (star == 1) {
					user.setStar(true);
				}
			}

			if (!json.isNull("birthday")) {
				String birthdayStr = json.getString("birthday");
				if (StringUtil.isNotEmpty(birthdayStr)) {
				    birthdayStr = birthdayStr.replaceFirst("17", "19");
				    Date birthday = ParseUtil.getDate(birthdayStr, "yyyy-MM-dd");
				    user.setBirthday(birthday);
				}
			}

			if (!json.isNull("hometown_location")) {
				Location hometown = new Location();
				hometown.setCountry(getRawString("country", json));
				hometown.setProvince(getRawString("province", json));
				hometown.setCity(getRawString("city", json));
				user.setHometown(hometown.getFormatedAddress());
			}

			if (!json.isNull("career")) {
				List<Work> workHistory = new ArrayList<Work>();
				JSONArray jsonArray = json.getJSONArray("career");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject workJson = jsonArray.getJSONObject(i);
					Work work = new Work();
					work.setEmployer(ParseUtil.getRawString("company", workJson));
					work.setStartDate(ParseUtil.getDate("start_date", workJson, "yyyy-MM-dd"));
					work.setEndDate(ParseUtil.getDate("end_date", workJson, "yyyy-MM-dd"));
					workHistory.add(work);
				}
				user.setWorkHistory(workHistory);
			}

			List<Education> educationHistory = new ArrayList<Education>();

			if (!json.isNull("education")) {
				JSONArray jsonArray = json.getJSONArray("education");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject schoolJson = jsonArray.getJSONObject(i);
					Education education = new Education();
					education.setSchool(ParseUtil.getRawString("school", schoolJson));
					education.setYear(ParseUtil.getRawString("year", schoolJson));
					educationHistory.add(education);
				}
				user.setEducationHistory(educationHistory);
			}
			return user;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR, e);
		}
	}
}
