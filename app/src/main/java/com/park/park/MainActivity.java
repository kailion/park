package com.park.park;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends FragmentActivity {
    private FragmentTabHost tabHost;
    private MyViewPager pager;
    private List<Fragment> fragments;
    private MapFragment map_fragment;
    private SearchFragment search_fragment;
    private final int MAPREQUEST=1;
    private final int SEARCHREQUEST=2;
    private LatLng cenpt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        if(getIntent().getStringExtra("city")==null){
            Toast.makeText(this, "请打开定位", Toast.LENGTH_SHORT).show();}
        //初始化pager
        initPager();
        //初始化TabHost
        initTabHost();
        //添加监听关联TabHost和viewPager
        bindTabAndPager();
    }

        private void bindTabAndPager() {
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                int position = tabHost.getCurrentTab();
                pager.setCurrentItem(position,true);
            }
        });

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                return;
            }


            @Override
            public void onPageSelected(int position) {
                tabHost.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                return;
            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return true;
    }


    private void initPager() {
        pager = (MyViewPager) findViewById(R.id.pager);
        pager.setNoScroll(true);
        pager.setOffscreenPageLimit(3);
        fragments = new ArrayList<>();
        map_fragment=new MapFragment();
        fragments.add(map_fragment);
        search_fragment=new SearchFragment();
        fragments.add(search_fragment);
        fragments.add(new MsgFragment());
        fragments.add(new UserFragment());
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
    }
    private void initTabHost() {
        tabHost = (FragmentTabHost) findViewById(R.id.tab_host);
        tabHost.setup(this,getSupportFragmentManager(), R.id.pager);
        tabHost.getTabWidget().setDividerDrawable(null);
        tabHost.addTab(tabHost.newTabSpec("discover").setIndicator(createView(R.drawable.selector_bg_map,"地图")), MapFragment.class,null);
        tabHost.addTab(tabHost.newTabSpec("attach").setIndicator(createView(R.drawable.selector_bg_search,"搜索")), SearchFragment.class,null);
        tabHost.addTab(tabHost.newTabSpec("message").setIndicator(createView(R.drawable.selector_bg_message,"消息")), MsgFragment.class,null);
        tabHost.addTab(tabHost.newTabSpec("info").setIndicator(createView(R.drawable.selector_bg_user,"我的")), UserFragment.class,null);
    }
    //创建底部菜单栏控件
    private View createView(int icon, String tab){
        View view = getLayoutInflater().inflate(R.layout.fragment_tab,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        TextView  title = (TextView) view.findViewById(R.id.title);
        imageView.setImageResource(icon);
        title.setText(tab);
        return  view;
    }
//写点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }
    //判断是否隐藏键盘
    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case MAPREQUEST: {
                    cenpt = new LatLng(data.getDoubleExtra("latitude", 0), data.getDoubleExtra("longitude", 0));
                    map_fragment.setLocation(cenpt);break;
                }
                case SEARCHREQUEST:search_fragment.parkSearch(data.getStringExtra("address"));
                    break;
            }
        }
        return;
    }
}
