package com.cattong.weibo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.StringUtil;

public abstract class Emotions {
	private static final String CLASS_NAME_FORMAT_WEIBO = "com.cattong.weibo.impl.%1$s.%2$sEmotions";
	private static final String CLASS_NAME_FORMAT_SNS = "com.cattong.sns.impl.%1$s.%2$sEmotions";
	
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
		String className = "";
		switch (serviceProvider) {
		case Sina:
		case Sohu:
		case Tencent:
		case NetEase:
			className = String.format(CLASS_NAME_FORMAT_WEIBO, serviceProvider.toString().toLowerCase(), serviceProvider.toString());			
			break;
		case RenRen:
		case KaiXin:
		case QQZone:
			className = String.format(CLASS_NAME_FORMAT_SNS, serviceProvider.toString().toLowerCase(), serviceProvider.toString());
			break;
		default:
			break;
		}

		try {
			Class c = Class.forName(className);
			Method m = c.getMethod("getSingleton", new Class[]{});
			emotions = (Emotions) m.invoke(c, new Object[]{});
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
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
