package net.dev123.yibome.entity;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-9-23 下午5:47:41
 **/
public class StatusSyncResult {

	private String userId;
	private int serviceProviderNo;
	private boolean isSuccess;
	private String errorCode;
	private String errorDesc;

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getServiceProviderNo() {
		return serviceProviderNo;
	}
	public void setServiceProviderNo(int serviceProviderNo) {
		this.serviceProviderNo = serviceProviderNo;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorDesc() {
		return errorDesc;
	}
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

}
