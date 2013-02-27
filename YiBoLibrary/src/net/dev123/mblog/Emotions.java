package net.dev123.mblog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.StringUtil;
import net.dev123.mblog.netease.NetEaseEmotions;
import net.dev123.mblog.sina.SinaEmotions;
import net.dev123.mblog.sohu.SohuEmotions;
import net.dev123.mblog.tencent.TencentEmotions;
import net.dev123.sns.kaixin.KaiXinEmotions;
import net.dev123.sns.qqzone.QQZoneEmotions;
import net.dev123.sns.renren.RenRenEmotions;

public abstract class Emotions {
    public static final Pattern NORMALIZED_PATTERN = 
    	Pattern.compile("\\[(\\w|\\p{InCJKUnifiedIdeographs}){1,4}\\]");
	
    private static HashMap<String, String> emotionFan2JianMap;
	private static int versionFan2Jian = 0;
	private static int versionSpecialized = 0;
	protected static boolean isInit = false;
	
	public static void init(InputStream isEmotionsFan2Jian, InputStream isEmotionsSpecialized) {
		if (isInit) {
			return;
		}
		initFan2Jian(isEmotionsFan2Jian);
		initSpecialized(isEmotionsSpecialized);
		
		isInit = true;
	}
	
	private static void initFan2Jian(InputStream isEmotionsFan2Jian) {
		emotionFan2JianMap = new HashMap<String, String>();
		if (isEmotionsFan2Jian == null) {
			return;
		}
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(isEmotionsFan2Jian));
		String fan = null;
		String jian = null;
		String expression = null;
		String[] keyValues = null;
		
		while (true) {
			try {
			    expression = bufferedReader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//end of file
			if (expression == null) {
				break;
			}
			if (StringUtil.isEmpty(expression) || expression.startsWith("#")) {
				continue;
			}
			
			keyValues = expression.split("=");
			if (keyValues == null || keyValues.length != 2) {
				continue;
			}
			
			fan = keyValues[0].trim();
			jian = keyValues[1].trim();
			if (fan.equals("version")) {
				try {
					setVersionFan2Jian(Integer.valueOf(jian));
				} catch(NumberFormatException e) {				
					System.out.println("Wrong VersionFan2Jian: " + jian);
				}
				continue;
			}
			
			emotionFan2JianMap.put(fan, jian);			
		}	
	}
	
	private static void initSpecialized(InputStream isEmotionsSpecialized) {
		if (isEmotionsSpecialized == null) {
			return;
		}
		
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(isEmotionsSpecialized));
		String normal = null;
		String specialized = null;
		String expression = null;
		String[] keyValues = null;
		String[] specializeds = null;
		int spNo;
		Emotions emotions;
		
