package com.cattong.weibo;

import java.util.regex.Pattern;

import com.cattong.commons.ServiceProvider;


public class FeaturePatternUtils {

	//Tencent 个性域名以字母开头，6～20位字母、数字、下划线或减号
	private static final Pattern MENTION_TENCENT = Pattern.compile("@[a-zA-Z][a-zA-Z0-9_-]{1,19}");
	//Twitter 个性域名只能使用字母、数字或者下划线， 不能超过15个字符
	private static final Pattern MENTION_TWITTER = Pattern.compile("@\\w{1,15}");
	//Sina 昵称只能使用中文、字母、数字、下划线或减号，4~20个字符
	private static final Pattern MENTION_SINA = Pattern.compile("@[\\w\\p{InCJKUnifiedIdeographs}-]{1,20}");
	//Sohu 昵称只能使用中文，字母或数字，2~12个字符
	private static final Pattern MENTION_SOHU = Pattern.compile("@[\\p{Alnum}\\p{InCJKUnifiedIdeographs}]{1,12}");
	//NetEase 昵称只能使用中文，字母或数字，不能超过12个汉字
	private static final Pattern MENTION_NETEASE = Pattern.compile("@[\\p{Alnum}\\p{InCJKUnifiedIdeographs}]{1,24}");
	//Fanfou 昵称只能由汉字、英文字符、数字、下划线或小数点组成，最少 2 个汉字或 4 个字符，最多 6 个汉字或 12 个字符
	private static final Pattern MENTION_FANFOU = Pattern.compile("@[\\p{Alnum}\\p{InCJKUnifiedIdeographs}-.]{1,12}");

	private static final Pattern TOPIC_SINA = Pattern.compile("#[\\p{Print}\\p{InCJKUnifiedIdeographs}&&[^#]]+#");
	private static final Pattern TOPIC_NETEASE = Pattern.compile("#[\\p{InCJKUnifiedIdeographs}\\p{Graph}&&[^#]]+[ #]{0,1}");
	private static final Pattern TOPIC_TWITTER = Pattern.compile("#[\\w\\p{InCJKUnifiedIdeographs}]+");

	private static final String RETWEET_SEPARATOR_SINA = "//";
	private static final String RETWEET_SEPARATOR_TWITTER = "RT";
	private static final String RETWEET_SEPARATOR_TENCENT = "||";
	private static final String RETWEET_SEPARATOR_FANFOU = "转";

	private static final String RETWEET_FORMAT_SINA = " %1$s%2$s: %3$s";
	private static final String RETWEET_FORMAT_TENCENT = " %1$s %2$s: %3$s";
	private static final String RETWEET_FORMAT_FANFOU = " %1$s%2$s %3$s";

	//private static final Pattern URL_SINA = Pattern.compile("http://[t[sinaurl]]\\.cn/[a-zA-Z0-9]+");
	//private static final Pattern URL_TENCENT = Pattern.compile("http://url\\.cn/[a-zA-Z0-9]+");
	//private static final Pattern URL_NETEASE = Pattern.compile("http://163\\.fm/[a-zA-Z0-9]+");
	//private static final Pattern URL_SOHU = Pattern.compile("http://t\\.itc\\.cn/[a-zA-Z0-9]+");
	private static final Pattern URL_TWITTER = Pattern.compile("http://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]");

	/**
	 * 获取提到某人的匹配模式
	 *
	 * @param serviceProvider
	 *            ServiceProvider
	 * @return 提到某人的匹配模式
	 */
	public static Pattern getMentionPattern(ServiceProvider serviceProvider) {
		if (serviceProvider == null) {
			return null;
		}

		Pattern pattern = null;
		switch (serviceProvider) {
		case Sina:
			pattern = MENTION_SINA;
			break;
		case Tencent:
			pattern = MENTION_TENCENT;
			break;
		case Twitter:
			pattern = MENTION_TWITTER;
			break;
		case Sohu:
			pattern = MENTION_SOHU;
			break;
		case NetEase:
			pattern = MENTION_NETEASE;
			break;
		case Fanfou:
			pattern = MENTION_FANFOU;
			break;
		default:
			pattern = MENTION_SINA;
			break;
		}

		return pattern;
	}

	/**
	 * 获取话题的匹配模式
	 *
	 * @param serviceProvider
	 *            ServiceProvider
	 * @return 话题的匹配模式
	 */
	public static Pattern getTopicPattern(ServiceProvider serviceProvider) {
		if (serviceProvider == null) {
			return null;
		}

		Pattern pattern = null;
		switch (serviceProvider) {
		case NetEase:
			pattern = TOPIC_NETEASE;
			break;
		case Twitter:
			pattern = TOPIC_TWITTER;
			break;
		default:
			pattern = TOPIC_SINA;
			break;
		}

		return pattern;
	}

	/**
	 * 获取链接匹配模式
	 *
	 * @param serviceProvider
	 * @return 链接的匹配模式
	 */
	public static Pattern getUrlPattern(ServiceProvider serviceProvider) {
		if (serviceProvider == null) {
			return null;
		}

		Pattern pattern = null;
		switch (serviceProvider) {
		case Sina:
		case Tencent:
		case NetEase:
		case Sohu:
		case Twitter:
		case Fanfou:
		default:
			pattern = URL_TWITTER;
			break;
		}

		return pattern;
	}

	/**
	 * 获取转发时的分隔符号
	 *
	 * @param serviceProvider
	 *            ServiceProvider
	 * @return 转发时的分隔符号，默认为"||"
	 */
	public static String getRetweetSeparator(ServiceProvider serviceProvider) {
		if (serviceProvider == null) {
			return null;
		}

		String seprator = null;
		switch (serviceProvider) {
		case Tencent:
		case NetEase:
			seprator = RETWEET_SEPARATOR_TENCENT;
			break;
		case Twitter:
			seprator = RETWEET_SEPARATOR_TWITTER;
			break;
		case Fanfou:
			seprator = RETWEET_SEPARATOR_FANFOU;
			break;
		default:
			seprator = RETWEET_SEPARATOR_SINA;
		}

		return seprator;
	}

	public static String getRetweetFormat(ServiceProvider serviceProvider) {
		if (serviceProvider == null) {
			return null;
		}

		String seprator = null;
		switch (serviceProvider) {
		case Twitter:
		case Tencent:
		case NetEase:
			seprator = RETWEET_FORMAT_TENCENT;
			break;
		case Fanfou:
			seprator = RETWEET_FORMAT_FANFOU;
			break;
		default:
			seprator = RETWEET_FORMAT_SINA;
		}

		return seprator;
	}

}
