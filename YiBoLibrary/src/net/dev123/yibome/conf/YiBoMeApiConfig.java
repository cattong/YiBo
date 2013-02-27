package net.dev123.yibome.conf;


public interface YiBoMeApiConfig {

	String getRestBaseURL();

	String getRegisterURL();

	String getLoginURL();

	String getAccountSyncURL();

	String getGroupSyncURL();

	String getGroupUserSyncURL();

	String getTimeNowURL();

	String getStatusSubscribeURL();
	
	String getUserBaseInfoURL();
	
	String getUserExtInfoURL();
	
	String getEmotionVersionInfoURL();
	
	String getStatusSyncURL();
	
	String getPointsURL();
	
	String getLoginPointsAddURL();
	
	String getUrlServiceURL();
	
	String getUrlRedirectServiceURL();
	
	String getMyConfigAppsURL();
}
