package com.cattong.socialcat.converter;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.util.ParseUtil;
import com.cattong.entity.PointsOrderInfo;

public class PointsOrderInfoJSONConverter {
	
	public static PointsOrderInfo createPointsOrder(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createPointsOrder(json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}
	
	static PointsOrderInfo createPointsOrder(JSONObject json) throws LibException {
	    try {
	    	PointsOrderInfo orderInfo = new PointsOrderInfo();
	    	
	    	orderInfo.setOrderId(json.getLong("orderId"));
	    	orderInfo.setOrderType(json.getInt("orderType"));
	    	orderInfo.setThirdpartyOrderId(json.getString("thirdpartyOrderId"));
	    	orderInfo.setAmount(json.getInt("amount"));
	    	orderInfo.setPoints(json.getInt("points"));
            orderInfo.setState(json.getInt("state"));
            orderInfo.setCreatedAt(ParseUtil.getDate("createdAt", json));
			return orderInfo;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR);
		}
	}
}
