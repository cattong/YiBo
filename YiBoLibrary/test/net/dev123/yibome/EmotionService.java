package net.dev123.yibome;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.NullAuthorization;
import net.dev123.exception.LibException;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-9-20 上午11:58:26
 **/
public class EmotionService {
	private static YiBoMe yibome;

	@BeforeClass
	public static void beforClass() {
		NullAuthorization auth = new NullAuthorization(ServiceProvider.YiBoMe);
		yibome = new YiBoMeImpl(auth);
	}
	
	@Test
	public void getEmotionVersionInfo() throws LibException {
		String versionInfo = yibome.getEmotionVersionInfo();
		System.out.println(versionInfo);
	}
}
