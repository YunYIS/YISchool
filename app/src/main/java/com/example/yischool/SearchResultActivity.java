package com.example.yischool;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.yischool.Bean.ServerDatabaseBean.Category;
import com.example.yischool.Bean.ServerDatabaseBean.Commodity;
import com.example.yischool.Bean.jsonBean.CommodityCardBean;
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

//    public static final int

    /**
     * 布局相关
     */
    private DrawerLayout drawerLayout;
    private ImageView backImgButton;
    private EditTextView searchEditText;
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

    //商品展示卡片帮助类
    private CommodityRecyclerHelper recyclerHelper;
    //商品展示卡片数据源
    private HashSet<CommodityCardBean> cardHashSet;



    private static Handler handler;

    private CommodityCardSearchService.MyBinder myBinder;

    private ServiceConnection connection;

    private String searchContent;


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

        Intent intent = new Intent(SearchResultActivity.this, CommodityCardSearchService.class);
        intent.putExtra(SEARCH_CONTENT, searchContent);
        bindService(intent, connection, BIND_AUTO_CREATE);

        recyclerHelper = new CommodityRecyclerHelper(cardHashSet, SearchResultActivity.this, commodityDisplayRecyclerView);
        recyclerHelper.startCardRecycler();

        startService(intent);//开始查询

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_button:
                //退出按钮

                break;
            case R.id.filter_button:
                //筛选按钮

                break;
            case R.id.order_synthesize_button:
                //综合排序按钮

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

                    //数据加载完成，开始从Binder中取出数据
                    activity.cardHashSet = activity.myBinder.getResultCards();
                    activity.recyclerHelper.notifyDataChanged();

                }

            }
        }

    }
}

