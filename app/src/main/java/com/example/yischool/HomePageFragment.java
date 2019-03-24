package com.example.yischool;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ViewHelper.SlideViewPagerHelper;
import customview.EditTextView;
import customview.ImgTextButton;


public class HomePageFragment extends Fragment implements View.OnClickListener, ImgTextButton.OnClickedListener{

    private static HomePageFragment homePageFragment = new HomePageFragment();

    private EditTextView editTextView;//自定义输入搜索框
    private ImgTextButton phoneImgTextBtn;//商品分类按钮（上图下字）（10个）
    private ImgTextButton bookImgTextBtn;
    private ImgTextButton costumeImgTextBtn;
    private ImgTextButton digitalImgTextBtn;
    private ImgTextButton gameImgTextBtn;
    private ImgTextButton householdImgTextBtn;
    private ImgTextButton vehicleImgTextBtn;
    private ImgTextButton serviceImgTextBtn;
    private ImgTextButton sportsImgTextBtn;
    private ImgTextButton allCategoryImgTextBtn;
    private ViewPager slideViewPager;//首页轮播图ViewPager
    private LinearLayout indicatorDotLayoout;//指示器圆点线性布局

    private SlideViewPagerHelper slideViewPagerHelper;//轮播图工具类

    public static HomePageFragment newInstance() {
        return homePageFragment;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

    /**
     * 设置点击事件执行逻辑
     * @param v this隐式参数，指控件本身（该方法在控件中调用）
     */
    @Override
    public void onImgTextClick(View v) {
        Intent startSpecCategoryActivity = null;
        switch (v.getId()){
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
    /**
     * 初始化操作
     */
    private void init(View v){
        //findView
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
        slideViewPager = v.findViewById(R.id.slideshow_view_pager);
        indicatorDotLayoout = v.findViewById(R.id.slideshow_dot_linear);

        //注册监听器
        editTextView.setOnClickListener(this);
        phoneImgTextBtn.setOnClickedListener(this);
        bookImgTextBtn.setOnClickedListener(this);
        costumeImgTextBtn.setOnClickedListener(this);
        digitalImgTextBtn.setOnClickedListener(this);
        gameImgTextBtn.setOnClickedListener(this);
        householdImgTextBtn.setOnClickedListener(this);
        vehicleImgTextBtn.setOnClickedListener(this);
        serviceImgTextBtn.setOnClickedListener(this);
        sportsImgTextBtn.setOnClickedListener(this);
        allCategoryImgTextBtn.setOnClickedListener(this);

        //启动轮播图
        slideViewPagerHelper = new SlideViewPagerHelper(getActivity(), slideViewPager, indicatorDotLayoout);
        slideViewPagerHelper.startSlideViewPager();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        slideViewPagerHelper.stopSlideViewPager();
    }
}
