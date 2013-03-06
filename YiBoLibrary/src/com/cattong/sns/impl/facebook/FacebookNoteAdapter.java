package com.cattong.sns.impl.facebook;

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
import com.cattong.sns.entity.Note;

public class FacebookNoteAdapter {

	public static Note createNote(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createNote(json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<Note> createNoteList(String jsonString)
			throws LibException {
		try {
			if (StringUtil.isEquals("{}", jsonString)
				|| StringUtil.isEquals("[]", jsonString)) {
				return new ArrayList<Note>(0);
			}
			JSONArray jsonArray = new JSONArray(jsonString);
			int length = jsonArray.length();
			List<Note> notes = new ArrayList<Note>(length);
			for (int i = 0; i < length; i++) {
				notes.add(createNote(jsonArray.getJSONObject(i)));
			}
			return notes;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	static Note createNote(JSONObject json) throws LibException {
		if (json == null) {
			return null;
		}
		try {
			Note note = new Note();
			note.setId(ParseUtil.getRawString("id", json));
			note.setSubject(ParseUtil.getRawString("subject", json));
			note.setContent(ParseUtil.getRawString("message", json));
			note.setCreatedTime(ParseUtil.getDate("created_time", json, Facebook.DATE_FORMAT));
			note.setUpdatedTime(ParseUtil.getDate("updated_time", json, Facebook.DATE_FORMAT));
			if (json.has("comments")) {
				note.setCommentsCount(json.getJSONObject("comments").getJSONArray("data").length());
			}
			note.setServiceProvider(ServiceProvider.Facebook);
			return note;
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR, e);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

}
