package com.example.yischool;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yischool.Bean.ServerDatabaseBean.Category;
import com.example.yischool.Bean.ServerDatabaseBean.Commodity;
import com.example.yischool.Bean.jsonBean.CommodityCardBean;
import com.example.yischool.Utils.NetworkUtils;
import com.example.yischool.ViewHelper.CommodityRecyclerHelper;
import com.example.yischool.bmobNet.QueryHelper;
import com.example.yischool.customview.EditTextView;
import com.example.yischool.service.CommodityCardSearchService;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;

import static com.example.yischool.SearchActivity.SEARCH_CONTENT;

/**
 * @author 张云天
 * date on 2019/5/17
 * describe: 搜索结果展示页
 */
public class SearchResultActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String LOG_TAG = "SearchResultActivity";

    //综合排序
    public static final String ORDER_SYNTHESIZE = "browseCount";
    public static final String ORDER_RECENTLY = "";
    public static final String ORDER_NEWEST = "createdAt";
    public static final String ORDER_NONE = "none";

    //查询参数key
    public static final String KEY_ORDER = "synthesize_order";
    public static final String KEY_SKIP_COUNT = "skip_counts";
    public static final String KEY_PRICE = "price";

    /**
     * 布局相关
     */
    private DrawerLayout drawerLayout;
    private ImageView backImgButton;
    private EditText searchEditText;
    private Button orderSynthesizeButton;
    private Button orderPriceButton;
    private Button orderAreaButton;
    private Button filterButton;
    private RecyclerView commodityDisplayRecyclerView;
    private FloatingActionButton floatingButton;
    private TextView priceAreaTextView;
    private EditText lowPriceEditView;
    private TextView lineText;
    private EditText highPriceEditView;
    private TextView publishTimeTextView;
    private RadioGroup publishTimeRadioGroup;
    private RadioButton threeRadioButton;
    private RadioButton sevenRadioButton;
    private RadioButton thirtyRadioButton;
    private TextView exchangeMeansTextView;
    private CheckBox takeCheck;
    private CheckBox faceCheck;
    private CheckBox mailCheck;
    private Button resetButton;
    private Button confirmButon;
    private ViewGroup loadingHintLayout;//include
    private ViewStub networkErrorViewStub;
    private ViewStub emptyContentViewStub;
    private RelativeLayout networkErrorLayout;
    private RelativeLayout emptyContentLayout;


    //商品展示卡片帮助类
    private CommodityRecyclerHelper recyclerHelper;
    //商品展示卡片数据源
    private HashSet<CommodityCardBean> cardHashSet;


    //线程通信
    private static Handler handler;
    //启动搜索服务（传递当前查询条件）
    private Intent startServiceIntent;
    //服务通信
    private CommodityCardSearchService.MyBinder myBinder;
    private ServiceConnection connection;
    //搜索内容
    private String searchContent;
    //当前查询是否还有数据；
    private boolean isHaveData = false;
    //当前查询已加载数据数量 (查询前跳过的数据数据)
    private int queryDataCount = 0;

    //综合排序，弹出框
    private PopupWindow orderPopupWindow;
    //综合排序，ListView
    private ListView orderListView;
    //综合排序，ListView适配器
    private ArrayAdapter orderListAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //布局控件
        drawerLayout = findViewById(R.id.drawer_layout);
        backImgButton = findViewById(R.id.back_img_button);
        searchEditText = findViewById(R.id.search_edit_text);
        orderSynthesizeButton = findViewById(R.id.order_synthesize_button);
        orderPriceButton = findViewById(R.id.order_price_button);
        orderAreaButton = findViewById(R.id.order_area_button);
        filterButton = findViewById(R.id.filter_button);
        commodityDisplayRecyclerView = findViewById(R.id.commodity_display_recycler_view);
        floatingButton = findViewById(R.id.floating_button);
        priceAreaTextView = findViewById(R.id.price_area_text_view);
        lowPriceEditView = findViewById(R.id.low_price_edit_view);
        lineText = findViewById(R.id.line_text);
        highPriceEditView = findViewById(R.id.high_price_edit_view);
        publishTimeTextView = findViewById(R.id.publish_time_text_view);
        publishTimeRadioGroup = findViewById(R.id.publish_time_radio_group);
        threeRadioButton = findViewById(R.id.three_radio_button);
        sevenRadioButton = findViewById(R.id.seven_radio_button);
        thirtyRadioButton = findViewById(R.id.thirty_radio_button);
        exchangeMeansTextView = findViewById(R.id.exchange_means_text_view);
        takeCheck = findViewById(R.id.take_check);
        faceCheck = findViewById(R.id.face_check);
        mailCheck = findViewById(R.id.mail_check);
        resetButton = findViewById(R.id.reset_button);
        confirmButon = findViewById(R.id.confirm_buton);
        networkErrorViewStub = findViewById(R.id.network_error_view_stub);
        emptyContentViewStub = findViewById(R.id.empty_content_view_stub);
        loadingHintLayout = findViewById(R.id.loading_progressbar);

        backImgButton.setOnClickListener(this);
        searchEditText.setOnClickListener(this);
        orderSynthesizeButton.setOnClickListener(this);
        orderPriceButton.setOnClickListener(this);
        orderAreaButton.setOnClickListener(this);
        filterButton.setOnClickListener(this);



        //初始化Handler
        handler = new MyHandler(SearchResultActivity.this);
        cardHashSet = new HashSet<>();
        searchContent = getIntent().getStringExtra(SEARCH_CONTENT);

        //设置搜索框内容
        searchEditText.setText(searchContent);

        //绑定活动
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                myBinder = (CommodityCardSearchService.MyBinder)service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        //绑定搜索服务
        Intent intent = new Intent(SearchResultActivity.this, CommodityCardSearchService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        //设置搜索结果展示页
        recyclerHelper = new CommodityRecyclerHelper(cardHashSet, SearchResultActivity.this, commodityDisplayRecyclerView);
        recyclerHelper.initRecyclerView();

        //传递搜索结果，开始查询
        startServiceIntent = new Intent(SearchResultActivity.this, CommodityCardSearchService.class);
        startServiceIntent.putExtra(SEARCH_CONTENT, searchContent);
        startServiceIntent.putExtra(KEY_ORDER, ORDER_SYNTHESIZE);

        //初始化时查询
        showProgressBar();//显示进度条
        if(!NetworkUtils.isNetworkConnected(this)){
            //没有网络连接
            closeProgressBar();
            showNetworkErrorLayout();

        }else {
            //启动服务查询
            startService(startServiceIntent);
        }

        //设置下拉监听事件
        recyclerHelper.setPullUpLoadingListener(new CommodityRecyclerHelper.OnPullUpLoadingListener() {
            @Override
            public void done() {
                //设置参数
                startServiceIntent.putExtra(KEY_SKIP_COUNT, cardHashSet.size());
                //继续查询
                startQuery();
            }
        });

        //初始化综合排序弹出框
        initPopupWindow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_button:
                //退出按钮
                finish();
                break;
            case R.id.filter_button:
                //筛选按钮
                drawerLayout.openDrawer(GravityCompat.END);
                break;
            case R.id.order_synthesize_button:
                //综合排序按钮
                orderPopupWindow.showAsDropDown(orderSynthesizeButton);
                break;
            case R.id.order_price_button:
                //价格排序按钮

                break;
            case R.id.order_area_button:
                //区域选择按钮按钮

                break;
            case R.id.search_edit_text:
                //搜索按钮(启动搜索活动)

                break;
            /*
             * 右侧侧滑栏相关
             */
            case R.id.reset_button:
                //重置按钮

                break;
            case R.id.confirm_buton:
                //确定按钮

                break;
            default:break;
        }
    }

    /**
     * 开始查询，界面操作，及逻辑功能
     */
    private void startQuery(){

        showProgressBar();//显示进度条

        if(!NetworkUtils.isNetworkConnected(this)){
            //没有网络连接
            closeProgressBar();
            showNetworkErrorLayout();

        }else {
            //启动服务查询
            startService(startServiceIntent);
            //FootView 重置
            recyclerHelper.recyclerLoadingHint();
        }
    }

    /**
     * 展示加载提示进度条
     */
    public void showProgressBar(){

        if(loadingHintLayout.getVisibility() == View.GONE){
            loadingHintLayout.setVisibility(View.VISIBLE);
        }
        
    }
    /**
     * 取消加载提示进度条
     */
    public void closeProgressBar(){

        if(loadingHintLayout.getVisibility() == View.VISIBLE){
            loadingHintLayout.setVisibility(View.GONE);
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
                    startQuery();//重新开始查询
                }
            });
            TextView textView = networkErrorLayout.findViewById(R.id.no_network_text_button);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    networkErrorLayout.setVisibility(View.GONE);
                    startQuery();//重新开始查询
                }
            });

        }catch (Exception e){

            if(networkErrorLayout == null){
                networkErrorLayout = findViewById(R.id.network_error_inflateId);
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
    /**
     * 展示空数据布局
     */
    public void showEmptyContentLayout(){

        try {//获取空数据布局
            emptyContentLayout = (RelativeLayout) emptyContentViewStub.inflate();//膨胀网络错误提示布局(此处错误提示布局可见)
        }catch (Exception e){
            if(emptyContentLayout == null){
                emptyContentLayout = findViewById(R.id.empty_content_inflateId);
            }
            if(emptyContentLayout.getVisibility()==View.GONE){
                emptyContentLayout.setVisibility(View.VISIBLE);//(已膨胀后的布局设置可见)
            }
        }
    }

    /**
     * 取消空数据布局
     */
    public void closeEmptyContentLayout(){

        if(networkErrorLayout != null){
            if(networkErrorLayout.getVisibility()==View.VISIBLE){
                networkErrorLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑服务
        if(connection != null){
            unbindService(connection);
        }
    }

    public static Handler getHandler(){
        return handler;
    }

    private void initPopupWindow(){

        //初始化列表数据
        orderListView = new ListView(SearchResultActivity.this);
        final String[] listString = new String[]{"综合排序", "离我最近", "最新发布"};
        orderListAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item, listString);
        orderListView.setAdapter(orderListAdapter);
        //点击某一条
        orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                //设置参数
                startServiceIntent.putExtra(KEY_SKIP_COUNT, 0);
                //重新查询
                startQuery();
            }
        });

        orderPopupWindow = new PopupWindow(this);

        orderPopupWindow.setContentView(orderListView);
        orderPopupWindow.setBackgroundDrawable(new ColorDrawable(0xFFFFFF));
        //解决popupWindow遮住输入法问题
        orderPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        orderPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        orderPopupWindow.setOutsideTouchable(true);
        orderPopupWindow.setFocusable(false);
    }

    static class MyHandler extends Handler {

        WeakReference<SearchResultActivity> weakReference;

        public MyHandler(SearchResultActivity activity){
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SearchResultActivity activity = weakReference.get();
            if(activity != null){
                if(msg.what == CommodityCardSearchService.RESULT_COMPLETED){

                    //关闭空数据界面
                    activity.closeEmptyContentLayout();

                    Log.d(LOG_TAG, "活动收到数据："+activity.myBinder.getResultCards().size());
                    int size = activity.myBinder.getResultCards().size();
                    if(size >= 20){
                        //数据加载完成，开始从Binder中取出数据
                        activity.cardHashSet.addAll(activity.myBinder.getResultCards());
                        //更新界面
                        activity.recyclerHelper.notifyDataChanged();

                    }else if(size < 20 && size > 0){
                        //数据加载完成，开始从Binder中取出数据
                        activity.cardHashSet.addAll(activity.myBinder.getResultCards());
                        //更新界面
                        activity.recyclerHelper.notifyDataChanged();
                        //FooterView更换显示完成
                        activity.recyclerHelper.recyclerLoadingComplete();
                    }else {
                        //搜索成功，但是无数据
                        activity.showEmptyContentLayout();
                        //FooterView更换显示完成
                        activity.recyclerHelper.recyclerLoadingComplete();
                    }
                    //关闭进度条
                    activity.closeProgressBar();
                }
            }
        }
    }
    
    
}

