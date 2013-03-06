package com.cattong.socialcat.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.entity.AppInfo;
import com.cattong.entity.DeviceInfo;

public interface DeviceAppService {

	DeviceInfo addDeviceInfo(DeviceInfo deviceInfo) throws LibException;
	
	DeviceInfo addAppInfo(String deviceId, List<AppInfo> appInfoList) throws LibException;
}
