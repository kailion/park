package com.park.park;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by dai on 2017/1/25.
 */

public class MyViewPager extends ViewPager {

        private boolean noScroll = true;

        public MyViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
            // TODO Auto-generated constructor stub
        }

        public MyViewPager(Context context) {
            super(context);
        }

        public void setNoScroll(boolean noScroll) {
            this.noScroll = noScroll;
        }

        @Override
        public void scrollTo(int x, int y) {
                super.scrollTo(x, y);
        }

        @Override
        public boolean onTouchEvent(MotionEvent arg0) {
        /* return false;//super.onTouchEvent(arg0); */
            if (noScroll)
                return false;
            else
                return super.onTouchEvent(arg0);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent arg0) {
            if (noScroll)
                return false;
            else
                return super.onInterceptTouchEvent(arg0);
        }

    public void setCurrentItem(int item, boolean smoothScroll) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item, false);
    }

    }




