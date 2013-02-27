package net.dev123.yibome;

import static org.junit.Assert.assertTrue;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.OAuthAuthorization;
import net.dev123.exception.LibException;
import net.dev123.yibome.entity.PointLevel;
import net.dev123.yibome.entity.PointOrderInfo;

import org.junit.BeforeClass;
import org.junit.Test;

public class PointService {
	//28804f4de111539bb6da840ff8e0c0f2,iZOLwAmL4H9vmLPdKkVIN19gFq2MHEDG
	private static YiBoMe yiboMe;

	@BeforeClass
	public static void beforClass() {
		OAuthAuthorization auth = new OAuthAuthorization("77c58346232a821559eec11171e16a18", 
			"1GFUUyXDEmF4Nmecwt5CYCiUtwgfrN1h", ServiceProvider.YiBoMe);
		yiboMe = new YiBoMeImpl(auth);
	}

	@Test
	public void getPoints() throws LibException {
		PointLevel pointLevel = yiboMe.getPoints();
		System.out.println(pointLevel);
		assertTrue(pointLevel != null);
	}
	
	@Test
	public void addLoginPoints() throws LibException {
		PointOrderInfo orderInfo = yiboMe.addLoginPoints();
		System.out.println(orderInfo);
		assertTrue(orderInfo != null);
	}
}
