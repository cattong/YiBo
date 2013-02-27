package net.dev123.yibome.conf;

import net.dev123.commons.util.StringUtil;

public class YiBoMeApiConfigImpl implements YiBoMeApiConfig {

	private String restBaseURL;

	private String registerURL;
	private String loginURL;
	private String accountSyncURL;
	private String groupSyncURL;
	private String groupUserSyncURL;
    private String statusSubscribeURL;
    private String userBaseInfoURL;
    private String userExtInfoURL;
	private String timeNowURL;
    private String emotionVersionInfoURL;
    private String statusSyncURL;
    private String pointsURL;
    private String loginPointsAddURL;
    private String urlServiceURL;
    private String urlRedirectServiceURL;
    private String myConfigAppsURL;

	public YiBoMeApiConfigImpl() {
		this.setRestBaseURL("http://yibo.me/service/");
		
		this.setLoginURL(getRestBaseURL() + "passport/login.do");
		this.setRegisterURL(getRestBaseURL() + "passport/register.do");
		this.setAccountSyncURL(getRestBaseURL() + "account/sync.do");
		this.setGroupSyncURL(getRestBaseURL() + "group/sync.do");
		this.setGroupUserSyncURL(getRestBaseURL() + "group/user/sync.do");
		this.setStatusSubscribeURL(getRestBaseURL() + "status/subscribe.do");
		this.setUserBaseInfoURL(getRestBaseURL() + "user/baseInfo.do");
		this.setUserExtInfoURL(getRestBaseURL() + "user/extInfo.do");
		this.setTimeNowURL(getRestBaseURL() + "time/now.do");
		this.setEmotionVersionInfoURL(getRestBaseURL() + "emotion/version.do");
		this.setStatusSyncURL(getRestBaseURL() + "status/sync.do");
		this.setPointsURL(getRestBaseURL() + "point/get.do");
		this.setLoginPointsAddURL(getRestBaseURL() + "point/addLoginPoints.do");
		this.setUrlServiceURL(getRestBaseURL() + "urlService.do");
		this.setUrlRedirectServiceURL(getRestBaseURL() + "urlRedirectService.do?channel=%1$s&sourceUrl=%2$s");
		this.setMyConfigAppsURL(getRestBaseURL() + "configApp/getMyConfigApps.do");
	}

	public String getRestBaseURL() {
		return restBaseURL;
	}

	public void setRestBaseURL(String restBaseURL) {
		if (StringUtil.isEmpty(restBaseURL)) {
			throw new NullPointerException("RestBaseURL is null.");
		}
		if (!restBaseURL.endsWith("/")) {
			restBaseURL += "/";
		}
		this.restBaseURL = restBaseURL;
	}

	public String getRegisterURL() {
		return registerURL;
	}

	public void setRegisterURL(String registerURL) {
		this.registerURL = registerURL;
	}

	public String getLoginURL() {
		return loginURL;
	}

	public void setLoginURL(String loginURL) {
		this.loginURL = loginURL;
	}

	public String getAccountSyncURL() {
		return accountSyncURL;
	}

	public void setAccountSyncURL(String accountSyncURL) {
		this.accountSyncURL = accountSyncURL;
	}

	public String getGroupSyncURL() {
		return groupSyncURL;
	}

	public void setGroupSyncURL(String groupSyncURL) {
		this.groupSyncURL = groupSyncURL;
	}

	public String getGroupUserSyncURL() {
		return groupUserSyncURL;
	}

	public void setGroupUserSyncURL(String groupUserSyncURL) {
		this.groupUserSyncURL = groupUserSyncURL;
	}

	public String getTimeNowURL() {
		return timeNowURL;
	}

	public void setTimeNowURL(String timeNowURL) {
		this.timeNowURL = timeNowURL;
	}

	@Override
	public String getStatusSubscribeURL() {
		return statusSubscribeURL;
	}

	public void setStatusSubscribeURL(String statusSubscribeURL) {
		this.statusSubscribeURL = statusSubscribeURL;
	}

	public String getEmotionVersionInfoURL() {
		return emotionVersionInfoURL;
	}

	public void setEmotionVersionInfoURL(String emotionVersionInfoURL) {
		this.emotionVersionInfoURL = emotionVersionInfoURL;
	}

	public String getUserBaseInfoURL() {
		return userBaseInfoURL;
	}

	public void setUserBaseInfoURL(String userBaseInfoURL) {
		this.userBaseInfoURL = userBaseInfoURL;
	}

	public String getUserExtInfoURL() {
		return userExtInfoURL;
	}

	public void setUserExtInfoURL(String userExtInfoURL) {
		this.userExtInfoURL = userExtInfoURL;
	}

	public String getStatusSyncURL() {
		return statusSyncURL;
	}

	public void setStatusSyncURL(String statusSyncURL) {
		this.statusSyncURL = statusSyncURL;
	}

	@Override
	public String getPointsURL() {
		return pointsURL;
	}

	@Override
	public String getLoginPointsAddURL() {
		return loginPointsAddURL;
	}

	public void setPointsURL(String pointsURL) {
		this.pointsURL = pointsURL;
	}

	public void setLoginPointsAddURL(String loginPointsAddURL) {
		this.loginPointsAddURL = loginPointsAddURL;
	}

	@Override
	public String getUrlServiceURL() {
		return this.urlServiceURL;
	}
	
	public void setUrlServiceURL(String serviceURL) {
		this.urlServiceURL = serviceURL;
	}
    
	public String getMyConfigAppsURL() {
		return myConfigAppsURL;
	}

	public void setMyConfigAppsURL(String myConfigAppsURL) {
		this.myConfigAppsURL = myConfigAppsURL;
	}

	public String getUrlRedirectServiceURL() {
		return urlRedirectServiceURL;
	}

	public void setUrlRedirectServiceURL(String urlRedirectServiceURL) {
		this.urlRedirectServiceURL = urlRedirectServiceURL;
	}
}
