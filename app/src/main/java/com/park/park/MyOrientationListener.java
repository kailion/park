package com.park.park;

/**
 * Created by dai on 2017/1/25.
 */

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Arrays;


public class MyOrientationListener implements SensorEventListener
{

    private Context context;
    private SensorManager sensorManager;
    private OnOrientationListener onOrientationListener ;
    private Sensor accelerometer; // 加速度传感器
    private Sensor magnetic; // 地磁场传感器
    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    private float z;//偏转角度



    public MyOrientationListener(Context context)
    {
        this.context = context;
    }

    // 开始
    public void start()
    {
        // 获得传感器管理器
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null)
        {
            // 获得加速度、地磁传感器
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
        // 注册
        if (magnetic != null&&magnetic!=null)
        {//注册监听器
            sensorManager.registerListener(this, accelerometer, Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, magnetic, Sensor.TYPE_MAGNETIC_FIELD);
        }

    }

    // 停止检测
    public void stop()
    {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        // 接受方向感应器的类型
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = event.values;
        }
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues,
                magneticFieldValues);
        SensorManager.getOrientation(R, values);
        //防止抖动
        if(Math.abs((float) Math.toDegrees(values[0])-z)<=5){onOrientationListener.onOrientationChanged(z);}
        else{z = (float) Math.toDegrees(values[0]);
        onOrientationListener.onOrientationChanged(z);}

    }

    public void setOnOrientationListener(OnOrientationListener onOrientationListener)
    {
        this.onOrientationListener = onOrientationListener ;
    }


    public interface OnOrientationListener
    {
        void onOrientationChanged(float z);
    }

}