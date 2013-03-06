package com.cattong.weibo.impl.netease;

import java.util.HashMap;
import java.util.regex.Pattern;

import com.cattong.weibo.Emotions;


public class NetEaseEmotions extends Emotions{

	private static final Pattern SPECIALIZED_PATTERN = Pattern.compile("\\[(\\w|\\p{InCJKUnifiedIdeographs}){1,4}\\]");

	private static NetEaseEmotions emontion;

	private HashMap<String, String> specializedToNormalizedMap;

	private HashMap<String, String> normalizedToSpecializedMap;

	private NetEaseEmotions() {
		specializedToNormalizedMap = new HashMap<String, String>();
		normalizedToSpecializedMap = new HashMap<String, String>();
	}

	public void loadEmontion(String normalized, String specialized) {
		if (!normalizedToSpecializedMap.containsKey(normalized)) {
			normalizedToSpecializedMap.put(normalized, specialized);
		}

		specializedToNormalizedMap.put(specialized, normalized);
	}

	public static synchronized NetEaseEmotions getSingleton() {
		if (emontion == null) {
			emontion = new NetEaseEmotions();

		}

		return emontion;
	}

	@Override
	public Pattern getSpecializedPattern() {
		return SPECIALIZED_PATTERN;
	}

	@Override
	public String getNormalizedEmotion(String specializedEmotion) {
		return specializedToNormalizedMap.get(specializedEmotion);
	}

	@Override
	public String getSpecializedEmotion(String normalizedEmotion) {
		return normalizedToSpecializedMap.get(normalizedEmotion);
	}

}
