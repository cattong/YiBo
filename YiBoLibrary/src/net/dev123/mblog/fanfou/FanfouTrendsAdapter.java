package net.dev123.mblog.fanfou;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
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

/**
 * @author Weiping Ye
 * @version 创建时间：2011-8-1 上午11:33:45
 **/
public class FanfouTrendsAdapter {

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
			JSONArray trendsJsonArray = json.getJSONArray("trends");

			Trend[] trendArray = new Trend[trendsJsonArray.length()];
			for(int i = 0; i < trendsJsonArray.length(); i++) {
				Trend trend = createTrend(trendsJsonArray.getJSONObject(i));
				trendArray[i] = trend;
			}

			Trends trends = new Trends();
			trends.setTrends(trendArray);
			trends.setAsOf(asOf);

			trendsList = new ArrayList<Trends>();
			trendsList.add(trends);
			Collections.sort(trendsList);

			return trendsList;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR, e);
		}
	}

	private static Date parseTrendsDate(String asOfStr) throws ParseException {
		Date parsed = ParseUtil.getDate(asOfStr, "EEE MMM dd HH:mm:ss Z yyyy");
		return parsed;
	}
}
