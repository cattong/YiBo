package com.shejiaomao.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;

import com.cattong.commons.Logger;
import com.cattong.entity.DeviceInfo;
import com.cattong.entity.Os;
import com.cattong.entity.SimCardInfo;

public class HardwareUtil {

	public final static String FILE_MEMINFO = "/proc/meminfo";
	public final static String FILE_CPUINFO = "/proc/cpuinfo";
	
	public static String getDeviceId(Context context) {
		TelephonyManager telManager = (TelephonyManager)context.getSystemService(
				Context.TELEPHONY_SERVICE);
		//imei
		String deviceId = telManager.getDeviceId();
		return deviceId;
	}
	
	public static long getRamMemory() {
		long totalSize = 0l;

		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
		    fileReader = new FileReader(FILE_MEMINFO);
		    bufferedReader = new BufferedReader(fileReader, 8192);
		    
		    String lineStr = bufferedReader.readLine();
		    Logger.debug("{}", lineStr);
		    
		    String[] tokens = lineStr.split("\\s+");
		    totalSize = Long.valueOf(tokens[1]) * 1024;
		    
		} catch(IOException e) {
			
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (Exception e) {}
			}
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (Exception e) {};
			}
		}
		
		return totalSize;
	}
	
	public static long getRamAvailMemory(Context context) {
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);
		
		return mi.availMem;
	}
	
	public static long getRomMemory() {
		long size = 0l;
		File root = Environment.getDataDirectory();
		StatFs sf = new StatFs(root.getPath());
		long bSize = sf.getBlockSize();
		long bCount = sf.getBlockCount();
		
		size = bSize * bCount;//总大小
		
		return size;
	}
	
	public static long getRomAvailMemory() {
		long size = 0l;
		File root = Environment.getDataDirectory();
		StatFs sf = new StatFs(root.getPath());
		long bSize = sf.getBlockSize();
		long availBlocks = sf.getAvailableBlocks();
		
		size = bSize * availBlocks;//可用大小
		
		return size;
	}
	
	public static long[] getSDCardMemory() {
		long[] sdCardInfo = new long[2];
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long bSize = sf.getBlockSize();
			long bCount = sf.getBlockCount();
			long availBlocks = sf.getAvailableBlocks();
			sdCardInfo[0] = bSize * bCount;//总大小
			sdCardInfo[1] = bSize * availBlocks;//可用大小
		}
		
		return sdCardInfo;
	}
	
	public static String[] getCpuInfo() {		
		String str2 = "";
		String[] cpuInfo = {"", ""};
		String[] splits;
		
		BufferedReader bufferedReader = null;
		try{
			FileReader fr = new FileReader(FILE_CPUINFO);
			bufferedReader = new BufferedReader(fr, 8192);
			str2 = bufferedReader.readLine();
			splits = str2.split("\\s+");
			for (int i = 2; i < splits.length; i++) {
			    cpuInfo[0] = cpuInfo[0] + splits[i] + " ";
			}
			
			str2 = bufferedReader.readLine();
			splits = str2.split("\\s+");
			cpuInfo[1] += splits[2];
		} catch(IOException e) {
			Logger.error(e.getMessage(), e);
		} finally {
			if (bufferedReader != null) {
			    try {
			    	bufferedReader.close();
			    } catch (Exception e) {}
			}
		}
		
		return cpuInfo;
	}
	
	public static String formatSize(long size) {
		String suffix = null;
		float fSize = 0;
		if (size >= 1024) {
		    suffix = "KB";
		    fSize = size/1024;
			if (fSize >= 1024) {
				suffix = "MB";
				fSize /= 1024;
			}
			if (fSize >= 1024) {
			    suffix = "GB";
			    fSize /= 1024;
			}
		} else {
		    fSize = size;
		}
		
		java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
		StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
		
		if (suffix != null) {
		    resultBuffer.append(suffix);
		}
		
		return resultBuffer.toString();
	}
	
	public static DeviceInfo getDeviceInfo(Context context) {
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId(getDeviceId(context));
		deviceInfo.setModel(CompatibilityUtil.getModel());
		deviceInfo.setFireware(CompatibilityUtil.getRelease());
		deviceInfo.setOs(Os.Android);
		deviceInfo.setCpu(getCpuInfo()[0]);
		deviceInfo.setRam(getRamMemory());
		deviceInfo.setRom(getRomMemory());
		return deviceInfo;
	}
	
	public static SimCardInfo getSimCardInfo(Context context) {
		SimCardInfo simCardInfo = null;
		if (context == null) {
			return simCardInfo;
		}
		
		TelephonyManager telManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
		//无卡
		if (telManager.getSimState() != TelephonyManager.SIM_STATE_READY) {
			return simCardInfo;
		}
		
		simCardInfo = new SimCardInfo();
		// 国际移动用户识别码. 需要权限：READ_PHONE_STATE  
		String imsi = telManager.getSubscriberId(); //
		simCardInfo.setSimImsi(imsi);

		//手机号： GSM手机的 MSISDN. Return null if it is unavailable.  
		String phoneNum = telManager.getLine1Number();  
		simCardInfo.setPhoneNum(phoneNum);
		
		//手机类型： 例如： PHONE_TYPE_NONE 无信号 PHONE_TYPE_GSM GSM信号 PHONE_TYPE_CDMA CDMA信号
		int phoneType = telManager.getPhoneType();// int
		simCardInfo.setPhoneType(phoneType);
		
		//获取ISO标准的国家码，即国际长途区号。当前网络。
		String simCountryIso = telManager.getNetworkCountryIso();
		//获取ISO国家码，相当于提供SIM卡的国家码 
		simCountryIso = telManager.getSimCountryIso();
		simCardInfo.setSimCountryIso(simCountryIso);
		
		//MCC+MNC(mobile country code + mobile network code) 当前网络。
		String simOperator = telManager.getNetworkOperator(); 
		//获取SIM卡提供的移动国家码和移动网络码.5或6位的十进制数字.
		simOperator = telManager.getSimOperator();// String  
		simCardInfo.setSimOperator(simOperator);
		
		//当前网络供应商的名称
		String simOperatorName = telManager.getNetworkOperatorName();
		//服务商名称： 例如：中国移动、联通 SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断). 
		simOperatorName = telManager.getSimOperatorName(); 	  
		simCardInfo.setSimOperatorName(simOperatorName);
		
		//SIM卡的序列号： 需要权限：READ_PHONE_STATE 
		String simSerialNumber = telManager.getSimSerialNumber();
		simCardInfo.setSimSerialNumber(simSerialNumber);
		
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		if (imsi.startsWith("46000") || imsi.startsWith("46002")) {  
			simOperatorName = "中国移动";  
		} else if (imsi.startsWith("46001")) {		
			simOperatorName = "中国联通";		
		} else if (imsi.startsWith("46003")) {		
			simOperatorName = "中国电信";		
		}  
		simCardInfo.setSimOperatorName(simOperatorName);
		
		return simCardInfo; 
	}
}
