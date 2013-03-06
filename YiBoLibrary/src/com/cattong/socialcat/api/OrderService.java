package com.cattong.socialcat.api;

import java.security.PublicKey;
import java.util.Date;
import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.entity.Passport;
import com.cattong.entity.PointsOrderInfo;
import com.cattong.entity.TaskOrderInfo;
import com.cattong.entity.WithdrawOrderInfo;

public interface OrderService {

	Passport addPointsOrderInfo(List<PointsOrderInfo> orderInfoList, PublicKey publicKey) throws LibException;

	WithdrawOrderInfo withdraw(String withdrawTo, int orderType, int money, PublicKey publicKey) throws LibException;
	
	List<WithdrawOrderInfo> findWithdrawOrderInfo(Date startedAt, Date endedAt) throws LibException;
	
	Passport addPreloadTaskOrderInfo(List<TaskOrderInfo> taskOrderInfoList, PublicKey publicKey) throws LibException;
	
	TaskOrderInfo activePreloadTaskOrderInfo(long taskId, String deviceId, PublicKey publicKey) throws LibException;
}
