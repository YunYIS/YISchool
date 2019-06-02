package com.example.yischool.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.yischool.Bean.ServerDatabaseBean.Category;
import com.example.yischool.Bean.ServerDatabaseBean.Collection;
import com.example.yischool.Bean.ServerDatabaseBean.Commodity;
import com.example.yischool.Bean.ServerDatabaseBean.User;
import com.example.yischool.Bean.ServerDatabaseBean.browsingHistory;
import com.example.yischool.Bean.jsonBean.CommodityCardBean;
import com.example.yischool.InitApplication;
import com.example.yischool.bmobNet.QueryHelper;
import com.example.yischool.mainFragment.HomePageFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class HomePagerCommodityService extends Service {

    public static final String LOG_TAG = "HomePagCommodiService";

    /**
     * 查询动作请求码，区分handler返回的消息
     */
    //有无用户登陆
    public static final int REQUEST_LOGGED = 1;
    public static final int REQUEST_NO_LOGIN = 2;
    //浏览记录
    public static final int REQUEST_BROWSE_HISTORY = 3;
    //收藏记录
    public static final int REQUEST_COLLECTION = 4;

    /**
     * 返回活动数据，handler标志
     */
    public static final int RESULT_COMPLETED = 11;

    private MyBinder myBinder;
    //接收查询线程消息
    private static Handler serviceHandler;
    //通知活动（Fragment）
    private Handler fragmentHandler;
    //保存用户的浏览历史
    private List<browsingHistory> browsingHistoryList;
    //用户喜欢浏览的种类集合
    private HashSet<Category> categoryHashSet;
    //商品搜索结果
    private HashSet<Commodity> commodityHashSet;
    //搜索结果的商品卡片
    private HashSet<CommodityCardBean> commodityCardHashSet;
    //查询跳过条数
    private int skipCounts;
    //收藏量保存
    private List<Integer> countList;

    @Override
    public void onCreate() {
        super.onCreate();
        myBinder = new MyBinder();
        serviceHandler = new MyHandler(this);
        commodityHashSet = new HashSet<>();
        commodityCardHashSet = new HashSet<>();
        countList = new ArrayList<>();
        fragmentHandler = HomePageFragment.getHandler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //当前是否有用户登陆
        boolean isLogged = InitApplication.getCurrentUser() != null ? true : false;
        if(intent != null && intent.hasExtra(HomePageFragment.SKIP_COUNT_KEY)){
            skipCounts = intent.getIntExtra(HomePageFragment.SKIP_COUNT_KEY, 0);
        }

        /**
         * if当前有用户登陆，安照用户的浏览记录，来进行推荐
         */
        if(isLogged){
            browsingHistoryList = new ArrayList<>();
            //先查询用户的浏览记录
            QueryHelper<browsingHistory> browseQueryHelper = QueryHelper.getInstance();
            browseQueryHelper.addAndEqualKey("browseUser", InitApplication.getCurrentUser());
            browseQueryHelper.combineQueryList()
                    .queryFormServer(new browsingHistory(), serviceHandler, REQUEST_BROWSE_HISTORY);

        }else{

            //没有用户登陆，按发布时间最近推荐，随机
            queryNoLogin();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return myBinder;
    }

    /**
     * 用户未登录时查询，（按发布时间最近推荐，随机）
     */
    public void queryNoLogin(){

        QueryHelper<Commodity> queryHelper = QueryHelper.getInstance();

        queryHelper.setInclude("publishUser[username|headPortrait]")
                .setOrder("-createdAt")
                .setLimitNum(20)
                .setSkipCount(skipCounts)
                .queryFormServer(new Commodity(), serviceHandler, REQUEST_NO_LOGIN);
    }

    /**
     * 通过查询到的历史记录列表，组装该用户喜欢浏览的商品分类集合
     */
    public void setCategorySet(){

        categoryHashSet = new HashSet<>();

        if(browsingHistoryList.size() > 0){
            for(browsingHistory b : browsingHistoryList){
                Commodity commodity = b.getBrowseCommodity();
                Category c = commodity.getCategory();
                categoryHashSet.add(c);
            }
        }
    }

    /**
     * 根据查询到了用户浏览种类，对用户进行推荐
     */
    public void queryCommodityInCategory(){

        if(categoryHashSet != null && categoryHashSet.size() > 0){

            QueryHelper<Commodity> queryHelper = QueryHelper.getInstance();
            queryHelper.addAndContainedInKey("category", new ArrayList<>(categoryHashSet));

            queryHelper.combineQueryList()
                    .setInclude("publishUser[username|headPortrait]")
                    .setLimitNum(20)
                    .setSkipCount(skipCounts)
                    .queryFormServer(new Commodity(), serviceHandler, REQUEST_LOGGED);

        }else{
            queryNoLogin();
        }
    }

    /**
     * 所有数据查询完成后，开始合成商品卡片数据
     */
    public void compoundCardData(){

        Log.d(LOG_TAG, "commodity size: " + commodityHashSet.size());
        Log.d(LOG_TAG, "countList size: " + countList.size());

        Boolean isCountSuccess = false;
        if(commodityHashSet.size() == countList.size()){
            isCountSuccess = true;
        }

        int i = 0;
        for(Commodity commodity : commodityHashSet) {

            User user = commodity.getPublishUser();
            CommodityCardBean card = new CommodityCardBean(commodity.getPictureAndVideo().get(0).getUrl(),
                    commodity.getTitle(), commodity.getPrice(), isCountSuccess ? countList.get(i): 0,
                    user.getHeadPortrait() == null ? null : user.getHeadPortrait().getUrl(),
                    user.getUsername(), commodity.getObjectId());
            commodityCardHashSet.add(card);
            ++i;
        }

        //组装完成后，保存数据在Binder中,与活动通信
        myBinder.setResultCards(commodityCardHashSet);
        Log.d(LOG_TAG, "mybinder: " + myBinder.resultCards.size());

        //通知活动数据加载完成，可以取出
        Message message = new Message();
        message.what = RESULT_COMPLETED;
        fragmentHandler.sendMessage(message);

    }

    /**
     * 收藏量统计查询
     */
    private void queryCollections(){

        if(commodityHashSet.size() > 0){
            QueryHelper<Collection> queryHelper = QueryHelper.getInstance();
            //对统计结果进行过滤
            HashMap<String, Object> map = new HashMap<>();
            for(Commodity c : commodityHashSet){

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("$eq", c);
                    map.put("collectCommodity", jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            queryHelper.setGroupby(new String[]{"collectCommodity"})
                    .setHaving(map)
                    .setLimitNum(20)
                    .queryStatisticsFormServer(Collection.class, serviceHandler, REQUEST_COLLECTION);
        }
    }

    static class MyHandler extends Handler{

        WeakReference<HomePagerCommodityService> weakReference;

        public MyHandler(HomePagerCommodityService service){
            weakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HomePagerCommodityService service = weakReference.get();
            if(service != null){
                //查询成功
                if(msg.what == QueryHelper.QUERY_HELPER_SUCCESS){
                    switch (msg.arg1){
                        case REQUEST_BROWSE_HISTORY:
                            //历史记录
                            service.browsingHistoryList = (List<browsingHistory>)msg.obj;
                            //开始组装商品类型
                            service.setCategorySet();
                            //组装完成后，以该种类集为查询条件，开始推荐商品
                            service.queryCommodityInCategory();
                            break;
                        case REQUEST_NO_LOGIN:
                            //接受查询数据
                            service.commodityHashSet.addAll((List<Commodity>)msg.obj);
                            //通过接收到的商品数据，查询每个商品的收藏量
                            service.queryCollections();
                            break;
                        case REQUEST_LOGGED:
                            service.commodityHashSet.addAll((List<Commodity>)msg.obj);
                            service.queryCollections();
                            break;
                        case REQUEST_COLLECTION:
                            service.countList = (List<Integer>)msg.obj;
                            //开始组装商品卡片，并通知活动
                            service.compoundCardData();
                            break;

                    }
                }else if(msg.what == QueryHelper.QUERY_HELPER_FAILED){

                    //查询失败，处理返回结果
                    switch (msg.arg1){
                        case REQUEST_BROWSE_HISTORY:
                            Log.d(LOG_TAG, "查询失败");
                            break;
                        case REQUEST_NO_LOGIN:
                            Log.d(LOG_TAG, "小类别查询失败");
                            break;
                        case REQUEST_LOGGED:
                            Log.d(LOG_TAG, "具体类别查询失败");
                            break;
                        case REQUEST_COLLECTION:
                            Log.d(LOG_TAG, "商品收藏查询失败");
                            //开始组装商品卡片数据
                            service.compoundCardData();
                            break;
                    }
                }
            }
        }
    }


    public class MyBinder extends Binder {

        private HashSet<CommodityCardBean> resultCards = null;//保存查询到的类别数据

        public HashSet<CommodityCardBean> getResultCards() {
            return resultCards;
        }

        public void setResultCards(HashSet<CommodityCardBean> resultCards) {
            this.resultCards = resultCards;
        }
    }
}
