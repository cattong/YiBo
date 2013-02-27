package net.dev123.mblog.netease;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.dev123.commons.util.ParseUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Trend;
import net.dev123.mblog.entity.Trends;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NetEaseTrendsAdapter {

	public static Trend createTrend(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createTrend(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<Trends> createTrends(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			List<Trends> trendsList = new ArrayList<Trends>();
			if (!json.isNull("trends")) {
				Trends trends = new Trends();
				JSONArray jsonArray = json.getJSONArray("trends");
				Trend[] trendArray = new Trend[jsonArray.length()];
				for (int i = 0; i < jsonArray.length(); i++) {
					Trend trend = createTrend(jsonArray.getJSONObject(i));
					trendArray[i] = trend;
				}
				trends.setTrends(trendArray);
				trends.setTrendAt(new Date());
				trends.setAsOf(new Date());
				trendsList.add(trends);
			}
			return trendsList;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	static Trend createTrend(JSONObject json) throws LibException {
		Trend trend = new Trend();
		trend.setName(ParseUtil.getRawString("name", json));
		trend.setUrl(ParseUtil.getRawString("url", json));
		trend.setQuery(ParseUtil.getRawString("query", json));
		return trend;
	}

//	private static Date parseTrendsDate(String asOfStr) throws ParseException {
//        Date parsed;
//        switch (asOfStr.length()) {
//            case 10:
//                parsed = new Date(Long.parseLong(asOfStr) * 1000);
//                break;
//            case 20:
//                parsed = ParseUtil.getDate(asOfStr, "yyyy-mm-dd'T'HH:mm:ss'Z'");
//                break;
//            default:
//                parsed = ParseUtil.getDate(asOfStr, "EEE, d MMM yyyy HH:mm:ss z");
//        }
//        return parsed;
//    }


}
