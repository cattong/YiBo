package net.dev123.sns.renren;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RenRenPageAdapter {

	public static Page createPage(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createPage(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<Page> createPageList(String jsonString)
			throws LibException {
		try {
			if (StringUtil.isEquals("{}", jsonString)
					|| StringUtil.isEquals("[]", jsonString)) {
				return new ArrayList<Page>(0);
			}
			JSONArray jsonArray = new JSONArray(jsonString);
			int length = jsonArray.length();
			List<Page> pages = new ArrayList<Page>(length);
			for (int i = 0; i < length; i++) {
				pages.add(createPage(jsonArray.getJSONObject(i)));
			}
			return pages;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	static Page createPage(JSONObject json) throws LibException {
		if (json == null) {
			return null;
		}
		try {
			Page page = new Page();
			page.setId(ParseUtil.getRawString("page_id", json));
			page.setName(ParseUtil.getRawString("name", json));
			page.setPicture(ParseUtil.getRawString("headurl", json));
			page.setDescription(ParseUtil.getRawString("desc", json));
			page.setCategory(ParseUtil.getRawString("category", json));
			page.setFollowersCount(ParseUtil.getLong("fans_count", json));

			if (json.has("base_info")) {
				Map<String, String> propMap = getKeyValueMap(json.getJSONArray("base_info"));
				page.setWebsite(propMap.get("官方主页"));
			}

			if (json.has("contact_info")) {

			}

			if (json.has("detail_info")) {

			}
			page.setServiceProvider(ServiceProvider.RenRen);
			return page;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}

	}

	private static Map<String, String> getKeyValueMap(JSONArray propArray)
			throws JSONException {
		Map<String, String> propMap = new HashMap<String, String>();
		int length = propArray.length();
		if (length > 0) {
			JSONObject propJson = null;
			String propKey = null;
			String propValue = null;
			for (int i = 0; i < length; i++) {
				propJson = propArray.getJSONObject(i);
				propKey = ParseUtil.getRawString("key", propJson);
				propValue = ParseUtil.getRawString("value", propJson);
				propMap.put(propKey, propValue);
			}
		}
		return propMap;
	}

}
