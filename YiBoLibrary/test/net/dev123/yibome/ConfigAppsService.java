package net.dev123.yibome;

import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.OAuthAuthorization;
import net.dev123.yibome.entity.ConfigApp;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-10-31 下午2:04:40
 **/
public class ConfigAppsService {
	private static YiBoMe yibome;
	
	@BeforeClass
	public static void beforClass() {
		OAuthAuthorization auth = new OAuthAuthorization("5afd915f089911deb272ee689c520fcb", "tXdBXlbfYQRAB300MeGzzbA4mhxhwsb0", ServiceProvider.YiBoMe);
		yibome = new YiBoMeImpl(auth);
	}
	
	@Test
	public void getMyConfigApps() throws Exception {
		List<ConfigApp> appList = yibome.getMyConfigApps();
		org.junit.Assert.assertTrue(appList.size() > 0);
	}
}
