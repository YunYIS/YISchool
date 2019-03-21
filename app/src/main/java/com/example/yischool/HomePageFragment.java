package com.example.yischool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import customview.EditTextView;
import customview.ImgTextButton;


public class HomePageFragment extends Fragment  implements View.OnClickListener, ImgTextButton.OnClickedListener{

    private static HomePageFragment fragment = new HomePageFragment();

    private EditTextView editTextView;
    private ImgTextButton phoneImgTextBtn;
    private ImgTextButton bookImgTextBtn;
    private ImgTextButton costumeImgTextBtn;
    private ImgTextButton digitalImgTextBtn;
    private ImgTextButton gameImgTextBtn;
    private ImgTextButton householdImgTextBtn;
    private ImgTextButton vehicleImgTextBtn;
    private ImgTextButton serviceImgTextBtn;
    private ImgTextButton sportsImgTextBtn;
    private ImgTextButton allCategoryImgTextBtn;

    public static HomePageFragment newInstance() {
        return fragment;
    }

    /**
     * 初始化操作
     */
    private void init(View v){
        editTextView = v.findViewById(R.id.search_edit_text);
        phoneImgTextBtn = v.findViewById(R.id.category_phone);
        bookImgTextBtn = v.findViewById(R.id.category_book);
        costumeImgTextBtn = v.findViewById(R.id.category_costume);
        digitalImgTextBtn = v.findViewById(R.id.category_digital_products);
        gameImgTextBtn = v.findViewById(R.id.category_game);
        householdImgTextBtn  = v.findViewById(R.id.category_household_appliances);
        vehicleImgTextBtn = v.findViewById(R.id.category_vehicle);
        serviceImgTextBtn = v.findViewById(R.id.category_service);
        sportsImgTextBtn = v.findViewById(R.id.category_sports);
        allCategoryImgTextBtn = v.findViewById(R.id.category_all);

        editTextView.setOnClickListener(this);
        phoneImgTextBtn.setOnClickedListener(this,R.id.category_phone);
        bookImgTextBtn.setOnClickedListener(this,R.id.category_book);
        costumeImgTextBtn.setOnClickedListener(this,R.id.category_costume);
        digitalImgTextBtn.setOnClickedListener(this,R.id.category_digital_products);
        gameImgTextBtn.setOnClickedListener(this,R.id.category_game);
        householdImgTextBtn.setOnClickedListener(this,R.id.category_household_appliances);
        vehicleImgTextBtn.setOnClickedListener(this,R.id.category_vehicle);
        serviceImgTextBtn.setOnClickedListener(this,R.id.category_service);
        sportsImgTextBtn.setOnClickedListener(this,R.id.category_sports);
        allCategoryImgTextBtn.setOnClickedListener(this,R.id.category_all);
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

    @Override
    public void onImgTextClick(int viewId) {
        Intent startSpecCategoryActivity = null;
        switch (viewId){
            case R.id.category_phone:
                startSpecCategoryActivity = new Intent(getContext(), SpecificCategoryActivity.class);
                //@TODO 传递信息以区分是哪一个类别的跳转
                break;
            case R.id.category_book:
                startSpecCategoryActivity = new Intent(getContext(), SpecificCategoryActivity.class);
                //@TODO 传递信息以区分是哪一个类别的跳转
                break;
            case R.id.category_costume:
                startSpecCategoryActivity = new Intent(getContext(), SpecificCategoryActivity.class);
                //@TODO 传递信息以区分是哪一个类别的跳转
                break;
            case R.id.category_digital_products:
                startSpecCategoryActivity = new Intent(getContext(), SpecificCategoryActivity.class);
                //@TODO 传递信息以区分是哪一个类别的跳转
                break;
            case R.id.category_game:
                startSpecCategoryActivity = new Intent(getContext(), SpecificCategoryActivity.class);
                //@TODO 传递信息以区分是哪一个类别的跳转
                break;
            case R.id.category_household_appliances:
                startSpecCategoryActivity = new Intent(getContext(), SpecificCategoryActivity.class);
                //@TODO 传递信息以区分是哪一个类别的跳转
                break;
            case R.id.category_vehicle:
                startSpecCategoryActivity = new Intent(getContext(), SpecificCategoryActivity.class);
                //@TODO 传递信息以区分是哪一个类别的跳转
                break;
            case R.id.category_service:
                startSpecCategoryActivity = new Intent(getContext(), SpecificCategoryActivity.class);
                //@TODO 传递信息以区分是哪一个类别的跳转
                break;
            case R.id.category_sports:
                startSpecCategoryActivity = new Intent(getContext(), SpecificCategoryActivity.class);
                //@TODO 传递信息以区分是哪一个类别的跳转
                break;
            case R.id.category_all:
                //@TODO 所有类别不能共用一个模版Activity
                break;
            default:break;
        }
        if(startSpecCategoryActivity != null){
            startActivity(startSpecCategoryActivity);
        }
    }
}