		while (true) {
			try {
				expression = bufferedReader.readLine();
			} catch (IOException e) {
				break;
			}
			
			//end of file
			if (expression == null) {
				break;
			}			
			if (StringUtil.isEmpty(expression) || expression.startsWith("#")) {
				continue;
			}
			
			keyValues = expression.split("=");
			if (keyValues == null || keyValues.length != 2) {
				continue;
			}
			
			String key = keyValues[0].trim();
			String value = keyValues[1].trim();
			if (key.equals("version")) {
				try {
					setVersionSpecialized(Integer.valueOf(value));
				} catch(NumberFormatException e) {
					System.out.println("Wrong Emotions Version: " + value);
				}
				continue;
			}
			
			normal = key;
			specializeds = value.split(",");
			String[] spEmotions = null;
			for (int i = 0; i < specializeds.length; i++) {
				spEmotions = specializeds[i].split(":");
				if (spEmotions == null || spEmotions.length != 2) {
					continue;
				}
				
				spNo = -1;
				try {
					spNo = Integer.valueOf(spEmotions[0].trim());
				} catch (NumberFormatException e) {}
				ServiceProvider sp = ServiceProvider.getServiceProvider(spNo);
				if (sp == null) {
					continue;
				}
				
				specialized = spEmotions[1].trim();
				emotions = getEmontionsInstance(sp);
				if (emotions != null) {
					emotions.loadEmontion(normal, specialized);	
				}
			}
			
		}
	}
	
	public abstract void loadEmontion(String normalized, String specialized);

	public static Pattern getNormalizedPattern(){
		return NORMALIZED_PATTERN;
	}

	public abstract Pattern getSpecializedPattern();

	public abstract String getNormalizedEmotion(String specializedEmotion);

	public abstract String getSpecializedEmotion(String normalizedEmotion);

	public String normalize(String specialized) {
		if (StringUtil.isEmpty(specialized)) {
			return specialized;
		}

		Matcher matcher = getSpecializedPattern().matcher(specialized);
		StringBuffer normalized = new StringBuffer();
		while (matcher.find()) {
			String jian = fan2Jian(matcher.group());
			String normalizedEmontion = null;
			
			normalizedEmontion = getNormalizedEmotion(jian);

			if (normalizedEmontion != null) {
				matcher.appendReplacement(normalized, normalizedEmontion);
			}
		}
		matcher.appendTail(normalized);
		return normalized.toString();
	}

	public String specialize(String normalized) {
		if (StringUtil.isEmpty(normalized)) {
			return normalized;
		}

		Matcher matcher = NORMALIZED_PATTERN.matcher(normalized);
		StringBuffer specialized = new StringBuffer();
		while (matcher.find()) {
			String specializedEmontion = getSpecializedEmotion(matcher.group());

			if (specializedEmontion != null) {
				matcher.appendReplacement(specialized, specializedEmontion);
			}
		}
		matcher.appendTail(specialized);
		return specialized.toString();
	}
	
	/**
	 * 繁体转为简体。如果找不到对应简体字或者未初始化，返回原值。
	 */
	public static String fan2Jian(String emotion) {
		if (!isInit) {
			return emotion;
		}
		String jian = emotionFan2JianMap.get(emotion);
		if (jian != null) {
			return jian;
		} else {
			return emotion;
		}
	}
	
	public static String specializeEmotion(ServiceProvider serviceProvider, String text) {
		if (!isInit) {
			return text;
		}
		String specialized = text;
		Emotions emotions = Emotions.getEmontionsInstance(serviceProvider);
		if (emotions != null) {
			specialized = emotions.specialize(text);
		}

		return specialized;
	}
	
	public static String normalizeEmotion(ServiceProvider serviceProvider, String text) {
		if (!isInit) {
			return text;
		}
		String normalized = text;
		Emotions emotions = Emotions.getEmontionsInstance(serviceProvider);
		if (emotions != null) {
			normalized = emotions.normalize(text);
		}

		return normalized;
	}
	
	public static Emotions getEmontionsInstance(ServiceProvider serviceProvider) {
		Emotions emotions = null;
		switch (serviceProvider) {
		case Sina:
			emotions = SinaEmotions.getSingleton();
			break;
		case Sohu:
			emotions = SohuEmotions.getSingleton();
			break;
		case Tencent:
			emotions = TencentEmotions.getSingleton();
			break;
		case NetEase:
			emotions = NetEaseEmotions.getSingleton();
			break;
		case RenRen:
			emotions = RenRenEmotions.getSingleton();
			break;
		case KaiXin:
			emotions = KaiXinEmotions.getSingleton();
			break;
		case QQZone:
			emotions = QQZoneEmotions.getSingleton();
			break;
		default:
			break;
		}

		return emotions;
	}

	public static int getVersionFan2Jian() {
		return versionFan2Jian;
	}

	private static void setVersionFan2Jian(int versionFan2Jian) {
		Emotions.versionFan2Jian = versionFan2Jian;
	}

	public static int getVersionSpecialized() {
		return versionSpecialized;
	}

	private static void setVersionSpecialized(int versionSpecialized) {
		Emotions.versionSpecialized = versionSpecialized;
	}

}
