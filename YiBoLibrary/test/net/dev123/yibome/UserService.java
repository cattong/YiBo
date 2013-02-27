package net.dev123.yibome;

import static org.junit.Assert.assertTrue;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.OAuthAuthorization;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.User;
import net.dev123.yibome.entity.UserExtInfo;

import org.junit.BeforeClass;
import org.junit.Test;

public class UserService {
	//28804f4de111539bb6da840ff8e0c0f2,iZOLwAmL4H9vmLPdKkVIN19gFq2MHEDG
	private static YiBoMe yiboMe;

	@BeforeClass
	public static void beforClass() {
		OAuthAuthorization auth = new OAuthAuthorization("77c58346232a821559eec11171e16a18", 
			"1GFUUyXDEmF4Nmecwt5CYCiUtwgfrN1h", ServiceProvider.YiBoMe);
		yiboMe = new YiBoMeImpl(auth);
	}

	@Test
	public void getUserBaseInfo() throws LibException {
		User user = yiboMe.getUserBaseInfo("1698891950", ServiceProvider.Sina);
		assertTrue(user != null);
	}
	
	@Test
	public void getUserExtInfo_Sina() throws LibException {
		UserExtInfo userExtInfo = yiboMe.getUserExtInfo("1618051664", ServiceProvider.Sina);
		assertTrue(userExtInfo != null);
	}
	
	@Test
	public void getUserExtInfo_Sina_Bus() throws LibException {
		//企业微博:
		UserExtInfo userExtInfo = yiboMe.getUserExtInfo("1727978503", ServiceProvider.Sina);
		assertTrue(userExtInfo != null);
	}
	
	@Test
	public void getUserExtInfo_Tencent() throws LibException {
		UserExtInfo userExtInfo = yiboMe.getUserExtInfo("Darwin007", ServiceProvider.Tencent);
		assertTrue(userExtInfo != null);
	}
	
	@Test
	public void getUserExtInfo_Sohu() throws LibException {
		UserExtInfo userExtInfo = yiboMe.getUserExtInfo("1321363", ServiceProvider.Sohu);
		assertTrue(userExtInfo != null);
	}
	
	@Test
	public void getUserExtInfo_NetEase() throws LibException {
		UserExtInfo userExtInfo = yiboMe.getUserExtInfo("-5636810287207191572", ServiceProvider.NetEase);
		assertTrue(userExtInfo != null);
	}
}
