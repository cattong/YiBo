package com.cattong.sns.impl.renren;

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

public class RenRenNoteAdapter {

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
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray("blogs");
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

	public static Note createNote(JSONObject json) throws LibException {
		if (json == null) {
			return null;
		}
		try {
			Note note = new Note();
			note.setId(ParseUtil.getRawString("id", json));
			note.setSubject(ParseUtil.getRawString("title", json));
			note.setContent(ParseUtil.getRawString("content", json));
			note.setCommentsCount(ParseUtil.getLong("comment_count", json));
			note.setCreatedTime(ParseUtil.getDate("time", json, "yyyy-MM-dd hh:mm:ss"));
			note.setUpdatedTime(note.getCreatedTime());
			note.setServiceProvider(ServiceProvider.RenRen);
			return note;
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR, e);
		}
	}

}
