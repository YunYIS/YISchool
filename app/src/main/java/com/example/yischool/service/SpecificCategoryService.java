package com.example.yischool.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.yischool.Bean.ServerDatabaseBean.Category;
import com.example.yischool.Bean.ServerDatabaseBean.CategoryRecommend;
import com.example.yischool.Bean.jsonBean.CommodityCardBean;

import java.util.HashSet;
import java.util.List;

public class SpecificCategoryService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }



    public class MyBinder extends Binder {

        private HashSet<Category> resultHotBrands = null;//保存查询到的类别数据
        private HashSet<CommodityCardBean> resultCards = null;//保存查询到的商品卡片数据
        private List<CategoryRecommend> resultCategoryRecommends = null;//商品推荐

        public HashSet<CommodityCardBean> getResultCards() {
            return resultCards;
        }

        public void setResultCards(HashSet<CommodityCardBean> resultCards) {
            this.resultCards = resultCards;
        }
    }
}
