package net.dev123.sns.renren;

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

public class RenRenAlbumAdapter {

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
			album.setId(ParseUtil.getRawString("aid", json));
			album.setName(ParseUtil.getRawString("name", json));
			album.setCreatedTime(ParseUtil.getDate("create_time", json, "yyyy-MM-dd hh:mm:ss"));
			album.setUpdatedTime(ParseUtil.getDate("update_time", json, "yyyy-MM-dd hh:mm:ss"));
			album.setCoverPicture(ParseUtil.getRawString("url", json));
			album.setDescription(ParseUtil.getRawString("description", json));
			album.setPhotosCount(ParseUtil.getLong("size", json));
			album.setLocation(ParseUtil.getRawString("location", json));
			album.setServiceProvider(ServiceProvider.RenRen);
			return album;
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR);
		}

	}

}
