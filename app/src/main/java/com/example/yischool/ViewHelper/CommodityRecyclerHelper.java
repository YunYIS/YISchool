package com.example.yischool.ViewHelper;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.yischool.R;

import java.util.HashSet;

import com.example.yischool.Bean.jsonBean.CommodityCardBean;
import com.example.yischool.adapter.CommodityRecyclerAdapter;

public class CommodityRecyclerHelper {

    private HashSet<CommodityCardBean> commodityCards;
    private GridLayoutManager layoutManager;
    private CommodityRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private Context context;

    public CommodityRecyclerHelper(Context context, RecyclerView recyclerView){
        this.context = context;
        this.recyclerView = recyclerView;
    }

    /**
     * 初始化数据源
     */
    private void initData(){
        //TODO 从网络访问数据，现在只是模拟
        commodityCards = new HashSet<>();
        CommodityCardBean c = null;
        for(int i = 0; i < 16; i++){
            c = new CommodityCardBean.Builder()
                    .setCommodityImgUrl(""+R.drawable.recycler_test_1)
                    .setAccountPhotoUrl(""+R.drawable.account_img_test)
                    .setTitle("测试商品的标题(商品标题一般都是两排，所以很长)")
                    .setAccountName("测试卖家账户名")
                    .setCollectNumber(i)
                    .setPrice(10.99)
                    .build();
            commodityCards.add(c);
        }
    }

    /**
     * 初始化适配器，和监听事件
     */
    private void initListener(){
        adapter = new CommodityRecyclerAdapter(context, commodityCards);
        layoutManager = new GridLayoutManager(context, 2);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void startCardRecycler(){
        initData();
        initListener();
    }

}
