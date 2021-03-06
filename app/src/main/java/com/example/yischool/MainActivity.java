package com.example.yischool;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.yischool.mainFragment.CommunicativeFragment;
import com.example.yischool.mainFragment.HomePageFragment;
import com.example.yischool.mainFragment.PersonalFragment;
import com.example.yischool.mainFragment.LoginHintFragment;
import com.example.yischool.publish.PublishActivity;

import com.example.yischool.customview.ImgTextButton;

import cn.bmob.newim.BmobIM;

public class MainActivity extends AppCompatActivity{

    public static final String LOG_TAG = "MainActivity";

    private TabLayout tabLayout;
    private Fragment fragment = HomePageFragment.newInstance();//动态加载碎片
    private Fragment[] fragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "onCreate exe");
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(InitApplication.getCurrentUser() != null){
            //tabLayout没有选择时，则返回-1
            int index = tabLayout.getSelectedTabPosition()==-1 ? 0 : tabLayout.getSelectedTabPosition();
            if(index != 2 && index != 1){
                fragment = fragments[index];
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
            }
        }
    }
    /**
     * 初始化操作
     */
    private void init(){
        //进入MainActivity添加HomePageFragment
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
        //初始化tabLayout，并添加下方tab键
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setCustomView(
                new ImgTextButton(this).setText("首页").setIsTouch(false).setImage(R.drawable.ic_home_page)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(
                new ImgTextButton(this).setText("分类").setIsTouch(false).setImage(R.drawable.ic_all_category)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(
                new ImgTextButton(this).setText("发布").setIsTouch(false).setImage(R.drawable.ic_publish)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(
                new ImgTextButton(this).setText("消息").setIsTouch(false).setImage(R.drawable.ic_communication)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(
                new ImgTextButton(this).setText("个人").setIsTouch(false).setImage(R.drawable.ic_person)));

        fragments = new Fragment[5];
        fragments[0] = HomePageFragment.newInstance();
        fragments[1] = null;
        fragments[2] = null;
        fragments[3] = CommunicativeFragment.newInstance();
        fragments[4] = PersonalFragment.newInstance();
        //监听事件
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        fragment = fragments[0];//首页（不需要登陆）
                        break;
                    case 1:
                        Intent intent1 = new Intent(MainActivity.this, AllCategoryActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        if(InitApplication.getCurrentUser() == null){//当前没有用户登陆
                            fragment = LoginHintFragment.newInstance();
                        }else{//用户已登录
                            Intent intent2 = new Intent(MainActivity.this, PublishActivity.class);
                            startActivity(intent2);
                        }
                        break;
                    case 3:
                    case 4:
                        if(InitApplication.getCurrentUser() == null){//当前没有用户登陆
                            fragment = LoginHintFragment.newInstance();
                        }else{//用户已登录
                            fragment = fragments[tab.getPosition()];
                        }
                        break;
                    default:break;
                }
                if(fragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy()");
        //断开IM服务器连接
        BmobIM.getInstance().disConnect();
    }
}
