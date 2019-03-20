package com.example.yischool;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import customview.ImgTextButton;

public class MainActivity extends AppCompatActivity{

    private TabLayout tabLayout;
    private Fragment fragment = HomePageFragment.newInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        //进入MainActivity添加HomePageFragment
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
        //初始化tabLayout，并添加下方tab键
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setCustomView(new ImgTextButton(this).setText("首页").setImage(R.drawable.ic_home_page)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(new ImgTextButton(this).setText("消息").setImage(R.drawable.ic_communication)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(new ImgTextButton(this).setText("发布").setImage(R.drawable.ic_publish)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(new ImgTextButton(this).setText("购物车").setImage(R.drawable.ic_shopping_cart)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(new ImgTextButton(this).setText("个人").setImage(R.drawable.ic_person)));

//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_page).setText("首页"));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_communication).setText("消息"));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_publish).setText("发布"));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_shopping_cart).setText("购物车"));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_person).setText("个人"));
        //监听事件
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        fragment = HomePageFragment.newInstance();
                        break;
                    case 1:
                        fragment = CommunicativeFragment.newInstance();
                        break;
                    case 2:
                        fragment = PublishFragment.newInstance();
                        break;
                    case 3:
                        fragment = ShoppingCartFragment.newInstance();
                        break;
                    case 4:
                        fragment = PersonalFragment.newInstance();
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
