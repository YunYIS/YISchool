package com.example.yischool.mainFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.yischool.AllCategoryActivity;
import com.example.yischool.R;
import com.example.yischool.SearchActivity;
import com.example.yischool.SpecificCategoryActivity;

import com.example.yischool.ViewHelper.CommodityRecyclerHelper;
import com.example.yischool.ViewHelper.SlideViewPagerHelper;
import com.example.yischool.customview.EditTextView;
import com.example.yischool.customview.ImgTextButton;


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
    private RecyclerView commodityCardRecyclerView;//首页商品卡片展示

    private SlideViewPagerHelper slideViewPagerHelper;//轮播图工具类
    private CommodityRecyclerHelper commodityRecyclerHelper;//商品卡片展示类

    public static HomePageFragment newInstance() {
        return homePageFragment;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("HomePagerFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        /*
         * 初始化操作
         */
        //findView
        editTextView = view.findViewById(R.id.search_edit_text);
        phoneImgTextBtn = view.findViewById(R.id.category_phone);
        bookImgTextBtn = view.findViewById(R.id.category_book);
        costumeImgTextBtn = view.findViewById(R.id.category_costume);
        digitalImgTextBtn = view.findViewById(R.id.category_digital_products);
        gameImgTextBtn = view.findViewById(R.id.category_game);
        householdImgTextBtn  = view.findViewById(R.id.category_household_appliances);
        vehicleImgTextBtn = view.findViewById(R.id.category_vehicle);
        serviceImgTextBtn = view.findViewById(R.id.category_service);
        sportsImgTextBtn = view.findViewById(R.id.category_sports);
        allCategoryImgTextBtn = view.findViewById(R.id.category_all);
        slideViewPager = view.findViewById(R.id.slideshow_view_pager);
        indicatorDotLayoout = view.findViewById(R.id.slideshow_dot_linear);
        commodityCardRecyclerView = view.findViewById(R.id.commodity_display_recycler_view);

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
        slideViewPagerHelper = new SlideViewPagerHelper(getActivity(), slideViewPager, indicatorDotLayoout,
                getSaveBundleCurrPosition(savedInstanceState));
        slideViewPagerHelper.startSlideViewPager();
        commodityRecyclerHelper = new CommodityRecyclerHelper(getActivity(), commodityCardRecyclerView);
        commodityRecyclerHelper.startCardRecycler();

        return view;
    }
    /**
     * 扩展或原生控件 设置点击事件执行逻辑
     * @param v
     */
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
     * 自定义控件 设置点击事件执行逻辑
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
                startSpecCategoryActivity = new Intent(getContext(), AllCategoryActivity.class);
                break;
            default:break;
        }
        if(startSpecCategoryActivity != null){
            startActivity(startSpecCategoryActivity);
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //临时保存切换Fragment时的轮播图位置
        outState.putInt("currPosition", slideViewPagerHelper.getCurrPosition());
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("HomePagerFragment", "onDestroyView");
        slideViewPagerHelper.stopSlideViewPager();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("HomePagerFragment", "onDestroyView");

    }

    /**
     * 从临时保存的数据中获取currPosition的值
     * @param save 但要考虑第一次进入时saveInstanceState为空
     * @return
     */
    private int getSaveBundleCurrPosition(Bundle save){
        if(save == null){
            return 0;
        }else{
            return save.getInt("currPosition");
        }
    }
}
