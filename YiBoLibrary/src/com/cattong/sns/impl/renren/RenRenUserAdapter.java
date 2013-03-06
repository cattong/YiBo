package com.cattong.sns.impl.renren;

import static com.cattong.commons.util.ParseUtil.getRawString;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ParseUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Education;
import com.cattong.entity.Gender;
import com.cattong.entity.Location;
import com.cattong.entity.Work;
import com.cattong.entity.Education.SchoolType;
import com.cattong.sns.entity.User;

public class RenRenUserAdapter {

	public static User createSimpleUser(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createSimpleUser(json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	public static User createUser(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createUser(json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
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
	public static List<User> createSimpleUserList(String jsonString)
			throws LibException {
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
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<User> createUserList(String jsonString)
			throws LibException {
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
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<String> createUserIdList(String jsonString)
			throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<String>(0);
			}
			JSONArray jsonArray = new JSONArray(jsonString);
			int size = jsonArray.length();
			List<String> listUserId = new ArrayList<String>(size);
			for (int i = 0; i < size; i++) {
				listUserId.add(jsonArray.getString(i));
			}
			return listUserId;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
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
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
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
		User user = new User();
		user.setUserId(ParseUtil.getRawString("uid", json));
		user.setScreenName(ParseUtil.getRawString("name", json));
		user.setName(user.getScreenName());
		user.setHeadUrl(ParseUtil.getRawString("headurl", json));
		user.setServiceProvider(ServiceProvider.RenRen);
		return user;
	}

	static User createUser(JSONObject json) throws LibException {
		try {
			User user = new User();
			user.setServiceProvider(ServiceProvider.RenRen);
			user.setUserId(json.getString("uid"));
			user.setScreenName(getRawString("name", json));
			user.setName(user.getScreenName());

			user.setTinyUrl(getRawString("tinyurl", json));
			user.setHeadUrl(getRawString("headurl", json));
			user.setMainUrl(getRawString("mainurl", json));
			if (!json.isNull("sex")) {
				int gender = json.getInt("sex");
				if (gender == 1) {
					user.setGender(Gender.Male);
				} else if (gender == 0) {
					user.setGender(Gender.Female);
				} else {
					user.setGender(Gender.Unkown);
				}
			}

			if (!json.isNull("star")) {
				int star = json.getInt("star");
				user.setStar(false);
				if (star == 1) {
					user.setStar(true);
				}
			}
			if (!json.isNull("zidou")) {
				int zidou = json.getInt("zidou");
				user.setVip(false);
				if (zidou == 1) {
					user.setVip(true);
				}
			}
			if (!json.isNull("vip")) {
				user.setVipLevel(json.getInt("vip"));
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

			if (!json.isNull("work_history")) {
				List<Work> workHistory = new ArrayList<Work>();
				JSONArray jsonArray = json.getJSONArray("work_history");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject workJson = jsonArray.getJSONObject(i);
					Work work = new Work();
					work.setEmployer(ParseUtil.getRawString("company_name",workJson));
					work.setStartDate(ParseUtil.getDate("start_date", workJson,	"yyyy-MM-dd"));
					work.setEndDate(ParseUtil.getDate("end_date", workJson, "yyyy-MM-dd"));
					workHistory.add(work);
				}
				user.setWorkHistory(workHistory);
			}

			List<Education> educationHistory = new ArrayList<Education>();
			if (!json.isNull("university_history")) {
				JSONArray jsonArray = json.getJSONArray("university_history");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject educationJson = jsonArray.getJSONObject(i);
					Education education = new Education();
					education.setSchool(
						ParseUtil.getRawString("name", educationJson));
					education.setDepartment(
						ParseUtil.getRawString("department", educationJson));
					education.setYear(
						ParseUtil.getRawString("year", educationJson));
					education.setSchoolType(SchoolType.COLLEGE);
					educationHistory.add(education);
				}
			}
			if (!json.isNull("hs_history")) {
				JSONArray jsonArray = json.getJSONArray("hs_history");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject educationJson = jsonArray.getJSONObject(i);
					Education education = new Education();
					education.setSchool(ParseUtil.getRawString("name", educationJson));
					education.setYear(ParseUtil.getRawString("grad_year", educationJson));
					education.setSchoolType(SchoolType.HIGH_SCHOOL);
					educationHistory.add(education);
				}
			}
			user.setEducationHistory(educationHistory);

			return user;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR, e);
		}
	}
}
