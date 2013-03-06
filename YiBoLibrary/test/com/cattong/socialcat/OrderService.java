package com.cattong.socialcat;

import static org.junit.Assert.assertNotNull;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.Constants;
import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.RsaUtil;
import com.cattong.entity.Passport;
import com.cattong.entity.PointsOrderInfo;
import com.cattong.entity.TaskOrderInfo;
import com.cattong.entity.TaskType;
import com.cattong.entity.WithdrawOrderInfo;
import com.cattong.socialcat.impl.socialcat.SocialCat;

public class OrderService {
	private static SocialCat socialCat;

	@BeforeClass
	public static void beforClass() {
		Authorization auth = new Authorization(ServiceProvider.SocialCat);
		auth.setAccessToken("863020017969605_M2AtaAmCwYwIyXFImz53h7DHwO3yTSu0"); 
	    
	    socialCat = new SocialCat(auth);
	}
	
	@Test
	public void testWithdraw() throws LibException {
		String withdrawTo = "1360603909";
		int orderType = WithdrawOrderInfo.ORDER_TYPE_QBI;
		int money = 1;
		PublicKey publicKey = RsaUtil.toPublicKey(Constants.PUBLIC_KEY);
		
		WithdrawOrderInfo orderInfo = socialCat.withdraw(withdrawTo, orderType, money, publicKey);
	    
		Logger.verbose("orderInfo: {}", orderInfo);
		assertNotNull(orderInfo);
	}
	
	@Test
	public void addPointsOrderInfo() throws LibException {
		List<PointsOrderInfo> orderInfoList = new ArrayList<PointsOrderInfo>();
		PointsOrderInfo orderInfo = new PointsOrderInfo();
		orderInfo.setAmount(1);
		orderInfo.setOrderType(PointsOrderInfo.ORDER_TYPE_MIJI);
		orderInfo.setPoints(200);
		orderInfoList.add(orderInfo);
		
		PublicKey publicKey = RsaUtil.toPublicKey(com.cattong.commons.Constants.PUBLIC_KEY);
		Passport passport = socialCat.addPointsOrderInfo(orderInfoList, publicKey);
		
		assertNotNull(passport);
	}
	
	@Test
	public void addPreloadTaskOrderInfo() throws LibException {
		List<TaskOrderInfo> taskOrderInfoList = new ArrayList<TaskOrderInfo>();
		TaskOrderInfo taskOrderInfo = new TaskOrderInfo();
		taskOrderInfo.setTaskId(4l);
		taskOrderInfo.setDeviceId("33333333");
		taskOrderInfo.setTaskType(TaskType.App_Preloader);
		taskOrderInfoList.add(taskOrderInfo);
		
		PublicKey publicKey = RsaUtil.toPublicKey(com.cattong.commons.Constants.PUBLIC_KEY);
		Passport passport = socialCat.addPreloadTaskOrderInfo(taskOrderInfoList, publicKey);
		
		assertNotNull(passport);
	}
	
	@Test
	public void activePreloadTaskOrderInfo() throws LibException {
		
		PublicKey publicKey = RsaUtil.toPublicKey(com.cattong.commons.Constants.PUBLIC_KEY);
		TaskOrderInfo taskOrderInfo = socialCat.activePreloadTaskOrderInfo(
			4l, "33333333", publicKey);
		
		assertNotNull(taskOrderInfo);
	}
}
