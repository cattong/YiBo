package com.cattong.weibo.impl.sina;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.ParseUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.GeoLocation;
import com.cattong.entity.Location;

/**
 * @author cattong.com
 * @version 
 **/
class SinaGeoAdaptor {

	public static Location createLocation(String jsonStr)
			throws LibException {
		Location location = null;
		try {
			if ("[]".equals(jsonStr) || "{}".equals(jsonStr)) {
				return null;
			}
			
			GeoLocation geoLocation = createGeoLocation(jsonStr);
			if (geoLocation != null 
				&& ListUtil.isNotEmpty(geoLocation.getLocationList())) {
				location = geoLocation.getLocationList().get(0);
			} else {
				throw new JSONException("");
			}
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
		
		return location;
	}
	
	public static GeoLocation createGeoLocation(String jsonStr) throws LibException {
		GeoLocation geoLocation = null;
		
		try {
			if ("[]".equals(jsonStr) || "{}".equals(jsonStr)) {
				return geoLocation;
			}
			
			geoLocation = new GeoLocation();
			
			JSONObject jsonObj = new JSONObject(jsonStr);
			if (!jsonObj.isNull("geos")) {
				
				JSONArray jsonList = jsonObj.getJSONArray("geos");
				int size = jsonList.length();
				List<Location> locationList = new ArrayList<Location>(size);
				for (int i = 0; i < size; i++) {
					locationList.add(createLocation(jsonList.getJSONObject(i)));
				}
				
				geoLocation.setLocationList(locationList);
			}
		
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
		
		return geoLocation;
	}
	
	public static Location createLocation(JSONObject json)
		throws LibException {
		Location location = null;
		
		try {
			location = new Location();
			location.setLatitude(ParseUtil.getDouble("latitude", json));
			location.setLongitude(ParseUtil.getDouble("longitude", json));
			location.setProvince(ParseUtil.getRawString("province_name", json));
			location.setCity(ParseUtil.getRawString("city_name", json));
			location.setDistrict(ParseUtil.getRawString("district_name", json));
			location.setLandmake(ParseUtil.getRawString("name", json));
			if (!json.isNull("address")) {
				String address = json.getString("address");
				address = address.replace(location.getProvince(), "");
				address = address.replace(location.getCity(), "");
				if (StringUtil.isNotEmpty(location.getDistrict())) {
					address = address.replace(location.getDistrict(), "");	
				}
				if (StringUtil.isNotEmpty(location.getLandmark())) {
				    address = address.replace(location.getLandmark(), "");
				}
				location.setStreet(address);
			}
		
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
		return location;
	}
}
