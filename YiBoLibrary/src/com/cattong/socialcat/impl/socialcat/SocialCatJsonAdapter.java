package com.cattong.socialcat.impl.socialcat;

import java.lang.reflect.Type;
import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.entity.Account;
import com.cattong.entity.AppInfo;
import com.cattong.entity.AppTaskInfo;
import com.cattong.entity.DeviceInfo;
import com.cattong.entity.Passport;
import com.cattong.entity.TaskOrderInfo;
import com.cattong.entity.WithdrawOrderInfo;
import com.cattong.entity.WitkeyTaskInfo;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

class SocialCatJsonAdapter {

	public static Passport createPassport(String json, Gson gson) throws LibException {
		Passport passport = null;
		try {
			passport = gson.fromJson(json, Passport.class);
		} catch(JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		return passport;
	}

	public static List<Passport> createPassportList(String json, Gson gson) throws LibException {
		List<Passport> passportList = null;
		try {
			Type type = new TypeToken<List<Passport>>(){}.getType();
			passportList = gson.fromJson(json, type);
		} catch(JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		return passportList;
	}
	
	public static Account createAccount(String json, Gson gson) throws LibException {
		Account account = null;
		try {
			account = gson.fromJson(json, Account.class);
		} catch(JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		return account;
	}
	
	public static List<Account> createAccountList(String json, Gson gson) throws LibException {
		List<Account> accountList = null;
		try {
			Type type = new TypeToken<List<Account>>(){}.getType();
			accountList = gson.fromJson(json, type);
		} catch(JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		return accountList;
	}
	
	public static WithdrawOrderInfo createWithdrawOrderInfo(String json, Gson gson) throws LibException {
		WithdrawOrderInfo withdrawOrderInfo = null;
		try {
			withdrawOrderInfo = gson.fromJson(json, WithdrawOrderInfo.class);
		} catch(JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		return withdrawOrderInfo;
	}
	
	public static List<WithdrawOrderInfo> createWithdrawOrderInfoList(String json, Gson gson) throws LibException {
		List<WithdrawOrderInfo> withdrawOrderInfoList = null;
		try {
			Type type = new TypeToken<List<WithdrawOrderInfo>>(){}.getType();
			withdrawOrderInfoList = gson.fromJson(json, type);
		} catch(JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		return withdrawOrderInfoList;
	}
	
	public static DeviceInfo createDeviceInfo(String json, Gson gson) throws LibException {
		DeviceInfo deviceInfo = null;
		try {
		    deviceInfo = gson.fromJson(json, DeviceInfo.class);
		} catch(JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		return deviceInfo;
	}
	
	public static AppInfo createAppInfo(String json, Gson gson) throws LibException {
		AppInfo appInfo = null;
		try {
		    appInfo = gson.fromJson(json, AppInfo.class);
		} catch(JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		return appInfo;
	}
	
	public static List<AppInfo> createAppInfoList(String json, Gson gson) throws LibException {
		List<AppInfo> appInfoList = null;
		try {
			Type type = new TypeToken<List<AppInfo>>(){}.getType();
		    appInfoList = gson.fromJson(json, type);
		} catch(JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		return appInfoList;
	}
	
	public static AppTaskInfo createAppTaskInfo(String json, Gson gson) throws LibException {
		AppTaskInfo appTaskInfo = null;
		try {
		    appTaskInfo = gson.fromJson(json, AppTaskInfo.class);
		} catch(JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		return appTaskInfo;
	}
	
	public static List<AppTaskInfo> createAppTaskInfoList(String json, Gson gson) throws LibException {
		List<AppTaskInfo> appTaskInfoList = null;
		try {
			Type type = new TypeToken<List<AppTaskInfo>>(){}.getType();
		    appTaskInfoList = gson.fromJson(json, type);
		} catch(JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		return appTaskInfoList;
	}
	
	public static TaskOrderInfo createTaskOrderInfo(String json, Gson gson) throws LibException {
		TaskOrderInfo taskOrderInfo = null;
		try {
			taskOrderInfo = gson.fromJson(json, TaskOrderInfo.class);
		} catch(JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		return taskOrderInfo;
	}
	
	public static WitkeyTaskInfo createWitkeyTaskInfo(String json, Gson gson) throws LibException {
		WitkeyTaskInfo witkeyTaskInfo = null;
		try {
			witkeyTaskInfo = gson.fromJson(json, WitkeyTaskInfo.class);
		} catch(JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		return witkeyTaskInfo;
	}
	
	public static List<WitkeyTaskInfo> createWitkeyTaskInfoList(String json, Gson gson) throws LibException {
		List<WitkeyTaskInfo> witkeyTaskInfoList = null;
		try {
			Type type = new TypeToken<List<WitkeyTaskInfo>>(){}.getType();
		    witkeyTaskInfoList = gson.fromJson(json, type);
		} catch(JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		return witkeyTaskInfoList;
	}
}
