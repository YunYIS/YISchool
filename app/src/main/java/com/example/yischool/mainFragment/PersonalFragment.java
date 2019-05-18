package com.example.yischool.mainFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.yischool.R;
import com.example.yischool.adapter.MenuListAdapter;
import com.example.yischool.customview.ImgTextButton;
import com.example.yischool.personal.UserPagerActivity;

import org.raphets.roundimageview.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import static com.example.yischool.Utils.ListViewUtils.setListViewHeight;

public class PersonalFragment extends Fragment implements View.OnClickListener{

    private static PersonalFragment personalFragment;

    private ImageView setButton;
    private TextView titleUserNameText;
    private RoundImageView userPortraitImage;
    private TextView userNameText;
    private Button userPagerButton;
    private ListView listView;
    private ImgTextButton buyButton;
    private ImgTextButton collectionButton;
    private ImgTextButton browseButton;
    private ImgTextButton saleButton;
    private ImgTextButton publishHistoryButton;
    private ImgTextButton cancelSaleButton;
    private ImgTextButton evaluateButton;
    private ImgTextButton leaveWordsButton;
    private ScrollView scrollView;

    //ListView每项数据
    private final String[] strings = new String[]{"防骗指南", "实人认证", "平台规则", "意见反馈"};
    private final int[] icons = new int[]{R.drawable.ic_guide, R.drawable.ic_authentication,
            R.drawable.ic_rules, R.drawable.ic_feedback};
    private MenuListAdapter adapter;
    private List<View> listData;

    public static PersonalFragment newInstance(){
        if(personalFragment == null){
            personalFragment = new PersonalFragment();
        }
        return personalFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        //控件
        setButton = view.findViewById(R.id.set_button);
        titleUserNameText = view.findViewById(R.id.title_user_name_text);
        userPortraitImage = view.findViewById(R.id.user_portrait_image);
        userNameText = view.findViewById(R.id.user_name_text);
        userPagerButton = view.findViewById(R.id.user_pager_button);
        listView = view.findViewById(R.id.list_view);
        buyButton = view.findViewById(R.id.buy_button);
        collectionButton = view.findViewById(R.id.collection_button);
        browseButton = view.findViewById(R.id.browse_button);
        saleButton = view.findViewById(R.id.sale_button);
        publishHistoryButton = view.findViewById(R.id.publish_history_button);
        cancelSaleButton = view.findViewById(R.id.cancel_sale_button);
        evaluateButton = view.findViewById(R.id.evaluate_button);
        leaveWordsButton = view.findViewById(R.id.leave_words_button);
        scrollView = view.findViewById(R.id.scroll_view);

        setButton.setOnClickListener(this);
        userPortraitImage.setOnClickListener(this);
        userNameText.setOnClickListener(this);
        userPagerButton.setOnClickListener(this);
        buyButton.setOnClickListener(this);
        collectionButton.setOnClickListener(this);
        browseButton.setOnClickListener(this);
        saleButton.setOnClickListener(this);
        publishHistoryButton.setOnClickListener(this);
        cancelSaleButton.setOnClickListener(this);
        evaluateButton.setOnClickListener(this);
        leaveWordsButton.setOnClickListener(this);


        /**
         * 取消scrollView滑动顶部，底部动画
         */
        if(android.os.Build.VERSION.SDK_INT >=9){
            scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        //List数据源
        listData = new ArrayList<>();
        for(int i = 0; i < strings.length; i++){
            LinearLayout itemView = (LinearLayout) inflater.inflate(R.layout.menu_list_item, null, false);
            //装载每项数据
            ((ImageView)itemView.findViewById(R.id.item_icon_view)).setImageResource(icons[i]);
            ((TextView)itemView.findViewById(R.id.item_text_view)).setText(strings[i]);
            listData.add(itemView);
        }
        //适配器
        adapter = new MenuListAdapter(listData);
        listView.setAdapter(adapter);
        setListViewHeight(listView, adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //防骗指南

                        break;
                    case 1:
                        //实人认证

                        break;
                    case 2:
                        //平台规则

                        break;
                    case 3:
                        //意见反馈

                        break;
                    default:break;
                }
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.set_button:
                //设置界面


                break;
            case R.id.user_portrait_image:
            case R.id.user_name_text:
            case R.id.user_pager_button:
                //进入个人资料页面
                Intent intent2 = new Intent(getActivity(), UserPagerActivity.class);
                startActivity(intent2);
                break;
            case R.id.buy_button:
                //进入已购买页面

                break;
            case R.id.collection_button:
                //进入收藏页面

                break;
            case R.id.browse_button:
                //进入浏览历史页面

                break;
            case R.id.sale_button:
                //进入已卖出页面

                break;
            case R.id.publish_history_button:
                //进入发布历史页面

                break;
            case R.id.cancel_sale_button:
                //进入已下架页面

                break;
            case R.id.evaluate_button:
                //进入评价页面

                break;
            case R.id.leave_words_button:
                //进入留言页面

                break;
            default:break;



        }
    }
}
