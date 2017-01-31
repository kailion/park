package com.park.park;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PoiActivity extends AppCompatActivity{
    private ListView searchResult;
    private ClearableEditText searchEdit;
    private SuggestionSearch mSearch;
    private List<SuggestionResult.SuggestionInfo> poiResult;
    private SimpleAdapter simpleAdapter;
    private List list;
    private Map map;
    private String city;
    private String searchText;
    private boolean isLastRow=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);
        init();
    }
    private void init(){
        city = getIntent().getStringExtra("city");
        if (city == null) {
            city = "北京";
        }
        searchEdit = (ClearableEditText) findViewById(R.id.search_edit);
        searchEdit.setSingleLine();
        searchEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    poiResult=new ArrayList<SuggestionResult.SuggestionInfo>();
                    // 获取输入地名
                    searchText = searchEdit.getText().toString();
                    //隐藏键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    //输入为空则不搜索
                    if (searchText == null) {
                        Toast.makeText(PoiActivity.this, "请输入地名", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    if(city==null){Toast.makeText(PoiActivity.this,"请先定位",Toast.LENGTH_SHORT).show();return true;}
                    mSearch.requestSuggestion((new SuggestionSearchOption())
                            .city(city)
                            .keyword(searchText));
                }
                return true;
            }
        });
        searchResult = (ListView) findViewById(R.id.search_listview);
        searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("latitude", poiResult.get(position).pt.latitude);
                intent.putExtra("longitude", poiResult.get(position).pt.longitude);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mSearch = SuggestionSearch.newInstance();
        mSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult result) {
                if (result == null || result.getAllSuggestions() == null) {

                    //未找到相关结果
                    Toast.makeText(PoiActivity.this,"未能查询到结果",Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取在线建议检索结果
                poiResult.addAll(result.getAllSuggestions());
                int i;
                if(result.getAllSuggestions().get(0).pt==null){
                    poiResult.remove(0);
                    i=1;
                }
                i=0;
                list = new ArrayList<Map<String, Object>>();
                System.out.println("num:"+result.getAllSuggestions().size());
                int length = result.getAllSuggestions().size();
                for (; i < length; i++) {
                    map = new HashMap<String, Object>();
                    map.put("title", result.getAllSuggestions().get(i).city+result.getAllSuggestions().get(i).district);
                    map.put("info", result.getAllSuggestions().get(i).key);
                    list.add(map);
                }

                if (list != null) {
                    simpleAdapter=new SimpleAdapter(PoiActivity.this, list, R.layout.search, new String[]{"title", "info"}, new int[]{R.id.textView_title, R.id.textView_info});
                    searchResult.setAdapter(simpleAdapter);
                }
                else{
                    Toast.makeText(PoiActivity.this, "未搜索到具体位置", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }
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
    protected void onDestroy() {
        if(mSearch!=null){
            mSearch.destroy();}
        super.onDestroy();
    }

    /*@Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE&&searchPage<totalPage) {
            //加载元素
            searchPage++;
            mSearch.searchInCity((new PoiCitySearchOption())
                    .city(city)
                    .keyword(searchText)
                    .pageNum(searchPage).pageCapacity(20));
            isLastRow = false;
        }
        return;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
            isLastRow = true;
        }
    }*/
}
