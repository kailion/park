package com.park.park;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private final int SEARCHREQUEST=2;
    private ListView parkListView;
    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MyViewPager pager=(MyViewPager)getActivity().findViewById(R.id.pager);
        pager.setNoScroll(false);
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        Button buttonPark= (Button) view.findViewById(R.id.button_park);
        buttonPark.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),PoiActivity.class);
                String city=getActivity().getIntent().getStringExtra("city");
                intent.putExtra("city",city==null? "北京":city);
                getActivity().startActivityForResult(intent,SEARCHREQUEST);
            }
        });
        parkListView= (ListView) view.findViewById(R.id.park_listview);
        return view;
    }
    protected void parkSearch(String address){

    }
}
