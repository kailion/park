package com.park.park;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplashActivity extends Activity {
    private ViewPager viewPager;
    private LayoutInflater lf;
    private ArrayList viewList = new ArrayList<View>();
    private ImageView scroll1;
    private ImageView scroll2;
    private ImageView scroll3;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println(Arrays.toString(permissions)+Arrays.toString(grantResults));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    private void init() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        scroll1= (ImageView) findViewById(R.id.splash_scroll1);
        scroll1.setBackgroundResource(R.mipmap.splash_selected);
        scroll2= (ImageView) findViewById(R.id.splash_scroll2);
        scroll2.setBackgroundResource(R.mipmap.splash_scroll);
        scroll3= (ImageView) findViewById(R.id.splash_scroll3);
        scroll3.setBackgroundResource(R.mipmap.splash_scroll);
        //        将要分页显示的View装入数组中
        lf = getLayoutInflater().from(this);
        View splash1 = lf.inflate(R.layout.splash1, null);
        View splash2 = lf.inflate(R.layout.splash2, null);
        View splash3 = lf.inflate(R.layout.splash3, null);
        Button button_splash = (Button) splash3.findViewById(R.id.button_splash);
        button_splash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("city",getIntent().getStringExtra("city"));
                intent.putExtra("accuracy",getIntent().getFloatExtra("accuracy",0.0f));
                intent.putExtra("latitude",getIntent().getDoubleExtra("latitude",0.0));
                intent.putExtra("longitude",getIntent().getDoubleExtra("longitude",0.0));
                startActivity(intent);
                finish();
            }
        });
        viewList.add(splash1);
        viewList.add(splash2);
        viewList.add(splash3);
        PagerAdapter pagerAdapter = new SplashAdapter(viewList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setScrollBackGround();
                switch(position){
                    case 0:scroll1.setBackgroundResource(R.mipmap.splash_selected);break;
                    case 1:scroll2.setBackgroundResource(R.mipmap.splash_selected);break;
                    case 2:scroll3.setBackgroundResource(R.mipmap.splash_selected);break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }
    private void setScrollBackGround(){
        scroll1.setBackgroundResource(R.mipmap.splash_scroll);
        scroll2.setBackgroundResource(R.mipmap.splash_scroll);
        scroll3.setBackgroundResource(R.mipmap.splash_scroll);
    }


    public class SplashAdapter extends PagerAdapter {
        private ArrayList<View> viewList;

        public SplashAdapter(ArrayList<View> viewList) {
            this.viewList=viewList;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) viewList.get(position));
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView((View) viewList.get(position));

            return (View) viewList.get(position);
        }

    }
}
