package com.cattong.socialcat.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.entity.Passport;

public interface PassportService {

	Passport register(String email, String username, String password, int passportType) throws LibException;
			
	Passport login(String emailOrUsername, String password) throws LibException;
	
	/**
	 * 获取积分
	 * 需要oauth2验证
	 * @return 通行证对象
	 */
	Passport getPoints() throws LibException;
	
	/**
	 * 完善资料接口
	 * 需要oauth2验证
	 * @return 通行证对象
	 */
	Passport updateEmail(String email, String password, String promoterId) throws LibException;
	
	Passport checkIn() throws LibException;

	List<Passport> findRankings() throws LibException;
}
