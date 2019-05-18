package com.example.yischool.personal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yischool.R;
import com.example.yischool.adapter.MenuListAdapter;
import com.example.yischool.bmobNet.QueryHelper;
import com.example.yischool.customview.BanSlidingListView;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.raphets.roundimageview.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import static com.example.yischool.Utils.ListViewUtils.setListViewHeight;

/**
 * @author 张云天
 * date on 2019/5/16
 * describe: 个人资料页
 */
public class UserPagerActivity extends AppCompatActivity {

    private final String[] goalStrings = new String[]{"个性签名", "学校", "专业","生日", "邮箱", "注册日期"};

    private ImageView backgroundImageView;
    private ImageView backButton;
    private RoundImageView circleHeadImage;
    private ImageView changePortraitButton;
    private TextView userNameText;
    private ImageView sexIconView;
    private ListView listView;
    private TextView hobbiesLabel;
    private TagFlowLayout labelDisplayLayout;
    private Button moreInfoButton;

    
    private List<View> listData;
    private MenuListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pager);

        backgroundImageView = findViewById(R.id.background_image_view);
        backButton = findViewById(R.id.back_button);
        circleHeadImage = findViewById(R.id.circle_head_image);
        changePortraitButton = findViewById(R.id.change_portrait_button);
        userNameText = findViewById(R.id.user_name_text);
        sexIconView = findViewById(R.id.sex_icon_view);
        listView =  findViewById(R.id.list_view);
        hobbiesLabel = findViewById(R.id.hobbies_label);
        labelDisplayLayout = findViewById(R.id.label_display_layout);
        moreInfoButton = findViewById(R.id.more_info_button);

        //List数据源
        listData = new ArrayList<>();
        for(int i = 0; i < goalStrings.length; i++){
            LinearLayout itemView = (LinearLayout) LayoutInflater.from(UserPagerActivity.this)
                    .inflate(R.layout.message_list_item, null, false);
            //装载每项数据
            ((TextView)itemView.findViewById(R.id.item_text_view)).setText(goalStrings[i]);
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
                        //个性签名

                        break;
                    case 1:
                        //学校

                        break;
                    case 2:
                        //专业

                        break;
                    case 3:
                        //生日

                        break;
                    case 4:
                        //邮箱

                        break;
                    default:break;
                }
            }
        });


    }
}
