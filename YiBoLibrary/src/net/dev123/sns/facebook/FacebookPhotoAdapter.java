package net.dev123.sns.facebook;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Photo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookPhotoAdapter {

	public static Photo createPhoto(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createPhoto(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<Photo> createPhotoList(String jsonString)
			throws LibException {
		try {
			if (StringUtil.isEquals("{}", jsonString)
					|| StringUtil.isEquals("[]", jsonString)) {
				return new ArrayList<Photo>(0);
			}
			JSONArray jsonArray = new JSONArray(jsonString);
			int length = jsonArray.length();
			List<Photo> photos = new ArrayList<Photo>(length);
			for (int i = 0; i < length; i++) {
				photos.add(createPhoto(jsonArray.getJSONObject(i)));
			}
			return photos;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static Photo createPhoto(JSONObject json) throws LibException {
		if (json == null) {
			return null;
		}
		try {
			Photo photo = new Photo();
			photo.setId(ParseUtil.getRawString("id", json));
			photo.setAlbumId(ParseUtil.getRawString("aid", json));
			photo.setCaption(ParseUtil.getRawString("name", json));
			if (json.has("comments")) {
				photo.setCommentsCount(json.getJSONObject("comments").getJSONArray("data").length());
			}
			photo.setThumbnailPicture(ParseUtil.getRawString("picture", json));
			photo.setOriginalPicture(ParseUtil.getRawString("source", json));
			photo.setMiddlePicture(ParseUtil.getRawString("source", json));
			if (json.has("images")) {
				JSONArray imagesArray = json.getJSONArray("images");
				photo.setMiddlePicture(ParseUtil.getRawString("source", imagesArray.getJSONObject(1)));
			}
			photo.setServiceProvider(ServiceProvider.Facebook);
			return photo;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}

	}

}
