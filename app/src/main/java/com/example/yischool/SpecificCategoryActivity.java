package com.example.yischool;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yischool.mainFragment.HomePageFragment;

import org.raphets.roundimageview.RoundImageView;

public class SpecificCategoryActivity extends AppCompatActivity {


    private String categoryName; //当前活动由哪一个具体分类启动
    private String primaryCategoryName;


    private ImageView backgroundImageView;
    private TextView categoryNameTextView;
    private EditText searchButton;
    private RoundImageView firstCommodityImage;
    private RoundImageView secondCommodityImage;
    private ImageView firstBrandImage;
    private ImageView secondBrandImage;
    private ImageView thirdBrandImage;
    private ImageView fourthBrandImage;
    private ImageView fifthBrandImage;
    private ImageView sixthBrandImage;
    private View view1;
    private TextView hotRecommendText;
    private View view2;
    private RecyclerView recyclerView;
    private ImageView backButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_category);

        backgroundImageView = findViewById(R.id.background_image_view);
        categoryNameTextView = findViewById(R.id.category_name_text_view);
        searchButton = findViewById(R.id.search_button);
        firstCommodityImage = findViewById(R.id.first_commodity_image);
        secondCommodityImage = findViewById(R.id.second_commodity_image);
        firstBrandImage = findViewById(R.id.first_brand_image);
        secondBrandImage = findViewById(R.id.second_brand_image);
        thirdBrandImage = findViewById(R.id.third_brand_image);
        view1 = findViewById(R.id.view_1);
        hotRecommendText = findViewById(R.id.hot_recommend_text);
        view2 = findViewById(R.id.view_2);
        recyclerView = findViewById(R.id.recycler_view);
        backButton = findViewById(R.id.back_button);
        fourthBrandImage = findViewById(R.id.fourth_brand_image);
        fifthBrandImage = findViewById(R.id.fifth_brand_image);
        sixthBrandImage = findViewById(R.id.sixth_brand_image);


        categoryName = getIntent().getStringExtra(HomePageFragment.SPECIFIC_CATEGORY_KEY);

        setBackgroundImageView(categoryName);



    }

    /**
     * 根据种类更换界面背景
     * @param categoryName
     */
    private void setBackgroundImageView(String categoryName){

        if("手机".equals(categoryName)){

            backgroundImageView.setImageResource(R.drawable.specific_background_1);

        }else if("图书".equals(categoryName)){

            backgroundImageView.setImageResource(R.drawable.specific_background_5);


        }else if("服饰".equals(categoryName)){

            backgroundImageView.setImageResource(R.drawable.specific_background_2);


        }else if("数码".equals(categoryName)){

            backgroundImageView.setImageResource(R.drawable.specific_background_4);


        }else if("游戏".equals(categoryName)){

            backgroundImageView.setImageResource(R.drawable.specific_background_8);

        }else if("家居家电".equals(categoryName)){

            backgroundImageView.setImageResource(R.drawable.specific_background_6);

        }else if("二手车".equals(categoryName)){

            backgroundImageView.setImageResource(R.drawable.specific_background_9);
        }else if("生活百货".equals(categoryName)){

            backgroundImageView.setImageResource(R.drawable.specific_background_3);
        }else if("体育运动".equals(categoryName)){

            backgroundImageView.setImageResource(R.drawable.specific_background_10);
        }else if("服务".equals(categoryName)){

            backgroundImageView.setImageResource(R.drawable.specific_background_7);
        }
    }
}
