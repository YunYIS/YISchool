package com.example.yischool;

import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

import org.litepal.LitePalApplication;

import com.example.yischool.Bean.ServerDatabaseBean.User;
import com.example.yischool.session.MyMessageHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.Bmob;

public class InitApplication extends LitePalApplication {

    private static Context context;
    private static User currentUser = null;//当前登陆用户

    @Override
    public void onCreate() {
        super.onCreate();
        context = getContext();
        Bmob.initialize(this, "d9c46a4e0dbb4be72ae50f8aa9c5e122");
        SDKInitializer.initialize(getApplicationContext());
        //TODO 集成：1.8、初始化IM SDK，并注册消息接收器
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            BmobIM.init(this);
            BmobIM.registerDefaultMessageHandler(new MyMessageHandler());
        }
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

    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
