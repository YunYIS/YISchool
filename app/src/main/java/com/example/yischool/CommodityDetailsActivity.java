package com.example.yischool;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.yischool.Bean.ServerDatabaseBean.Commodity;
import com.example.yischool.Bean.ServerDatabaseBean.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raphets.roundimageview.RoundImageView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class CommodityDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String LOG_TAG = "CommodityDetailActivit";


    private ImageView backButton;
    private TextView titleTextView;
    private TextView priceTextView;
    private TextView detailTextView;
    private LinearLayout displayPhotosLayout;
    private RoundImageView circleHeadImage;
    private TextView userNameText;
    private TextView commodityNumberTextView;
    private TextView saleNumberTextView;
    private TextView createdNumberTextView;
    private Button buyButton;
    private Button sendMessageButton;
    private CheckBox collectionCheckedButton;
    private ViewGroup loadingLayout;

    private String commodityId;
    private Commodity mCommodity;
    private User seller;//卖家
    private int sellingCount;//在售商品数量
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_details);

        backButton = findViewById(R.id.back_button);
        titleTextView = findViewById(R.id.title_text_view);
        priceTextView = findViewById(R.id.price_text_view);
        detailTextView = findViewById(R.id.detail_text_view);
        displayPhotosLayout = findViewById(R.id.display_photos_layout);
        circleHeadImage = findViewById(R.id.circle_head_image);
        userNameText = findViewById(R.id.user_name_text);
        commodityNumberTextView = findViewById(R.id.commodity_number_text_view);
        saleNumberTextView = findViewById(R.id.sale_number_text_view);
        createdNumberTextView = findViewById(R.id.created_number_text_view);
        buyButton = findViewById(R.id.buy_button);
        sendMessageButton = findViewById(R.id.send_message_button);
        collectionCheckedButton = findViewById(R.id.collection_checked_button);
        loadingLayout = findViewById(R.id.loading_progressbar);

        if(getIntent().hasExtra("commodityId")){
            commodityId = getIntent().getStringExtra("commodityId");
            //启动商品详情查询
            queryCommodityDetail();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_button:
                finish();
                break;
            case R.id.send_message_button:
                //启动聊天

                break;

        }
    }

    /**
     * 查询单条商品表数据，商品详情
     */
    private void queryCommodityDetail(){

        BmobQuery<Commodity> commodityQuery = new BmobQuery<>();
        commodityQuery.getObject(commodityId, new QueryListener<Commodity>() {
            @Override
            public void done(Commodity commodity, BmobException e) {
                if(e == null){
                    //查询成功
                    mCommodity = commodity;
                    Log.d(LOG_TAG, mCommodity.getPublishUser().getObjectId());
                    if(loadingLayout.getVisibility() == View.VISIBLE){
                        loadingLayout.setVisibility(View.GONE);
                    }
                    //查询卖家
                    queryUser();
                }
            }
        });
    }

    /**
     * 查询卖家名片
     */
    public void queryUser(){
        BmobQuery<User> userQuery = new BmobQuery<>();
        userQuery.addQueryKeys("objectId,username,headPortrait,createdAt");
        userQuery.getObject(mCommodity.getPublishUser().getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {

                if(e == null){
                    seller = user;

                    //统计查询卖家在售商品数量
                    BmobQuery<Commodity> commodityQuery = new BmobQuery<>();
                    commodityQuery.groupby(new String[]{"publishUser"});
                    commodityQuery.addWhereEqualTo("publishUser",seller);
                    commodityQuery.setHasGroupCount(true);
                    commodityQuery.findStatistics(Commodity.class, new QueryListener<JSONArray>() {
                        @Override
                        public void done(JSONArray jsonArray, BmobException e) {
                            try {
                                Log.d(LOG_TAG, jsonArray.toString());
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                sellingCount = jsonObject.getInt("_count");
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            //开始设置控件内数据资源
                            setViewData();
                        }
                    });
                }
            }
        });


    }
    /**
     * 设置控件内数据资源
     */
    public void setViewData(){

        if(mCommodity != null){
            titleTextView.setText(mCommodity.getTitle());
            detailTextView.setText(mCommodity.getDetail());
            priceTextView.setText(mCommodity.getPrice().toString());
            userNameText.setText(seller.getUsername());
            commodityNumberTextView.setText(String.valueOf(sellingCount));
//            createdNumberTextView.setText(seller.getCreatedAt());

            //加载商品图片
            for(BmobFile file : mCommodity.getPictureAndVideo()){
                ImageView imageView = new ImageView(this);

                Glide.with(this).load(file.getUrl()).into(imageView);
                displayPhotosLayout.addView(imageView);
            }
            //加载头像
            Glide.with(this).load(seller.getHeadPortrait().getUrl()).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    circleHeadImage.setImageDrawable(resource);
                }
            });
        }
    }
}
