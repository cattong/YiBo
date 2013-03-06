package com.cattong.socialcat;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.DeviceInfo;
import com.cattong.entity.WitkeyTaskInfo;
import com.cattong.socialcat.impl.socialcat.SocialCat;

public class TaskService {

	private static SocialCat socialCat;

	@BeforeClass
	public static void beforClass() {
		Authorization auth = new Authorization(ServiceProvider.SocialCat);
		auth.setAccessToken("863020017969605_M2AtaAmCwYwIyXFImz53h7DHwO3yTSu0"); 
	    
	    socialCat = new SocialCat(auth);
	}
	
	@Test
	public void findWitkeyTaskInfo() throws LibException {
		
		DeviceInfo deviceInfo = new DeviceInfo();
		List<WitkeyTaskInfo> witkeyTaskInfoList = socialCat.findWitkeyTaskInfo(deviceInfo, null);
		
		assertTrue(ListUtil.isNotEmpty(witkeyTaskInfoList));
	}
}
