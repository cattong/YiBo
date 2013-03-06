package com.cattong.socialcat.conf;


public interface SocialCatApiConfig {

	String getRestBaseURL();

	String getRegisterURL();

	String getLoginURL();

	String getPointsURL();
	
	String getUpdateEmailURL();
	
	String getCheckInURL();
	
	String getFindRankingsURL();
	
	String getSyncAccountURL();

	String getSyncAccountListURL();
	
	String getStatusExtInfoURL();
	
	String getStatusCatalogURL();
	
	String getMobilePhotoURL();
	
	String getUserBaseInfoURL();
	
	String getUserExtInfoURL();
	
	String getAddPointsOrderInfoURL();
	
	String getWithdrawURL();
	
	String getFindWithdrawOrderInfoURL();
	
	String getAddPreloadTaskOrderInfoURL();
	
	String getActivePreloadTaskOrderInfoURL();
	
	String getAddDeviceInfoURL();
	
	String getAddAppInfoURL();
	
	String getFindPreloadAppTaskInfoURL();
	
	String getFindWitkeyTaskInfoURL();
	
	String getGetWitkeyTaskInfoURL();
}
