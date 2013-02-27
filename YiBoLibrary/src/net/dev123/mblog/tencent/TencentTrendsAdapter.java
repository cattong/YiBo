package net.dev123.mblog.tencent;

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

public class TencentTrendsAdapter {

	static Trend createTrend(JSONObject json) throws LibException {
		Trend trend = new Trend();
		trend.setName(ParseUtil.getRawString("name", json));
		trend.setQuery(ParseUtil.getRawString("keywords", json));
		return trend;
	}

	public static List<Trends> createTrends(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			if (!json.has("info")) {
				return new ArrayList<Trends>(0);
			}

			List<Trends> trendsList = new ArrayList<Trends>(1);
			JSONArray jsonArray = json.getJSONArray("info");
			Date asOf = new Date();
			Date trendAt = asOf;
			Trend[] trendArray = new Trend[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonTrend = jsonArray.getJSONObject(i);
				trendArray[i] = createTrend(jsonTrend);
			}

			Trends trends = new Trends();
			trends.setAsOf(asOf);
			trends.setTrendAt(trendAt);
			trends.setTrends(trendArray);

			trendsList.add(trends);

			return trendsList;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}
}
