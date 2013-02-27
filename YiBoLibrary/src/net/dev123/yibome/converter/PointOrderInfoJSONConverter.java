package net.dev123.yibome.converter;

import java.text.ParseException;

import net.dev123.commons.util.ParseUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.yibome.entity.PointOrderInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class PointOrderInfoJSONConverter {
	
	public static PointOrderInfo createPointOrder(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createPointOrder(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}
	
	static PointOrderInfo createPointOrder(JSONObject json) throws LibException {
	    try {
	    	PointOrderInfo orderInfo = new PointOrderInfo();
	    	
	    	orderInfo.setOrderId(json.getLong("order_id"));
	    	orderInfo.setOrderType(json.getInt("order_type"));
	    	orderInfo.setThirdpartyOrderId(json.getString("thirdparty_order_id"));
	    	orderInfo.setCount(json.getInt("count"));
	    	orderInfo.setPoints(json.getInt("points"));
            orderInfo.setState(json.getInt("state"));
            orderInfo.setCreatedAt(ParseUtil.getDate("created_at", json));
			return orderInfo;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR);
		}
	}
}
