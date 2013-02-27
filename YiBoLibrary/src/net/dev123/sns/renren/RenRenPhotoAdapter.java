package net.dev123.sns.renren;

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

public class RenRenPhotoAdapter {

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
		Photo photo = new Photo();
		photo.setId(ParseUtil.getRawString("pid", json));
		photo.setAlbumId(ParseUtil.getRawString("aid", json));
		photo.setCaption(ParseUtil.getRawString("caption", json));
		photo.setCommentsCount(ParseUtil.getLong("comment_count", json));
		photo.setThumbnailPicture(ParseUtil.getRawString("url_tiny", json));
		photo.setMiddlePicture(ParseUtil.getRawString("url_head", json));
		photo.setOriginalPicture(ParseUtil.getRawString("url_large", json));
		photo.setServiceProvider(ServiceProvider.RenRen);
		return photo;
	}

}
