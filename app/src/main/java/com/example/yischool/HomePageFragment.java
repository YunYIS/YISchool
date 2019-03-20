package com.example.yischool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import customview.EditTextView;


public class HomePageFragment extends Fragment  implements View.OnClickListener{

    private static HomePageFragment fragment = new HomePageFragment();

    private EditTextView editTextView;

    public static HomePageFragment newInstance() {
        return fragment;
    }

    /**
     * 初始化操作
     */
    private void init(View v){
        editTextView = v.findViewById(R.id.search_edit_text);
        editTextView.setOnClickListener(this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        init(view);
        return view;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_edit_text:
                Intent startSearchActivity = new Intent(getContext(), SearchActivity.class);
                startActivity(startSearchActivity);
                break;
            default:break;
        }
    }
}
