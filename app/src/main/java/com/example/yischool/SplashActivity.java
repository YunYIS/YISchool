package com.example.yischool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.yischool.Bean.ServerDatabaseBean.User;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class SplashActivity extends AppCompatActivity {

    public static final int SPLASH_TIME = 2800;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //自动登录
        SharedPreferences sharedPreferences = getSharedPreferences("LoginUserData", MODE_PRIVATE);
        Log.d("SplashActivity", "sharedPreferences userName"+
                sharedPreferences.contains("userName")+sharedPreferences.contains("password"));

        if(sharedPreferences.contains("userName") && sharedPreferences.contains("password")){
            String phone = sharedPreferences.getString("mobilePhone","");
            String password = sharedPreferences.getString("password", "");
            Log.d("SplashActivity", "userName:"+phone + " " +password);
            BmobUser.loginByAccount(phone, password, new LogInListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if(user != null){
                        if (e == null) {
                            InitApplication.setCurrentUser(user);
                            Log.d("SplashActivity", "登录成功");
                            Log.d("SplashActivity", "currUser:"+InitApplication.getCurrentUser().getUsername());
                        } else {
                            Log.d("SplashActivity", "自动登录失败");
                        }
                    }
                }
            });
        }

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, SPLASH_TIME);



    }
}
