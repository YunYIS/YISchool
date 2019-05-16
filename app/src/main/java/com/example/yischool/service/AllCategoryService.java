package com.example.yischool.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.yischool.AllCategoryActivity;

import java.util.List;

import com.example.yischool.Bean.ServerDatabaseBean.Category;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AllCategoryService extends Service {

    public static final String LOG_TAG = "AllCategoryService";
    private MyBinder myBinder = new MyBinder();

    public AllCategoryService(){}

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
    /**
     * startService调用的方法，每start一次Service，则调用该方法查询一次数据，然后由myBinder对象保存每一次的查询状态和数据
     * @param intent 每次查询哪一个大类别（primaryCategory参数）
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //根据每次传递的primaryCategoryStr查询条件参数来查询后台数据库中的类别数据
        String primaryCategoryStr = intent.getStringExtra("primaryCategory");
        final Message message = new Message();
        message.what = myBinder.queryStatus;
        if("热门推荐".equals(primaryCategoryStr)){//热门推荐大类别由点击量排行统计得出

//            myBinder.categories = new ArrayList<>();
//            myBinder.queryStatus = myBinder.QUERY_STATUS_BEING;
//            /*
//             * 热门分类排行：统计查询Commodity表中点击量max的前6个商品的具体类别（六个已填写具体类别的商品）
//             */
//            //查询browsingHistory表中的商品点击量前6位（按browseCommodity列分组统计个数，再排序）
//            BmobQuery<browsingHistory> browsingHistoryQuery = new BmobQuery<>();
//            browsingHistoryQuery.groupby(new String[]{"browseCommodity"})
//                    .setHasGroupCount(true)//返回每个分组的记录数(_count字段)
//                    .findStatistics(browsingHistory.class, new QueryListener<JSONArray>() {
//                        @Override
//                        public void done(JSONArray jsonArray, BmobException e) {
//                            if (e == null) {
//                                Log.d(LOG_TAG, "查询成功：jsonData:" + jsonArray.toString());
//                                try {
//                                    //由于分组统计个数之后没有按count排序，所以在客户端排序
//                                    JSONObject jsonObject = null;
//                                    int count = 0;
//                                    String commodityId = null;
//                                    List<Integer> countList = new ArrayList<>();
//                                    List<String> commodityIdList = new ArrayList<>();
//                                    for(int i = 0; i < jsonArray.length(); i++){//取到按商品分组的记录数
//                                        jsonObject = jsonArray.getJSONObject(i);
//                                        count = jsonObject.getInt("_count");
//                                        countList.add(count);
//                                        commodityId = jsonObject.getJSONObject("browseCommodity").getString("objectId");
//                                        commodityIdList.add(commodityId);
//                                    }
//                                    Log.d(LOG_TAG, "countList size: " + countList.size());
//                                    Log.d(LOG_TAG, "commodityIdList size: " + commodityIdList.size());
//                                    List<String> maxCommodityIds = new ArrayList<>();//保存count（浏览量）最大的商品Id
//                                    for(int i = 0; i < 15; i++){//15次循环，取得15次count最大值(但只需六个排行类别，15次是由于有些商品未填类别)
//                                        if(countList.size() > 0){//如果没有10个以上分组则退出循环
//                                            int max = countList.get(0);
//                                            int index = 0;//最大值索引
//                                            for(int j = 1; j < countList.size(); j++){//取得该次count最大值
//                                                if(max < countList.get(j)){
//                                                    max = countList.get(j);
//                                                    index = j;
//                                                }
//                                            }
//                                            //获取该次最大值后，保存对应count最大值的commodityId
//                                            maxCommodityIds.add(commodityIdList.get(index));
//                                            countList.remove(index);//去掉该次最大值，下次循环继续找最大值，则为次大值
//                                            commodityIdList.remove(index);
//                                        }else{
//                                            break;
//                                        }
//                                    }
//                                    Log.d(LOG_TAG, "maxCommodityIds size: " + maxCommodityIds.size()
//                                            +", "+maxCommodityIds.toArray().toString());
//                                    //通过商品Id，查询商品具体类别(即热门分类排行榜)
//                                    BmobQuery<Commodity> commodityQuery = new BmobQuery<>();
//                                    commodityQuery.addQueryKeys("category")
//                                            .addWhereContainedIn("objectId", maxCommodityIds)
//                                            .findObjects(new FindListener<Commodity>() {
//                                                @Override
//                                                public void done(List<Commodity> list, BmobException e) {
//                                                    if (e == null) {
//                                                        Log.d(LOG_TAG, "查询成功：list size：" + list.size());
//                                                        for(Commodity commodity : list){
//                                                            Log.d(LOG_TAG, "Category img url:" +
//                                                                    commodity.getCategory().getSpecificCategoryURL());
//
//                                                            Category category = commodity.getCategory();
//                                                            category.setPrimaryCategory("热门推荐");//对应页面显示数据
//                                                            category.setSecondaryCategory("热门分类排行");
//                                                            myBinder.categories.add(category);
//                                                        }
//                                                        myBinder.queryStatus = myBinder.QUERY_STATUS_SUCCESS;
//                                                    } else {
//                                                        Log.d(LOG_TAG, "查询失败" + e.getMessage());
//                                                        myBinder.queryStatus = MyBinder.QUERY_STATUS_FAILURE;
//                                                    }
//
//                                                    //查询方法结束，通知AllCategoryActivity更新界面
//                                                    message.what = myBinder.queryStatus;
//                                                    AllCategoryActivity.serviceHandler.sendMessage(message);
//                                                }
//                                            });
//
//                                    Log.d(LOG_TAG, "myBinder.categories size：" + myBinder.categories.size());
//
//                                } catch (JSONException e1) {
//                                    e1.printStackTrace();
//                                }
//                            } else {
//                                Log.d(LOG_TAG, "查询失败"+e.getMessage());
//                                myBinder.queryStatus = MyBinder.QUERY_STATUS_FAILURE;
//                                myBinder.categories =null;
//
//                                //查询方法结束，通知AllCategoryActivity更新界面
//                                message.what = myBinder.queryStatus;
//                                AllCategoryActivity.serviceHandler.sendMessage(message);
//                            }
//                        }
//                    });
//            /*
//             * 猜你喜欢：查询登陆用户浏览商品的历史记录，统计具体类别的最大浏览次数前几位（若未登录，则不推荐（不显示））
//             */
//            if(InitApplication.getCurrentUser() != null){//用户已登录
//
//            }

        } else {
            BmobQuery<Category> categoryQuery = new BmobQuery<>();
            Log.d(LOG_TAG, "com.example.yischool.service currentThread: " + Thread.currentThread().getId());
            myBinder.queryStatus = MyBinder.QUERY_STATUS_BEING;//正在查询状态
            categoryQuery.addWhereEqualTo("primaryCategory", primaryCategoryStr)
                    .findObjects(new FindListener<Category>() {
                        @Override
                        public void done(List<Category> list, BmobException e) {
                            if (e == null) {
                                Log.d(LOG_TAG, "查询成功：" + list.size());
                                Log.d(LOG_TAG, "bmob currentThread: " + Thread.currentThread().getId());
                                myBinder.queryStatus = MyBinder.QUERY_STATUS_SUCCESS;
                                myBinder.categories = list;
                            } else {
                                Log.d(LOG_TAG, "查询失败,e.toString(): " + e.toString()+"e.getMessage(): "+e.getMessage());
                                myBinder.queryStatus = MyBinder.QUERY_STATUS_FAILURE;
                                myBinder.categories = null;
                            }

                            //查询方法结束，通知AllCategoryActivity更新界面
                            message.what = myBinder.queryStatus;
                            AllCategoryActivity.serviceHandler.sendMessage(message);
                        }
                    });
        }

        stopSelf();//查询完成结束Service（由于绑定，所以Service还未销毁）
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy()");
    }

    public class MyBinder extends Binder {
        public static final int QUERY_STATUS_FAILURE = 0;//查询状态失败
        public static final int QUERY_STATUS_SUCCESS = 1;//查询状态成功
        public static final int QUERY_STATUS_BEING = 2;//查询状态正在查询

        private int queryStatus = QUERY_STATUS_BEING;
        private List<Category> categories = null;//保存查询到的类别数据

        public List<Category> getCategories() {//如果categories不为空，则查询成功
            return categories;
        }

        public int getQueryStatus() {
            return queryStatus;
        }
    }
}
