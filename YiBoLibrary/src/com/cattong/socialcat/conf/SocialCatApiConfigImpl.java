package com.cattong.socialcat.conf;

import com.cattong.commons.util.StringUtil;

public class SocialCatApiConfigImpl implements SocialCatApiConfig {

	private String restBaseURL;

	private String registerURL;
	private String loginURL;
	private String pointsURL;
	private String updateEmailURL;
	private String checkInURL;
	private String findRankingsURL;
	private String syncAccountURL;
	private String syncAccountListURL;
	private String statusExtInfoURL;
    private String statusCatalogURL;
    private String mobilePhotoURL;
    private String userBaseInfoURL;
    private String userExtInfoURL;
    private String addPointsOrderInfoURL;
    private String withdrawURL;
    private String findWithdrawOrderInfoURL;
    private String addPreloadTaskOrderInfoURL;
    private String activePreloadTaskOrderInfoURL;
    private String addDeviceInfoURL;
    private String addAppInfoURL;
    private String findPreloadAppTaskInfoURL;
    private String findWitkeyTaskInfoURL;
    private String getWitkeyTaskInfoURL;
    
	public SocialCatApiConfigImpl() {
		//this.setRestBaseURL("http://www.jifenbang.net/service/");
	    this.setRestBaseURL("http://test.jifenbang.net/service/");
		
		this.setLoginURL(getRestBaseURL() + "passport/login.do");
		this.setRegisterURL(getRestBaseURL() + "passport/register.do");
		this.setPointsURL(getRestBaseURL() + "points/get.do");
		this.setUpdateEmailURL(getRestBaseURL() + "passport/updateEmail.do");
		this.setCheckInURL(getRestBaseURL() + "passport/checkIn.do");
		this.setFindRankingsURL(getRestBaseURL() + "passport/findRankings.do");
		this.setSyncAccountURL(getRestBaseURL() + "account/syncAccount.do");
		this.setSyncAccountListURL(getRestBaseURL() + "account/syncAccountList.do");
		this.setStatusExtInfoURL(getRestBaseURL() + "status/extInfo.do");
		this.setStatusCatalogURL(getRestBaseURL() + "statuses/statusCatalog.do");
		this.setMobilePhotoURL(getRestBaseURL() + "statuses/mobilePhoto.do");
		this.setUserBaseInfoURL(getRestBaseURL() + "user/baseInfo.do");
		this.setUserExtInfoURL(getRestBaseURL() + "user/extInfo.do");
		this.setAddPointsOrderInfoURL(getRestBaseURL() + "order/addPointsOrderInfo.do");
		this.setWithdrawURL(getRestBaseURL() + "order/withdraw.do");
		this.setFindWithdrawOrderInfoURL(getRestBaseURL() + "order/findWithdrawOrderInfo.do");
		this.setAddPreloadTaskOrderInfoURL(getRestBaseURL() + "order/addPreloadTaskOrderInfo.do");
		this.setActivePreloadTaskOrderInfoURL(getRestBaseURL() + "order/activePreloadTaskOrderInfo.do");
	    this.setAddDeviceInfoURL(getRestBaseURL() + "deviceApp/addDeviceInfo.do");
	    this.setAddAppInfoURL(getRestBaseURL() + "deviceApp/addAppInfo.do");
	    this.setFindPreloadAppTaskInfoURL(getRestBaseURL() + "task/findPreloadAppTaskInfo.do");
	    this.setFindWitkeyTaskInfoURL(getRestBaseURL() + "task/findWitkeyTaskInfo.do");
	    this.setGetWitkeyTaskInfoURL(getRestBaseURL() + "task/getWitkeyTaskInfo.do");
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
	
	public String getSyncAccountURL() {
		return syncAccountURL;
	}

	public void setSyncAccountURL(String syncAccountURL) {
		this.syncAccountURL = syncAccountURL;
	}

	public String getSyncAccountListURL() {
		return syncAccountListURL;
	}

	public void setSyncAccountListURL(String syncAccountListURL) {
		this.syncAccountListURL = syncAccountListURL;
	}

	@Override
	public String getStatusCatalogURL() {
		return statusCatalogURL;
	}

	public void setStatusCatalogURL(String statusCatalogURL) {
		this.statusCatalogURL = statusCatalogURL;
	}

	public String getMobilePhotoURL() {
		return mobilePhotoURL;
	}

	public void setMobilePhotoURL(String mobilePhotoURL) {
		this.mobilePhotoURL = mobilePhotoURL;
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

	@Override
	public String getPointsURL() {
		return pointsURL;
	}

	public void setPointsURL(String pointsURL) {
		this.pointsURL = pointsURL;
	}

	public String getUpdateEmailURL() {
		return updateEmailURL;
	}

	public void setUpdateEmailURL(String updateEmailURL) {
		this.updateEmailURL = updateEmailURL;
	}

	public String getCheckInURL() {
		return checkInURL;
	}

	public void setCheckInURL(String checkInURL) {
		this.checkInURL = checkInURL;
	}

	public String getAddPointsOrderInfoURL() {
		return addPointsOrderInfoURL;
	}

	public void setAddPointsOrderInfoURL(String addPointsOrderInfoURL) {
		this.addPointsOrderInfoURL = addPointsOrderInfoURL;
	}

	@Override
	public String getStatusExtInfoURL() {
		return statusExtInfoURL;
	}

	public void setStatusExtInfoURL(String statusExtInfoURL) {
		this.statusExtInfoURL = statusExtInfoURL;
	}

	public String getWithdrawURL() {
		return withdrawURL;
	}

	public void setWithdrawURL(String withdrawURL) {
		this.withdrawURL = withdrawURL;
	}
	
	public String getFindWithdrawOrderInfoURL() {
		return findWithdrawOrderInfoURL;
	}

	public void setFindWithdrawOrderInfoURL(String findWithdrawOrderInfoURL) {
		this.findWithdrawOrderInfoURL = findWithdrawOrderInfoURL;
	}

	public String getAddPreloadTaskOrderInfoURL() {
		return addPreloadTaskOrderInfoURL;
	}

	public void setAddPreloadTaskOrderInfoURL(String addPreloadTaskOrderInfoURL) {
		this.addPreloadTaskOrderInfoURL = addPreloadTaskOrderInfoURL;
	}

	public String getActivePreloadTaskOrderInfoURL() {
		return activePreloadTaskOrderInfoURL;
	}

	public void setActivePreloadTaskOrderInfoURL(String activePreloadTaskOrderInfoURL) {
		this.activePreloadTaskOrderInfoURL = activePreloadTaskOrderInfoURL;
	}

	public String getAddDeviceInfoURL() {
		return addDeviceInfoURL;
	}

	public void setAddDeviceInfoURL(String addDeviceInfoURL) {
		this.addDeviceInfoURL = addDeviceInfoURL;
	}

	public String getAddAppInfoURL() {
		return addAppInfoURL;
	}

	public void setAddAppInfoURL(String addAppInfoURL) {
		this.addAppInfoURL = addAppInfoURL;
	}

	public String getFindPreloadAppTaskInfoURL() {
		return findPreloadAppTaskInfoURL;
	}

	public void setFindPreloadAppTaskInfoURL(String findPreloadAppTaskInfoURL) {
		this.findPreloadAppTaskInfoURL = findPreloadAppTaskInfoURL;
	}

	public String getFindWitkeyTaskInfoURL() {
		return findWitkeyTaskInfoURL;
	}

	public void setFindWitkeyTaskInfoURL(String findWitkeyTaskInfoURL) {
		this.findWitkeyTaskInfoURL = findWitkeyTaskInfoURL;
	}

	public String getFindRankingsURL() {
		return findRankingsURL;
	}

	public void setFindRankingsURL(String findRankingsURL) {
		this.findRankingsURL = findRankingsURL;
	}

	public String getGetWitkeyTaskInfoURL() {
		return getWitkeyTaskInfoURL;
	}

	public void setGetWitkeyTaskInfoURL(String getWitkeyTaskInfoURL) {
		this.getWitkeyTaskInfoURL = getWitkeyTaskInfoURL;
	}
}
