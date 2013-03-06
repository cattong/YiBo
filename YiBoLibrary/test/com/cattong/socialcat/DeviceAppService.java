package com.cattong.socialcat;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.AppInfo;
import com.cattong.entity.AppTaskInfo;
import com.cattong.entity.DeviceInfo;
import com.cattong.entity.Os;
import com.cattong.socialcat.impl.socialcat.SocialCat;

public class DeviceAppService {
	private static SocialCat socialCat;

	@BeforeClass
	public static void beforClass() {
		Authorization auth = new Authorization(ServiceProvider.SocialCat);
		auth.setAccessToken("863020017969605_M2AtaAmCwYwIyXFImz53h7DHwO3yTSu0"); 
	    
	    socialCat = SocialCatFactory.getInstance(auth, "2.3.1");
	}
	
	@Test
	public void testAddDeviceInfo() {
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("jdjsfjdskjf11");
		deviceInfo.setModel("MiOne");
		deviceInfo.setOs(Os.Android);
		deviceInfo.setFireware("android 4.2");
		
		DeviceInfo resultDeviceInfo = null;
		try {
			resultDeviceInfo = socialCat.addDeviceInfo(deviceInfo);
		} catch (LibException e) {
			e.printStackTrace();
		}
		
		assertNotNull(resultDeviceInfo);
	}
	
	@Test
	public void testAddAppInfoList() {
		List<AppInfo> appInfoList = new ArrayList<AppInfo>();
		for (int i = 0; i < 2; i++) {
			AppInfo appInfo = new AppInfo();
			appInfo.setAppId("com.shejiaomao.jifenbang" + i);
			appInfo.setAppName("积分邦");
			appInfo.setOs(Os.Android);
			appInfo.setPackageName("com.shejiaomao.jifenbang");
			appInfo.setVersionName("1.2");
			appInfo.setVersionCode("102");
			
			appInfoList.add(appInfo);
		}
		
		DeviceInfo deviceInfo = null;
		try {
			deviceInfo = socialCat.addAppInfo("jdjsfjdskjf11", appInfoList);
		} catch (LibException e) {
			e.printStackTrace();
		}
		
		assertNotNull(deviceInfo);
		
	}
	
	@Test
	public void testFindPreloadAppTaskInfo() {
		List<AppTaskInfo> appTaskInfoList = null;
		try {
			appTaskInfoList = socialCat.findPreloadAppTaskInfo();
		} catch (LibException e) {
			e.printStackTrace();
		}
		
		assertTrue(!ListUtil.isEmpty(appTaskInfoList));
	}
}
