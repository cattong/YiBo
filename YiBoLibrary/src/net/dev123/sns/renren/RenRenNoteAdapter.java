package net.dev123.sns.renren;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Note;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RenRenNoteAdapter {

	public static Note createNote(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createNote(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
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
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR, e);
		}
	}

}
