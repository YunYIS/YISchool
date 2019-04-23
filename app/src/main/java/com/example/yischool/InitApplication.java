package com.example.yischool;

import android.content.Context;

import org.litepal.LitePalApplication;

import Bean.ServerDatabaseBean.User;
import cn.bmob.v3.Bmob;

public class InitApplication extends LitePalApplication {

    private static Context context;
    private static User currentUser = null;//当前登陆用户

    @Override
    public void onCreate() {
        super.onCreate();
        context = getContext();
        Bmob.initialize(this, "d9c46a4e0dbb4be72ae50f8aa9c5e122");
    }
    /**
     * 获得一个应用程序级别的Context
     * @return context
     */
    public static Context getAppContext(){
        return context;
    }
    /**
     * 获取当前已登录的用户实体
     * @return 已登录：用户； 未登录：null
     */
    public static User getCurrentUser(){
        return currentUser;
    }
    /**
     * 登陆时设置当前用户实体
     * @param user
     */
    public static void setCurrentUser(User user){
        currentUser = user;
    }
}
