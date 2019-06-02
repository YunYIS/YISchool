package com.example.yischool.mainFragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yischool.AllCategoryActivity;
import com.example.yischool.Bean.jsonBean.CommodityCardBean;
import com.example.yischool.InitApplication;
import com.example.yischool.R;
import com.example.yischool.SearchActivity;
import com.example.yischool.SearchResultActivity;
import com.example.yischool.SpecificCategoryActivity;

import com.example.yischool.Utils.NetworkUtils;
import com.example.yischool.ViewHelper.CommodityRecyclerHelper;
import com.example.yischool.ViewHelper.SlideViewPagerHelper;
import com.example.yischool.customview.EditTextView;
import com.example.yischool.customview.ImgTextButton;
import com.example.yischool.service.CommodityCardSearchService;
import com.example.yischool.service.HomePagerCommodityService;

import java.lang.ref.WeakReference;
import java.util.HashSet;


public class HomePageFragment extends Fragment implements View.OnClickListener, ImgTextButton.OnClickedListener{

    public static final String LOG_TAG = "HomePageFragment";

    //当前推荐跳过条数 Intent key
    public static final String SKIP_COUNT_KEY = "skip_counts";

    //启动具体分类活动 Intent key
    public static final String SPECIFIC_CATEGORY_KEY = "specific_category";

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
    private ImgTextButton dailyArticlesBtn;
    private ViewPager slideViewPager;//首页轮播图ViewPager
    private LinearLayout indicatorDotLayoout;//指示器圆点线性布局
    private RecyclerView commodityCardRecyclerView;//首页商品卡片展示
    private ViewStub networkErrorViewStub;
    private RelativeLayout networkErrorLayout;//网络错误提示布局
    //Fragment 布局View
    private View layoutView;


    private SlideViewPagerHelper slideViewPagerHelper;//轮播图工具类
    private CommodityRecyclerHelper commodityRecyclerHelper;//商品卡片展示类
    //start查询服务
    private Intent startServiceIntent;
    //保存服务查询后返回的商品卡片集合
    private HashSet<CommodityCardBean> commodityCardHashSet;
    private ServiceConnection connection;
    private HomePagerCommodityService.MyBinder myBinder;

    private static MyHandler handler;


