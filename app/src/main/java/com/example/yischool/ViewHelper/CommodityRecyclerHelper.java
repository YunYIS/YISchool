package com.example.yischool.ViewHelper;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yischool.R;

import java.util.HashSet;

import com.example.yischool.Bean.jsonBean.CommodityCardBean;
import com.example.yischool.adapter.CommodityRecyclerAdapter;

/**
 * @author 张云天
 * date on 2019/4/24
 * describe: 商品展示卡片布局列表（两列卡片布局）
 */
public class CommodityRecyclerHelper {

    private HashSet<CommodityCardBean> commodityCards;
    private GridLayoutManager layoutManager;
    private CommodityRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private Context context;

    private OnPullUpLoadingListener listener;

    public CommodityRecyclerHelper(HashSet<CommodityCardBean> commodityCards, Context context, RecyclerView recyclerView){
        this.commodityCards = commodityCards;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    /**
     * 初始化适配器，和监听事件
     */
    public void initRecyclerView(){

        adapter = new CommodityRecyclerAdapter(context, commodityCards);
        //设置卡片布局，两列
        layoutManager = new GridLayoutManager(context, 2);

        //设置FootView(最后一行) 跨两列
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == adapter.getItemCount() - 1 ? 2 : 1;
            }
        });
        //是否启用平滑滚动条
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setHasFixedSize(true);

        //设置两列Grid布局管理器
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //设置上拉加载，监听RecyclerView滑动事件
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastPosition = -1;
                //当前状态为停止滑动状态SCROLL_STATE_IDLE
                if(newState == RecyclerView.SCROLL_STATE_IDLE){

                    //过LayoutManager找到当前显示的最后的item的position
                    RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                    if(manager instanceof GridLayoutManager){
                        lastPosition = ((GridLayoutManager)manager).findLastVisibleItemPosition();
                    }
                    //此时，RecyclerView滑动到最后
                    if(lastPosition == recyclerView.getLayoutManager().getItemCount() - 1){
                        //执行滑动到底部事件
                        if(listener != null){
                            listener.done();
                        }
                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    /**
     * 通知数据更改，布局刷新
     */
    public void notifyDataChanged(){

        adapter.notifyDataSetChanged();
    }

    /**
     * 设置使用NestedScrollingView 解决滑动冲突
     */
    public void setNestedScrollingChild(){

        recyclerView.setNestedScrollingEnabled(false);
    }

    /**
     * 设置上拉（至底部）加载监听器
     * @param listener
     */
    public void setPullUpLoadingListener(OnPullUpLoadingListener listener){
        this.listener = listener;
    }
    /**
     * 上拉（至底部）加载监听器接口
     */
    public interface OnPullUpLoadingListener{

        void done();
    }

    /**
     * RecyclerView 底部提示正在加载
     */
    public void recyclerLoadingHint(){

        View footView = adapter.getFootView();

        TextView textView = footView.findViewById(R.id.load_hint_text);
        textView.setText("加载中...");

        View progressView = footView.findViewById(R.id.loading_progressbar);
        if(progressView.getVisibility() == View.GONE){
            progressView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * RecyclerView 底部提示没有数据
     */
    public void recyclerLoadingComplete(){

        //获取FooterView，更换显示
        View footView = adapter.getFootView();

        TextView textView = footView.findViewById(R.id.load_hint_text);
        textView.setText("没有更多了");

        View progressView = footView.findViewById(R.id.loading_progressbar);
        if(progressView.getVisibility() == View.VISIBLE){
            progressView.setVisibility(View.GONE);
        }
    }
}
