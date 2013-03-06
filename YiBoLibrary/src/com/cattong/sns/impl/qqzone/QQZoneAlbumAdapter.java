package com.cattong.sns.impl.qqzone;

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
import com.cattong.sns.entity.Album;

public class QQZoneAlbumAdapter {

	public static Album createAlbum(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createAlbum(json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<Album> createAlbumList(String jsonString)
			throws LibException {
		try {
			if (StringUtil.isEquals("{}", jsonString)
					|| StringUtil.isEquals("[]", jsonString)) {
				return new ArrayList<Album>(0);
			}
			JSONObject json = new JSONObject(jsonString);
			JSONArray jsonArray = json.getJSONArray("album");
			int length = jsonArray.length();
			List<Album> albums = new ArrayList<Album>(length);
			for (int i = 0; i < length; i++) {
				albums.add(createAlbum(jsonArray.getJSONObject(i)));
			}
			return albums;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	public static Album createAlbum(JSONObject json) throws LibException {
		if (json == null) {
			return null;
		}
		Album album = new Album();
		album.setId(ParseUtil.getRawString("albumid", json));
		album.setName(ParseUtil.getRawString("name", json));
		long timestamp = ParseUtil.getLong("createtime", json);
		album.setCreatedTime(new Date(timestamp * 1000));
		album.setDescription(ParseUtil.getRawString("desc", json));
		album.setPhotosCount(ParseUtil.getLong("picnum", json));
		album.setServiceProvider(ServiceProvider.QQZone);
		return album;
	}

}
