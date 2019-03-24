package adapter;

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
import java.util.List;

import Bean.CommodityCardBean;

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
//        viewHolder.cardCommodityImageView.setImageDrawable();
    }

    @Override
    public int getItemCount() {
        return commodityCards.size();
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
