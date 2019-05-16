package com.example.yischool.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yischool.R;

import java.util.HashSet;
import java.util.Iterator;

import com.example.yischool.Bean.jsonBean.CommodityCardBean;

public class CommodityRecyclerAdapter extends RecyclerView.Adapter {

    private Context context;
    private HashSet<CommodityCardBean> commodityCards;

    public CommodityRecyclerAdapter(Context context, HashSet<CommodityCardBean> commodityCards){
        this.context = context;
        this.commodityCards = commodityCards;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_commodity_item_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        CommodityCardBean commodityCardBean = (CommodityCardBean) getElement(commodityCards, position);
        viewHolder.cardCommodityImageView.setImageResource(Integer.valueOf(commodityCardBean.getCommodityImgUrl()));//只是模拟
        viewHolder.cardTitleTextView.setText(commodityCardBean.getTitle());
        viewHolder.cardPriceTextView.setText("￥"+String.valueOf(commodityCardBean.getPrice()));
        viewHolder.cardPhotoImageView.setImageResource(Integer.valueOf(commodityCardBean.getAccountPhotoUrl()));
        viewHolder.cardAccountTextView.setText(commodityCardBean.getAccountName());
        viewHolder.cardCollectTextView.setText(commodityCardBean.getCollectNumber()+"人收藏");
    }

    @Override
    public int getItemCount() {
        return commodityCards.size();
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

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView cardCommodityImageView;
        private TextView cardTitleTextView;
        private TextView cardPriceTextView;
        private TextView cardCollectTextView;
        private ImageView cardPhotoImageView;
        private TextView cardAccountTextView;
        
        public ViewHolder(View itemView){
            super(itemView);
            cardCommodityImageView = itemView.findViewById(R.id.card_commodity_image_view);
            cardTitleTextView = itemView.findViewById(R.id.card_title_text_view);
            cardPriceTextView = itemView.findViewById(R.id.card_price_text_view);
            cardCollectTextView = itemView.findViewById(R.id.card_collect_text_view);
            cardPhotoImageView = itemView.findViewById(R.id.card_photo_image_view);
            cardAccountTextView = itemView.findViewById(R.id.card_account_text_view);
        }
    }

}