    public static HomePageFragment newInstance() {
        return homePageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new MyHandler(HomePageFragment.this);
        //启动查询服务
        startServiceIntent = new Intent(getActivity(), HomePagerCommodityService.class);

        if(!NetworkUtils.isNetworkConnected(getActivity())){
            //没有网络连接
            showNetworkErrorLayout();

        }else {
            //启动服务查询
            getActivity().startService(startServiceIntent);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("HomePagerFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        layoutView = view;
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
        slideViewPager = view.findViewById(R.id.slideshow_view_pager);
        indicatorDotLayoout = view.findViewById(R.id.slideshow_dot_linear);
        commodityCardRecyclerView = view.findViewById(R.id.commodity_display_recycler_view);
        networkErrorViewStub = view.findViewById(R.id.network_error_view_stub);
        dailyArticlesBtn = view.findViewById(R.id.category_daily_articles);

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
        dailyArticlesBtn.setOnClickedListener(this);

        //启动轮播图
        slideViewPagerHelper = new SlideViewPagerHelper(getActivity(), slideViewPager, indicatorDotLayoout,
                getSaveBundleCurrPosition(savedInstanceState));
        slideViewPagerHelper.startSlideViewPager();

        //初始化推荐商品RecyclerView
        commodityCardHashSet = new HashSet<>();
        commodityRecyclerHelper = new CommodityRecyclerHelper(commodityCardHashSet, getActivity(), commodityCardRecyclerView);
        commodityRecyclerHelper.initRecyclerView();
        commodityRecyclerHelper.setNestedScrollingChild();

        //绑定首页查询服务
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myBinder = (HomePagerCommodityService.MyBinder)service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        Intent intent = new Intent(getActivity(), HomePagerCommodityService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);

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

        //启动具体分类Intent
        Intent specCategoryIntent = new Intent(getActivity(), SpecificCategoryActivity.class);
        //通过判断不同的Extra值，判断那个分类按钮的点击

        switch (v.getId()){
            case R.id.category_phone:
                specCategoryIntent.putExtra(SPECIFIC_CATEGORY_KEY, "手机");
                break;
            case R.id.category_book:
                specCategoryIntent.putExtra(SPECIFIC_CATEGORY_KEY, "图书");
                break;
            case R.id.category_costume:
                //服饰
                specCategoryIntent.putExtra(SPECIFIC_CATEGORY_KEY, "服饰");
                break;
            case R.id.category_digital_products:
                //电子产品
                specCategoryIntent.putExtra(SPECIFIC_CATEGORY_KEY, "数码");
                break;
            case R.id.category_game:
                specCategoryIntent.putExtra(SPECIFIC_CATEGORY_KEY, "游戏");
                break;
            case R.id.category_household_appliances:
                //家具家电
                specCategoryIntent.putExtra(SPECIFIC_CATEGORY_KEY, "家居家电");
                break;
            case R.id.category_vehicle:
                //二手车
                specCategoryIntent.putExtra(SPECIFIC_CATEGORY_KEY, "二手车");
                break;
            case R.id.category_service:
                //服务
                specCategoryIntent.putExtra(SPECIFIC_CATEGORY_KEY, "服务");
                break;
            case R.id.category_sports:
                specCategoryIntent.putExtra(SPECIFIC_CATEGORY_KEY, "体育运动");
                break;
            case R.id.category_daily_articles:
                specCategoryIntent.putExtra(SPECIFIC_CATEGORY_KEY, "生活百货");
                break;
            default:break;
        }

        //启动活动
        startActivity(specCategoryIntent);

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
        //解绑服务
        if(connection != null){
            try{
                getActivity().unbindService(connection);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 从临时保存的数据中获取轮播图currPosition的值
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
    /**
     * 展示网络错误布局
     */
    public void showNetworkErrorLayout(){

        try {
            //获取网络错误提示布局
            networkErrorLayout = (RelativeLayout) networkErrorViewStub.inflate();//膨胀网络错误提示布局(此处错误提示布局可见)

            //获取网络错误提示布局中的控件，并添加点击事件(点击提示图片或文字)
            ImageView imageView = networkErrorLayout.findViewById(R.id.no_network_img);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    networkErrorLayout.setVisibility(View.GONE);//隐藏错误提示布局
                    getActivity().startService(startServiceIntent);//重新开始查询
                }
            });
            TextView textView = networkErrorLayout.findViewById(R.id.no_network_text_button);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    networkErrorLayout.setVisibility(View.GONE);
                    getActivity().startService(startServiceIntent);//重新开始查询
                }
            });

        }catch (Exception e){

            if(networkErrorLayout == null){
                networkErrorLayout = layoutView.findViewById(R.id.network_error_inflateId);
            }
            if(networkErrorLayout.getVisibility()==View.GONE){
                networkErrorLayout.setVisibility(View.VISIBLE);//(已膨胀后的布局设置可见)
            }
        }
    }

    /**
     * 取消网络错误布局
     */
    public void closeNetworkErrorLayout(){

        if(networkErrorLayout != null){
            if(networkErrorLayout.getVisibility()==View.VISIBLE){
                networkErrorLayout.setVisibility(View.GONE);
            }
        }
    }

    public static MyHandler getHandler(){
        return handler;
    }

    static class MyHandler extends Handler {

        WeakReference<HomePageFragment> weakReference;

        public MyHandler(HomePageFragment fragment){
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HomePageFragment fragment = weakReference.get();
            if(fragment != null){
                if(msg.what == HomePagerCommodityService.RESULT_COMPLETED){

                    Log.d(LOG_TAG, "活动收到数据："+fragment.myBinder.getResultCards().size());
                    int size = fragment.myBinder.getResultCards().size();
                    if(size >= 20){
                        //数据加载完成，开始从Binder中取出数据
                        fragment.commodityCardHashSet.addAll(fragment.myBinder.getResultCards());
                        //更新界面
                        fragment.commodityRecyclerHelper.notifyDataChanged();

                    }else if(size < 20 && size > 0){
                        //数据加载完成，开始从Binder中取出数据
                        fragment.commodityCardHashSet.addAll(fragment.myBinder.getResultCards());
                        //更新界面
                        fragment.commodityRecyclerHelper.notifyDataChanged();
                        //FooterView更换显示完成
                        fragment.commodityRecyclerHelper.recyclerLoadingComplete();
                    }else {
                        //FooterView更换显示完成
                        fragment.commodityRecyclerHelper.recyclerLoadingComplete();
                    }
                }

            }
        }
    }
}
