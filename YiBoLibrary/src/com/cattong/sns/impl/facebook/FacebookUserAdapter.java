package com.cattong.sns.impl.facebook;

import static com.cattong.commons.util.ParseUtil.getRawString;

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
import com.cattong.entity.Education;
import com.cattong.entity.Gender;
import com.cattong.entity.Work;
import com.cattong.entity.Education.SchoolType;
import com.cattong.sns.entity.User;

public class FacebookUserAdapter {

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
				listUserId.add(ParseUtil.getRawString("id",	jsonArray.getJSONObject(i)));
			}
			return listUserId;
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
		user.setUserId(ParseUtil.getRawString("id", json));
		user.setScreenName(ParseUtil.getRawString("name", json));
		user.setName(user.getScreenName());
		user.setHeadUrl(String.format(Facebook.PICTURE_URL_FORMAT, user.getUserId()));
		user.setServiceProvider(ServiceProvider.Facebook);
		return user;
	}

	static User createUser(JSONObject json) throws LibException {
		try {
			User user = new User();
			user.setServiceProvider(ServiceProvider.Facebook);
			user.setUserId(json.getString("id"));
			user.setScreenName(getRawString("name", json));
			user.setName(getRawString("username", json));
			user.setHeadUrl(String.format(Facebook.PICTURE_URL_FORMAT, user.getUserId()));
			user.setBirthday(ParseUtil.getDate("birthday", json , "MM/dd/yyyy"));
			user.setDescription(getRawString("bio", json));
			try {
				user.setGender(Gender.valueOf(getRawString("gender", json)));
			} catch (Exception e) {
				user.setGender(Gender.Unkown);
			}

			if (json.has("hometown")) {
				user.setHometown(getRawString("name", json.getJSONObject("hometown")));
			}
			if (json.has("location")) {
				user.setLocation(getRawString("name", json.getJSONObject("location")));
			}

			if (json.has("work")) {
				List<Work> workHistory = new ArrayList<Work>();
				JSONArray jsonArray = json.getJSONArray("work");
				JSONObject workJson = null;
				Work work = null;
				for (int i = 0; i < jsonArray.length(); i++) {
					workJson = jsonArray.getJSONObject(i);
					work = new Work();
					work.setEmployer(getRawString("name",	workJson.getJSONObject("employer")));
					work.setStartDate(ParseUtil.getDate("start_date", workJson, "yyyy-MM"));
					work.setStartDate(ParseUtil.getDate("end_date", workJson, "yyyy-MM"));
					if (workJson.has("location")) {
						work.setLocation(getRawString("name", workJson.getJSONObject("location")));
					}
					if (workJson.has("position")) {
						work.setPosition(getRawString("name", workJson.getJSONObject("position")));
					}
					if (workJson.has("projects")) {
						JSONArray projectsArray = workJson.getJSONArray("projects");
						int length = projectsArray.length();
						Work.Project[] projects = new Work.Project[length];
						Work.Project project = null;
						JSONObject projectJson = null;
						for (int j = 0; j < length; j++) {
							projectJson = projectsArray.getJSONObject(j);
							project = new Work.Project();
							project.setName(getRawString("name", projectJson));
							project.setDescription(getRawString("description", projectJson));
							project.setStartDate(ParseUtil.getDate("start_date", projectJson, "yyyy-MM"));
							project.setEndDate(ParseUtil.getDate("end_date", projectJson, "yyyy-MM"));
							project.setServiceProvider(ServiceProvider.Facebook);
							projects[j] = project;
						}
						work.setProjects(projects);
					}
					workHistory.add(work);
				}
				user.setWorkHistory(workHistory);
			}

			List<Education> educationHistory = new ArrayList<Education>();
			if (json.has("education")) {
				JSONArray jsonArray = json.getJSONArray("education");
				JSONObject educationJson = null;
				Education education = null;
				for (int i = 0; i < jsonArray.length(); i++) {
					educationJson = jsonArray.getJSONObject(i);
					education = new Education();
					education.setSchool(ParseUtil.getRawString("name", educationJson.getJSONObject("school")));
					String type = getRawString("type", educationJson);
					if ("College".equals(type)) {
						education.setSchoolType(SchoolType.COLLEGE);
					} else if ("Graduate School".equals(type)) {
						education.setSchoolType(SchoolType.GRADUATE_SCHOOL);
					} else if ("High School".equals(type)) {
						education.setSchoolType(SchoolType.HIGH_SCHOOL);
					}
					if (educationJson.has("year")) {
						education.setYear(getRawString("year", educationJson.getJSONObject("year")));
					}
					education.setDepartment(getRawString("department", educationJson));
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
