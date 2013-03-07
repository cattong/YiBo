package com.shejiaomao.common;

import com.cattong.commons.Logger;

import android.os.Debug;
import android.util.Log;

public class MemoryManager {
	public static final String TAG = "MemoryManager";

    public static final int REMAIN_MEMORY_LEVEL_1 = 1024 * 1024; //回收内存的条件，空闲内存值;

	public static void trace() {
		long maxMemory = Runtime.getRuntime().maxMemory();
		long totalMemory = Runtime.getRuntime().totalMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		if (Logger.level <= Logger.DEBUG) {
			Log.d(TAG, "Trace Runtime Memory: Max=" + maxMemory/1024/1024 + "MB"
					+ ", Total=" + totalMemory/1024 + "KB"
					+ ", Free=" + freeMemory/1024 + "KB" );
			Log.d(TAG, "Trace Native Memory: Total=" + Debug.getNativeHeapSize()/1024 + "KB"
					+ ", Free=" + Debug.getNativeHeapFreeSize()/1024 + "KB"
					+ ", Allocated=" + Debug.getNativeHeapAllocatedSize()/1024 + "KB" );
		}
	}

	/*
	 * 获得Java虚拟机的总内存
	 */
	public static long getJavaAvaiableMemorySize() {
		return Runtime.getRuntime().totalMemory();
	}

	/*
	 * 获得Java虚拟机未分配的内存
	 */
	public static long getJavaFreeMemorySize() {
		return Runtime.getRuntime().freeMemory();
	}

	/*
	 * 获得可供Native分配的最大内存数
	 */
	public static long getAvailableNativeMemorySize() {
		if (Logger.level <= Logger.DEBUG) trace();
		return Runtime.getRuntime().maxMemory() // 允许的最大内存
				- Runtime.getRuntime().totalMemory(); //Java Heap 所占用的内存
				//+ Debug.getNativeHeapFreeSize(); //Native Heap 已分配的内存
	}

	/*
	 * 判断是否需要进行内存回收
	 */
	public static boolean isNeedReclaim() {
		boolean isNeedRecaim = false;
		if (getJavaFreeMemorySize() < REMAIN_MEMORY_LEVEL_1) {
			isNeedRecaim = true;
		}

		return isNeedRecaim;
	}

}
