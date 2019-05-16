package com.example.yischool.Utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author 张云天
 * date on 2019/5/15
 * describe: http请求帮助类
 */
public class HttpUtils {

    /**
     * 发送http请求
     * @param address
     * @param callback
     */
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        //创建一个OkHttpClient实例
        OkHttpClient client = new OkHttpClient();
        //如果想要发起一条HTTP请求，就需要创建一个Request对象
        Request request = new Request.Builder().url(address).build();
        /**
         * 调用OkHttpClient实例的newCall()方法来创建一个Call对象，并调用它的execute()方法来发送请求并获取服务器返回的数据
         * 将返回一个Response实例对象,这个对象就是服务器返回的数据
         * 如何获取数据的内容：
         * String responseData = response.body().string();
         */
        client.newCall(request).enqueue(callback);//异步请求
    }
}
