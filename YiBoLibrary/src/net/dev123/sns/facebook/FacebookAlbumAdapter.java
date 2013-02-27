package net.dev123.sns.facebook;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Album;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookAlbumAdapter {

	public static Album createAlbum(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createAlbum(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<Album> createAlbumList(String jsonString)
			throws LibException {
		try {
			if (StringUtil.isEquals("{}", jsonString)
					|| StringUtil.isEquals("[]", jsonString)) {
				return new ArrayList<Album>(0);
			}
			JSONArray jsonArray = new JSONArray(jsonString);
			int length = jsonArray.length();
			List<Album> albums = new ArrayList<Album>(length);
			for (int i = 0; i < length; i++) {
				albums.add(createAlbum(jsonArray.getJSONObject(i)));
			}
			return albums;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static Album createAlbum(JSONObject json) throws LibException {
		if (json == null) {
			return null;
		}
		try {
			Album album = new Album();
			album.setId(ParseUtil.getRawString("id", json));
			album.setName(ParseUtil.getRawString("name", json));
			album.setDescription(ParseUtil.getRawString("description", json));
			album.setCoverPicture(ParseUtil.getRawString("cover_photo", json));
			album.setCreatedTime(ParseUtil.getDate("created_time", json, Facebook.DATE_FORMAT));
			album.setUpdatedTime(ParseUtil.getDate("updated_time", json, Facebook.DATE_FORMAT));
			album.setPhotosCount(ParseUtil.getLong("count", json));
			album.setLocation(ParseUtil.getRawString("location", json));
			album.setServiceProvider(ServiceProvider.Facebook);
			return album;
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR);
		}

	}

}
