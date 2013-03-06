package com.cattong.entity;

import com.cattong.commons.util.HashCodeHelper;


public class AppInfo extends BaseEntity {
	private static final long serialVersionUID = 7556119381725669529L;
	
	private String appId;
	
	private String appName;
	
	private String packageName;
	
	private String versionName;
	
	private String versionCode;

	private Integer osNo;
	private Os os;
	
	private String vendor;
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public Integer getOsNo() {
		return osNo;
	}

	public void setOsNo(Integer osNo) {
		this.osNo = osNo;
		this.os = Os.getOs(osNo);
	}

	public Os getOs() {
		return os;
	}

	public void setOs(Os os) {
		this.os = os;
		this.osNo = os == null ? Os.Unknow.getOsNo() : os.getOsNo();
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	@Override
	public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
			return false;
        }
        
        final AppInfo obj = (AppInfo) o;
        if (appId == null 
        	|| appName == null
        	|| packageName == null) {
        	return false;
        }
        
        if (appId.equals(obj.getAppId()) 
        	&& appName.equals(obj.getAppName())
            && packageName.equals(obj.getPackageName())) {
        	return true;
        }
        
        return false;
	}

	@Override
	public int hashCode() {
		HashCodeHelper helper = HashCodeHelper.getInstance();
		helper.appendObj(appId).appendObj(appName).appendObj(packageName);
		return helper.getHashCode();
	}

}
