package net.dev123.sns.facebook;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookPageAdapter {

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

		Page page = new Page();
		page.setId(ParseUtil.getRawString("id", json));
		page.setName(ParseUtil.getRawString("name", json));
		page.setPicture(String.format(Facebook.PICTURE_URL_FORMAT, page.getId()));
		page.setDescription(ParseUtil.getRawString("company_overview", json));
		page.setCategory(ParseUtil.getRawString("category", json));
		page.setFollowersCount(ParseUtil.getLong("likes", json));
		page.setWebsite(ParseUtil.getRawString("website", json));
		return page;
	}

}
