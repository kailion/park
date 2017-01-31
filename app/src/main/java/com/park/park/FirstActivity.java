package com.park.park;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.Arrays;


public class FirstActivity extends Activity {
    private SharedPreferences preferences;
    private LocationClient mLocationClient;
    private SharedPreferences.Editor editor;
    private ImageView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},123);
            init();
        }
        else {
            init();
            mLocationClient.start();
        }
    }
    private void init() {
        view = (ImageView) findViewById(R.id.imageView);
        view.setBackgroundResource(R.mipmap.first);
        mLocationClient=new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(false);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(true);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new MyListener());
        preferences = getSharedPreferences("count", MODE_PRIVATE);
        editor = preferences.edit();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i=0;i<permissions.length;i++) {
            if (permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                if (grantResults[i] == 0) {
                    mLocationClient.start();
                } else {
                    mLocationClient.start();
                }
            }
        }
    }

    @Override
    protected void onDestroy(){
        mLocationClient.stop();
        super.onDestroy();
    }

    private class MyListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (preferences.getBoolean("firststart", true)) {
                        //将登录标志位设置为false，下次登录时不在显示首次登录界面
                        editor.putBoolean("firststart", false);
                        editor.commit();
                        finish();
                        Intent intent = new Intent(FirstActivity.this, SplashActivity.class);
                        intent.putExtra("city",bdLocation.getCity());
                        intent.putExtra("accuracy",bdLocation.getRadius());
                        intent.putExtra("latitude",bdLocation.getLatitude());
                        intent.putExtra("longitude",bdLocation.getLongitude());
                        startActivity(intent);
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    } else {
                        finish();
                        Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                        intent.putExtra("city",bdLocation.getCity());
                        intent.putExtra("accuracy",bdLocation.getRadius());
                        intent.putExtra("latitude",bdLocation.getLatitude());
                        intent.putExtra("longitude",bdLocation.getLongitude());
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade, R.anim.hold);
                    }
                }
            }).start();
        }
    }
}

