package net.dev123.mblog.sina;

import net.dev123.commons.util.ParseUtil;
import net.dev123.entity.GeoLocation;
import net.dev123.entity.Location;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-8-19 下午1:51:09
 **/
public class SinaGeoAdaptor {

	public static Location createLocationFromJson(String jsonStr)
			throws LibException {
		Location location = null;
		try {
			if ("[]".equals(jsonStr) || "{}".equals(jsonStr)) {
				return null;
			}
			JSONObject jsonObj = new JSONObject(jsonStr);
			if (!jsonObj.isNull("address")) {
				JSONObject jsonAddr = jsonObj.getJSONObject("address");
				location = new Location();
				location.setProvince(ParseUtil.getRawString("prov_name", jsonAddr));
				location.setCity(ParseUtil.getRawString("city_name", jsonAddr));
				location.setDistrict(ParseUtil.getRawString("district_name", jsonAddr));
				if (!jsonAddr.isNull("street")) {
					location.setStreet(ParseUtil.getRawString("street", jsonAddr));
				}
			}

		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
		return location;
	}
}
