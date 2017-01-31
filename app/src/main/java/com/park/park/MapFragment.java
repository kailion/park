package com.park.park;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;



/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment{
    private TextureMapView mapView;
    private BaiduMap mBaiduMap;
    private Button searchButton;
    private MapStatus mMapStatus;
    private LocationClient mLocationClient;
    private String city;//定位城市
    private LatLng cenpt;//定位坐标
    private Float dir=0.0f;//定位方向
    private Float accuracy;
    private boolean isStart;//判断是否需要停止定位控件
    private boolean isFirst=true;//判断是否第一次定位
    private MyLocationConfiguration.LocationMode LocationMode = MyLocationConfiguration.LocationMode.NORMAL;
    private MyOrientationListener getDir;//获取方向
    private final int MAPREQUEST=1;

    public MapFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        MyViewPager pager=(MyViewPager)getActivity().findViewById(R.id.pager);
        pager.setNoScroll(true);
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        if(mapView==null){
        mapView = (TextureMapView) view.findViewById(R.id.bmapView);
        mBaiduMap = mapView.getMap();
        getDir = new MyOrientationListener(getActivity().getApplicationContext());
            getDir.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
                @Override
                public void onOrientationChanged(float z) {
                    dir = z;

                    if (accuracy == 0.0f || cenpt == null) {
                        return;
                    }
                    mBaiduMap.setMyLocationData(new MyLocationData.Builder()
                            .accuracy(accuracy)
                            .direction(dir)
                            .latitude(cenpt.latitude)
                            .longitude(cenpt.longitude).build());

                }

            });
        mLocationClient=new LocationClient(getActivity());
            initLocation();
            mLocationClient.registerLocationListener(new MyLocationListener());
        city=getActivity().getIntent().getStringExtra("city");
        Button button_locate= (Button) view.findViewById(R.id.button_locate);
            button_locate.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isStart=true;
                    isFirst=true;
                    mLocationClient.start();
                    //判断定位是否成功
                    //设置地图显示中心点
                }
            });
            searchButton=(Button) view.findViewById(R.id.button_search);
        searchButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),PoiActivity.class);
                intent.putExtra("city",city);
                getActivity().startActivityForResult(intent,MAPREQUEST);
            }
        });
        }
        initMap();



        System.out.println("new");
        return view;

    }

    private void initMap() {
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                LocationMode, true, null));

        if (city==null) {
            city = "北京";
        } else {
            accuracy=getActivity().getIntent().getFloatExtra("accuracy",0.0f);
            cenpt=new LatLng(getActivity().getIntent().getDoubleExtra("latitude",0.0),getActivity().getIntent().getDoubleExtra("longitude",0.0));
            getDir.start();
            mMapStatus = new MapStatus.Builder()
                    .target(cenpt)
                    .zoom(16)
                    .build();
//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//改变地图状态
            mBaiduMap.setMapStatus(mMapStatusUpdate);
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(true);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }
private class MyLocationListener implements BDLocationListener{

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if(bdLocation.getLocType()==61||bdLocation.getLocType()==161){
            if(city==null){city=bdLocation.getCity();}
            cenpt=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
            accuracy=bdLocation.getRadius();
            if(isFirst){
            getDir.start();
            mMapStatus = new MapStatus.Builder()
                    .target(cenpt)
                    .zoom(16)
                    .build();
//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//改变地图状态
            mBaiduMap.setMapStatus(mMapStatusUpdate);
                isFirst=false;
            }
        }
       if(bdLocation.getLocType()==61){Toast.makeText(getActivity(),"请打开网络",Toast.LENGTH_SHORT).show();}
          else{  if(isFirst==true){
            Toast.makeText(getActivity(),"请打开定位",Toast.LENGTH_SHORT).show();isFirst=false;}
        }
    }
}
    public  void setLocation(LatLng location){
        mBaiduMap.clear();
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_mark);
        OverlayOptions option = new MarkerOptions()
                .position(location).icon(bitmap);

//在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        mMapStatus = new MapStatus.Builder()
                .target(location)
                .zoom(14)
                .build();
//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        return;
    }
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        if(isStart){mLocationClient.stop();
        getDir.stop();}
        super.onDestroy();
    }


}

