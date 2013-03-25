package com.shejiaomao.weibo.service.listener;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;

import com.cattong.commons.Logger;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;

public class ShakeUpdateListener {
	private static final String TAG = "ShakeUpdateListener";
	
	private static final long SHAKE_INTERVAL_TIME = 15000;
	private static final float SHAKE_UPDATE_ACCELERATION_X = 5.0F;
	private static final float SHAKE_UPDATE_ACCELERATION_Y = 1.0F;
	
	private Context context;
	private SheJiaoMaoApplication sheJiaoMao;
	private SensorManager sensorManager; 
	private Sensor acceleromererSensor;
	
	private long lastShakeTime;
	private float lastX;
	private float lastY;
	private float lastDiffX;
	private float lastDiffY;
    public ShakeUpdateListener(Context context) {
    	this.context = context;
    	this.sheJiaoMao = (SheJiaoMaoApplication)context.getApplicationContext();
    	// 获取传感器管理器
    	sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        // 获取加速度传感器
        acceleromererSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
    
    public void startMonitor() {        
        //在传感器管理器中注册监听器
    	sensorManager.registerListener(acceleromererListener, acceleromererSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    public void stopMonitor() {
    	sensorManager.unregisterListener(acceleromererListener);
    }
    
    //产生振动效果
    public void vibrateToUpdate() {
    	lastShakeTime = System.currentTimeMillis();
    	Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
    	//vibrator.vibrate(1000);       
    	vibrator.vibrate(new long[]{50, 150}, -1);
    	
    	Intent updateIntent = new Intent(Constants.ACTION_RECEIVER_AUTO_UPDATE); 
    	context.sendBroadcast(updateIntent);
    }
    
    // 定义传感器事件监听器
    SensorEventListener acceleromererListener = new SensorEventListener() {    
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //什么也不干
        }
    
        //传感器数据变动事件
        @Override
        public void onSensorChanged(SensorEvent event) {
        	long currentTime = System.currentTimeMillis();
            if (currentTime - lastShakeTime < SHAKE_INTERVAL_TIME 
            	|| !sheJiaoMao.isRefreshOnShake()) {
            	return;
            }
         
            //获取加速度传感器的三个参数
            float x = event.values[SensorManager.DATA_X];
            float y = event.values[SensorManager.DATA_Y];
            float z = event.values[SensorManager.DATA_Z];
            //System.out.println("加速度: x->" + x + ", y->" + y + ", z->" + z);

        	float tempDiffX = x - lastX;
        	float tempDiffY = y - lastY;
            lastX = x;
            lastY = y;
            if (lastDiffX < 0 
            	&& lastDiffY < 0 
            	&& tempDiffX > SHAKE_UPDATE_ACCELERATION_X 
            	&& tempDiffY > SHAKE_UPDATE_ACCELERATION_Y) {
            	if (Logger.isDebug()) Log.v(TAG, "vibrateToUpdate: x->" + x + ", y->" + y + ", z->" + z);
            	vibrateToUpdate();
            }
            
            lastDiffX = tempDiffX;
            lastDiffY = tempDiffY;
        }
    };
}
