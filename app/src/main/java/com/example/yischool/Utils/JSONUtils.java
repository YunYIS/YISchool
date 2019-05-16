package com.example.yischool.Utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author 张云天
 * date on 2019/4/11
 * describe: json数据解析帮助类
 */
public class JSONUtils {

    /**
     * 获取本地assets文件数据
     * @param context
     * @param fileName
     * @return 文件数据字符串
     */
    public static String getLocalData(Context context, final String fileName){
        final StringBuilder stringBuilder = new StringBuilder();
        //获得assets资源管理器
        final AssetManager assetManager = context.getAssets();
        //使用IO流读取json文件内容 (不是不开启子线程，而是需要使用同步操作)
        try {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                assetManager.open(fileName),"utf-8"));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line.trim());
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
    /**
     * 解析json数组
     * @param <T>
     * @param type
     * @param jsonData
     * @return
     */
    public static <T> List<T> resolveJsonArray(Type type, String jsonData){
        Gson gson = new Gson();
        return gson.fromJson(jsonData, type);
    }
    /**
     * 解析json对象
     * @param type
     * @param jsonData
     * @param <T>
     * @return
     */
    public static <T> T resolveJsonObject(Class<T> type, String jsonData) {
        Gson gson = new Gson();
        return gson.fromJson(jsonData, type);
    }
}
