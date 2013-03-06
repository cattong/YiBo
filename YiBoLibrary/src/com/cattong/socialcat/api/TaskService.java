package com.cattong.socialcat.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.entity.AppTaskInfo;
import com.cattong.entity.DeviceInfo;
import com.cattong.entity.SimCardInfo;
import com.cattong.entity.TaskType;
import com.cattong.entity.WitkeyTaskInfo;

public interface TaskService {

	List<WitkeyTaskInfo> findWitkeyTaskInfo(DeviceInfo deviceInfo, SimCardInfo simCardInfo) throws LibException;
	
	List<AppTaskInfo> findPreloadAppTaskInfo() throws LibException;
	
	WitkeyTaskInfo getWitkeyTaskInfo(TaskType taskType) throws LibException;
}
