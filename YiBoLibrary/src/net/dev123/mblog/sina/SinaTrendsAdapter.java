package net.dev123.mblog.sina;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.dev123.commons.util.ParseUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Trend;
import net.dev123.mblog.entity.Trends;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SinaTrendsAdapter {

	public static Trend createTrend(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createTrend(json);
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

	public static List<Trends> createTrends(String jsonString) throws LibException {
		try {
			List<Trends> trendsList = null;
			JSONObject json = new JSONObject(jsonString);
			Date asOf = parseTrendsDate(json.getString("as_of"));
			JSONObject trendsJson = json.getJSONObject("trends");
			trendsList = new ArrayList<Trends>(trendsJson.length());
			Iterator<?> ite = trendsJson.keys();
			while (ite.hasNext()) {
				String key = (String) ite.next();
				JSONArray jsonArray = trendsJson.getJSONArray(key);
				Trend[] trendArray = new Trend[jsonArray.length()];
				for (int i = 0; i < jsonArray.length(); i++) {
					Trend trend = createTrend(jsonArray.getJSONObject(i));
					trendArray[i] = trend;
				}

				Date trendAt = null;
				if (key.length() == 19) {
					// current trends
					trendAt = ParseUtil.getDate(key, "yyyy-MM-dd HH:mm:ss");
				} else if (key.length() == 16) {
					// daily trends
					trendAt = ParseUtil.getDate(key, "yyyy-MM-dd HH:mm");
				} else if (key.length() == 10) {
					// weekly trends
					trendAt = ParseUtil.getDate(key, "yyyy-MM-dd");
				}

				Trends trends = new Trends();
				trends.setAsOf(asOf);
				trends.setTrendAt(trendAt);
				trends.setTrends(trendArray);

				trendsList.add(trends);
			}
			Collections.sort(trendsList);

			return trendsList;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR, e);
		}
	}

	private static Date parseTrendsDate(String asOfStr) throws ParseException {
		Date parsed;
		switch (asOfStr.length()) {
		case 10:
			parsed = new Date(Long.parseLong(asOfStr) * 1000);
			break;
		case 20:
			parsed = ParseUtil.getDate(asOfStr, "yyyy-mm-dd'T'HH:mm:ss'Z'");
			break;
		default:
			parsed = ParseUtil.getDate(asOfStr, "EEE, d MMM yyyy HH:mm:ss z");
		}
		return parsed;
	}
	
	public static List<Trend> createUserTrends(String jsonStr) throws LibException {
		try {
			JSONArray jsonArray = new JSONArray(jsonStr);
			int size = jsonArray.length();
			List<Trend> trendList = new ArrayList<Trend>();
			Trend trend = null;
			JSONObject trendObj = null;
			for(int i = 0; i < size; i++) {
				trend = new Trend();
				trendObj = jsonArray.getJSONObject(i);
				trend.setId(ParseUtil.getRawString("trend_id", trendObj));
				trend.setName(ParseUtil.getRawString("hotword", trendObj));
				trend.setNum(ParseUtil.getLong("num", trendObj));
				
				trendList.add(trend);
			}
			return trendList;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

}
