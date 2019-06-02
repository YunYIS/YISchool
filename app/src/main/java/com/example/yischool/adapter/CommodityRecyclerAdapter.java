package com.example.yischool.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yischool.CommodityDetailsActivity;
import com.example.yischool.R;

import java.util.HashSet;
import java.util.Iterator;

import com.example.yischool.Bean.jsonBean.CommodityCardBean;

/**
 * @author 张云天
 * date on 2019/5/19
 * describe: 商品卡片RecyclerView适配器
 * 支持footerView显示 正在加载 或 无更多数据
 */
public class CommodityRecyclerAdapter extends RecyclerView.Adapter {

    //viewType
    public static final int NORMAL_VIEW = 1;
    public static final int FOOT_VIEW = 2;


    private Context context;
    private HashSet<CommodityCardBean> commodityCards;
    private LinearLayout loadingHintLayout;


    public CommodityRecyclerAdapter(Context context, HashSet<CommodityCardBean> commodityCards){
        this.context = context;
        this.commodityCards = commodityCards;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == FOOT_VIEW){
            View v = LayoutInflater.from(context).inflate(R.layout.pullup_loading_hint_layout, parent, false);
            loadingHintLayout = (LinearLayout) v;
            return new ViewHolder(loadingHintLayout, viewType);
        }else {
            View v = LayoutInflater.from(context).inflate(R.layout.recycler_commodity_item_card, parent, false);
            return new ViewHolder(v, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) == NORMAL_VIEW){

            ViewHolder viewHolder = (ViewHolder)holder;
            final CommodityCardBean commodityCardBean = (CommodityCardBean) getElement(commodityCards, position);

            Glide.with(context).load(commodityCardBean.getCommodityImgUrl())
                    .error(R.drawable.image_error).into(viewHolder.cardCommodityImageView);//只是模拟

            viewHolder.cardTitleTextView.setText(commodityCardBean.getTitle());
            viewHolder.cardPriceTextView.setText("￥"+String.valueOf(commodityCardBean.getPrice()));

            Glide.with(context).load(commodityCardBean.getAccountPhotoUrl())
                    .error(R.drawable.image_error).into(viewHolder.cardPhotoImageView);

            viewHolder.cardAccountTextView.setText(commodityCardBean.getAccountName());
            viewHolder.cardCollectTextView.setText(commodityCardBean.getCollectNumber()+"人收藏");

            //点击事件,启动商品详情页
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommodityDetailsActivity.class);
                    intent.putExtra("commodityId", commodityCardBean.getCommodityID());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return commodityCards.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == getItemCount() - 1){
            return FOOT_VIEW;
        }else {
            return NORMAL_VIEW;
        }
    }

    /**
     * 访问HashSet中position位置的元素
     * @param set
     * @param position
     * @return
     */
    private Object getElement(HashSet set, int position){
        Object retObj = null;
        Iterator iterator = set.iterator();
        for(int i = 0; i < position; i++){
            iterator.next();
        }
        if(iterator.hasNext()){
            retObj = iterator.next();
        }
        return retObj;
    }

    public View getFootView(){
        return loadingHintLayout;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView cardCommodityImageView;
        private TextView cardTitleTextView;
        private TextView cardPriceTextView;
        private TextView cardCollectTextView;
        private ImageView cardPhotoImageView;
        private TextView cardAccountTextView;

        private LinearLayout loadingHintLayout;

        public ViewHolder(View itemView, int viewType){
            super(itemView);
            if(viewType == FOOT_VIEW){
                //加载提示布局 pullup_loading_hint_layout.xml
                loadingHintLayout = (LinearLayout) itemView;

            }else {
                //加载普通布局
                cardCommodityImageView = itemView.findViewById(R.id.card_commodity_image_view);
                cardTitleTextView = itemView.findViewById(R.id.card_title_text_view);
                cardPriceTextView = itemView.findViewById(R.id.card_price_text_view);
                cardCollectTextView = itemView.findViewById(R.id.card_collect_text_view);
                cardPhotoImageView = itemView.findViewById(R.id.card_photo_image_view);
                cardAccountTextView = itemView.findViewById(R.id.card_account_text_view);
            }
        }
    }
}
