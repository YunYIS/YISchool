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
import com.example.yischool.mainFragment.PublishFragment;
import com.example.yischool.mainFragment.ShoppingCartFragment;
import com.example.yischool.mainFragment.LoginHintFragment;

import customview.ImgTextButton;

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
            //没有选择则返回-1
            fragment = fragments[tabLayout.getSelectedTabPosition()==-1 ? 0 : tabLayout.getSelectedTabPosition()];
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
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
                new ImgTextButton(this).setText("消息").setIsTouch(false).setImage(R.drawable.ic_communication)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(
                new ImgTextButton(this).setText("发布").setIsTouch(false).setImage(R.drawable.ic_publish)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(
                new ImgTextButton(this).setText("购物车").setIsTouch(false).setImage(R.drawable.ic_shopping_cart)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(
                new ImgTextButton(this).setText("个人").setIsTouch(false).setImage(R.drawable.ic_person)));

        fragments = new Fragment[5];
        fragments[0] = HomePageFragment.newInstance();
        fragments[1] = CommunicativeFragment.newInstance();
        fragments[2] = PublishFragment.newInstance();
        fragments[3] = ShoppingCartFragment.newInstance();
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
                    case 2:
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
}
