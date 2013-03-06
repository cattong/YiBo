package com.cattong.sns.impl.qqzone;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cattong.commons.util.StringUtil;
import com.cattong.weibo.Emotions;


public class QQZoneEmotions extends Emotions {

	private static final Pattern SPECIALIZED_PATTERN = Pattern.compile("\\/(\\w|\\p{InCJKUnifiedIdeographs}){1,3}");

	private static QQZoneEmotions emontion;

	private HashMap<String, String> specializedToNormalizedMap;

	private HashMap<String, String> normalizedToSpecializedMap;

	private QQZoneEmotions() {
		specializedToNormalizedMap = new HashMap<String, String>();
		normalizedToSpecializedMap = new HashMap<String, String>();
	}

	public void loadEmontion(String normalized, String specialized) {
		if (!normalizedToSpecializedMap.containsKey(normalized)) {
			normalizedToSpecializedMap.put(normalized, specialized);
		}

		specializedToNormalizedMap.put(specialized, normalized);
	}

	public static synchronized QQZoneEmotions getSingleton() {
		if (emontion == null) {
			emontion = new QQZoneEmotions();

		}

		return emontion;
	}

	@Override
	public Pattern getSpecializedPattern() {
		return SPECIALIZED_PATTERN;
	}

	@Override
	public String getNormalizedEmotion(String specializedEmotion) {
		return specializedToNormalizedMap.get(fan2Jian(specializedEmotion));
	}

	@Override
	public String getSpecializedEmotion(String normalizedEmotion) {
		return normalizedToSpecializedMap.get(normalizedEmotion);
	}

	@Override
	public String normalize(String specialized) {
		if (StringUtil.isEmpty(specialized)) {
			return specialized;
		}

		Matcher matcher = getSpecializedPattern().matcher(specialized);
		StringBuffer normalized = new StringBuffer();
		while (matcher.find()) {
			String matchedStr = matcher.group();
			int i = matchedStr.length();
			while (i > 0) {
				String normalizedEmontion = getNormalizedEmotion(matchedStr.substring(0, i));
				if (normalizedEmontion != null) {
					matcher.appendReplacement(normalized, normalizedEmontion + matchedStr.substring(i));
					break;
				}
				i--;
			}
		}
		matcher.appendTail(normalized);
		return normalized.toString();
	}

}
